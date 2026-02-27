package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository eventRepository;

    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void handleOrderCreated(Object message, @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header("X-Correlation-ID") byte[] correlationIdBytes) {
        String correlationId = new String(correlationIdBytes);
        log.info("Received order-created event [CorrelationID: {}] for order: {}", correlationId, key);

        UUID orderId = UUID.fromString(key);

        eventRepository.findByOrderId(key)
                .filter(e -> "ORDER_CONFIRMED".equals(e.getEventType()))
                .hasElements()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Orden {} ya procesada (idempotencia).", key);
                        return reactor.core.publisher.Mono.empty();
                    }

                    return orderRepository.findById(orderId)
                            .flatMap(order -> {
                                log.info("Validating inventory for order: {}", orderId);
                                order.markAsConfirmed();
                                return orderRepository.save(order);
                            })
                            .flatMap(confirmedOrder -> {
                                org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity event = org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity
                                        .builder()
                                        .orderId(confirmedOrder.getId().toString())
                                        .eventType("ORDER_CONFIRMED")
                                        .payload(confirmedOrder)
                                        .timestamp(java.time.LocalDateTime.now())
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
