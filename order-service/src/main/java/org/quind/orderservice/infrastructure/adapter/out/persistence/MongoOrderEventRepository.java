package org.quind.orderservice.infrastructure.adapter.out.persistence;

import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MongoOrderEventRepository extends ReactiveMongoRepository<OrderEventEntity, String> {
    Flux<OrderEventEntity> findByOrderId(String orderId);
}
