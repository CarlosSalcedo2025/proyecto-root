package org.quind.orderservice.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Order {
    private UUID id;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void cancel() {
        if (this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.CANCELLED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Order can only be cancelled in PENDING or CONFIRMED status");
        }
    }
}
