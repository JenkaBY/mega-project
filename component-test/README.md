# 🚀 Component test for the `mega-app`

A modern, modular Java project built with Gradle, featuring comprehensive **component-level testing** using **Cucumber**
and Gherkin syntax.
The project is designed to follow best practices in maintainability, scalability, and behavior-driven development (BDD).

## 📦 Project Structure

All the code under test sources in the src/test folder. It follows a testing intention of this submodule and
improve the project build time while skipping the tests(`gradle -X test`).

## 🛠️ Tech Stack

- **Java 21+**
- **Gradle**
- **Spring Boot test**
- **Cucumber JVM**
- **JUnit 5**
- **AssertJ**
- **Awaitality**
- **testcontainers**

## ⚙️ Build & Run

### 🧪 Run Component Tests (Cucumber)

```shell
env APP_TEST_LATENCY_VALIDATION_THRESHOLD=5 ./gradlew :clean :component-test:test
``` 

or run from IntelliJIdea:

```shell
cd .. && env APP_TEST_LATENCY_VALIDATION_THRESHOLD=5 ./gradlew :clean :component-test:test
```

where `APP_TEST_LATENCY_VALIDATION_THRESHOLD` is an environment variable responsible for the threshold used to
validate metrics received by the metric register. The default is 3 ms, sometime it is not enough to pass.

Cucumber reports will be available in:

```text
./build/reports/tests/test/index.html
```