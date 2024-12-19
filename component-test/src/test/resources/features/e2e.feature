Feature: Cucumber test behaviour

  Scenario: Application name should be correct
    Given the 'component-test' module uses spring-boot-test-starter
    Then application name should be "component-test-mega"
