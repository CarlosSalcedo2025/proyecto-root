package org.quind.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Debe crear una orden con estado PENDING")
    void shouldCreateOrderWithPendingStatus() {
        Order order = Order.builder()
                .customerId("customer1")
                .items(List.of(new OrderItem("prod1", 2, new BigDecimal("10.0"))))
                .build();

        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertTrue(new BigDecimal("20.0").compareTo(order.getTotalAmount()) == 0);
    }

    @Test
    @DisplayName("Debe fallar al marcar como confirmado si no está PENDING")
    void shouldFailMarkAsConfirmedIfNotPending() {
        Order order = Order.builder()
                .status(OrderStatus.CANCELLED)
                .build();

        assertThrows(IllegalStateException.class, order::markAsConfirmed);
    }

    @Test
    @DisplayName("Debe pasar de PENDING a CONFIRMED")
    void shouldTransitionToConfirmed() {
        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        order.markAsConfirmed();
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("Debe pasar a PAID solo si está CONFIRMED")
    void shouldTransitionToPaidOnlyIfConfirmed() {
        Order order = Order.builder()
                .status(OrderStatus.CONFIRMED)
                .build();

        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        Order pendingOrder = Order.builder().status(OrderStatus.PENDING).build();
        assertThrows(IllegalStateException.class, pendingOrder::markAsPaid);
    }

    @Test
    @DisplayName("Debe realizar el flujo completo: CONFIRMED -> PAID -> SHIPPED -> DELIVERED")
    void shouldFollowFullLifecycle() {
        Order order = Order.builder().status(OrderStatus.CONFIRMED).build();

        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.markAsShipped();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        order.markAsDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
}
