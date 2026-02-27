package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.Order;
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
                                String eventName = (status == OrderStatus.PAID) ? "ORDER_PAID" : "PAYMENT_FAILED";
                                return saveEvent(savedOrder, eventName)
                                        .thenReturn(savedOrder);
                            })
                            .flatMap(order -> {
                                if (order.getStatus() == OrderStatus.PAID) {
                                    return reactor.core.publisher.Mono.delay(java.time.Duration.ofSeconds(2))
                                            .then(orderRepository.findById(orderId))
                                            .flatMap(o -> {
                                                o.markAsShipped();
                                                return orderRepository.save(o);
                                            })
                                            .flatMap(shipped -> saveEvent(shipped, "ORDER_SHIPPED").thenReturn(shipped))
                                            .delayElement(java.time.Duration.ofSeconds(2))
                                            .flatMap(shipped -> {
                                                shipped.markAsDelivered();
                                                return orderRepository.save(shipped);
                                            })
                                            .flatMap(delivered -> saveEvent(delivered, "ORDER_DELIVERED")
                                                    .thenReturn(delivered));
                                }
                                return reactor.core.publisher.Mono.just(order);
                            });
                })
                .doOnSuccess(v -> log.info("Actualización de flujo de orden {} completada.", orderId))
                .subscribe();
    }

    private reactor.core.publisher.Mono<org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity> saveEvent(
            Order order, String eventType) {
        org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity event = org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity
                .builder()
                .orderId(order.getId().toString())
                .eventType(eventType)
                .payload(order)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return eventRepository.save(event);
    }

    private String extractOrderId(Object message) {
        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            return (String) map.get("orderId");
        }
        return null;
    }
}
