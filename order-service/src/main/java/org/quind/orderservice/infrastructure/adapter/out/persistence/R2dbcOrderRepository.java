package org.quind.orderservice.infrastructure.adapter.out.persistence;

import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface R2dbcOrderRepository extends ReactiveCrudRepository<OrderEntity, UUID> {
    Flux<OrderEntity> findByCustomerId(String customerId);
}
