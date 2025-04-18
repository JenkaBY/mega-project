Implemented:
- [x] gradle multi projects
- [x] gradle version catalog
- [x] postgres testcontainers
- [x] slice tests (JPA, WebMvc)
- [x] component tests via Cucumber
- [x] github CI
- [x] kafka: Kafka consumer
- [x] kafka: string SerDe
- [x] kafka: JSON SerDe
- [x] kafka: Avro schemas
- [x] kafka: schema registry
- [x] kafka: avro SerDe
- [x] graceful shutdown. [Note](#gracefully-shutdown)
- [x] kafka: skip retrying message consuming for specific exception
- [x] kafka: filter kafka message by field or header value
- [x] Add profile to docker-compose file to easily start only minimal number of services. [Note](#start-infrastructure)
- [x] AOP (around method execution and @annotation)
- [x] Bean Post Processor(BPP)(CGLib proxy type)
- [x] Bean Post Processor(BPP)(JDK Dynamic proxy type)
- [x] @Timed Micrometer
- [x] improve GitHub CI. Parallelize test executions, cache build and so on

In progress
- [ ] Measure performance difference between AOP, BPP(CGLib, DynamicProxy), Micrometer Timed and native


TODO:
- [ ] add kafka-gitops approach to create topics and acl [kafka-gitops](https://github.com/devshawn/kafka-gitops)
- [ ] use the best-practise for topic name convention [dev.io](https://dev.to/devshawn/apache-kafka-topic-naming-conventions-3do6)
- [ ] kafka ssl via SslBundles
- [ ] spring web via https with self-signed certificate
- [ ] QueryDSL or jooq
- [ ] retryable schema registry (it should be on the main source
  soon https://github.com/confluentinc/schema-registry/pull/3424)
- [ ] Spring Security(JWT, Basic)

### Notes:
 The docker compose file contains several services that use host network 
 ```
  service:
    promehteus: 
      network_mode: host
 ```
It was done because of the issues with docker running in WSL and running the 'mega-app' service on windows host.
It was the easiest way to forward ports and to connect the running services with Prometheus.
Ideally the prometheus container should be able to establish connection to the running services via {{ docker.for.win.host.internal }}  or {{ host.docker.internal:host-gateway }}
.See the [How to connect to the Docker host from inside a Docker container?](https://medium.com/@TimvanBaarsen/how-to-connect-to-the-docker-host-from-inside-a-docker-container-112b4c71bc66)
Additionally, it's needed to prometheus config to be reconfigured according to docker network configuration(use service name) for grafana, prom/alertmanager and prometheus itself.

### Start infrastructure

Start infrastructure(db, kafka's services):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'kafka-admin' --profile 'app' up -d
```

To stop infrastructure(db, kafka's services):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'kafka-admin' --profile 'app' stop
```

To start observability infrastructure(prometheus, grafana, prometheus-alert and so):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'observability' up -d
```

To stop ALL infrastructure(prometheus, grafana, prometheus-alert and so):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'observability' --profile 'kafka-admin' --profile 'app' stop
```

### Gracefully shutdown
To gracefully shutdown application, send POST localhost:8080/actuator/shutdown