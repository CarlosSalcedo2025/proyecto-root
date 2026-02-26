package org.quind.orderservice.domain.port.in;

import org.quind.orderservice.domain.model.Order;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface CancelOrderUseCase {
    Mono<Order> cancel(UUID orderId);
}
