package org.quind.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.in.CancelOrderUseCase;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCaseImpl implements CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Override
    public Mono<Order> cancel(UUID orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found")))
                .map(order -> {
                    order.cancel();
                    return order;
                })
                .flatMap(orderRepository::save)
                .flatMap(cancelledOrder -> eventPublisher.publishOrderCancelled(cancelledOrder)
                        .thenReturn(cancelledOrder));
    }
}
