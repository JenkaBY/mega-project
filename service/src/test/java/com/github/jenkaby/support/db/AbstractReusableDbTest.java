package com.github.jenkaby.support.db;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static com.github.jenkaby.support.db.DbProperties.POSTGRES_IMAGE_NAME;

@ActiveProfiles("test")
public class AbstractReusableDbTest {

    @ServiceConnection
    static final PostgreSQLContainer postgres;

    static {
        postgres = new PostgreSQLContainer(POSTGRES_IMAGE_NAME);
        postgres.start();
    }
}
