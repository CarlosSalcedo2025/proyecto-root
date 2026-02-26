package org.quind.orderservice.infrastructure.adapter.in.rest.mapper;

import org.quind.orderservice.domain.model.Order;
import org.quind.orderservice.domain.model.OrderItem;
import org.quind.orderservice.infrastructure.adapter.in.rest.dto.OrderItemRequest;
import org.quind.orderservice.infrastructure.adapter.in.rest.dto.OrderRequest;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class OrderRestMapper {

    public Order toDomain(OrderRequest request) {
        return Order.builder()
                .customerId(request.getCustomerId())
                .items(request.getItems().stream()
                        .map(this::toItemDomain)
                        .collect(Collectors.toList()))
                .totalAmount(calculateTotal(request))
                .build();
    }

    private OrderItem toItemDomain(OrderItemRequest itemRequest) {
        return OrderItem.builder()
                .productId(itemRequest.getProductId())
                .quantity(itemRequest.getQuantity())
                .price(itemRequest.getPrice())
                .build();
    }

    private BigDecimal calculateTotal(OrderRequest request) {
        return request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
