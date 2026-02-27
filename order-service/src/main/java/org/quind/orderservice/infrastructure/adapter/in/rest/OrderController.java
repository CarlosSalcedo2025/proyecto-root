package org.quind.orderservice.infrastructure.adapter.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.in.CancelOrderUseCase;
import org.quind.orderservice.domain.port.in.CreateOrderUseCase;
import org.quind.orderservice.domain.port.in.GetOrderUseCase;
import org.quind.orderservice.infrastructure.adapter.in.rest.dto.OrderRequest;
import org.quind.orderservice.infrastructure.adapter.in.rest.mapper.OrderRestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository eventRepository;
    private final OrderRestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        return createOrderUseCase.create(mapper.toDomain(request));
    }

    @GetMapping("/{id}")
    public Mono<Order> getOrder(@PathVariable UUID id) {
        return getOrderUseCase.getById(id);
    }

    @GetMapping
    public reactor.core.publisher.Flux<Order> listOrders(
            @RequestParam String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getOrderUseCase.getByCustomerId(customerId, page, size);
    }

    @PatchMapping("/{id}/cancel")
    public Mono<Order> cancelOrder(@PathVariable UUID id) {
        return cancelOrderUseCase.cancel(id);
    }

    @GetMapping("/{id}/events")
    public reactor.core.publisher.Flux<org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEventEntity> getEvents(
            @PathVariable String id) {
        return eventRepository.findByOrderId(id);
    }
}
