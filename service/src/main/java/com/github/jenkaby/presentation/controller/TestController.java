package com.github.jenkaby.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequestMapping("/api/test")
@RestController
public class TestController {

    @GetMapping
    public String testProm() {
        try {
            var sleep = ThreadLocalRandom.current().nextInt(400, 699 + 1);
            log.info("Requested metrics. Sleep {} ms", sleep);
            Thread.sleep(sleep);
//            log.info("Start fibonacci calculation");
//            fibonacci(48);
//            log.info("End fibonacci calculation");
        } catch (InterruptedException e) {
            // ignore
        }
        return "# test";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @GetMapping("/not-found")
    public String notFound() {
        return "not-found";
    }

    @GetMapping("/internal-error")
    public String internalError() {
        if (1 == 1) {
            throw new RuntimeException("Intended exception");
        }

        return "Should never be reached";
    }

    private static long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n-1) + fibonacci(n-2);
    }
}
