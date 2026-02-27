package org.quind.orderservice.domain.model;

public enum OrderStatus {
    PENDING, // New
    CONFIRMED, // Inventory validated
    PAYMENT_PROCESSING, // Waiting for payment
    PAID, // Payment success
    SHIPPED, // Shipped to customer
    DELIVERED, // Received by customer
    CANCELLED, // Cancelled
    FAILED // Error in processing
}
