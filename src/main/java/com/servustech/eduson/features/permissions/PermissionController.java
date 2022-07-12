package com.servustech.eduson.features.permissions;

import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.servustech.eduson.features.permissions.PermissionsService;
import com.servustech.eduson.features.permissions.permissions.PermissionDto;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionDto;
import com.servustech.eduson.security.constants.AuthConstants;
import com.servustech.eduson.security.handler.RequestHandler;
import com.servustech.eduson.security.jwt.JwtTokenProvider;
import com.servustech.eduson.security.userdetails.CustomUserDetailsService;
import com.servustech.eduson.utils.httpresponse.HttpResponseUtil;
import com.servustech.eduson.security.payload.UserDetailsResponse;
import com.servustech.eduson.features.permissions.subscriptions.PeriodDto;
import com.servustech.eduson.security.auth.JwtService;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionType;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.permissions.transactions.TransactionService;

import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
  private final JwtService jwtService;
  private final PermissionsService permissionsService;
  private final TransactionService transactionService;
  private final PaymentIntentService paymentIntentService;
  private final HttpResponseUtil httpResponseUtil;

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PostMapping("/subs")
  public ResponseEntity<?> createSubscription(@RequestBody SubscriptionDto subscriptionDto) {
    return ResponseEntity.ok(permissionsService.createSubscription(subscriptionDto));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PutMapping("/subs")
  public ResponseEntity<?> updateSubscription(@RequestBody SubscriptionDto subscriptionDto) {
    return ResponseEntity.ok(permissionsService.updateSubscription(subscriptionDto));
  }

  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  // @DeleteMapping
  // public ResponseEntity<?> deleteSubscription(@PathVariable Long
  // subscriptionId) {
  // return
  // ResponseEntity.ok(permissionsService.deleteSubscription(subscriptionId));
  // } // TODO
  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/subs/{subscriptionId}")
  public ResponseEntity<?> getSubscription(
      @RequestHeader(required = false, value = AuthConstants.AUTH_KEY) String authToken,
      @PathVariable Long subscriptionId) {
    var user = jwtService.getUserFromAuthOk(authToken);
    return ResponseEntity.ok(permissionsService.getSubscription(subscriptionId, user));
  }

  @GetMapping("/subs/retail")
  public ResponseEntity<?> getRetails() {
    return ResponseEntity.ok(permissionsService.getRetails(null));
  }
  @GetMapping("/subs/retails-for")
  public ResponseEntity<?> getRetails(@RequestHeader(value = AuthConstants.AUTH_KEY) String authToken) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(permissionsService.getRetails(user));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/subs/all")
  public ResponseEntity<?> getAllSubscriptions(@RequestParam(required = false) SubscriptionType subscriptionType) {
    return ResponseEntity.ok(permissionsService.getAllSubscriptions(subscriptionType));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> createPermission(
    @RequestHeader(AuthConstants.AUTH_KEY) String authToken,
    @RequestBody PermissionDto permissionDto)
  {
    var user = jwtService.getUserFromAuth(authToken);
    if (!user.isAdmin()) {
      permissionDto.setOwnerId(user.getId());
      permissionDto.setCart(true);
      permissionDto.setActive(false);
      permissionDto.setAddedDate(ZonedDateTime.now());
      if (permissionDto.getQuantity() > 50L) {
        permissionDto.setQuantity(50L);
      }
    }
    if (permissionDto.getUserId() == null) {
      permissionDto.setUserId(user.getId());
    }
    if (permissionDto.getOwnerId() == null) {
      permissionDto.setOwnerId(user.getId());
    }
    for (Long i = 0L; i < permissionDto.getQuantity(); i++) {
      permissionsService.createPermission(permissionDto);
    }
    return ResponseEntity.ok(
        httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "Permission created successfully"));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PutMapping("/transactions")
  public ResponseEntity<?> updateTransaction(@RequestBody TransactionDto transactionDto) {
    return ResponseEntity.ok(transactionService.updateTransaction(transactionDto));
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER', 'ROLE_LECTOR')")
  @PutMapping("/transactions/recreate-invoice/{id}")
  public ResponseEntity<?> recreateInvoice(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, @PathVariable Long id) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(paymentIntentService.recreateInvoice(id, user));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> createTransaction(
    @RequestBody TransactionDto transactionDto)
  {
    transactionService.createTransaction(transactionDto);
    return ResponseEntity.ok(
        httpResponseUtil.createHttpResponse(HttpStatus.CREATED, "Transaction created successfully"));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PutMapping
  public ResponseEntity<?> updatePermission(@RequestBody PermissionDto permissionDto) {
    return ResponseEntity.ok(permissionsService.updatePermission(permissionDto));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PutMapping("/after-transfer")
  public ResponseEntity<?> afterTransfer(@RequestBody TransactionDto transactionDto) {
    return ResponseEntity.ok(paymentIntentService.afterTransfer(transactionDto));
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @PutMapping("/activate")
  public ResponseEntity<?> activatePermission(@RequestBody PermissionDto permissionDto) {
    return ResponseEntity.ok(permissionsService.activatePermission(permissionDto));
  }

  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @DeleteMapping("/{permissionId}")
  public ResponseEntity<?> deletePermission(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
    @PathVariable Long permissionId) {
      var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(permissionsService.deletePermission(permissionId, user));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/{permissionId}")
  public ResponseEntity<?> getPermission(@PathVariable Long permissionId) {
    return ResponseEntity.ok(permissionsService.getPermission(permissionId));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/transactions/{transactionId}")
  public ResponseEntity<?> getTransaction(@PathVariable Long transactionId) {
    return ResponseEntity.ok(transactionService.getTransaction(transactionId));
  }

  @GetMapping("/user")
  public ResponseEntity<?> getUserPermissions(@RequestHeader(AuthConstants.AUTH_KEY) String authToken,
    Pageable pageable) {
    var user = jwtService.getUserFromAuth(authToken);
    List<Long> userIds = Arrays.asList(new Long[] { user.getId() });
    return ResponseEntity
      .ok(permissionsService.getAllPermissions(userIds, pageable, null, null, null, null));
  }

  @GetMapping("/shopping-cart")
  public ResponseEntity<?> getShoppingCart(@RequestHeader(AuthConstants.AUTH_KEY) String authToken, Pageable pageable) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(permissionsService.getShoppingCart(user, null));
  }
  @PutMapping("/shopping-cart/change-user/{id}/{invitationId}")
  public ResponseEntity<?> shoppingCartChangeUser(
    @RequestHeader(AuthConstants.AUTH_KEY) String authToken, 
    @PathVariable Long id,
    @PathVariable Long invitationId,
    Pageable pageable) {
    var user = jwtService.getUserFromAuth(authToken);
    return ResponseEntity.ok(permissionsService.changeUser(user, id, invitationId));
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  @GetMapping("/page")
  public ResponseEntity<?> getAllPermissions(
      @RequestParam(required = false) String filterByName,
      @RequestParam(required = false) ProductType productType,
      @RequestParam(required = false) Long periodId,
      @RequestParam(required = false) List<Long> userId,
      @RequestParam(required = false) List<Long> productId,
      // TODO userId - but in this case, it is queried by admin.
      Pageable pageable) {
    return ResponseEntity.ok(permissionsService.getAllPermissions(userId, pageable, filterByName, // paymentType,
        productType, productId, periodId));
  }

  @GetMapping("/periods")
  public ResponseEntity<?> getPeriods() {
    return ResponseEntity.ok(permissionsService.getPeriods());
  }

  // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
  // @PostMapping("/periods")
  // public ResponseEntity<?> createPeriod(@RequestBody PeriodDto periodDto) {
  // return ResponseEntity.ok(permissionsService.createPeriod(periodDto));
  // }

  @GetMapping("/transactions/page/{c}")
  public ResponseEntity<?> getAllTransactions(
    @RequestHeader(AuthConstants.AUTH_KEY) String authToken,
    @PathVariable String c,
    @RequestParam(required = false) String filterByName,
    @RequestParam(required = false) PaymentType paymentType,
    @RequestParam(required = false) List<Long> userIds,
    Pageable pageable
  ) {
    var user = jwtService.getUserFromAuth(authToken);
    if (!user.isAdmin()) {
      userIds = Arrays.asList(new Long[] { user.getId() });
    }
    var notPaidYet = false;
    if (c != null) {
      try {
        var transaction = transactionService.getTransactionByPaymentIntent(c);
        if (transaction.getPaidAt() == null && transaction.getUserId() == user.getId()) {
          notPaidYet = true;
        }
      } catch (java.lang.Exception e) {
      }
    }
    return ResponseEntity
      .ok(UserPermissionsReply.builder()
        .page(transactionService.getAllTransactions(userIds, pageable, filterByName, paymentType))
        .notPaidYet(notPaidYet).build()
      );
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/subs/{subscriptionId}/togglepublish")
	public void togglePublish(@PathVariable Long subscriptionId) {
		permissionsService.togglePublish(subscriptionId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/subs/{subscriptionId}/publish")
	public void publish(@PathVariable Long subscriptionId) {
		permissionsService.publish(subscriptionId);
	}
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping("/subs/{subscriptionId}/unpublish")
	public void unpublish(@PathVariable Long subscriptionId) {
		permissionsService.unpublish(subscriptionId);
	}
}