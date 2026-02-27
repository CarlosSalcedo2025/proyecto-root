package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository eventRepository;

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    public void handlePaymentProcessed(Object message,
            @org.springframework.messaging.handler.annotation.Header("X-Correlation-ID") byte[] correlationIdBytes) {
        String correlationId = new String(correlationIdBytes);
        log.info("Recibido payment-processed [CorrelationID: {}]", correlationId);

        String orderIdStr = extractOrderId(message);
        if (orderIdStr != null) {
            updateOrderStatus(UUID.fromString(orderIdStr), OrderStatus.PAID, "PAYMENT_CONFIRMED", correlationId);
        }
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(Object message,
            @org.springframework.messaging.handler.annotation.Header("X-Correlation-ID") byte[] correlationIdBytes) {
        String correlationId = new String(correlationIdBytes);
        log.error("Recibido payment-failed [CorrelationID: {}]", correlationId);

        String orderIdStr = extractOrderId(message);
        if (orderIdStr != null) {
            updateOrderStatus(UUID.fromString(orderIdStr), OrderStatus.FAILED, "PAYMENT_FAILED", correlationId);
        }
    }

    private void updateOrderStatus(UUID orderId, OrderStatus status, String eventType, String correlationId) {
        eventRepository.findByOrderId(orderId.toString())
                .filter(e -> eventType.equals(e.getEventType()))
                .hasElements()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Evento {} ya procesado para orden {}. Saltando.", eventType, orderId);
                        return reactor.core.publisher.Mono.empty();
                    }

                    return orderRepository.findById(orderId)
                            .flatMap(order -> {
                                if (status == OrderStatus.PAID) {
                                    order.markAsPaid();
                                } else {
                                    order.markAsFailed();
                                }
                                return orderRepository.save(order);
                            })
                            .flatMap(savedOrder -> {
                                org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity event = org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity
                                        .builder()
                                        .orderId(savedOrder.getId().toString())
                                        .eventType(eventType)
                                        .payload(savedOrder)
                                        .timestamp(java.time.LocalDateTime.now())
                                        .build();
                                return eventRepository.save(event);
                            });
                })
                .doOnSuccess(v -> log.info("Actualización de orden {} a {} completada.", orderId, status))
                .subscribe();
    }

    private String extractOrderId(Object message) {
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            return (String) map.get("orderId");
        }
        return null;
    }
}
