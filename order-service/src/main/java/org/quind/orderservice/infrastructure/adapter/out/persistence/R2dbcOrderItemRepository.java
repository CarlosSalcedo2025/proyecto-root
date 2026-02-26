package org.quind.orderservice.infrastructure.adapter.out.persistence;

import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface R2dbcOrderItemRepository extends ReactiveCrudRepository<OrderItemEntity, Long> {
    Flux<OrderItemEntity> findByOrderId(UUID orderId);
}
