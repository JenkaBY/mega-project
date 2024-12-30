package com.github.jenkaby.presentation.controller;


import com.github.jenkaby.messaging.producer.NotificationMessageSender;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/send")
    public String sendToNotificationTopic(@RequestParam("key") String key, @RequestParam("payload") String payload) {
        log.info("GET '/api/kafka/send' request was received. Key={} payload={}", key, payload);
        messageSender.sendNotificationMessage(key, payload);
        return "OK";
    }
}
