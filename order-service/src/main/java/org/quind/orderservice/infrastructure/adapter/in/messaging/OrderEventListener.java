package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.OrderStatus;
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

    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void handleOrderCreated(Object message, @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header("X-Correlation-ID") byte[] correlationIdBytes) {
        String correlationId = new String(correlationIdBytes);
        log.info("Received order-created event [CorrelationID: {}] for order: {}", correlationId, key);

        UUID orderId = UUID.fromString(key);

        orderRepository.findById(orderId)
                .flatMap(order -> {
                    log.info("Validating inventory for order: {}", orderId);
                    // Simulation: Always validate inventory successfully
                    order.setStatus(OrderStatus.CONFIRMED);
                    return orderRepository.save(order);
                })
                .flatMap(confirmedOrder -> eventPublisher.publishInventoryValidated(confirmedOrder)
                        .contextWrite(ctx -> ctx.put("X-Correlation-ID", correlationId)))
                .doOnSuccess(v -> log.info("Inventory validated and Published for order: {}", orderId))
                .subscribe();
    }
}
