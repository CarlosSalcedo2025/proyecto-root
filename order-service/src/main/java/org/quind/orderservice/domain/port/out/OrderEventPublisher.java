package org.quind.orderservice.domain.port.out;

import org.quind.orderservice.domain.model.Order;
import reactor.core.publisher.Mono;

public interface OrderEventPublisher {
    Mono<Void> publishOrderCreated(Order order);

    Mono<Void> publishOrderCancelled(Order order);

    Mono<Void> publishInventoryValidated(Order order);
}
