package org.quind.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.in.GetOrderUseCase;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Mono<Order> getById(UUID id) {
        return orderRepository.findById(id);
    }

    @Override
    public Flux<Order> getByCustomerId(String customerId, int page, int size) {
        return orderRepository.findByCustomerId(customerId, page, size);
    }
}
