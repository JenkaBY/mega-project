package com.github.jenkaby.presentation.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public String authorizationDeniedException(AuthorizationDeniedException authzDeniedException) {
        log.warn("The logged in user has insufficient permissions: {}", authzDeniedException.getAuthorizationResult());
        return authzDeniedException.getAuthorizationResult().toString();
    }

    //    special handle 500 error for favicon.ico requests. Actually it can throw any status code
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("The resource '{}' not found by reason: {}", ex.getResourcePath(), ex.getMessage());
        return new ResponseEntity<>(ex.getBody(), ex.getStatusCode());
    }
}
