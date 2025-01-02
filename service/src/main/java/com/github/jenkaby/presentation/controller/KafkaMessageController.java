package com.github.jenkaby.presentation.controller;


import com.github.jenkaby.messaging.producer.NotificationMessageSender;
import com.github.jenkaby.presentation.controller.mapper.TransactionEventMapper;
import com.github.jenkaby.presentation.controller.model.TransactionEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/kafka")
@RestController
public class KafkaMessageController {

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
    @PostMapping("/{topic-name}")
    public String sendToJsonTransactionTopic(@PathVariable("topic-name") String topic,
                                             @RequestParam("key") String key,
                                             @RequestBody TransactionEventRequest txnDto) {
        log.info("POST '/api/kafka/{}' request was received. Key={} payload={}",topic, key, txnDto);
        messageSender.sendMessageToTopic(topic, key, transactionEventMapper.toEntity(txnDto));
        return "OK";
    }
}
