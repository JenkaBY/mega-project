Feature: Describe endpoints related to Secured resources controller

  Background:
    Given application is started
    And the user is authenticated with bearer token 'valid-jwt-token'

  Scenario: Verify the secured resources endpoints are secured
    When a GET request has been made to '/api/v1/secured-resources' endpoint
    Then the response status is 200

  Scenario: Verify the secured resources endpoints are secured
    Given there's no authenticated user
    When a GET request has been made to '/api/v1/secured-resources' endpoint
    Then the response status is 401

  Scenario: Verify the secured resources endpoints are secured with has role access and @PreAuthorize annotation requiring a non-existing role
    When a GET request has been made to '/api/v1/secured-resources/has-roles' endpoint
    Then the response status is 403

  Scenario: Verify the secured 'developer' resource are accessible for user with 'developer' role
    When a GET request has been made to '/api/v1/secured-resources/has-roles/developer' endpoint
    Then the response status is 200

  Scenario: The resource guarded by basic authz must not be accessible for an user authenticated by bearer token
    When a GET request has been made to '/api/v1/secured-resources/basic/has-roles/developer' endpoint
    Then the response status is 401

  Scenario: Verify the secured 'iam' resources endpoint return all JWT claims
    When a GET request has been made to '/api/v1/secured-resources/i-am' endpoint
    Then the response status is 200

  Scenario: Verify the secured 'developer' resource are accessible for user with 'developer' role authenticated by basic authorization
#    Clear Bearer token
    Given there's no authenticated user
#   Set Basic token
    And the user is authenticated with basic username 'testuser-basic'
    When a GET request has been made to '/api/v1/secured-resources/basic/has-roles/developer' endpoint
    Then the response status is 200
