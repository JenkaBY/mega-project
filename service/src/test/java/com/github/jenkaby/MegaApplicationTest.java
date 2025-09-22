package com.github.jenkaby;

import com.github.jenkaby.support.db.AbstractReusableDbTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MegaApplicationTest extends AbstractReusableDbTest {

    @Test
    @Timeout(30)
    void contextLoads() {

    }

    @TestConfiguration
    @MockBeans({
            @MockBean(classes = JwtDecoder.class)
    })
    static class TestConfig {
    }
}