package org.quind.orderservice.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.quind.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepository {

        private final R2dbcOrderRepository repository;
        private final R2dbcOrderItemRepository itemRepository;

        @Override
        public Mono<Order> save(Order order) {
                return repository.existsById(order.getId())
                                .flatMap(exists -> {
                                        OrderEntity entity = OrderEntity.builder()
                                                        .id(order.getId())
                                                        .customerId(order.getCustomerId())
                                                        .totalAmount(order.getTotalAmount())
                                                        .status(order.getStatus())
                                                        .createdAt(order.getCreatedAt())
                                                        .updatedAt(order.getUpdatedAt())
                                                        .isNew(!exists)
                                                        .build();

                                        return repository.save(entity)
                                                        .flatMap(savedOrder -> {
                                                                if (exists) {
                                                                        return Mono.just(order);
                                                                }
                                                                Flux<OrderItemEntity> itemEntities = Flux
                                                                                .fromIterable(order.getItems() == null
                                                                                                ? java.util.Collections
                                                                                                                .emptyList()
                                                                                                : order.getItems())
                                                                                .map(item -> OrderItemEntity.builder()
                                                                                                .orderId(savedOrder
                                                                                                                .getId())
                                                                                                .productId(item.getProductId())
                                                                                                .quantity(item.getQuantity())
                                                                                                .price(item.getPrice())
                                                                                                .build());
                                                                // Assuming items are replacing old ones, but to be
                                                                // simple we save them. Note: In real scenarios,
                                                                // handling item updates would be more complex.
                                                                return itemRepository.saveAll(itemEntities)
                                                                                .collectList()
                                                                                .thenReturn(order);
                                                        });
                                });
        }

        @Override
        public Mono<Order> findById(UUID id) {
                return repository.findById(id)
                                .flatMap(e -> itemRepository.findByOrderId(e.getId()).collectList()
                                                .map(items -> Order.builder()
                                                                .id(e.getId())
                                                                .customerId(e.getCustomerId())
                                                                .status(e.getStatus())
                                                                .createdAt(e.getCreatedAt())
                                                                .updatedAt(e.getUpdatedAt())
                                                                .items(items.stream()
                                                                                .map(i -> org.quind.orderservice.domain.model.OrderItem
                                                                                                .builder()
                                                                                                .productId(i.getProductId())
                                                                                                .quantity(i.getQuantity())
                                                                                                .price(i.getPrice())
                                                                                                .build())
                                                                                .collect(java.util.stream.Collectors
                                                                                                .toList()))
                                                                .build()));
        }

        @Override
        public Flux<Order> findByCustomerId(String customerId, int page, int size) {
                return repository
                                .findByCustomerId(customerId,
                                                org.springframework.data.domain.PageRequest.of(page, size))
                                .flatMap(e -> findById(e.getId()));
        }
}
