package org.quind.paymentservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.paymentservice.infrastructure.adapter.out.messaging.PaymentEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final PaymentEventPublisher publisher;

    @KafkaListener(topics = "inventory-validated", groupId = "payment-group")
    public void handleInventoryValidated(Object orderMessage,
            @Header("X-Correlation-ID") byte[] correlationIdBytes) {
        String correlationId = new String(correlationIdBytes);
        log.info("Received inventory-validated event [CorrelationID: {}]: {}", correlationId, orderMessage);

        String orderIdStr = extractOrderId(orderMessage);
        if (orderIdStr != null) {
            UUID orderId = UUID.fromString(orderIdStr);
            // Simulación: Fallar aleatoriamente 1 de cada 4 veces para probar la Saga
            if (Math.random() > 0.75) {
                publisher.publishPaymentFailed(orderId, correlationId, "Fondos insuficientes o error de pasarela");
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
