package com.github.jenkaby.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DelayService {

    public void makeDelay(long sleepMs) {
        try {
            log.info("Sleeping {} ms", sleepMs);
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            log.info("An error occurred", e);
        }
    }
}
