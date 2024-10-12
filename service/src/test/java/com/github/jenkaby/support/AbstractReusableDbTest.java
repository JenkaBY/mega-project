package com.github.jenkaby.support;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.github.jenkaby.support.DbProperties.POSTGRES_IMAGE_NAME;

public class AbstractReusableDbTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres ;

    static {
        postgres = new PostgreSQLContainer<>(
                POSTGRES_IMAGE_NAME);
        postgres.start();
    }
}
