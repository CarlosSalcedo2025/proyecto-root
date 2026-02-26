package org.quind.paymentservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.paymentservice.infrastructure.adapter.out.messaging.PaymentEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final PaymentEventPublisher publisher;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(Object orderMessage) {
        log.info("Received order-created event: {}", orderMessage);

        // Simulation: Always succeed payment for now
        // In a real scenario, we would extract orderId and amount
        // For simplicity, let's assume message is a Map or JSON

        String orderIdStr = extractOrderId(orderMessage);
        if (orderIdStr != null) {
            publisher.publishPaymentProcessed(UUID.fromString(orderIdStr));
        }
    }

    private String extractOrderId(Object message) {
        // Very simplified extraction for simulation
        if (message instanceof Map) {
            return (String) ((Map<?, ?>) message).get("id");
        }
        return null;
    }
}
