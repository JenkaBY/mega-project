package com.github.jenkaby.service.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ClientDelayServiceContext {

    private final Map<String, ClientDelayService> context;

    public ClientDelayServiceContext(List<ClientDelayService> implementations) {
        this.context = implementations.stream()
                .peek(impl -> log.debug("** {}", impl.getClass()))
                .collect(Collectors.toMap(ClientDelayService::getImplementationType, Function.identity()));
    }

    public ClientDelayService getByType(String type) {
        return Optional.ofNullable(context.get(type))
                .orElseThrow(() -> new RuntimeException("Implementation for %s type not found"));
    }
}
