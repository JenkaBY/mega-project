package com.github.jenkaby.mega.config.db;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.jenkaby.mega.config.db.DbProperties.POSTGRES_IMAGE_NAME;

@Testcontainers
public class AbstractDbTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            POSTGRES_IMAGE_NAME
    );
}
