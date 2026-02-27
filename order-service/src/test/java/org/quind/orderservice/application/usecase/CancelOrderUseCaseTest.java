package org.quind.orderservice.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private CancelOrderUseCaseImpl useCase;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Debe cancelar una orden en estado PENDING")
    void shouldCancelPendingOrder() {
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.PENDING)
                .build();

        when(repository.findById(orderId)).thenReturn(Mono.just(order));
        when(repository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(eventPublisher.publishOrderCancelled(any(Order.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.cancel(orderId))
                .expectNextMatches(o -> o.getStatus() == OrderStatus.CANCELLED)
                .verifyComplete();
    }

    @Test
    @DisplayName("No debe cancelar una orden si ya está enviada")
    void shouldNotCancelShippedOrder() {
        Order order = Order.builder()
                .id(orderId)
                .status(OrderStatus.SHIPPED)
                .build();

        when(repository.findById(orderId)).thenReturn(Mono.just(order));

        StepVerifier.create(useCase.cancel(orderId))
                .expectError(IllegalStateException.class)
                .verify();
    }
}
