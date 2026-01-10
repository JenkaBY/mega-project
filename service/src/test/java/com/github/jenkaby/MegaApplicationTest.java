package com.github.jenkaby;

import com.github.jenkaby.support.db.AbstractReusableDbTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MegaApplicationTest extends AbstractReusableDbTest {

    @Test
    @Timeout(30)
    void contextLoads() {

    }

}