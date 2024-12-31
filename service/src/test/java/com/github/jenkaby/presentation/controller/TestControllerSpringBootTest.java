package com.github.jenkaby.presentation.controller;

import com.github.jenkaby.support.db.AbstractReusableDbTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestControllerSpringBootTest extends AbstractReusableDbTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @SneakyThrows
    @Test
    void notFoundEndpoint_Should_ReturnNotFound() {
        var url = "/api/test/not-found";
        var response = restTemplate.getForEntity(url, String.class);

        System.out.println("Response " + response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
        assertThat(response.getBody()).isEqualTo("not-found");
    }
}