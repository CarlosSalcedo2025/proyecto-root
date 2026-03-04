package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY;
import static reactor.core.publisher.Mono.empty;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final MongoOrderEventRepository eventRepository;

    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void handleOrderCreated(
            @Payload Map<String, Object> message,
            @Header(RECEIVED_KEY) String key,
            @Header(value = "X-Correlation-ID", required = false) byte[] correlationIdBytes) {

        String correlationId = (correlationIdBytes != null) ? new String(correlationIdBytes)
                : randomUUID().toString();
        log.info("Received order-created event [CorrelationID: {}] for order: {}", correlationId, key);

        UUID orderId = UUID.fromString(key);

        eventRepository.findByOrderId(key)
                .filter(e -> "ORDER_CONFIRMED".equals(e.getEventType()))
                .hasElements()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Orden {} ya procesada (idempotencia).", key);
                        return empty();
                    }

                    return orderRepository.findById(orderId)
                            .flatMap(order -> {
                                log.info("Validating inventory for order: {}", orderId);
                                order.markAsConfirmed();
                                return orderRepository.save(order);
                            })
                            .flatMap(confirmedOrder -> {
                                OrderEventEntity event = OrderEventEntity
                                        .builder()
                                        .orderId(confirmedOrder.getId().toString())
                                        .eventType("ORDER_CONFIRMED")
                                        .payload(confirmedOrder)
                                        .timestamp(now())
                                        .build();
                                return eventRepository.save(event)
                                        .then(eventPublisher.publishInventoryValidated(confirmedOrder)
                                                .contextWrite(ctx -> ctx.put("X-Correlation-ID", correlationId)));
                            });
                })
                .doOnSuccess(v -> log.info("Confirmación de orden {} completada.", key))
                .subscribe();
    }
}
