package com.servustech.eduson.features.permissions;

import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.security.payload.UserDetailsResponse;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.features.permissions.TransactionDto;

import com.stripe.Stripe;
import com.stripe.model.StripeObject;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.model.PaymentMethod;
import com.stripe.exception.SignatureVerificationException;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.StripeException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {
  private final JwtService jwtService;
  private final PaymentIntentService paymentIntentService;
  private final StripeKeyService stripeKeyService;

  private Event verify(String sigHeader, String payload, String endpointSecret, List<Boolean> flags) {
    Event event = null;
    if (!flags.get(0) && !flags.get(1)) {
      try {
        event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        flags.set(0, true); // found
      } catch (JsonSyntaxException e) {
        flags.set(1, true); // syntaxException
      } catch (SignatureVerificationException e) {
      }
    }
    return event;
  }

  @PostMapping
  public ResponseEntity<?> webhook(@RequestHeader("Stripe-Signature") String sigHeader, @RequestBody String payload) throws StripeException {
    // TODO log all requests!
    System.out.println("webhook");
    System.out.println(sigHeader);
    System.out.println(payload);
    Stripe.apiKey = stripeKeyService.getApiKey();
    List<String> endpointSecrets = stripeKeyService.getEndpointSecrets();
    // "whsec_oNWMENDzfshY5qr2GTQyBH76zNlPqugq";
    // "whsec_9NNLZaKbn8UUEanYQGCmqoCOULVvcbsd";
    Event event = null;
    List<Boolean> flags = new ArrayList<>();
    flags.add(false); // found
    flags.add(false); // syntaxException;
    List<Event> events = endpointSecrets
      .stream()
      .map(endpointSecret -> verify(sigHeader, payload, endpointSecret, flags))
      .collect(Collectors.toList());
    if (flags.get(1)) { // syntaxException
      // Invalid payload
      return ResponseEntity
          .status(400)
          // .headers(headers)
          .body("Invalid payload");      
    }
    if (!flags.get(0)) { // found
      return ResponseEntity
          .status(403)
          // .headers(headers)
          .body("Signature error");      
    }
    for (int i = events.size() - 1; i >= 0; i--) {
      if (events.get(i) != null) {
        event = events.get(i);
        break;
      }
    }
    System.out.println(event);
    // Deserialize the nested object inside the event
    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject = null;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      // Deserialization failed, probably due to an API version mismatch.
      // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
      // instructions on how to handle this case, or return an error here.
      return ResponseEntity
          .status(400)
          // .headers(headers)
          .body("Deserialization failed");
    }
    System.out.println(event.getType());

    PaymentIntent paymentIntent = null;
    PaymentMethod paymentMethod = null;

    // Handle the event
    switch (event.getType()) {
      case "payment_intent.succeeded":
        paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("PaymentIntent was successful! " + paymentIntent.toString());
        break;
      case "payment_intent.payment_failed":
        paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("PaymentIntent failed! " + paymentIntent.toString());
        break;
      case "payment_intent.canceled":
        paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("PaymentIntent canceled! " + paymentIntent.toString());
        break;
      case "payment_intent.requires_action":
        paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("PaymentIntent requires action! " + paymentIntent.toString());
        break;
      case "payment_intent.requires_payment_method":
        paymentIntent = (PaymentIntent) stripeObject;
        System.out.println("PaymentIntent requires payment method! " + paymentIntent.toString());
        break;
      case "payment_method.attached":
        paymentMethod = (PaymentMethod) stripeObject;
        System.out.println("PaymentMethod was attached to a Customer! " + paymentMethod.toString());
        // TODO
        // update Subscription.default_payment_method
        // and maybe
        // var customerId = paymentMethod.getCustomer();
        // var customer = Customer.retrieve(customerId);
        // CustomerUpdateParams customerUpdateParams = CustomerUpdateParams
        // .builder()
        // .setInvoiceSettings(
        // CustomerUpdateParams.InvoiceSettings.builder()
        // .setDefaultPaymentMethod(paymentMethod.getId())
        // .build()
        // )
        // .build();
        // customer.update(customerUpdateParams);
        break;
      case "payment_method.detached":
        paymentMethod = (PaymentMethod) stripeObject;
        System.out.println("PaymentMethod was detached from a Customer! " + paymentMethod.toString());
        break;
      case "charge.succeeded":
        paymentIntentService.chargeSucceeded(stripeObject);
        break;
      case "invoice.paid":
        paymentIntentService.invoicePaid(stripeObject);
        System.out.println("invoice.paid event" + stripeObject.toString());
        break;
      case "invoice.payment_action_required":
        System.out.println("invoice.payment_action_required" + stripeObject.toString());
        break;
      case "customer.subscription.updated":
        System.out.println("customer.subscription.updated" + stripeObject.toString());
        break;

      // ... handle other event types
      default:
        System.out.println("Unhandled event type: " + event.getType() + " " + stripeObject.toString());
    }
    return ResponseEntity.ok("");
  }

  // @PostMapping("/create-payment-intent")
  // public ResponseEntity<?> createPaymentIntent(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
  //     @RequestBody TransactionDto transactionDto) throws StripeException {
  //   var user = jwtService.getUserFromAuth(authToken);
  //   return paymentIntentService.create(user, transactionDto, true);
  // }
  @PostMapping("/create-payment-intent")
  public ResponseEntity<?> createPaymentIntent(@RequestHeader(AuthConstants.AUTH_KEY) String authToken
  ) throws StripeException {
    var user = jwtService.getUserFromAuth(authToken);
    return paymentIntentService.create(user, true);
  }

  @PostMapping("/send-mail")
  public ResponseEntity<?> sendMail(@RequestHeader(AuthConstants.AUTH_KEY) String authToken
  ) throws StripeException {
    var user = jwtService.getUserFromAuth(authToken);
    return paymentIntentService.create(user, false);
  }
}