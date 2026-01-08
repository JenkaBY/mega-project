package com.github.jenkaby.config.security.support;

import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;

/**
 * This class is a custom implementation of the ResourceRetriever interface
 * that uses Spring's <bold>RestTemplate to fetch resources</bold> from a given URL.
 */
public class WebClientResourceRetriever implements ResourceRetriever {

    private final RestTemplate webClient;

    public WebClientResourceRetriever(RestTemplate webClient) {
        this.webClient = webClient;
    }

    @Override
    public Resource retrieveResource(URL url) throws IOException {
        try {
            String body = webClient.getForObject(url.toURI(), String.class);

            return new Resource(body, MediaType.APPLICATION_JSON_VALUE);
        } catch (Exception e) {
            throw new IOException("Failed to retrieve JWKS from " + url, e);
        }
    }
}

