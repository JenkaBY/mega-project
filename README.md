# Project Mega-App

There are several services and sub-folders in this project:

- service: [main app](/service/README.md) leverage Spring Boot application
- load-test: project for generating load tests via Gatling framework to be able to observe metrics on the Grafana
  dashboard
- component-test: implemented via Cucumber framework to test components integration
- docker: contains [docker-compose file](/docker/docker-compose.yaml) with obligatory and complimentary services for
  local development and testing
- Gradle: wrapper files and [version catalog](/gradle/libs.versions.toml) for dependencies management


### Start infrastructure

Start obligatory infrastructure(db, Kafka's services):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'kafka-admin' --profile 'app' --profile 'observability' up -d
```

To stop infrastructure(db, Kafka's services):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'kafka-admin' --profile 'app' stop
```

To start observability infrastructure(Prometheus, Grafana, prometheus-alert and so):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'observability' up -d
```

To stop ALL infrastructure(Prometheus, Grafana, prometheus-alert and so):

```shell
docker compose --file ./docker/docker-compose.yaml --profile 'observability' --profile 'kafka-admin' --profile 'app' stop
```

##### Notes:

The docker compose file contains several services that use host network

 ```
service:
    promehteus: 
      network_mode: host
 ```

It was done because of the issues with docker running in WSL and running the 'mega-app' service on Windows host.
It was the easiest way to forward ports and to connect the running services with Prometheus.
Ideally the Prometheus container should be able to establish connection to the running services via {{
docker.for.win.host.internal }} or {{ host.docker.internal:host-gateway }}
.See
the [How to connect to the Docker host from inside a Docker container?](https://medium.com/@TimvanBaarsen/how-to-connect-to-the-docker-host-from-inside-a-docker-container-112b4c71bc66)
Additionally, it's needed to Prometheus config to be reconfigured according to docker network configuration(use service
name) for Grafana, prom/alertmanager and Prometheus itself.


### Performance test (Load test)

To run Gatling test see the file [README.md](./load-test/README.md)

### Component test (Load test)

To run component test see the file [README.md](./component-test/README.md)
