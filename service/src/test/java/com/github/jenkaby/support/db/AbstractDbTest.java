package com.github.jenkaby.support.db;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static com.github.jenkaby.support.db.DbProperties.POSTGRES_IMAGE_NAME;

@Testcontainers
public class AbstractDbTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            POSTGRES_IMAGE_NAME
    );
}
