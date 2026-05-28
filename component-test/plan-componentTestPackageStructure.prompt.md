# Component Test Module Package Structure

## Overview

This document describes the package organization for the component-test module, which uses Cucumber/BDD for
component-level testing.

## Package Structure

```
src/test/java/com/github/jenkaby/
├── config/
│   ├── db/
│   ├── messaging/
│   ├── security/
│   ├── RestTemplateConfig.java
│   ├── SpyConfig.java
│   ├── TestPropertiesConfig.java
│   └── WebConfig.java
├── context/
│   ├── LocalMessagesStore.java
│   └── ScenarioContext.java
├── infrastructure/
│   └── listener/
│       └── TestKafkaListeners.java
├── model/
│   ├── AuthTokenType.java
│   ├── Constant.java
│   ├── JsonPathExpectation.java
│   ├── MessageLogExpectation.java
│   └── ReceivedNotificationMessage.java
├── steps/
│   ├── common/
│   │   ├── hook/
│   │   │   ├── DbSteps.java
│   │   │   ├── HookSteps.java
│   │   │   └── SpringTransactionHooks.java
│   │   ├── ApplicationSteps.java
│   │   ├── KafkaSteps.java
│   │   └── WebRequestSteps.java
│   ├── AuthSteps.java
│   ├── KafkaMessageControllerSteps.java
│   ├── MessageLogSteps.java
│   ├── MetricsStep.java
│   ├── MockSteps.java
│   └── NotificationMessageSteps.java
├── transformer/
│   ├── MessageLogExpectationTransformer.java
│   ├── ParameterTypes.java
│   └── TransactionEventDtoTransformer.java
└── RunComponentTests.java
```

## Package Descriptions

### `config/`

Contains all Spring test context configuration classes for component tests.

**Subpackages:**

- **`config/db/`** - Database configuration for tests (currently empty, reserved for future DB test configs)
- **`config/messaging/`** - Kafka and messaging configuration:
    - `TestKafkaMessageConsumerConfig.java` - Kafka consumer setup for test scenarios
- **`config/security/`** - Security configuration for tests:
    - `TestSecurityConfig.java` - Authentication/authorization setup for test contexts

**Files:**

- `RestTemplateConfig.java` - HTTP client configuration for REST API testing
- `SpyConfig.java` - Mockito spy bean definitions for monitoring interactions
- `TestPropertiesConfig.java` - Test-specific property loading and management
- `WebConfig.java` - Web layer configuration (MVC, filters, interceptors)

### `context/`

Manages test execution context and state between Cucumber steps.

**Files:**

- `ScenarioContext.java` - Stores scenario-scoped data shared across steps within a single scenario
- `LocalMessagesStore.java` - In-memory store for messages received during test execution (Kafka, notifications, etc.)

### `infrastructure/`

Infrastructure components that integrate with external systems during testing.

**Subpackages:**

- **`infrastructure/listener/`** - Event and message listeners:
    - `TestKafkaListeners.java` - Kafka message listeners that capture messages for verification in tests

### `model/`

Domain models, constants, and test-specific data structures.

**Files:**

- `AuthTokenType.java` - Enumeration of authentication token types used in tests
- `Constant.java` - Test constants and utility methods (e.g., `getTxnId()`)
- `JsonPathExpectation.java` - Model for JSON path-based assertions
- `MessageLogExpectation.java` - Model for message log verification expectations
- `ReceivedNotificationMessage.java` - DTO representing captured notification messages

### `steps/`

Cucumber step definitions that map Gherkin steps to Java implementation.

**Subpackages:**

- **`steps/common/`** - Reusable step definitions across multiple features:
    - **`steps/common/hook/`** - Cucumber lifecycle hooks:
        - `DbSteps.java` - Database setup/teardown hooks
        - `HookSteps.java` - General before/after scenario hooks
        - `SpringTransactionHooks.java` - Transaction management for test scenarios
    - `ApplicationSteps.java` - Application-level steps (startup, health checks)
    - `KafkaSteps.java` - Generic Kafka message production/consumption steps
    - `WebRequestSteps.java` - HTTP request execution and response handling

**Files:**

- `AuthSteps.java` - Authentication and authorization step definitions
- `KafkaMessageControllerSteps.java` - Steps for Kafka message controller interactions
- `MessageLogSteps.java` - Steps for message log verification
- `MetricsStep.java` - Steps for metrics and monitoring validation
- `MockSteps.java` - Steps for configuring mocks and stubs
- `NotificationMessageSteps.java` - Steps for notification message verification

### `transformer/`

Cucumber data transformers that convert DataTables and parameters into domain objects.

**Files:**

- `TransactionEventDtoTransformer.java` - Transforms DataTable rows into `TransactionEventRequest` objects using
  `@DataTableType`
- `MessageLogExpectationTransformer.java` - Transforms DataTable into `MessageLogExpectation` objects
- `ParameterTypes.java` - Custom parameter type definitions using `@ParameterType` for Cucumber expressions

### Root Level

- `RunComponentTests.java` - Main Cucumber test runner with `@CucumberOptions` configuration

## Resources Structure

```
src/test/resources/
├── features/
│   └── *.feature
├── application-test.yaml
└── junit-platform.properties
```

**Description:**

- **`features/`** - Gherkin feature files containing BDD scenarios
- **`application-test.yaml`** - Spring Boot test configuration properties
- **`junit-platform.properties`** - JUnit Platform configuration

## Design Principles

1. **Separation of Concerns**: Configuration, steps, models, and infrastructure are clearly separated
2. **Reusability**: Common steps are organized in `steps/common/` for use across multiple features
3. **Context Management**: Scenario state is managed through dedicated context classes
4. **Type Safety**: Transformers provide type-safe conversion from Gherkin to Java objects
5. **Test Independence**: Hooks ensure proper setup/teardown between scenarios
6. **Infrastructure Integration**: Real infrastructure components (Kafka, DB) are properly isolated in dedicated
   packages

## Usage Patterns

### Adding New Step Definitions

1. Create step definition class in `steps/` (feature-specific) or `steps/common/` (reusable)
2. Inject required context and configuration beans
3. Use `@Given`, `@When`, `@Then` annotations

### Adding New Transformers

1. Create transformer class in `transformer/`
2. Use `@DataTableType` for table transformations
3. Use `@ParameterType` for inline parameter conversions

### Adding New Test Configuration

1. Place in appropriate `config/` subpackage based on concern (db/messaging/security)
2. Use `@TestConfiguration` or `@Configuration` annotations
3. Define beans needed for test scenarios

### Managing Test State

1. Use `ScenarioContext` for scenario-scoped data
2. Use `LocalMessagesStore` for captured messages
3. Clear state in hooks to ensure test independence
