Feature: Application is running

  Scenario: Application is running and  its status should be OK
    Given application is started
    When a GET request has been made to '/actuator/health' endpoint
    Then the response status is 200
    And the response contains
      | path                     | value |
      | $.status                 | UP    |
      | $.components.ping.status | UP    |