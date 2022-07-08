package com.servustech.eduson.features.permissions;

import com.amazonaws.services.kms.model.NotFoundException;
import com.google.gson.Gson;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.account.users.data.IndividualService;
import com.servustech.eduson.features.account.users.data.LegalService;
import com.servustech.eduson.features.permissions.subscriptions.Interval;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionPeriodsService;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.utils.mail.MailSenderPostmarkService;
import com.servustech.eduson.utils.mail.SubscriptionsFirstInvoicePaidOnce;
import com.servustech.eduson.features.permissions.transactions.Transaction;
import com.servustech.eduson.features.permissions.transactions.TransactionService;
import com.servustech.eduson.utils.mail.InvoiceService;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.files.File;

import com.stripe.Stripe;
import com.stripe.model.StripeObject;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.Product;
import com.stripe.model.Charge;
import com.stripe.model.Invoice;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.InvoiceItemCreateParams;
import com.stripe.model.PaymentMethod;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.model.InvoiceItem;
import com.stripe.param.InvoicePayParams;
import com.stripe.exception.StripeException;
import com.stripe.param.InvoiceCreateParams.PaymentSettings.PaymentMethodType;
// import com.stripe.param.SubscriptionCreateParams.PaymentSettings.PaymentMethodType;

import javax.transaction.Transactional;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;
import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;
import java.sql.Timestamp;
@Service
@AllArgsConstructor
public class PaymentIntentService {

  private final UserService userService;
  private final SubscriptionPeriodsService subscriptionPeriodsService;
  private final PermissionsService permissionsService;
  private final MailSenderPostmarkService mailSenderPostmarkService;
  private final SubscriptionsFirstInvoicePaidOnce subscriptionsFirstInvoicePaidOnce;
  private final TransactionService transactionService;
  private final InvoiceService invoiceService;
  private final FileService fileService;
  private final IndividualService individualService;
  private final LegalService legalService;
  private final StripeKeyService stripeKeyService;

  @Transactional
  public ResponseEntity<?> create(User user, Boolean stripe) throws StripeException {

    if (!user.isAdmin()) {
      var isIndividual = user.getInvoiceAddressPersonal();

      if (isIndividual == null) {
        throw new NotFoundException("buyers-invoicing-data-missing");
      } else {
        if (isIndividual.equals(false)) {
          var legal = legalService.findById(user.getId()).orElseThrow(() -> new NotFoundException("legal-invoicing-data-missing"));
          var vatDataDto = legalService.checkVat(legal.getCui(), null);
          if (vatDataDto.getIsValid()) {
          } else {
            throw new CustomException("vat-num-buy-as-individual");
          }
        } else {
          individualService.findById(user.getId()).orElseThrow(() -> new NotFoundException("individual-invoicing-data-missing"));
        }
      }
    }

    var now = ZonedDateTime.now();

    Transaction transaction = Transaction.builder()
      .timestamp(now)
      .userId(user.getId())
      .paymentType(stripe ? PaymentType.STRIPE : PaymentType.TRANSFER)
    .build();
    transaction = transactionService.save(transaction);

    List<PermissionDto> permissionDtos_ = permissionsService.snapshotCart(user, transaction, now);

    Stripe.apiKey = stripeKeyService.getApiKey();

    var customerId = stripe ? getCustomerId(user) : null;

    // put all the same recurring products in one record. TODO set back somehow the stripe subscription item id.

    List<MutableTriple<PermissionDto, List<PermissionDto>, String>> packed = new ArrayList<>();
    for (Iterator <PermissionDto> it = permissionDtos_.iterator(); it.hasNext();) {
      PermissionDto permissionDto = it.next();
      boolean found = false;
      for (Iterator <MutableTriple<PermissionDto, List<PermissionDto>, String>> it2 = packed.iterator(); it2.hasNext();) {
        MutableTriple<PermissionDto, List<PermissionDto>, String> elem = it2.next();
        PermissionDto permissionDto2 = elem.getLeft();
        if (permissionDto.getProductType().equals(permissionDto2.getProductType())
        && permissionDto.getProductId().equals(permissionDto2.getProductId())
        && permissionDto.getPeriodId() != null // TODO ?
        && permissionDto.getPeriodId().equals(permissionDto2.getPeriodId())
        ) {
          found = true;
          elem.getMiddle().add(permissionDto);
          permissionDto2.setQuantity(permissionDto2.getQuantity() + 1);
          break;
        }
      }
      if (!found) {
        permissionDto.setQuantity(1L);
        packed.add(MutableTriple.of(permissionDto, new ArrayList<>(), null));
      }
    }

    List<MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> builders = new ArrayList<>();

    List<Float> sum = new ArrayList<>();
    sum.add(0F);

    for (Iterator <MutableTriple<PermissionDto, List<PermissionDto>, String>> it = packed.iterator(); it.hasNext();) {
      MutableTriple<PermissionDto, List<PermissionDto>, String> elem = it.next();
      setupPrice(elem, stripe, user, sum, builders);
    }

    // TODO we must check if item price changed since last stripe price use and create a new stripe price if it did

    final double THRESHOLD = .000001;
    if (Math.abs(sum.get(0) - 0) < THRESHOLD) {
      var permissionsHere = permissionsService.getByOwnerAndTransaction(user.getId(), transaction);
      freeSucceeded(transaction, permissionsHere, now, null);
      // TODO if recurrent subscription is free, how it will renew without stripe? and the same for recurring with transfer
      PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder().client_secret("succeeded").build();
      return ResponseEntity.ok(paymentResponseDto);
    }

    // TODO refuse to go further if sum is less than 2. Or 200.

    if (stripe) {
      for (Iterator <MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> it = builders.iterator(); it.hasNext();) {
        MutableTriple<Long, SubscriptionCreateParams.Builder, Float> b = it.next();
        List<com.stripe.param.SubscriptionCreateParams.PaymentSettings.PaymentMethodType> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add(com.stripe.param.SubscriptionCreateParams.PaymentSettings.PaymentMethodType.CARD);
        b.setMiddle(SubscriptionCreateParams.builder()
          .setCustomer(customerId)
          .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
          .setOffSession(true)
          // .addAllExpand(Arrays.asList("latest_invoice")) // TODO !!!! try to avoid last invoice retrieving
          .setPaymentSettings(SubscriptionCreateParams.PaymentSettings.builder()
            .addAllPaymentMethodType(paymentMethodTypes).build()
          ));
      }
    }

    List<List<Permission>> permissions = new ArrayList<>();
    int index = 0;
    for (Iterator <MutableTriple<PermissionDto, List<PermissionDto>, String>> it = packed.iterator(); it.hasNext();) {
      MutableTriple<PermissionDto, List<PermissionDto>, String> elem = it.next();
      permissions.add(addItemToNextInvoice(customerId, stripe, elem, now, builders, index));
      index++;
    }

    Subscription subscription = null;
    Invoice invoice = null;
    if (stripe) {
      Long valueSum = 0L;
      if (builders.size() > 0) {
        for (Iterator <MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> it = builders.iterator(); it.hasNext();) {
          MutableTriple<Long, SubscriptionCreateParams.Builder, Float> b = it.next();
          SubscriptionCreateParams.Builder builder  = b.getMiddle();
          SubscriptionCreateParams subscriptionParams = builder.build();
          if (it.hasNext()) {
            InvoiceItemCreateParams params = InvoiceItemCreateParams.builder()
              .setAmount((new Float(b.getRight() * 100)).longValue())
              .setCurrency("ron")
              .setCustomer(customerId)
            .build();
            InvoiceItem.create(params);
          } else {
            subscription = createSubscription(subscriptionParams, permissions);
          }
        }
        for (Iterator <MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> it = builders.iterator(); it.hasNext();) {
          MutableTriple<Long, SubscriptionCreateParams.Builder, Float> b = it.next();
          SubscriptionCreateParams.Builder builder  = b.getMiddle();
          SubscriptionCreateParams subscriptionParams = builder.build();
          if (it.hasNext()) {
            createSubscription(subscriptionParams, permissions);
          }
        }
      } else {
        List<PaymentMethodType> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add(PaymentMethodType.CARD);
        InvoiceCreateParams invoiceParams = InvoiceCreateParams.builder()
          .setCustomer(customerId)
          .setPaymentSettings(InvoiceCreateParams.PaymentSettings.builder()
            .addAllPaymentMethodType(paymentMethodTypes).build()
          )
        .build();
        invoice = Invoice.create(invoiceParams);
      }
    }
    PaymentIntent paymentIntent = null;
    if (stripe) {
      paymentIntent = PaymentIntent.retrieve(getInvoice(invoice, subscription).getPaymentIntent());
      // List<String> paymentMethodTypes = new ArrayList<>();
      // paymentMethodTypes.add("card");
      PaymentIntentUpdateParams paymentIntentUpdateParams = PaymentIntentUpdateParams.builder()
        .setSetupFutureUsage(PaymentIntentUpdateParams.SetupFutureUsage.OFF_SESSION)
        // .setAmount(amount)
        // .setCurrency("ron")
        // .addAllPaymentMethodType(paymentMethodTypes)
      .build();
      paymentIntent = paymentIntent.update(paymentIntentUpdateParams);
    }

    transaction.setPaymentIntent(stripe ? paymentIntent.getId() : null);
    transaction.setValue(sum.get(0));
    // Transaction transaction2 = transaction;
    // permissions
    //   .stream()
    //   .forEach(permission -> permission.setTransaction(transaction2));
    File file = null;
    if (!stripe) {
      List<Permission> permissions2 = new ArrayList<>();
      for (Iterator<List<Permission>> po = permissions.iterator();po.hasNext();) {
        for (Iterator<Permission> pi = po.next().iterator();pi.hasNext();) {
          permissions2.add(pi.next());
        }
      }
      file = sendTransferMail(user, permissions2, transaction);
      permissionsService.emptyCart(transaction.getUserId());
    }
  
    var clientSecret = stripe ? paymentIntent.getClientSecret() : "";

    // TODO set up return_url at paying (maybe at client)
    PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder().client_secret(clientSecret).build();
    return ResponseEntity.ok(paymentResponseDto);
  }

  private Subscription createSubscription(SubscriptionCreateParams subscriptionParams, List<List<Permission>> permissions) throws StripeException {
    Subscription subscription = Subscription.create(subscriptionParams);
    SubscriptionItemCollection subscriptionItemCollection = subscription.getItems();
    for (SubscriptionItem subscriptionItem : subscriptionItemCollection.autoPagingIterable()) {
      var id = subscriptionItem.getId();
      var indexString = subscriptionItem.getMetadata().get("temp");
      var index = Integer.parseInt(indexString);
      List<Permission> permissionsE = permissions.get(index);
      permissionsE.stream().forEach(p -> p.setStripe(id));
    }
    return subscription;
  }

  private Invoice getInvoice(Invoice invoice, Subscription subscription) throws StripeException {
    if (invoice != null) {
      return invoice.finalizeInvoice();
    } else {
      return Invoice.retrieve(subscription.getLatestInvoice());
    }
  }
  // 
  // String: priceStripeId
  void setupPrice(
    MutableTriple<PermissionDto, List<PermissionDto>, String> elem, 
    Boolean stripe, 
    User user, 
    List<Float> sum,
    List<MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> builders
  ) throws StripeException {

    PermissionDto permissionDto = elem.getLeft();
    if (permissionDto.getUserId() != null) {
      user = userService.findById(permissionDto.getUserId());
    } else {
      if (permissionDto.getUserEmail() != null) {
        user = userService.findByEmail(permissionDto.getUserEmail());
      }
    }
    permissionDto.setUserId(user.getId());
    List<PermissionDto> permissionDtos = elem.getMiddle();
    for (Iterator<PermissionDto> it = permissionDtos.iterator(); it.hasNext();) {
      it.next().setUserId(user.getId());
    }

    Benefit benefit = permissionsService.getProduct2(permissionDto.getProductType(), permissionDto.getProductId());
    var stripeProductId = stripe ? getStripeProductId(benefit) : null;

    Price price = null;
    Long periodId = permissionDto.getPeriodId();
    var amount = calculateOrderAmount(benefit, periodId);
    final double THRESHOLD = .000001;
    if (Math.abs(amount - permissionDto.getValue() * 100) > THRESHOLD) {
      throw new CustomException("price-changed");
    }
    sum.set(0, sum.get(0) + permissionDto.getQuantity() * amount);
    String priceStripeId = null;
    if (periodId != null) {
      boolean found = false;
      for (Iterator <MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> it = builders.iterator(); it.hasNext();) {
        MutableTriple<Long, SubscriptionCreateParams.Builder, Float> b = it.next();
        if (b.getLeft().equals(periodId)) {
          found = true;
          b.setRight(b.getRight() + permissionDto.getQuantity() * permissionDto.getValue());
          break;
        }
      }
      if (!found) {
        builders.add(MutableTriple.of(periodId, null, permissionDto.getQuantity() * permissionDto.getValue()));
      }
      // we have to set a price in stripe if it doesn't already exist
      var subscriptionPeriod = subscriptionPeriodsService.get(permissionDto.getProductType(),
          permissionDto.getProductId(), periodId);
      priceStripeId = stripe ? subscriptionPeriod.getStripe() : null;
      if (stripe && priceStripeId == null) {
        var period = subscriptionPeriodsService.getPeriod(periodId);
        PriceCreateParams.Recurring recurring = PriceCreateParams.Recurring.builder()
            .setInterval(PriceCreateParams.Recurring.Interval.valueOf(period.getInterval().name()))
            .setIntervalCount(period.getIntervalCount()).build();
        PriceCreateParams priceCreateParams = PriceCreateParams.builder().setCurrency("ron").setProduct(stripeProductId)
            .setUnitAmount(amount).setRecurring(recurring).build();
        price = Price.create(priceCreateParams);
        priceStripeId = price.getId();
        subscriptionPeriodsService.setStripePriceId(subscriptionPeriod, priceStripeId);

      } else if (stripe) {
        price = Price.retrieve(priceStripeId);
      }
    } else {
      if (stripe) {
        priceStripeId = benefit.getPriceStripe();
        if (priceStripeId == null) {
          PriceCreateParams priceCreateParams = PriceCreateParams.builder().setCurrency("ron").setProduct(stripeProductId)
            .setUnitAmount(amount).build();
          price = Price.create(priceCreateParams);
          priceStripeId = price.getId();
          benefit.setPriceStripe(priceStripeId);
        }
      }
    }
    elem.setRight(priceStripeId);
  }

  List<Permission> addItemToNextInvoice(
    String customerId, 
    Boolean stripe, 
    MutableTriple<PermissionDto, List<PermissionDto>, String> elem, 
    ZonedDateTime now, 
    List<MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> builders,
    int index
  ) throws StripeException {
    PermissionDto permissionDto = elem.getLeft();
    String priceStripeId = elem.getRight();
    Long periodId = permissionDto.getPeriodId();
    Permission permission = permissionsService.findById(permissionDto.getId());
    List<PermissionDto> permissionDtos = elem.getMiddle();
    List<Permission> permissions = permissionDtos.stream().map(dto -> permissionsService.findById(dto.getId())).collect(Collectors.toList());
    permissions.add(permission);
    if (periodId == null) {

      InvoiceItem invoiceItem = null;
      if (stripe) {
        InvoiceItemCreateParams params = InvoiceItemCreateParams.builder()
          .setPrice(priceStripeId)
          .setCustomer(customerId)
        .build();
        invoiceItem = InvoiceItem.create(params);
      }
      final InvoiceItem invoiceItem2 = invoiceItem;
      // permission.setStripe(invoiceItem == null ? null : invoiceItem.getId());
      permissions.stream().forEach(p -> p.setStripe(invoiceItem2 == null ? null : invoiceItem2.getId()));
    } else {
      MutableTriple<Long, SubscriptionCreateParams.Builder, Float> found = null;
      for (Iterator <MutableTriple<Long, SubscriptionCreateParams.Builder, Float>> it = builders.iterator(); it.hasNext();) {
        MutableTriple<Long, SubscriptionCreateParams.Builder, Float> b = it.next();
        if (b.getLeft().equals(periodId)) {
          found = b;
          break;
        }
      }
      if (found == null) {
        throw new CustomException("error-735832106494541");
      }
      found.setMiddle(found.getMiddle()
        .addItem(
          SubscriptionCreateParams.Item.builder().setQuantity(permissionDto.getQuantity()).setPrice(priceStripeId)
          .putMetadata("temp", "" + index)
          .build()
        )
      );
    }
    // permission.setAddedDate(now);
    permissions.stream().forEach(p -> p.setAddedDate(now));
    return permissions;
  }

  private String getCustomerId(User user) throws StripeException {
    Customer customer = null;
    if (user.getStripe() != null) {
      customer = Customer.retrieve(user.getStripe());
    } else {
      var metadata = new HashMap<String, String>();
      metadata.put("userId", user.getId().toString());
      CustomerCreateParams customerParams = new CustomerCreateParams.Builder().putAllMetadata(metadata)
          .setEmail(user.getEmail()).setName(user.getFullName()).build();
      customer = Customer.create(customerParams);
      user.setStripe(customer.getId());
    }
    return customer.getId();
  }

  private Long calculateOrderAmount(Benefit benefit, Long periodId) {
    var amount = benefit.getAmount(periodId);
    if (amount == null) {
      throw new CustomException("product-has-no-price-4period");
    }
    return new Long((long) (amount * 100));
  }

  private String getStripeProductId(Benefit benefit) throws StripeException {
    var stripeProductId = benefit.getStripe();
    Product product = null;
    if (stripeProductId == null) {
      ProductCreateParams productCreateParams = ProductCreateParams.builder().setName(benefit.getName()).build();
      product = Product.create(productCreateParams);
      stripeProductId = product.getId();
      benefit.setStripe(stripeProductId);
    } else {
      product = Product.retrieve(stripeProductId);
    }
    return stripeProductId;
  }

  @Transactional
  public void chargeSucceeded(StripeObject stripeObject) throws StripeException {
    System.out.println("chargeSucceeded function" + stripeObject);
  }
  public void freeSucceeded(Transaction transaction, List<Permission> permissions, ZonedDateTime now, String chargeId) throws StripeException {
    for (Iterator<Permission> it2 = permissions.iterator(); it2.hasNext();) {
      Permission permission = it2.next();
      permission.setActive(true);
      permission.setStartTime(now);
      if (permission.getPeriodId() != null) {
        var period = subscriptionPeriodsService.getPeriod(permission.getPeriodId());
        var interval = period.getInterval();
        var intervalCount = period.getIntervalCount();
        var expires = getExpires(now, interval, intervalCount); // maybe we should take from stripe objects
        permission.setExpires(expires);
      }
    }
    transaction.setTransactionId(chargeId != null ? chargeId : Timestamp.from(now.toInstant()).toString());
    transaction.setPaidAt(now);
  }

  // TODO sbdjbdfjsb what to do here? we have to make a new transaction.

  @Transactional
  public void invoicePaid(StripeObject stripeObject) throws StripeException {
    System.out.println("invoicePaid function" + stripeObject);
    // TODO check idempotency of these functions
    var invoice = (Invoice) stripeObject;
    var chargeId = invoice.getCharge();
    var amount = invoice.getTotal();
    String customerId = invoice.getCustomer();
    String paymentIntentId = invoice.getPaymentIntent();
    String subscriptionId = invoice.getSubscription();
    Subscription subscription = subscriptionId == null ? null : Subscription.retrieve(subscriptionId);
    ZonedDateTime now = ZonedDateTime.now();
    Transaction transaction = null;
    boolean initial = true;
    List<Permission> permissions = null;
    PaymentIntent paymentIntent = null;
    if(invoice.getBillingReason().equals("subscription_create") || invoice.getBillingReason().equals("manual")) {
      transaction = transactionService.getTransactionByPaymentIntent(paymentIntentId);
      permissions = transaction.getPermissions();
      paymentIntent = PaymentIntent.retrieve(paymentIntentId);
      freeSucceeded(transaction, permissions, now, chargeId);
    }
    if(invoice.getBillingReason().equals("subscription_cycle")) {
      initial = false;
      // check idempotency // not so accurate:
      try {
        transaction = transactionService.getTransactionByPaymentIntent(paymentIntentId);
        return;
      } catch(NotFoundException e) {
        // okay
      }
      transaction = 
        Transaction.builder()
          .userId(userService.findByStripe(customerId).getId())
          .timestamp(now)
          .paidAt(now)
          .transactionId(chargeId)
          .paymentIntent(paymentIntentId)
          .paymentType(PaymentType.STRIPE)
          .value(amount.floatValue())
        .build();
      transaction = transactionService.save(transaction);
      SubscriptionItemCollection subscriptionItemCollection = subscription.getItems();
      permissions = new ArrayList<>();
      for (SubscriptionItem subscriptionItem : subscriptionItemCollection.autoPagingIterable()) {
        var subscriptionItemId = subscriptionItem.getId();
        var quantity = subscriptionItem.getQuantity();
        var pl = permissionsService.findLastByStripe(subscriptionItemId, quantity);
        for (int i = 0; i < pl.size(); i++) {
          var p = pl.get(i);
          ZonedDateTime expires = null;
          if (p.getPeriodId() != null) {
            var period = subscriptionPeriodsService.getPeriod(p.getPeriodId());
            var interval = period.getInterval();
            var intervalCount = period.getIntervalCount();
            expires = getExpires(p.getExpires(), interval, intervalCount); // maybe we should take from stripe objects
          }
          var permission = permissionsService.createPermission(PermissionDto.builder()
            .userId(p.getUserId())
            .ownerId(p.getOwnerId())
            .productType(p.getProductType())
            .productId(p.getProductId())
            .periodId(p.getPeriodId())
            .active(true)
            .startTime(now)
            .expires(expires)
            .endsAt(p.getEndsAt())
            .addedDate(now)
            .stripe(subscriptionItemId)
            .value(p.getValue())
          .build());
          permissions.add(permission);
          permission.setTransaction(transaction);
        }
      }
    }
    if (initial) {
      permissionsService.emptyCart(transaction.getUserId());
    }
    transaction.setInvoiceId(createInvoiceId(now));
    var file = sendMail(permissions, transaction);
    if (initial) {
      subscriptionsFirstInvoicePaidOnce.payOutband(permissions, paymentIntent);
    }
  }

  // TODO do not create manual transactions, create manual permissions. if it is manual, it can grant permissions without a transaction.

  @Transactional
  public TransactionDto afterTransfer(TransactionDto transactionDto) {
    // for a renewing transfer, a new transaction has to be created at every cycle, that is, when the "ordered" invoice is created
    var transaction = transactionService.findById(transactionDto.getId());
    var pricePaid = transactionDto.getValue() * 100;
    var price = transaction.getValue();
    final double THRESHOLD = .000001;
    if (Math.abs(pricePaid - price) > THRESHOLD) {
      // TODO create a database for transfer price differences
      System.out.println("price difference");
      System.out.println(pricePaid);
      System.out.println(price);
      throw new CustomException("price-differs");
    }
    var permissions = transaction.getPermissions();
    var now = ZonedDateTime.now();
    for (Iterator<Permission> it2 = permissions.iterator(); it2.hasNext();) {
      Permission permission = it2.next();
      if (permission.getActive() != null && permission.getActive() == true) {
      }
      permission.setActive(true);
      permission.setStartTime(now);
      if (permission.getPeriodId() != null) {
        var period = subscriptionPeriodsService.getPeriod(permission.getPeriodId());
        var interval = period.getInterval();
        var intervalCount = period.getIntervalCount();
        var expires = getExpires(now, interval, intervalCount);
        permission.setExpires(expires);
      }
      // permission.setStartTime(
      //     ZonedDateTime.ofInstant(Instant.ofEpochSecond(charge.getCreated()), TimeZone.getDefault().toZoneId()));
    }
    transaction.setTransactionId(transactionDto.getTransactionId());
    transaction.setPaidAt(transactionDto.getPaidAt() != null ? transactionDto.getPaidAt() : now);
    transaction.setInvoiceId(createInvoiceId(now));
    var file = sendMail(permissions, transaction);
    return transactionService.getTransaction(transaction); // TODO create new from transaction
  }

  private ZonedDateTime getExpires(ZonedDateTime now, Interval interval, Long intervalCount) {
    // TODO we have to set stripe to attempt to renew the subscriptions some hours before end of the day. also, take this into account at transfer schedules.
    ZonedDateTime night = now.with(LocalTime.of(23, 59, 59));
    switch (interval) {
      case DAY:
        return night.plusDays(intervalCount);
      case WEEK:
        return night.plusWeeks(intervalCount);
      case MONTH:
        return night.plusMonths(intervalCount);
      case YEAR:
        return night.plusYears(intervalCount);
      default:
        return null;
    }
  }

  private File createInvoice(String status, User user, List<Permission> permissions, Transaction transaction) {
    Triple<java.io.File, String, String> triple = invoiceService.create(status, user, permissions, transaction);
    java.io.File file = triple.getLeft();
    String id = triple.getMiddle();
    String stripeId = triple.getRight();
    String path = fileService.storeFile(file);
    File f = fileService.saveWithFile(id + "_" + stripeId + ".pdf", path);
    return f;
  }

  public TransactionDto recreateInvoice(Long id, User user) {
    var transaction = transactionService.findById(id);
    if (!user.getId().equals(transaction.getUserId())) {
      if (!user.isAdmin()) {
        throw new NotFoundException("transaction-not-found");
      }
      user = userService.findById(transaction.getUserId());
    }
    if (!user.isAdmin()) {
      var isIndividual = user.getInvoiceAddressPersonal();

      if (isIndividual == null) {
        throw new NotFoundException("buyers-invoicing-data-missing");
      } else {
        if (isIndividual.equals(false)) {
          var legal = legalService.findById(user.getId()).orElseThrow(() -> new NotFoundException("legal-invoicing-data-missing"));
          var vatDataDto = legalService.checkVat(legal.getCui(), null);
          if (vatDataDto.getIsValid()) {
          } else {
            throw new CustomException("vat-num-invoice-as-individual");
          }
        } else {
          individualService.findById(user.getId()).orElseThrow(() -> new NotFoundException("individual-invoicing-data-missing"));
        }
      }
    }
    var permissions = permissionsService.getByOwnerAndTransaction(transaction.getUserId(), transaction);
    var isPaid = transaction.getPaidAt() != null;
    var file = createInvoice(isPaid ? "paid" : "ordered", user, permissions, transaction);
    if (isPaid) {
      transaction.setInvoiceFile(file);
    } else {
      transaction.setTransferFile(file);
    }
    return transactionService.getTransaction(transaction);
  }

  private File sendMail(List<Permission> permissions, Transaction transaction) {
    var user = userService.findById(transaction.getUserId());
    var file = createInvoice("paid", user, permissions, transaction);
    var inputStream = fileService.retrieve(file.getPath());
    mailSenderPostmarkService.sendInvoiceEmail(
      "paid", user, permissions, transaction, 
      individualService, legalService, 
      subscriptionPeriodsService, permissionsService, 
      inputStream);
    transaction.setInvoiceFile(file);
    return file;
  }

  private File sendTransferMail(User user, List<Permission> permissions, Transaction transaction) {
    var file = createInvoice("ordered", user, permissions, transaction);
    var inputStream = fileService.retrieve(file.getPath());
    mailSenderPostmarkService.sendInvoiceEmail(
      "ordered", user, permissions, transaction, 
      individualService, legalService, 
      subscriptionPeriodsService, permissionsService, 
      inputStream);
    transaction.setTransferFile(file);
    return file;
  }

  private String createInvoiceId(ZonedDateTime now) {
    return transactionService.getNextInvoiceId(now);
  }

}
