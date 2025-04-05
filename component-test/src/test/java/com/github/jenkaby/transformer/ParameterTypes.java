package com.github.jenkaby.transformer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenkaby.service.support.ProxyType;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;
import io.cucumber.java.ParameterType;
import io.micrometer.core.instrument.Tag;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterTypes {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object transformer(Object fromValue, Type toValueType) {
        return objectMapper.convertValue(fromValue, objectMapper.constructType(toValueType));
    }

    @ParameterType("GET|POST|PUT|PATCH|DELETE")
    public HttpMethod httpMethod(String methodName) {
        return HttpMethod.valueOf(methodName);
    }

    @ParameterType("JDK_DYNAMIC|CGLIB|NONE")
    public ProxyType proxyType(String type) {
        return ProxyType.valueOf(type);
    }

    @SneakyThrows
    @ParameterType(".*")
    public List<Tag> tags(String json) {
        if (json.isBlank()) {
            return List.of();
        }
        Map<String, String> jsonMap = objectMapper.readValue(json, new TypeReference<HashMap<String, String>>() {
        });
        return jsonMap.entrySet()
                .stream().map(e -> Tag.of(e.getKey(), e.getValue()))
                .toList();
    }
}
