# What is it?

It's a heart of the `mega-app` project. There is no any special logic here, just a simple Spring Boot application
with some integrated and implemented features and tools which can be useful in a real project.
See the [BACKLOG.md](../BACKLOG.md) file for the list of features.

# Security

### Authentification and Authorization

The app uses Spring Security and Keycloak for authentification and authorization. See the proper
keycloak [realm config](../docker/keycloak/realms/local-realm.json)
to request the secured endpoints.

```shell
curl -X POST http://localhost:8080/actuator/shutdown -H "Content-Type: application/json"
```

# Testing

## Unit and slice tests

## Manual testing secured endpoints

Obtain an access token from Keycloak using curl (the data taken from the realm config):

```bash
curl http://localhost:8091/realms/local/protocol/openid-connect/token \
  -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password" \
  -d "client_id=mega-app-client-id"
```

For basic authn and authz use the following credentials:

- Username: `testuser-basic`
- Password: `password`

# Other features

## Gracefully shutdown

To gracefully shutdown application, send POST localhost:8080/actuator/shutdown
