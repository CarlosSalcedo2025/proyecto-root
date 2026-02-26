package org.quind.orderservice.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<Void> publishOrderCreated(Order order) {
        return Mono.fromRunnable(() -> {
            log.info("Publishing OrderCreated event for order: {}", order.getId());
            kafkaTemplate.send("order-created", order.getId().toString(), order);
        }).then();
    }

    @Override
    public Mono<Void> publishOrderCancelled(Order order) {
        return Mono.fromRunnable(() -> {
            log.info("Publishing OrderCancelled event for order: {}", order.getId());
            kafkaTemplate.send("order-cancelled", order.getId().toString(), order);
        }).then();
    }
}
