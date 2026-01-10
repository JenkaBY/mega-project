package com.github.jenkaby.support.db;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.github.jenkaby.support.db.DbProperties.POSTGRES_IMAGE_NAME;

@ActiveProfiles("test")
@MockBean(classes = JwtDecoder.class)
public class AbstractReusableDbTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);
        postgres.start();
    }
}
