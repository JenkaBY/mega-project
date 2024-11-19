package com.github.jenkaby.presentation.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public String internalErrorHandler(Throwable error) {
        log.warn("Internal error occurred:", error);
        return "Internal error occurred";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public void internalErrorHandler(NoResourceFoundException resourceNotFound) {
        log.warn("Resource not found error occurred: {}", resourceNotFound.getMessage());
    }
}
