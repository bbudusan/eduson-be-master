package com.servustech.eduson.features.permissions;

import com.amazonaws.services.kms.model.NotFoundException;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import org.springframework.http.ResponseEntity;
import com.servustech.eduson.exceptions.CustomException;
import com.servustech.eduson.features.products.webinars.WebinarRepository;
import com.servustech.eduson.features.products.webinars.dto.WebinarViewResponse;
import com.servustech.eduson.features.categories.modules.ModuleWithContentRepository;
import com.servustech.eduson.features.categories.modules.ModuleC4CheckRepository;
import com.servustech.eduson.features.categories.modules.ModuleW4Check;
import com.servustech.eduson.features.categories.modules.ModuleW4CheckRepository;
import com.servustech.eduson.features.categories.modules.ModuleE4Check;
import com.servustech.eduson.features.categories.modules.ModuleE4CheckRepository;
import com.servustech.eduson.features.categories.modules.ModuleWithContent;
import com.servustech.eduson.features.products.courses.Course;
import com.servustech.eduson.features.products.courses.Course4Check;
import com.servustech.eduson.features.products.courses.Course4CheckRepository;
import com.servustech.eduson.features.products.liveEvents.LiveEvent4Check;
import com.servustech.eduson.features.products.liveEvents.LiveEvent4CheckRepository;
import com.servustech.eduson.features.products.webinars.Webinar;
import com.servustech.eduson.features.products.webinars.Webinar4Check;
import com.servustech.eduson.features.products.webinars.Webinar4CheckRepository;
import com.servustech.eduson.features.products.liveEvents.LiveEvent;
import com.servustech.eduson.features.products.courses.CourseRepository;
import com.servustech.eduson.features.products.liveEvents.LiveEventRepository;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionRepository;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionProductRepository;
import com.servustech.eduson.features.permissions.permissions.PermissionRepository;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.UserService;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionDto;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionPeriodsService;
import com.servustech.eduson.features.permissions.subscriptions.BenefitDto;
import com.servustech.eduson.features.permissions.subscriptions.Subscription;
import com.servustech.eduson.features.permissions.subscriptions.Subscription4CheckRepository;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionProduct;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionType;
import com.servustech.eduson.features.permissions.subscriptions.PeriodDto;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.permissions.transactions.TransactionService;
import com.servustech.eduson.features.permissions.transactions.Transaction;
import com.servustech.eduson.features.invitation.Invitation;
import com.servustech.eduson.features.invitation.InvitationService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
public class PermissionsService {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionProductRepository subscriptionProductRepository;
  private final ModuleWithContentRepository moduleWithContentRepository;
  private final WebinarRepository webinarRepository;
  private final Webinar4CheckRepository webinar4CheckRepository;
  private final Subscription4CheckRepository subscription4CheckRepository;
  private final ModuleC4CheckRepository moduleC4CheckRepository;
  private final ModuleW4CheckRepository moduleW4CheckRepository;
  private final ModuleE4CheckRepository moduleE4CheckRepository;
  private final Course4CheckRepository course4CheckRepository;
  private final LiveEvent4CheckRepository liveEvent4CheckRepository;
  private final CourseRepository courseRepository;
  private final LiveEventRepository liveEventRepository;
  private final PermissionRepository permissionRepository;
  private final SubscriptionPeriodsService subscriptionPeriodsService;
  private final TransactionService transactionService;
  private final HttpResponseUtil httpResponseUtil;
  private final UserService userService;
	private final InvitationService invitationService;

  public List<Webinar4Check> getWebinars(User user, String filterByName) {
    return webinar4CheckRepository.findAllByNameContains(filterByName).stream().filter(w -> hasAccessTo(user, w)).collect(Collectors.toList());
  }

  public List<Course4Check> getCourses(User user, String filterByName) {
    return course4CheckRepository.findAllByNameContains(filterByName).stream().filter(w -> hasAccessTo(user, w)).collect(Collectors.toList());
  }

  public List<LiveEvent4Check> getLiveEvents(User user, String filterByName) {
    return liveEvent4CheckRepository.findAllByNameContains(filterByName).stream().filter(w -> hasAccessTo(user, w)).collect(Collectors.toList());
  }

  private boolean lectorHasAccess(User user, Benefit4Check benefit) {
    if (!user.isLector()) {
      return false;
    }
    // TODO check if benefit is course
    var courses = course4CheckRepository.findAllByLector(user.getId());
    if (courses.stream().anyMatch(course -> course.isOrHas(benefit, this))) {
      return true;
    }
    // TODO check if benefit is webinar or course
    var webinars = webinar4CheckRepository.findAllWebinarsOf(user.getId());
    if (webinars.stream().anyMatch(webinar -> webinar.isOrHas(benefit, this))) {
      return true;
    }
    // TODO check if benefit is live event
    var liveEvents = liveEvent4CheckRepository.findAllLiveEventsOf(user.getId()); // TODO coordinator or lector!!!
    if (liveEvents.stream().anyMatch(liveEvent -> liveEvent.isOrHas(benefit, this))) {
      return true;
    }
    return false;
  }

  public boolean hasAccessTo(User user, Benefit4Check benefit) {
    if (user == null) {
      return false;
    }
    // get all the permissions for this user. list of product type and id. any of
    // them are okay.
    return user.isAdmin() || lectorHasAccess(user, benefit) || permissionRepository.findAllByUserId(user.getId()). // TODO
                                                                                                                   // and
                                                                                                                   // some
                                                                                                                   // other
                                                                                                                   // checks
                                                                                                                   // and
                                                                                                                   // modifications
                                                                                                                   // as
                                                                                                                   // side
                                                                                                                   // effects
        stream().filter(permission -> permission.getActive() != null && permission.getActive())
        .filter(permission -> permission.getExpires() == null || ZonedDateTime.now().isBefore(permission.getExpires()))
        .filter(permission -> permission.getEndsAt() == null || ZonedDateTime.now().isBefore(permission.getEndsAt()))
        .map(permission -> this.getProduct(permission.getProductType(), permission.getProductId())). // TODO enclose
                                                                                                     // getProduct in
                                                                                                     // try-catch here
        anyMatch(product -> product.isOrHas(benefit, this));

  }

  // public Benefit getProduct(ProductType productType, Long productId) {
  // return getProduct(productType, productId, true); // or maybe we never use for
  // anything other than check.
  // }

  public Benefit getProduct2(ProductType productType, Long productId) {
    switch (productType) {
      case SUBSCRIPTION:
        return findSubscriptionById(productId);
      case MODULE:
        return moduleWithContentRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
      case WEBINAR:
        return webinarRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist"));
      case COURSE:
        return courseRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("course-w-id-not-exist"));
      case LIVE_EVENT:
        return liveEventRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist"));
    }
    throw new NotFoundException("product-w-id-not-exist");

  }

  public Benefit4Check getProduct(ProductType productType, Long productId) {
    boolean check = true;
    if (check) {
      switch (productType) {
        case SUBSCRIPTION:
          return subscription4CheckRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("subscription-w-id-not-exist"));
        case MODULE:
          return moduleC4CheckRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
        case WEBINAR:
          return webinar4CheckRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("webinar-w-id-not-exist"));
        case COURSE:
          return course4CheckRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("course-w-id-not-exist"));
        case LIVE_EVENT:
          return liveEvent4CheckRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("live-event-w-id-not-exist"));
      }
    }
    throw new NotFoundException("product-w-id-not-exist");
  }

  public ModuleW4Check getModuleW4Check(Long id) {
    return moduleW4CheckRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
  }
  public ModuleE4Check getModuleE4Check(Long id) {
    return moduleE4CheckRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("module-w-id-not-exist"));
  }

  @Transactional
  public void checkPermissions(User user, Long productId, ProductType productType) {
    // TODO we should create a cache table, or rather, we should maintain a view table of all user×products permitted.
    // (because of getWebinars, getCourses, getLiveEvents)
    var product = getProduct(productType, productId);
    if (!hasAccessTo(user, product)) {
      throw new CustomException("access-denied");
    } else {
      // TODO analytics.
    }
  }

  public void saveProduct(ProductType productType, Benefit benefit) {
    switch (productType) {
      case SUBSCRIPTION:
        subscriptionRepository.save((Subscription) benefit);
        return;
      case MODULE:
        moduleWithContentRepository.save((ModuleWithContent) benefit);
        return;
      case WEBINAR:
        webinarRepository.save((Webinar) benefit);
        return;
      case COURSE:
        courseRepository.save((Course) benefit);
        return;
      case LIVE_EVENT:
        liveEventRepository.save((LiveEvent) benefit);
        return;
    }
    throw new NotFoundException("unhandled-product-type");
  }

  private void checkIdsAndAdd(List<Long> ids, Long id) {
    boolean first = true;
    System.out.print(id + " beg ");
    for (Iterator<Long> iterator = ids.iterator();iterator.hasNext();) {
      Long id2 = iterator.next();
      System.out.print(id2 + " ");
      if (id2.equals(id)) {
        if (first) {
          throw new CustomException("subscription-self-reference");
        }
        return;
      }
      first = false;
    }
    ids.add(id);
    System.out.println(" end");
  }
  private void checkForSelfReference(Subscription subscription, List<BenefitDto> benefits) {
    List<Long> ids = new ArrayList<>();
    ids.add(subscription.getId());
    for (Iterator<BenefitDto> iterator = benefits.iterator();iterator.hasNext();) {
      BenefitDto benefitDto = iterator.next();
      if (benefitDto.getProductType().isSubscription()) {
        checkIdsAndAdd(ids, benefitDto.getProductId());
      }
    }
    boolean first = true;
    for (int i = 1; i < ids.size(); i++) {
      Long id = ids.get(i);
      System.out.println("id: " + id);
      var subscription2 = findSubscriptionById(id);
      for (Iterator<SubscriptionProduct> iterator = subscription2.getSubscriptionProducts().iterator();iterator.hasNext();) {
        SubscriptionProduct sp = iterator.next();
        if (sp.getProductType().isSubscription()) {
          System.out.println("isSub: " + sp.getProductId());
          checkIdsAndAdd(ids, sp.getProductId());
        }
      }
    }
  }

  @Transactional
  public ResponseEntity<?> createSubscription(SubscriptionDto subscriptionDto) {
    if (subscriptionDto.getPeriods().size() == 0) {
      throw new CustomException("no-periods-set");
    }
    var subscription = Subscription.builder()
        .name(subscriptionDto.getName())
        .type(subscriptionDto.getType())
        .description(subscriptionDto.getDescription())
        .published(subscriptionDto.getPublished())
        .build();
    subscription = subscriptionRepository.save(subscription);
    checkForSelfReference(subscription, subscriptionDto.getBenefits());
    var subscriptionProducts = createSubscriptionProducts(subscription, subscriptionDto);
    subscription.setSubscriptionProducts(subscriptionProducts);
    subscriptionPeriodsService.save(subscription, ProductType.SUBSCRIPTION, subscriptionDto.getPeriods());
    return httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "Subscription created successfully");
  }

  private List<SubscriptionProduct> createSubscriptionProducts(Subscription subscription,
      SubscriptionDto subscriptionDto) {
    return subscriptionDto.getBenefits().stream().map(benefit -> {
      var b = SubscriptionProduct.builder()
          .subscription(subscription)
          .productType(benefit.getProductType())
          .productId(benefit.getProductId())
          .build();
      subscriptionProductRepository.save(b);
      return b;
    }).collect(Collectors.toList());
  }

  @Transactional
  public ResponseEntity<?> updateSubscription(SubscriptionDto subscriptionDto) {
    if (subscriptionDto.getPeriods().size() == 0) {
      throw new CustomException("no-periods-set");
    }
    var subscription = findSubscriptionById(subscriptionDto.getId());
    checkForSelfReference(subscription, subscriptionDto.getBenefits());
    subscription.setName(subscriptionDto.getName());
    subscription.setType(subscriptionDto.getType());
    subscription.setDescription(subscriptionDto.getDescription());
    subscription.setPublished(subscriptionDto.getPublished());
    subscriptionProductRepository.clearAll(subscription.getId());
    var subscriptionProducts = subscriptionDto.getBenefits().stream().map(benefit -> { // TODO why isn't createSubscriptionProducts called instead?
      var b = SubscriptionProduct.builder()
          .subscription(subscription)
          .productType(benefit.getProductType())
          .productId(benefit.getProductId())
          .build();
      b = subscriptionProductRepository.save(b);
      return b;
    }).collect(Collectors.toList());
    subscription.setSubscriptionProducts(subscriptionProducts);
    subscriptionPeriodsService.save(subscription, ProductType.SUBSCRIPTION, subscriptionDto.getPeriods()); // TODO
    return httpResponseUtil.createHttpResponse(HttpStatus.NO_CONTENT, "Subscription updated successfully");
  }

  private List<BenefitDto> convertToBenefits(List<SubscriptionProduct> subscriptionProducts) {
    return subscriptionProducts.stream().map(subscriptionProduct -> BenefitDto.builder()
        // .id(subscriptionProduct.getId())
        .name(this.getProduct2(subscriptionProduct.getProductType(), subscriptionProduct.getProductId()).getName())
        .productType(subscriptionProduct.getProductType())
        .productId(subscriptionProduct.getProductId())
        .build()).collect(Collectors.toList());
  }

  public SubscriptionDto getSubscription(Long id, User user) {
    var subscription = findSubscriptionById(id);
    if ((user == null || !user.isAdmin()) && !subscription.getPublished()) {
      throw new NotFoundException("subscription-w-id-not-exist");
    }
    var subscription4Check = subscription4CheckRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("subscription-w-id-not-exist"));
    return SubscriptionDto.builder()
        .id(subscription.getId())
        .name(subscription.getName())
        .type(subscription.getType())
        .published(subscription.getPublished())
        .periods(subscription.getSubscriptionPeriods().stream().map(sp -> PeriodDto
            .builder()
            .id(sp.getPeriod().getId())
            .name(sp.getPeriod().getName())
            .description(sp.getPeriod().getDescription())
            .interval(sp.getPeriod().getInterval())
            .intervalCount(sp.getPeriod().getIntervalCount())
            .price(sp.getPrice())
            .build()).collect(Collectors.toList()))
        .description(subscription.getDescription())
        .benefits(this.convertToBenefits(subscription.getSubscriptionProducts()))
        .hasAccess(user == null ? null : user.isAdmin() ? true : hasAccessTo(user, subscription4Check))
        .build();
  }

  public List<Benefit4Check> getPacked(User user) {
    List<Benefit4Check> products = new ArrayList<>();
    if (user != null) {
      List<Permission> permissions = getByOwnerAndTransaction(user.getId(), null);
      List<Permission> permissionsPacked = new ArrayList<>();
      for (Iterator<Permission> it = permissions.iterator(); it.hasNext();) {
        Permission permission = it.next();
        boolean found = false;
        for (Iterator<Permission> it2 = permissionsPacked.iterator(); it2.hasNext();) {
          Permission p = it2.next();
          if (
            p.getProductType().equals(permission.getProductType()) && 
            p.getProductId().equals(permission.getProductId())) {
            found = true;
            break;
          }
        }
        if (!found) {
          permissionsPacked.add(permission);
        }
      }  
      products = 
        permissionsPacked.stream()
        .map(p -> getProduct(p.getProductType(), p.getProductId())).collect(Collectors.toList());
    }
    return products;
  }

  public List<SubscriptionDto> getRetails(User user) {
    List<Benefit4Check> products = getPacked(user);
    return subscriptionRepository.findAllByType(SubscriptionType.RETAIL).stream()
        .filter(subscription -> subscription.getPublished())
        .filter(subscription -> subscription4CheckRepository.findById(subscription.getId())
            .orElseThrow(() -> new NotFoundException("subscription-w-id-not-exist")).isOrHasAll(products, this))
        .map(subscription -> SubscriptionDto.builder()
            .id(subscription.getId())
            .name(subscription.getName())
            .type(subscription.getType())
            .published(subscription.getPublished())
            .periods(subscription.getSubscriptionPeriods().stream().map(sp -> PeriodDto
                .builder()
                .id(sp.getPeriod().getId())
                .name(sp.getPeriod().getName())
                .description(sp.getPeriod().getDescription())
                .interval(sp.getPeriod().getInterval())
                .intervalCount(sp.getPeriod().getIntervalCount())
                .price(sp.getPrice())
                .build()).collect(Collectors.toList()))
            .description(subscription.getDescription())
            .benefits(this.convertToBenefits(subscription.getSubscriptionProducts()))
            .build())
        .collect(Collectors.toList());
  }

  public List<SubscriptionDto> getAllSubscriptions(SubscriptionType subscriptionType) {
    return subscriptionRepository.findAll().stream()
        .filter(subscription -> subscriptionType == null || subscription.getType() == subscriptionType)
        .map(subscription -> SubscriptionDto.builder()
            .id(subscription.getId())
            .name(subscription.getName())
            .type(subscription.getType())
            .published(subscription.getPublished())
            .periods(subscription.getSubscriptionPeriods().stream().map(sp -> PeriodDto
                .builder()
                .id(sp.getPeriod().getId())
                .name(sp.getPeriod().getName())
                .description(sp.getPeriod().getDescription())
                .interval(sp.getPeriod().getInterval())
                .intervalCount(sp.getPeriod().getIntervalCount())
                .price(sp.getPrice())
                .build()).collect(Collectors.toList()))
            .description(subscription.getDescription())
            .benefits(this.convertToBenefits(subscription.getSubscriptionProducts()))
            .build())
        .collect(Collectors.toList());
  }

  public Subscription findSubscriptionById(Long id) {
    return subscriptionRepository.findById(id).orElseThrow(() -> new NotFoundException("subscription-w-id-not-exist"));
  }

  @Transactional
	public void togglePublish(Long subscriptionId) {
		var subscription  = findSubscriptionById(subscriptionId);
		var published  = subscription.getPublished();
		subscription.setPublished(!published);
	}
	@Transactional
	public void publish(Long subscriptionId) {
		var subscription  = findSubscriptionById(subscriptionId);
		subscription.setPublished(true);
	}
	@Transactional
	public void unpublish(Long subscriptionId) {
		var subscription  = findSubscriptionById(subscriptionId);
		subscription.setPublished(false);
	}

  public Permission createPermission(PermissionDto permissionDto) {
    var permission = Permission.builder()
        .userId(permissionDto.getUserId())
        .ownerId(permissionDto.getOwnerId())
        .productType(permissionDto.getProductType())
        .productId(permissionDto.getProductId())
        .periodId(permissionDto.getPeriodId())
        .active(permissionDto.getActive())
        .startTime(permissionDto.getStartTime())
        .expires(permissionDto.getExpires())
        .addedDate(permissionDto.getAddedDate() == null ? ZonedDateTime.now() : permissionDto.getAddedDate())
        .endsAt(permissionDto.getEndsAt())
        // .paymentType(permissionDto.getPaymentType())
        .stripe(permissionDto.getStripe())
        .cart(permissionDto.getCart())
        .value(permissionDto.getValue()) // TODO the value is checked at buying
    .build();
    return permissionRepository.save(permission);
    // TODO create a Subscription item and add to users stripe subscription if
    // exists, and if it is not manual
  }

  public Permission findById(Long permissionId) {
    return permissionRepository.findById(permissionId)
        .orElseThrow(() -> new NotFoundException("permission-not-found"));
  }
  public List<Permission> findLastByStripe(String stripe, Long quantity) {
    Sort sort = JpaSort.unsafe(Sort.Direction.DESC, "startTime");
    Pageable pageable = PageRequest.of(0, quantity.intValue(), sort);
    return permissionRepository.findByStripe(stripe, pageable);
  }

  @Transactional
  public ResponseEntity<?> updatePermission(PermissionDto permissionDto) {
    var permission = findById(permissionDto.getId());
    permission.setUserId(permissionDto.getUserId());
    permission.setOwnerId(permissionDto.getOwnerId());
    permission.setProductType(permissionDto.getProductType());
    permission.setProductId(permissionDto.getProductId());
    permission.setPeriodId(permissionDto.getPeriodId());
    permission.setActive(permissionDto.getActive());
    permission.setStartTime(permissionDto.getStartTime());
    permission.setExpires(permissionDto.getExpires());
    if (permissionDto.getAddedDate() != null) {
      permission.setAddedDate(permissionDto.getAddedDate());
    }
    permission.setEndsAt(permissionDto.getEndsAt());
    // permission.setPaymentType(permissionDto.getPaymentType());
    permission.setStripe(permissionDto.getStripe());
    // TODO update in stripe subscription if exists and if it was not manual
    return httpResponseUtil.createHttpResponse(HttpStatus.NO_CONTENT, "Permission updated successfully");
  }

  public PermissionDto getPermission(Long id) {
    var permission = findById(id);
    return getPermission(permission);
  }
  public PermissionDto getPermission(Permission permission) {
    return PermissionDto.builder()
        .id(permission.getId())
        .userId(permission.getUserId())
        .ownerId(permission.getOwnerId())
        .userName(userService.findById(permission.getUserId()).getFullName())
        .ownerName(permission.getOwnerId() == null ? null : userService.findById(permission.getOwnerId()).getFullName())
        .productType(permission.getProductType())
        .productId(permission.getProductId())
        .productName(getProduct2(permission.getProductType(), permission.getProductId()).getName())
        .periodId(permission.getPeriodId())
        .periodName(
            permission.getPeriodId() != null ? subscriptionPeriodsService.getPeriod(permission.getPeriodId()).getName()
                : null)
        .active(permission.getActive())
        .startTime(permission.getStartTime())
        .expires(permission.getExpires())
        .addedDate(permission.getAddedDate())
        .endsAt(permission.getEndsAt())
        .value(permission.getValue())
        .stripe(permission.getStripe())
    .build();
  }

  public Page<PermissionDto> getAllPermissions(
      List<Long> userIds,
      Pageable pageable,
      String filterByName,
      // PaymentType paymentType,
      ProductType productType,
      List<Long> productIds,
      Long periodId) {
    var permissions = permissionRepository.findAllByData(
        userIds,
        userIds == null ? 0 : userIds.size(),
        // filterByName,
        // paymentType,
        productType,
        productIds,
        productIds == null ? 0 : productIds.size(),
        periodId,
        pageable);
    // var list = permissions.getContent().stream().filter(permission ->
    // permission.getActive() != null).map(permission -> PermissionDto.builder()
    // .id(permission.getId())
    // .userId(permission.getUserId())
    // .productType(permission.getProductType())
    // .productId(permission.getProductId())
    // .productName(user != null ? getProduct(permission.getProductType(),
    // permission.getProductId()).getName() : null)
    // .periodId(permission.getPeriodId())
    // .periodName(user != null && permission.getPeriodId() != null ?
    // subscriptionPeriodsService.getPeriod(permission.getPeriodId()).getName() :
    // null)
    // .active(permission.getActive())
    // .startTime(permission.getStartTime())
    // .expires(permission.getExpires())
    // .addedDate(permission.getAddedDate() == null ? ZonedDateTime.now() :
    // permission.getAddedDate())
    // .endsAt(permission.getEndsAt())
    // .paymentType(user != null ? null : permission.getPaymentType())
    // .price(user != null ?
    // (permission.getPaymentType() == PaymentType.STRIPE ?
    // (permission.getPeriodId() != null ?
    // // subscriptionPeriodsService.get(permission.getProductType(),
    // permission.getProductId(), permission.getPeriodId()).getPrice() // TODO a
    // link to transactions is better
    // transactionService.getTransactionByPermissionId(permission.getId()).getValue()
    // : // getProduct(permission.getProductType(),
    // permission.getProductId()).getAmount(null)
    // transactionService.getTransactionByPermissionId(permission.getId()).getValue()
    // )
    // : 0)
    // : null
    // )
    // .stripe(user != null ? null : permission.getStripe())
    // .build()).collect(Collectors.toList());
    var list = permissions.getContent().stream().map(permission -> {
      Transaction transaction = null;
      try {
        // transaction = transactionService.getTransactionByPermissionId(permission.getId());
      } catch (NotFoundException e) {
      }
      return PermissionDto.builder()
        // TODO filterByName
        .id(permission.getId())
        .userId(permission.getUserId())
        .userName(userService.findById(permission.getUserId()).getFullName())
        .productType(permission.getProductType())
        .productId(permission.getProductId())
        .productName(getProduct2(permission.getProductType(), permission.getProductId()).getName())
        .periodId(permission.getPeriodId())
        .periodName(
            permission.getPeriodId() != null ? subscriptionPeriodsService.getPeriod(permission.getPeriodId()).getName()
                : null)
        .active(permission.getActive())
        .startTime(permission.getStartTime())
        .expires(permission.getExpires())
        .addedDate(permission.getAddedDate())
        .endsAt(permission.getEndsAt())
        .value(permission.getValue())
        .ownerId(permission.getOwnerId()) // it is ok to be seen by the beneficiary as well
        .ownerName(permission.getOwnerId() == null ? null : userService.findById(permission.getOwnerId()).getFullName())
        // .paymentType(isAdmin ? permission.getPaymentType() : null)
        // .price(
        //     (permission.getPaymentType() == PaymentType.NOT_NEEDED ? 0 : getValueTemp(transaction)))
        .stripe(permission.getStripe())
        // .invoice(transaction != null ? transaction.getInvoiceFile() : transaction.getTransferFile())
        .build();}).collect(Collectors.toList());

    return new PageImpl<>(list, pageable, permissions.getTotalElements());
  }

  private Float getValueTemp(Transaction transaction) {
    try {
      return transaction == null ? 0f : transaction.getValue();
    } catch (Exception e) {
      return 0f;
      // return permission.getPeriodId() != null
      // ? subscriptionPeriodsService
      // .get(permission.getProductType(), permission.getProductId(),
      // permission.getPeriodId()).getPrice() // TODO
      // // a
      // // link
      // // to
      // // transactions
      // // is
      // // better
      // : getProduct(permission.getProductType(),
      // permission.getProductId()).getAmount(null);

    }
  }

  public ChangeBeneficiaryDto deletePermission(Long permissionId, User user) {
    var permission = permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("permission-not-found"));
    if (!user.isAdmin()) {
      if (!permission.getOwnerId().equals(user.getId())) {
        throw new NotFoundException("permission-not-found");
      }
      if (!permission.getCart()) {
        throw new CustomException("not-cart-item");
      }
    }
    permissionRepository.delete(permission);

    return ChangeBeneficiaryDto.builder().response("OK").build();
  }

  public List<PeriodDto> getPeriods() {
    return subscriptionPeriodsService.getPeriods();
  }

  public Permission save(Permission permission) {
    return permissionRepository.save(permission);
  }

  public void emptyCart(Long userId) {
    permissionRepository.deleteAllByOwnerIdAndCart(userId, true);
  }

  public List<PermissionDto> snapshotCart(User user, Transaction transaction, ZonedDateTime now) {
    List<Permission> cart = permissionRepository.findAllByOwnerIdAndCart(user.getId(), true);
    cart.stream().forEach(p -> {
      Permission permission = Permission.builder()
        .transaction(transaction)
        .userId(p.getUserId())
        .ownerId(p.getOwnerId())
        .productType(p.getProductType())
        .productId(p.getProductId())
        .periodId(p.getPeriodId())
        .active(false)
        .addedDate(now)
        .stripe(null)
        .cart(false)
        .value(p.getValue())
      .build();
      permissionRepository.save(permission);
      });
    return getShoppingCart(user, transaction);
  }

  public List<Permission> getByOwnerAndTransaction(Long ownerId, Transaction transaction) {
    if (transaction == null) {
      return permissionRepository.findAllByOwnerIdAndCart(ownerId, true);
    }
    return permissionRepository.findAllByOwnerIdAndTransaction(ownerId, transaction); // is ownerId needed here? TODO
  }

  public List<PermissionDto> getShoppingCart(User user, Transaction transaction) {
    Long ownerId = user.getId();
    var permissions = getByOwnerAndTransaction(ownerId, transaction);
    List<Invitation> invitations = invitationService.findAllByInvitedBy(user);
    return permissions.stream().map(permission -> {
      Invitation invitation = null;
      boolean isAccepted = true;
      if (user.getId() != permission.getUserId()) {
        invitation = invitations
          .stream()
          .filter(inv -> inv.getUser().getId() == permission.getUserId())
          .findAny()
          .orElseThrow(() -> new NotFoundException("cart-item-not-found"));
        isAccepted = invitation.getStatus().equals("ACCEPTED");
      }
      return PermissionDto.builder()
        // TODO filterByName
        .id(permission.getId())
//        .userId(permission.getUserId()) // only if accepted
        .userName(
          isAccepted ?
          userService.findById(permission.getUserId()).getFullName() : invitation.getEmail())
        // TODO only if accepted
        // .userEmail(userService. / invitations....) // TODO if not accepted, get from userId and invitations
        .productType(permission.getProductType())
        .productId(permission.getProductId())
        .productName(getProduct2(permission.getProductType(), permission.getProductId()).getName())
        .periodId(permission.getPeriodId())
        .periodName(
          permission.getPeriodId() != null ?
          subscriptionPeriodsService.getPeriod(permission.getPeriodId()).getName()
          : null)
        // .active(permission.getActive())
        // .startTime(permission.getStartTime())
        // .expires(permission.getExpires())
        // .addedDate(permission.getAddedDate())
        // .endsAt(permission.getEndsAt())
        .value(permission.getValue())
        // .paymentType(isAdmin ? permission.getPaymentType() : null)
        // .price(
        //     (permission.getPaymentType() == PaymentType.NOT_NEEDED ? 0 : getValueTemp(transaction)))
        // .stripe(isAdmin ? permission.getStripe() : null)
        // .invoice(transaction != null ? transaction.getInvoiceFile() : transaction.getTransferFile())
        .build();}).collect(Collectors.toList());
  }

  @Transactional
  public ChangeBeneficiaryDto changeUser(User user, Long id, Long invitationId) {
    Long ownerId = user.getId();
    Permission permission = permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("cart-item-not-found"));
    if (!permission.getCart() || !permission.getOwnerId().equals(ownerId)) {
      throw new NotFoundException("cart-item-not-found");
    }
    if (invitationId == -1) {
      permission.setUserId(user.getId());
      return ChangeBeneficiaryDto.builder().response(
        userService.findById(permission.getUserId()).getFullName()).build();
    } else {
      Invitation invitation = invitationService.findById2(invitationId);
      if (invitation.getInvitedBy().getId() != user.getId()) {
        throw new NotFoundException("invitation-not-found");
      }
      permission.setUserId(invitation.getUser().getId());
      return ChangeBeneficiaryDto.builder().response(invitation.getStatus().equals("ACCEPTED") ?
        userService.findById(permission.getUserId()).getFullName() : invitation.getEmail()).build();
    }
  }

  @Transactional
  public PermissionDto activatePermission(PermissionDto permissionDto) {
    var permission = permissionRepository.findById(permissionDto.getId()).orElseThrow(() -> new NotFoundException("permission-not-found"));
    permission.setActive(permissionDto.getActive());
    return getPermission(permission);
    // remove from cart TODO
  }

}
