package com.servustech.eduson.utils.mail;

import com.servustech.eduson.features.permissions.permissions.Permission;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.SubscriptionItem;
import com.stripe.param.InvoicePayParams;
import com.stripe.model.Subscription;
import com.stripe.model.Invoice;
import com.stripe.param.InvoicePayParams;
import com.stripe.model.PaymentIntent;
import com.stripe.param.SubscriptionUpdateParams;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@Async
@Service
public class SubscriptionsFirstInvoicePaidOnce {
  public void payOutband(List<Permission> permissions, PaymentIntent paymentIntent) throws StripeException {
    List<String> subscriptions = new ArrayList<>();
    for (Iterator<Permission> it2 = permissions.iterator(); it2.hasNext();) {
      Permission permission = it2.next();
      if (permission.getPeriodId() != null) {
        var subItemId = permission.getStripe();
        if (subItemId == null) {
          System.out.println("error subItemId");
          continue;
        }
        SubscriptionItem subscriptionItem = SubscriptionItem.retrieve(subItemId);
        String subscription = subscriptionItem.getSubscription();
        boolean found = false;
        for (Iterator<String> it = subscriptions.iterator(); it.hasNext();) {
          String sub = it.next();
          if (sub.equals(subscription)) {
            found = true;
            break;
          }
        }
        if (!found) {
          subscriptions.add(subscription);
        }
      }
    }
    for (Iterator<String> it = subscriptions.iterator(); it.hasNext();) {
      String sub = it.next();
      Subscription subscription = Subscription.retrieve(sub);
      Invoice inv = Invoice.retrieve(subscription.getLatestInvoice()); // TODO with expand
      if (!inv.getStatus().equals("paid")) {
        // pay outband
        InvoicePayParams invoicePayParams = InvoicePayParams.builder().setPaidOutOfBand(true).build();
        inv.pay(invoicePayParams);
      }
      SubscriptionUpdateParams params = SubscriptionUpdateParams
        .builder()
        .setDefaultPaymentMethod(paymentIntent.getPaymentMethod())
      .build();
      subscription.update(params);
    }
  }
  
}
