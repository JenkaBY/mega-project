package com.github.jenkaby.presentation.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public String internalErrorHandler(Throwable error) {
        log.warn("Internal error occurred:", error);
        return "Internal error occurred";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public String authorizationDeniedException(AuthorizationDeniedException authzDeniedException) {
        log.warn("The logged in user has insufficient permissions: {}", authzDeniedException.getAuthorizationResult());
        return authzDeniedException.getAuthorizationResult().toString();
    }
}
