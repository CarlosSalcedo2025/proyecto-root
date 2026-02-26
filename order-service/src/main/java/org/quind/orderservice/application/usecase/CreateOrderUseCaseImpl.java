package org.quind.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.in.CreateOrderUseCase;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository eventRepository;

    @Override
    public Mono<Order> create(Order order) {
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity event = org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity
                            .builder()
                            .orderId(savedOrder.getId().toString())
                            .eventType("ORDER_CREATED")
                            .payload(savedOrder)
                            .timestamp(LocalDateTime.now())
                            .build();
                    return eventRepository.save(event)
                            .then(eventPublisher.publishOrderCreated(savedOrder))
                            .thenReturn(savedOrder);
                });
    }
}
