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

    public void publishPaymentProcessed(UUID orderId, String correlationId) {
        log.info("Publishing payment-processed [CorrelationID: {}] for order: {}", correlationId, orderId);
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId.toString());
        event.put("status", "SUCCESS");

        org.apache.kafka.clients.producer.ProducerRecord<String, Object> record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                "payment-processed", orderId.toString(), event);
        record.headers().add("X-Correlation-ID", correlationId.getBytes());

        kafkaTemplate.send(record);
    }

    public void publishPaymentFailed(UUID orderId, String correlationId, String reason) {
        log.error("Publicando payment-failed [CorrelationID: {}] para la orden: {}. Razón: {}", correlationId, orderId,
                reason);
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId.toString());
        event.put("status", "FAILED");
        event.put("reason", reason);

        org.apache.kafka.clients.producer.ProducerRecord<String, Object> record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                "payment-failed", orderId.toString(), event);
        record.headers().add("X-Correlation-ID", correlationId.getBytes());

        kafkaTemplate.send(record);
    }
}
