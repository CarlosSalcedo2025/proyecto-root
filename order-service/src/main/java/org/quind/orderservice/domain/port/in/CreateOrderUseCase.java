package org.quind.orderservice.domain.port.in;

import org.quind.orderservice.domain.model.Order;
import reactor.core.publisher.Mono;

public interface CreateOrderUseCase {
    Mono<Order> create(Order order);
}
