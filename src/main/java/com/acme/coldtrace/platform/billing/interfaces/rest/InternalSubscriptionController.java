package com.acme.coldtrace.platform.billing.interfaces.rest;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Authenticated service boundary for at-least-once subscription initialization. */
@RestController
@RequestMapping("/internal/organizations/{organizationId}")
public class InternalSubscriptionController {
  private final SubscriptionBillingContextFacade subscriptions;
  private final byte[] serviceKey;
  public InternalSubscriptionController(SubscriptionBillingContextFacade subscriptions,
      @Value("${coldtrace.internal.service-key}") String key) {
    if (key==null || key.length()<32) throw new IllegalArgumentException("Service key must contain at least32 characters");
    this.subscriptions=subscriptions; this.serviceKey=key.getBytes(StandardCharsets.UTF_8);
  }
  private void authorize(String key,long organizationId) {
    if (key==null || !MessageDigest.isEqual(serviceKey,key.getBytes(StandardCharsets.UTF_8)))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    if (organizationId<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  }
  @PutMapping("/base-subscription")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void initialize(@RequestHeader(value="X-Service-Key",required=false) String key,
      @PathVariable long organizationId) {
    authorize(key,organizationId);
    subscriptions.initializeBaseSubscriptionForOrganization(organizationId);
  }
  @GetMapping("/entitlements")
  public SubscriptionBillingContextFacade.OrganizationEntitlementsSnapshot entitlements(
      @RequestHeader(value="X-Service-Key",required=false) String key,@PathVariable long organizationId) {
    authorize(key,organizationId);
    return subscriptions.fetchEntitlementsByOrganizationId(organizationId)
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
  @GetMapping("/entitlements/{entitlement}")
  public SubscriptionBillingContextFacade.EntitlementCheckSnapshot decision(
      @RequestHeader(value="X-Service-Key",required=false) String key,
      @PathVariable long organizationId,@PathVariable String entitlement) {
    authorize(key,organizationId);
    return subscriptions.checkEntitlement(organizationId,entitlement)
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
