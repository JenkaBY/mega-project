In progress

- [ ] Measure performance difference between AOP, BPP(CGLib, DynamicProxy), Micrometer Timed and native

TODO:

- [ ] add kafka-gitops approach to create topics and acl [kafka-gitops](https://github.com/devshawn/kafka-gitops)
- [ ] use the best-practise for topic name
  convention [dev.io](https://dev.to/devshawn/apache-kafka-topic-naming-conventions-3do6)
- [ ] Kafka ssl via SslBundles
- [ ] spring web via https with self-signed certificate
- [ ] QueryDSL or jooq
- [ ] retryable schema registry (it should be on the main source
  soon https://github.com/confluentinc/schema-registry/pull/3424)
- [ ] id "nebula.integtest" version "8.2.0" TODO Explore this plugin for integration tests
- [ ] add filtering fields to logger
- [ ] add http logging library(maybe from Zalando) instead of custom implementation
- Observability:
    - [ ] Grafana as logs collector
    - [ ] Grafana as traces collector
- [ ] move testcontainers to separate spring profile
- [ ] use embedded external dependencies with a embedded spring profile for local development and tests
- [ ] use spun up services for faster local testing
- [ ] investigate and apply `@TestBean`

Implemented:

- gradle
    - [x] multi projects structure
    - [x] version catalog
- Testing
    - [x] postgres testcontainers
    - [x] slice tests (JPA, WebMvc)
    - [x] component tests via Cucumber
    - [x] use the Gatling testing framework for performance tests
- Kafka:
    - [x] Kafka consumers
    - [x] string SerDe
    - [x] JSON SerDe
    - [x] Avro schemas
    - [x] schema registry
    - [x] Avro SerDe
    - [x] skip retrying message consuming for specific exception
    - [x] filter Kafka message by field or header value
- Spring general features:
    - [x] graceful shutdown. [Note](/service/README.md#gracefully-shutdown)
    - [x] AOP (around method execution and @annotation)
    - [x] Bean Post Processor(BPP)(CGLib proxy type)
    - [x] Bean Post Processor(BPP)(JDK Dynamic proxy type)
    - [x] @Timed Micrometer
- CI/CD:
    - [x] Add profile to docker-compose file to easily start only minimal number of
      services. [Note](README.md#start-infrastructure)
    - [x] github CI
    - [x] improve GitHub CI. Parallelize test executions, cache build and so on
- AuthZ and Authn:
    - [x] Spring Security(JWT, Basic)
    - [x] Use Keycloak as an authentication server
    - [x] Secure REST endpoints via roles
    - [x] Custom method arguments resolver(see com.github.jenkaby.config.security.support.LoggedUserResolver)
- Observability:
    - [x] Micrometer metrics
    - [x] Prometheus as metrics collector
    - [x] Grafana dashboard
    - [x] provisioning Grafana dashboard, alert-manager rules, Prometheus configurations