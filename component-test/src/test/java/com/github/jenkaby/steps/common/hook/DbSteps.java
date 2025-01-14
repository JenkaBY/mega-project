package com.github.jenkaby.steps.common.hook;

import io.cucumber.java.After;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DbSteps {

    private static final List<String> TABLE_TO_TRUNCATE = List.of(
            "message_log"
    );

    private final JdbcClient jdbcClient;

    @After
    public void truncateDb() {
        log.info("Deleting all records on tables {}", TABLE_TO_TRUNCATE);
        JdbcTestUtils.deleteFromTables(jdbcClient, TABLE_TO_TRUNCATE.toArray(new String[0]));
    }
}
