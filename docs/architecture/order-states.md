```mermaid
stateDiagram-v2
    [*] --> PENDING: Crear Orden
    PENDING --> CONFIRMED: Inventario Validado
    PENDING --> CANCELLED: Usuario Cancela
    
    CONFIRMED --> PAYMENT_PROCESSING: Iniciar Pago
    CONFIRMED --> CANCELLED: Usuario Cancela
    
    PAYMENT_PROCESSING --> PAID: Pago Exitoso
    PAYMENT_PROCESSING --> FAILED: Pago Rechazado
    
    PAID --> SHIPPED: Logística Despacha
    SHIPPED --> DELIVERED: Cliente Recibe
    
    FAILED --> [*]
    CANCELLED --> [*]
    DELIVERED --> [*]
```
