@run
Feature: Describe endpoints related to KafkaMessageController

  Background:
    Given application is started
    And the 'testuser-bdd' is authenticated

  Scenario: Verify the secured resources endpoints are secured
    When a GET request has been made to '/api/v1/secured-resources' endpoint
    Then the response status is 200

  Scenario: Verify the secured resources endpoints are secured
    Given there's no authenticated user
    When a GET request has been made to '/api/v1/secured-resources' endpoint
    Then the response status is 401

  Scenario: Verify the secured resources endpoints are secured with has role access and @PreAuthorize annotation
    When a GET request has been made to '/api/v1/secured-resources/has-roles/does-not-exist' endpoint
    Then the response status is 403

  Scenario: Verify the secured 'developer' resource are accessible for user with 'developer' role
    When a GET request has been made to '/api/v1/secured-resources/has-roles/developer' endpoint
    Then the response status is 200

  Scenario: Verify the secured 'iam' resources endpoint return all JWT claims
    When a GET request has been made to '/api/v1/secured-resources/i-am' endpoint
    Then the response status is 200
