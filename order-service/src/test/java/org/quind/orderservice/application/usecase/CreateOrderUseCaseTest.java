package org.quind.orderservice.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.out.OrderEventPublisher;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.MongoOrderEventRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderEventPublisher eventPublisher;
    @Mock
    private MongoOrderEventRepository eventRepository;

    private CreateOrderUseCaseImpl createOrderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createOrderUseCase = new CreateOrderUseCaseImpl(orderRepository, eventPublisher, eventRepository);
    }

    @Test
    void createOrder_shouldReturnSavedOrderAndPublishEvent() {
        // Arrange
        Order order = Order.builder()
                .customerId("customer-1")
                .items(new ArrayList<>())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(eventRepository.save(any())).thenReturn(Mono.empty());
        when(eventPublisher.publishOrderCreated(any(Order.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(createOrderUseCase.create(order))
                .expectNextMatches(savedOrder -> savedOrder.getStatus() == OrderStatus.PENDING &&
                        savedOrder.getCustomerId().equals("customer-1"))
                .verifyComplete();
    }
}
