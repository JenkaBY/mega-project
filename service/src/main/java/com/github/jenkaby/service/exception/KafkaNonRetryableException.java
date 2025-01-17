package com.github.jenkaby.service.exception;

public class KafkaNonRetryableException extends RuntimeException {

    public KafkaNonRetryableException(String message) {
        super(message);
    }
}
