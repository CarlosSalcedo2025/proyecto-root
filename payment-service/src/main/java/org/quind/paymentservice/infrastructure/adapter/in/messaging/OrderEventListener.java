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

    @KafkaListener(topics = "inventory-validated", groupId = "payment-group")
    public void handleInventoryValidated(
            @org.springframework.messaging.handler.annotation.Payload java.util.Map<String, Object> orderMessage,
            @org.springframework.messaging.handler.annotation.Header(value = "X-Correlation-ID", required = false) byte[] correlationIdBytes) {
        String correlationId = (correlationIdBytes != null) ? new String(correlationIdBytes)
                : java.util.UUID.randomUUID().toString();
        log.info("Received inventory-validated event [CorrelationID: {}]: {}", correlationId, orderMessage);

        String orderIdStr = extractOrderId(orderMessage);
        if (orderIdStr != null) {
            UUID orderId = UUID.fromString(orderIdStr);
            // Deterministic trigger: If customerId is "FAIL_USER", always fail.
            // Otherwise, keep the 25% random chance for Saga testing.
            String customerId = (String) orderMessage.get("customerId");

            if ("FAIL_USER".equalsIgnoreCase(customerId)) {
                log.warn("FORCED FAILURE: Failing payment for customer FAIL_USER [OrderId: {}]", orderId);
                publisher.publishPaymentFailed(orderId, correlationId, "Transacción declinada por el banco (Simulada)");
            } else if (Math.random() > 0.75) {
                log.warn("RANDOM FAILURE: Failing payment for testing purposes [OrderId: {}]", orderId);
                publisher.publishPaymentFailed(orderId, correlationId, "Fondos insuficientes (Simulado)");
            } else {
                publisher.publishPaymentProcessed(orderId, correlationId);
            }
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
