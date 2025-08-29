package com.github.jenkaby.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/secured-resources")
@RestController
@RequiredArgsConstructor
public class SecuredController {

    private final ObjectMapper mapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Map<String, Object> check() {
        log.info("[GET] Requested a secured endpoint.");
        return Map.of(
                "status", "SUCCESS"
        );
    }

    @PreAuthorize("hasRole('DOES_NOT_EXIST_ROLE')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/has-roles")
    public Map<String, Object> hasRoles() {
        log.info("[GET] Requested a secured endpoint with has role access.");
        return Map.of(
                "status", "SUCCESS"
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/has-roles/developer")
    public Map<String, Object> onlyFoDev() {
        log.info("[GET] Requested a secured endpoint for user having the 'developer' role.");
        return Map.of(
                "status", "SUCCESS"
        );
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/basic/has-roles/developer")
    public Map<String, Object> onlyFoDevWithBasicAuth() {
        log.info("[GET] Requested a secured endpoint for user having the 'developer' role + Basic Auth.");
        return Map.of(
                "status", "SUCCESS"
        );
    }

    @GetMapping("/i-am")
    public ResponseEntity<Object> whoAmI(JwtAuthenticationToken jwtAuthToken,
                                         @AuthenticationPrincipal Jwt principal) {
        log.info("[GET] Requested a who-am-i endpoint authorities authToken: {}", jwtAuthToken);
        log.info("[GET] Requested a who-am-i endpoint authorities JWT: {}", principal);
        try {
            var jsonString = mapper.writeValueAsString(jwtAuthToken);
//            log.info("IAM {}", jsonString);
            return ResponseEntity.ok(jwtAuthToken.getAuthorities());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "MALFORMED_RESPONSE"
            ));
        }
    }
}
