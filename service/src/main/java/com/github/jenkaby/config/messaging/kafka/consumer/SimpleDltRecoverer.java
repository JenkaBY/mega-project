package com.github.jenkaby.config.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;

@Slf4j
@RequiredArgsConstructor
public class SimpleDltRecoverer implements ConsumerRecordRecoverer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        log.error("Couldn't process message. Key = {}, message = {}, header = {}" +
                        " because of '{}'",
                consumerRecord.key(),
                consumerRecord.value(),
                consumerRecord.headers(),
                e.getMessage()
        );
//        TODO should be Dlt topic resolver strategy here
        var dltName = consumerRecord.topic() + ".dlt";
        var sendRes = kafkaTemplate.send(dltName,
                consumerRecord.key(), consumerRecord.value());

        sendRes.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message DLTed {}", result);
                return;
            }
            log.error("Let ignore this {}", ex.toString());
        });
    }
}
