package org.quind.orderservice.infrastructure.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Mono<Void> publishOrderCreated(Order order) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("X-Correlation-ID", UUID.randomUUID().toString());
            return Mono.fromRunnable(() -> {
                log.info("Publishing OrderCreated event [CorrelationID: {}] for order: {}", correlationId,
                        order.getId());
                org.apache.kafka.clients.producer.ProducerRecord<String, Object> record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                        "order-created", order.getId().toString(), order);
                record.headers().add("X-Correlation-ID", correlationId.getBytes());
                kafkaTemplate.send(record);
            });
        }).then();
    }

    @Override
    public Mono<Void> publishOrderCancelled(Order order) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("X-Correlation-ID", UUID.randomUUID().toString());
            return Mono.fromRunnable(() -> {
                log.info("Publishing OrderCancelled event [CorrelationID: {}] for order: {}", correlationId,
                        order.getId());
                org.apache.kafka.clients.producer.ProducerRecord<String, Object> record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                        "order-cancelled", order.getId().toString(), order);
                record.headers().add("X-Correlation-ID", correlationId.getBytes());
                kafkaTemplate.send(record);
            });
        }).then();
    }

    @Override
    public Mono<Void> publishInventoryValidated(Order order) {
        return Mono.deferContextual(ctx -> {
            String correlationId = ctx.getOrDefault("X-Correlation-ID", UUID.randomUUID().toString());
            return Mono.fromRunnable(() -> {
                log.info("Publishing InventoryValidated event [CorrelationID: {}] for order: {}", correlationId,
                        order.getId());
                org.apache.kafka.clients.producer.ProducerRecord<String, Object> record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                        "inventory-validated", order.getId().toString(), order);
                record.headers().add("X-Correlation-ID", correlationId.getBytes());
                kafkaTemplate.send(record);
            });
        }).then();
    }
}
