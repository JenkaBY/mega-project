package com.github.jenkaby;

import com.github.jenkaby.support.db.AbstractReusableDbTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MegaApplicationTest extends AbstractReusableDbTest {

    @Test
    void contextLoads() {
        assertThat(true).as("Context failed to load").isTrue();
    }
}