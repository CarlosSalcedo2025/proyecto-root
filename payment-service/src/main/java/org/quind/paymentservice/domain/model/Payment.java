package org.quind.paymentservice.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class Payment {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
}
