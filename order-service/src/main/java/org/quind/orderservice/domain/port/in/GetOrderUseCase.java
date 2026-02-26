package org.quind.orderservice.domain.port.in;

import org.quind.orderservice.domain.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface GetOrderUseCase {
    Mono<Order> getById(UUID id);

    Flux<Order> getByCustomerId(String customerId, int page, int size);
}
