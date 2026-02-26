package org.quind.paymentservice.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    @GetMapping("/{orderId}")
    public Mono<Map<String, Object>> getPaymentStatus(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "SUCCESS"); // Simulated
        response.put("method", "CREDIT_CARD");
        return Mono.just(response);
    }

    @PostMapping("/{orderId}/retry")
    public Mono<Map<String, Object>> retryPayment(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("message", "Retry initiated");
        return Mono.just(response);
    }
}
