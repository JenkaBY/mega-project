package com.github.jenkaby.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenkaby.config.security.support.LoggedUser;
import com.github.jenkaby.config.security.support.LoggedUserInfo;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "The secured endpoints",
        externalDocs = @ExternalDocumentation(description = "See the readme file",
                url = "https://github.com/JenkaBY/mega-project/blob/master/README.md#Manual-testing-secured-endpoints"))
public class SecuredController {

    private final ObjectMapper mapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(
            summary = "Check secured endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "successful response"),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
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
    @Operation(summary = "Secured endpoint for user having the 'developer' role + Basic Auth.",
            security = @SecurityRequirement(name = "basicAuth"))
    public Map<String, Object> onlyFoDevWithBasicAuth() {
        log.info("[GET] Requested a secured endpoint for user having the 'developer' role + Basic Auth.");
        return Map.of(
                "status", "SUCCESS"
        );
    }

    @GetMapping("/i-am")
    public ResponseEntity<Object> whoAmI(JwtAuthenticationToken jwtAuthToken,
                                         @AuthenticationPrincipal Jwt principal,
                                         @LoggedUser LoggedUserInfo userInfo) {
        log.info("[GET] Requested a who-am-i endpoint authorities authToken: {}", jwtAuthToken);
        log.info("[GET] Requested a who-am-i endpoint authorities JWT: {}", principal.getClaims());
        log.info("[GET] Requested a who-am-i endpoint  Custom @method argument resolver: {}", userInfo);
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
