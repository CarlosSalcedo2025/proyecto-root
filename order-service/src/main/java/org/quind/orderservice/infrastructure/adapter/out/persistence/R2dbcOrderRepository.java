package org.quind.orderservice.infrastructure.adapter.out.persistence;

import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;

import reactor.core.publisher.Flux;
import java.util.UUID;

public interface R2dbcOrderRepository
        extends org.springframework.data.repository.reactive.ReactiveCrudRepository<OrderEntity, UUID> {
    Flux<OrderEntity> findByCustomerId(String customerId, org.springframework.data.domain.Pageable pageable);
}
