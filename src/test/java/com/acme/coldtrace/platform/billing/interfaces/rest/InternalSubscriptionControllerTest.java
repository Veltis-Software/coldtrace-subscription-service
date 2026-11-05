package com.acme.coldtrace.platform.billing.interfaces.rest;

import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class InternalSubscriptionControllerTest {
  private static final String KEY = "isolated-contract-workload-key-with-32-characters";
  private final Set<Long> initialized = new HashSet<>();
  private final SubscriptionBillingContextFacade owner = new SubscriptionBillingContextFacade() {
    public void initializeBaseSubscriptionForOrganization(Long id) { initialized.add(id); }
    public Optional<OrganizationEntitlementsSnapshot> fetchEntitlementsByOrganizationId(Long id) {
      return initialized.contains(id) ? Optional.of(new OrganizationEntitlementsSnapshot(id, "base", "FREE", List.of())) : Optional.empty();
    }
    public Optional<EntitlementCheckSnapshot> checkEntitlement(Long id, String key) { return Optional.empty(); }
    public boolean canUseEntitlement(Long id, String key) { return false; }
  };

  @Test void rejectsBrowserCredentialBeforeCallingOwner() {
    var controller = new InternalSubscriptionController(owner, KEY);
    assertEquals(401, assertThrows(ResponseStatusException.class,
        () -> controller.initialize("Bearer browser-token", 7)).getStatusCode().value());
    assertTrue(initialized.isEmpty());
  }
  @Test void invalidOrganizationDoesNotInitializeASubscription() {
    var controller = new InternalSubscriptionController(owner, KEY);
    assertEquals(400, assertThrows(ResponseStatusException.class,
        () -> controller.initialize(KEY, 0)).getStatusCode().value());
    assertTrue(initialized.isEmpty());
  }
  @Test void passesOrganizationScopeAndMissingDecisionToTheCaller() {
    var controller = new InternalSubscriptionController(owner, KEY);
    controller.initialize(KEY, 7);
    assertEquals(7L, controller.entitlements(KEY, 7).organizationId());
    assertEquals(404, assertThrows(ResponseStatusException.class,
        () -> controller.entitlements(KEY, 8)).getStatusCode().value());
  }
}
