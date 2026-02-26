package org.quind.orderservice.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_events")
public class OrderEventEntity {
    @Id
    private String id;
    private String orderId;
    private String eventType;
    private Object payload;
    private LocalDateTime timestamp;
}
