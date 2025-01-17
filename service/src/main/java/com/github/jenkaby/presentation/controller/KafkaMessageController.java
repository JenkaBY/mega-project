package com.github.jenkaby.presentation.controller;


import com.github.jenkaby.messaging.producer.NotificationMessageSender;
import com.github.jenkaby.presentation.controller.mapper.TransactionEventMapper;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/kafka")
@RestController
public class KafkaMessageController {

    private static final String KAFKA_HEADER = "kafka_header.";
    private final NotificationMessageSender messageSender;
    private final TransactionEventMapper transactionEventMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/send")
    public String sendToNotificationTopic(@RequestParam("key") String key, @RequestParam("payload") String payload) {
        log.info("GET '/api/kafka/send' request was received. Key={} payload={}", key, payload);
        messageSender.sendNotificationMessage(key, payload);
        return "OK";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{topic-name}/json")
    public String sendToJsonTransactionTopic(@PathVariable("topic-name") String topic,
                                             @RequestParam("key") String key,
                                             @RequestParam(required = false) MultiValueMap<String, String> requestParams,
                                             @RequestBody TransactionEventRequest txnDto) {
        log.info("POST '/api/kafka/{}/json' request was received. Params={} payload={}", topic, requestParams, txnDto);
        var kafkaHeaders = requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(KAFKA_HEADER))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().substring(KAFKA_HEADER.length()),
                        entry -> entry.getValue().getLast()
                ));
        log.info("Kafka headers for JSON: {}", kafkaHeaders);
        messageSender.sendMessageToTopic(topic, kafkaHeaders, key, transactionEventMapper.toEntity(txnDto));
        return "OK";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{topic-name}/avro")
    public String sendToAvroTransactionTopic(@PathVariable("topic-name") String topic,
                                             @RequestParam("key") String key,
                                             @RequestParam(value = "shouldSkip", required = false) String skip,
                                             @RequestBody TransactionEventRequest txnDto) {
        log.info("POST '/api/kafka/{}/avro' request was received. Key={} payload={}", topic, key, txnDto);

        messageSender.sendMessageToTopic(topic, key, transactionEventMapper.toAvro(txnDto, this.getClass().getCanonicalName()));
        return "OK";
    }
}
