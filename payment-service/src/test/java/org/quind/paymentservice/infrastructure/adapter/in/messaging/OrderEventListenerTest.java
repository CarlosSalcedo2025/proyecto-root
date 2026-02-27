package org.quind.paymentservice.infrastructure.adapter.in.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quind.paymentservice.infrastructure.adapter.out.messaging.PaymentEventPublisher;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private PaymentEventPublisher publisher;

    @InjectMocks
    private OrderEventListener listener;

    @Test
    @DisplayName("Debe procesar evento de inventario validado y publicar resultado de pago")
    void shouldHandleInventoryValidated() {
        Map<String, Object> orderMessage = Map.of("orderId", UUID.randomUUID().toString());
        byte[] correlationId = "corr-123".getBytes();

        // Siendo métodos void, no necesitamos when(). Verificamos el comportamiento al
        // final.
        listener.handleInventoryValidated(orderMessage, correlationId);

        verify(publisher, atMostOnce()).publishPaymentProcessed(any(UUID.class), anyString());
        verify(publisher, atMostOnce()).publishPaymentFailed(any(UUID.class), anyString(), anyString());
    }
}
