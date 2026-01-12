package com.github.jenkaby.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";
    private static final String BASIC_SCHEME = "basicAuth";
    private static final Content PROBLEM_DETAILS = new Content()
            .addMediaType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    new MediaType().schema(
                            new Schema<ProblemDetail>().$ref("#/components/schemas/ProblemDetail")
                    ));
    private static final ApiResponse UNAUTHORIZED_API_RESPONSE = new ApiResponse().description("unauthorized");
    private static final ApiResponse FORBIDDEN_API_RESPONSE = new ApiResponse().description("forbidden");

    @Bean
    public GroupedOpenApi publicApi(@Value("${spring.application.name}") String appName) {
        return GroupedOpenApi.builder()
                .group(appName)
                .addOpenApiCustomizer(openApi -> {
                    openApi.setInfo(new Info().title("Mega App API"));
                })
                .pathsToMatch(org.springdoc.core.utils.Constants.ALL_PATTERN)
                .consumesToMatch()
                .build();
    }

    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            addSecurity(openApi);
            addCommonResponses(openApi);
        };
    }

    public static void addSecurity(OpenAPI openApi) {
        openApi.getComponents()
                .addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .in(SecurityScheme.In.HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                )
                .addSecuritySchemes(BASIC_SCHEME,
                        new SecurityScheme()
                                .in(SecurityScheme.In.HEADER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                );

        openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/api/v1/secured-resources") && !path.contains("/basic/")) {
                pathItem.readOperations().forEach(operation ->
                        operation.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                                .getResponses()
                                .addApiResponse("401", UNAUTHORIZED_API_RESPONSE)
                                .addApiResponse("403", FORBIDDEN_API_RESPONSE)
                );
            }
        });

        openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/api/v1/secured-resources/basic/")) {
                pathItem.readOperations().forEach(operation ->
                        operation.addSecurityItem(new SecurityRequirement().addList(BASIC_SCHEME))
                                .getResponses()
                                .addApiResponse("401", UNAUTHORIZED_API_RESPONSE)
                                .addApiResponse("403", FORBIDDEN_API_RESPONSE)
                );
            }
        });
    }

    private static void addCommonResponses(OpenAPI openApi) {
        openApi.getPaths().forEach((path, pathItem) -> {
            // add 400 and 500 response to all operations
            pathItem.readOperations().forEach(operation -> {
                operation.getResponses().addApiResponse("500",
                        new ApiResponse()
                                .description("internal server error")
                                .content(PROBLEM_DETAILS)
                );
            });
        });
    }
}
