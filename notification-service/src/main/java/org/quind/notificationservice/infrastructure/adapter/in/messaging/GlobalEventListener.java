package org.quind.notificationservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.notificationservice.infrastructure.adapter.out.persistence.EventLogEntity;
import org.quind.notificationservice.infrastructure.adapter.out.persistence.MongoEventLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalEventListener {

    private final MongoEventLogRepository repository;

    @KafkaListener(topics = { "order-created", "order-cancelled", "payment-processed",
            "payment-failed" }, groupId = "notification-group")
    public void handleEvents(Object message,
            @org.springframework.messaging.handler.annotation.Header(org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Notification Service received event from topic {}: {}", topic, message);

        EventLogEntity eventLog = EventLogEntity.builder()
                .eventType(topic)
                .aggregateId(extractAggregateId(message))
                .payload(message)
                .timestamp(LocalDateTime.now())
                .build();

        repository.save(eventLog).subscribe();
    }

    private String extractAggregateId(Object message) {
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            if (map.containsKey("id"))
                return map.get("id").toString();
            if (map.containsKey("orderId"))
                return map.get("orderId").toString();
        }
        return "unknown";
    }
}
