package org.quind.notificationservice.infrastructure.adapter.out.persistence;

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
@Document(collection = "events")
public class EventLogEntity {
    @Id
    private String id;
    private String eventType;
    private String aggregateId;
    private Object payload;
    private LocalDateTime timestamp;
}
