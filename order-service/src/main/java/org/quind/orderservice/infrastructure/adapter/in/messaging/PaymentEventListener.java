package org.quind.orderservice.infrastructure.adapter.in.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quind.orderservice.domain.model.OrderStatus;
import org.quind.orderservice.domain.port.out.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    public void handlePaymentProcessed(Object message) {
        log.info("Order Service received payment-processed event: {}", message);

        if (message instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) message;
            String orderIdStr = (String) map.get("orderId");
            if (orderIdStr != null) {
                UUID orderId = UUID.fromString(orderIdStr);
                orderRepository.findById(orderId)
                        .flatMap(order -> {
                            order.setStatus(OrderStatus.PAID);
                            return orderRepository.save(order);
                        })
                        .doOnSuccess(order -> log.info("Order {} updated to PAID", orderId))
                        .subscribe();
            }
        }
    }
}
