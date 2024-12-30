package com.github.jenkaby.context;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalMessagesStore {

    private final ConcurrentHashMap<String, List<ConsumerRecord<String, Object>>> localMessages = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void receiveMsg(String topic, ConsumerRecord<String, ? extends T> message) {
        localMessages.computeIfAbsent(topic, key-> new ArrayList<>())
                .add((ConsumerRecord<String, Object>) message);
    }

    @SuppressWarnings("unchecked")
    public <T> List<Message<T>> getMessagesOnTopic(String topic, Class<T> cls) {
        return localMessages.getOrDefault(topic, List.of()).stream()
                .map(record -> new Message<>(record.key(), (T) record.value()))
                .toList();
    }

    public void clear() {
        localMessages.clear();
    }

    public record Message<T> (String key, T value){}
}
