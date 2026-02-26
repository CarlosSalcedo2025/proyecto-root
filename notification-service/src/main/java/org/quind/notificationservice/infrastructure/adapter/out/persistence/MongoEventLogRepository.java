package org.quind.notificationservice.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MongoEventLogRepository extends ReactiveMongoRepository<EventLogEntity, String> {
    Flux<EventLogEntity> findByAggregateId(String aggregateId);
}
