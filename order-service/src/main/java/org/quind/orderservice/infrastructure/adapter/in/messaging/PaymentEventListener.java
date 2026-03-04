package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import reactor.core.publisher.Mono;
import static java.time.Duration.ofSeconds;
import static reactor.core.publisher.Mono.delay;
import static reactor.core.publisher.Mono.just;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final MongoOrderEventRepository eventRepository;

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    public void handlePaymentProcessed(
            @Payload Map<String, Object> message,
            @Header(value = "X-Correlation-ID", required = false) byte[] correlationIdBytes) {
        String correlationId = (correlationIdBytes != null) ? new String(correlationIdBytes)
                : UUID.randomUUID().toString();
        log.info("Recibido payment-processed [CorrelationID: {}]", correlationId);

        String orderIdStr = extractOrderId(message);
        if (orderIdStr != null) {
            updateOrderStatus(UUID.fromString(orderIdStr), OrderStatus.PAID, "PAYMENT_CONFIRMED", correlationId);
        }
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(
            @Payload Map<String, Object> message,
            @Header(value = "X-Correlation-ID", required = false) byte[] correlationIdBytes) {
        String correlationId = (correlationIdBytes != null) ? new String(correlationIdBytes)
                : UUID.randomUUID().toString();
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
                        return Mono.empty();
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
                                    return delay(ofSeconds(2))
                                            .then(orderRepository.findById(orderId))
                                            .flatMap(o -> {
                                                o.markAsShipped();
                                                return orderRepository.save(o);
                                            })
                                            .flatMap(shipped -> saveEvent(shipped, "ORDER_SHIPPED").thenReturn(shipped))
                                            .delayElement(ofSeconds(2))
                                            .flatMap(shipped -> {
                                                shipped.markAsDelivered();
                                                return orderRepository.save(shipped);
                                            })
                                            .flatMap(delivered -> saveEvent(delivered, "ORDER_DELIVERED")
                                                    .thenReturn(delivered));
                                }
                                return just(order);
                            });
                })
                .doOnSuccess(v -> log.info("Actualización de flujo de orden {} completada.", orderId))
                .subscribe();
    }

    private Mono<OrderEventEntity> saveEvent(
            Order order, String eventType) {
        OrderEventEntity event = OrderEventEntity
                .builder()
                .orderId(order.getId().toString())
                .eventType(eventType)
                .payload(order)
                .timestamp(LocalDateTime.now())
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
