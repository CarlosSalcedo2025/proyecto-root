package org.quind.paymentservice.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentProcessed(UUID orderId) {
        log.info("Publishing payment-processed for order: {}", orderId);
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId.toString());
        event.put("status", "SUCCESS");

        kafkaTemplate.send("payment-processed", orderId.toString(), event);
    }
}
