@subscription
Feature: Organization subscription and plan entitlements
  As an organization administrator
  I want to know which plan is active and what it enables
  So that the organization can grow its monitoring capacity when needed

  @US53 @US19
  Scenario: A new organization starts on the free base plan
    When organization 7 is created
    Then its subscription plan is "base" with status FREE
    And the plan entitlements are enabled

  @US25
  Scenario: A verified provider event upgrades the organization to a higher plan
    Given organization 7 is on the free base plan
    When the billing provider confirms plan "PRO" with status ACTIVE
    Then its subscription plan is "pro" with status ACTIVE
    And the plan entitlements are enabled

  @US24 @US53
  Scenario Outline: Entitlements depend on the subscription status
    Given organization 7 is on the free base plan
    When the billing provider confirms plan "pro" with status <status>
    Then the plan entitlements are <result>

    Examples:
      | status   | result   |
      | ACTIVE   | enabled  |
      | PAST_DUE | disabled |
      | CANCELED | disabled |

  @US24
  Scenario: Internal entitlement queries keep the organization scope
    Given the subscription owner initializes organization 7 with the workload key
    Then the internal entitlements for organization 7 are returned
    And the internal entitlements for organization 8 are not found
    And an initialization with a browser token is rejected as unauthorized
