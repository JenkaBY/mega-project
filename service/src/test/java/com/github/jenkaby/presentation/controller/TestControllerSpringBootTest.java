package com.github.jenkaby.presentation.controller;

import com.github.jenkaby.support.db.AbstractReusableDbTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.test.context.support.WithMockUser;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@Disabled("It fails. The Keycloak must be mocked")
@EnableWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestControllerSpringBootTest extends AbstractReusableDbTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setup() {
        stubFor(get(urlEqualTo("/realms/local/protocol/openid-connect/certs"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"keys\":[{\"kid\":\"1\",\"kty\":\"RSA\",\"alg\":\"RS256\"}]}")));

        stubFor(get(urlEqualTo("/realms/local/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                OPENID_CONF
                        )));

        stubFor(post(urlEqualTo("/realms/local/protocol/openid-connect/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"mock-token\",\"token_type\":\"Bearer\"}")));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "developer")
    void notFoundEndpoint_Should_ReturnNotFound() {
        var url = "/api/test/not-found";
        var response = restTemplate.getForEntity(url, String.class);

        System.out.println("Response " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        assertThat(response.getBody()).isEqualTo("not-found");
    }

    //    @TestConfiguration
    static class TestSecurityConfig {

//        @Bean
//        @Primary
//        public JwtDecoder jwtDecoder() {
//            System.out.println("!!!!!!!!!!!!!!!!!!!! ");
//            // return a fake JwtDecoder that always validates your test tokens
//            return token -> Jwt.withTokenValue(token)
//                    .claim("sub", "testuser-bearer")
//                    .claim("realm_access", Map.of("roles", List.of("developer")))
//                    .build();
//        }


    }


    private static final String OPENID_CONF =
            """
                    {
                      "issuer": "http://localhost:19090/realms/local",
                      "authorization_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/auth",
                      "token_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/token",
                      "introspection_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/token/introspect",
                      "userinfo_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/userinfo",
                      "end_session_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/logout",
                      "jwks_uri": "http://localhost:19090/realms/local/protocol/openid-connect/certs",
                    
                      "grant_types_supported": [
                        "authorization_code",
                        "client_credentials",
                        "implicit",
                        "password",
                        "refresh_token",
                      ],
                      "acr_values_supported": [
                        "0",
                        "1"
                      ],
                      "response_types_supported": [
                        "code",
                        "none",
                        "id_token",
                        "token",
                        "id_token token",
                        "code id_token",
                        "code token",
                        "code id_token token"
                      ],
                      "subject_types_supported": [
                        "public",
                        "pairwise"
                      ],
                      "prompt_values_supported": [
                        "none",
                        "login",
                        "consent"
                      ],
                      "id_token_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "id_token_encryption_alg_values_supported": [
                        "ECDH-ES+A256KW",
                        "ECDH-ES+A192KW",
                        "ECDH-ES+A128KW",
                        "RSA-OAEP",
                        "RSA-OAEP-256",
                        "RSA1_5",
                        "ECDH-ES"
                      ],
                      "id_token_encryption_enc_values_supported": [
                        "A256GCM",
                        "A192GCM",
                        "A128GCM",
                        "A128CBC-HS256",
                        "A192CBC-HS384",
                        "A256CBC-HS512"
                      ],
                      "userinfo_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512",
                        "none"
                      ],
                      "userinfo_encryption_alg_values_supported": [
                        "ECDH-ES+A256KW",
                        "ECDH-ES+A192KW",
                        "ECDH-ES+A128KW",
                        "RSA-OAEP",
                        "RSA-OAEP-256",
                        "RSA1_5",
                        "ECDH-ES"
                      ],
                      "userinfo_encryption_enc_values_supported": [
                        "A256GCM",
                        "A192GCM",
                        "A128GCM",
                        "A128CBC-HS256",
                        "A192CBC-HS384",
                        "A256CBC-HS512"
                      ],
                      "request_object_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512",
                        "none"
                      ],
                      "request_object_encryption_alg_values_supported": [
                        "ECDH-ES+A256KW",
                        "ECDH-ES+A192KW",
                        "ECDH-ES+A128KW",
                        "RSA-OAEP",
                        "RSA-OAEP-256",
                        "RSA1_5",
                        "ECDH-ES"
                      ],
                      "request_object_encryption_enc_values_supported": [
                        "A256GCM",
                        "A192GCM",
                        "A128GCM",
                        "A128CBC-HS256",
                        "A192CBC-HS384",
                        "A256CBC-HS512"
                      ],
                      "response_modes_supported": [
                        "query",
                        "fragment",
                        "form_post",
                        "query.jwt",
                        "fragment.jwt",
                        "form_post.jwt",
                        "jwt"
                      ],
                      "registration_endpoint": "http://localhost:9090/realms/local/clients-registrations/openid-connect",
                      "token_endpoint_auth_methods_supported": [
                        "private_key_jwt",
                        "client_secret_basic",
                        "client_secret_post",
                        "tls_client_auth",
                        "client_secret_jwt"
                      ],
                      "token_endpoint_auth_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "introspection_endpoint_auth_methods_supported": [
                        "private_key_jwt",
                        "client_secret_basic",
                        "client_secret_post",
                        "tls_client_auth",
                        "client_secret_jwt"
                      ],
                      "introspection_endpoint_auth_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "authorization_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "authorization_encryption_alg_values_supported": [
                        "ECDH-ES+A256KW",
                        "ECDH-ES+A192KW",
                        "ECDH-ES+A128KW",
                        "RSA-OAEP",
                        "RSA-OAEP-256",
                        "RSA1_5",
                        "ECDH-ES"
                      ],
                      "authorization_encryption_enc_values_supported": [
                        "A256GCM",
                        "A192GCM",
                        "A128GCM",
                        "A128CBC-HS256",
                        "A192CBC-HS384",
                        "A256CBC-HS512"
                      ],
                      "claims_supported": [
                        "aud",
                        "sub",
                        "iss",
                        "auth_time",
                        "name",
                        "given_name",
                        "family_name",
                        "preferred_username",
                        "email",
                        "acr"
                      ],
                      "claim_types_supported": [
                        "normal"
                      ],
                      "claims_parameter_supported": true,
                      "scopes_supported": [
                        "openid",
                        "profile",
                        "phone",
                        "service_account",
                        "roles",
                        "web-origins",
                        "email",
                        "secured-resources:read",
                        "basic",
                        "acr",
                        "address",
                        "microprofile-jwt",
                        "organization",
                        "offline_access"
                      ],
                      "request_parameter_supported": true,
                      "request_uri_parameter_supported": true,
                      "require_request_uri_registration": true,
                      "code_challenge_methods_supported": [
                        "plain",
                        "S256"
                      ],
                      "tls_client_certificate_bound_access_tokens": true,
                      "revocation_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/revoke",
                      "revocation_endpoint_auth_methods_supported": [
                        "private_key_jwt",
                        "client_secret_basic",
                        "client_secret_post",
                        "tls_client_auth",
                        "client_secret_jwt"
                      ],
                      "revocation_endpoint_auth_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "HS256",
                        "HS512",
                        "ES256",
                        "RS256",
                        "HS384",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "backchannel_logout_supported": true,
                      "backchannel_logout_session_supported": true,
                      "device_authorization_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/auth/device",
                      "backchannel_token_delivery_modes_supported": [
                        "poll",
                        "ping"
                      ],
                      "backchannel_authentication_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/ext/ciba/auth",
                      "backchannel_authentication_request_signing_alg_values_supported": [
                        "PS384",
                        "RS384",
                        "EdDSA",
                        "ES384",
                        "ES256",
                        "RS256",
                        "ES512",
                        "PS256",
                        "PS512",
                        "RS512"
                      ],
                      "require_pushed_authorization_requests": false,
                      "pushed_authorization_request_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/ext/par/request",
                      "mtls_endpoint_aliases": {
                        "token_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/token",
                        "revocation_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/revoke",
                        "introspection_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/token/introspect",
                        "device_authorization_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/auth/device",
                        "registration_endpoint": "http://localhost:19090/realms/local/clients-registrations/openid-connect",
                        "userinfo_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/userinfo",
                        "pushed_authorization_request_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/ext/par/request",
                        "backchannel_authentication_endpoint": "http://localhost:19090/realms/local/protocol/openid-connect/ext/ciba/auth"
                      },
                      "authorization_response_iss_parameter_supported": true
                    }
                    """;
}