package com.acme.coldtrace.platform.billing.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.acme.coldtrace.platform.billing.domain.model.aggregates.OrganizationSubscription;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.BillingProvider;
import com.acme.coldtrace.platform.billing.domain.model.valueobjects.SubscriptionStatus;
import com.acme.coldtrace.platform.billing.interfaces.acl.SubscriptionBillingContextFacade;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.web.server.ResponseStatusException;

public class OrganizationSubscriptionSteps {
  private static final String KEY = "isolated-contract-workload-key-with-32-characters";

  private OrganizationSubscription subscription;
  private final Set<Long> initialized = new HashSet<>();
  private InternalSubscriptionController controller;

  @When("organization {long} is created")
  @Given("organization {long} is on the free base plan")
  public void created(long organizationId) {
    subscription = new OrganizationSubscription(organizationId);
  }

  @When("the billing provider confirms plan {string} with status {word}")
  public void providerEvent(String plan, String status) {
    OffsetDateTime start = OffsetDateTime.parse("2026-11-01T00:00:00Z");
    subscription.synchronizeProviderState(
        plan, SubscriptionStatus.valueOf(status), BillingProvider.STRIPE, "cus_demo", "sub_demo",
        start, start.plusMonths(1), false, null);
  }

  @Then("its subscription plan is {string} with status {word}")
  public void plan(String plan, String status) {
    assertEquals(plan, subscription.getPlanCode());
    assertEquals(SubscriptionStatus.valueOf(status), subscription.getStatus());
  }

  @Then("the plan entitlements are {word}")
  public void entitlements(String state) {
    assertEquals("enabled".equals(state), subscription.allowsPlanEntitlements());
  }

  @Given("the subscription owner initializes organization {long} with the workload key")
  public void initialize(long organizationId) {
    controller =
        new InternalSubscriptionController(
            new SubscriptionBillingContextFacade() {
              public void initializeBaseSubscriptionForOrganization(Long id) {
                initialized.add(id);
              }

              public Optional<OrganizationEntitlementsSnapshot> fetchEntitlementsByOrganizationId(Long id) {
                return initialized.contains(id)
                    ? Optional.of(new OrganizationEntitlementsSnapshot(id, "base", "FREE", List.of()))
                    : Optional.empty();
              }

              public Optional<EntitlementCheckSnapshot> checkEntitlement(Long id, String key) {
                return Optional.empty();
              }

              public boolean canUseEntitlement(Long id, String key) {
                return false;
              }
            },
            KEY);
    controller.initialize(KEY, organizationId);
  }

  @Then("the internal entitlements for organization {long} are returned")
  public void internalEntitlements(long organizationId) {
    assertEquals(organizationId, controller.entitlements(KEY, organizationId).organizationId());
  }

  @And("the internal entitlements for organization {long} are not found")
  public void notFound(long organizationId) {
    var error =
        assertThrows(ResponseStatusException.class, () -> controller.entitlements(KEY, organizationId));
    assertEquals(404, error.getStatusCode().value());
  }

  @And("an initialization with a browser token is rejected as unauthorized")
  public void browserToken() {
    var error =
        assertThrows(ResponseStatusException.class, () -> controller.initialize("Bearer browser-token", 9));
    assertEquals(401, error.getStatusCode().value());
  }
}
