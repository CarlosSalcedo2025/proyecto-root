# E-commerce Order Management System (Arquitectura de Eventos)

## 🚀 Descripción General
Este proyecto es una implementación de un sistema de gestión de órdenes de alta escala, diseñado bajo los principios de **Arquitectura de Eventos (EDA)**, **Clean Architecture** y **Programación Reactiva**. El sistema orquestra un flujo complejo de pedidos, pagos y notificaciones de manera asíncrona y resiliente.

## 🏗️ Arquitectura Técnica
El sistema se divide en tres microservicios principales que se comunican de forma asíncrona mediante **Apache Kafka**:

1.  **Order Service (Puerto 8080)**: Núcleo del negocio. Gestiona el ciclo de vida de la orden y orquestra la saga coreografiada.
2.  **Payment Service (Puerto 8081)**: Simula una pasarela de pagos con lógica de reintentos y fallos aleatorios para probar la resiliencia.
3.  **Notification Service (Puerto 8082)**: Auditoría y registro de eventos mediante Event Sourcing.

### Stack Tecnológico
- **Java 21** & **Spring Boot 3.4.3**
- **Spring WebFlux** (Reactor) para I/O no bloqueante.
- **Apache Kafka** como backbone de mensajería.
- **PostgreSQL + R2DBC** para persistencia transaccional reactiva.
- **MongoDB Reactive** para Event Store y logs de auditoría.
- **Testcontainers** para pruebas de integración reales.
- **JaCoCo** para métricas de cobertura de código.

## 🛠️ Requisitos Previos
- Docker y Docker Compose.
- Java 21 JDK.
- Maven 3.9+.

## 🏁 Instalación y Ejecución

1.  **Levantar Infraestructura**:
    ```bash
    docker-compose up -d
    ```
    *Esto levantará: PostgreSQL, MongoDB, Kafka (Bitnami) y Kafdrop (UI para Kafka).*

2.  **Compilar y Ejecutar Servicios**:
    Desde la raíz del proyecto, puedes ejecutar cada servicio:
    ```bash
    # En terminales separadas
    cd order-service && mvn spring-boot:run
    cd payment-service && mvn spring-boot:run
    cd notification-service && mvn spring-boot:run
    ```

3.  **Verificar Estado**:
    - Order Health: `http://localhost:8080/actuator/health`
    - Kafka UI (Kafdrop): `http://localhost:9000`

## 🧪 Pruebas y Calidad
### Ejecución de Tests
```bash
mvn test
```

### Reporte de Cobertura (JaCoCo)
Tras ejecutar los tests, el reporte se genera en:
`order-service/target/site/jacoco/index.html`

## 📖 Documentación de APIs (Swagger UI)
Cada servicio cuenta con su propia interfaz de Swagger:
- **Order Service**: [http://localhost:8080/webjars/swagger-ui/index.html](http://localhost:8080/webjars/swagger-ui/index.html)
- **Payment Service**: [http://localhost:8081/webjars/swagger-ui/index.html](http://localhost:8081/webjars/swagger-ui/index.html)
- **Notification Service**: [http://localhost:8082/webjars/swagger-ui/index.html](http://localhost:8082/webjars/swagger-ui/index.html)

### Ejemplos de Uso (Quick Start)
**Crear Orden (`POST /api/v1/orders`)**
```json
{
  "customerId": "user_789",
  "items": [
    { "productId": "PROD_001", "quantity": 2, "price": 50.0 }
  ]
}
```

**Consultar Eventos (`GET /api/v1/orders/{id}/events`)**
Permite ver todo el historial de la Saga (Event Sourcing).

## 🛡️ Decisiones de Diseño (ADRs)
Contamos con registros detallados en `docs/ADRs/`:
- **ADR-001**: Implementación de Clean Architecture.
- **ADR-002**: Selección de Kafka vs RabbitMQ.
- **ADR-003**: Patrón Saga Coreografiado para consistencia eventual.

## 📈 Trazabilidad y Observabilidad
- **Correlation ID**: Todas las peticiones generan un header `X-Correlation-ID` que viaja por Kafka y se registra en los logs JSON.
- **Logs Estructurados**: Salida en formato GELF/JSON optimizada para ELK Stack.

## 🔮 Roadmap y Mejoras Futuras
1.  **API Gateway**: Implementar Spring Cloud Gateway con Rate Limiting.
2.  **Outbox Pattern**: Garantizar atomicidad absoluta entre DB y Kafka.
3.  **Seguridad**: Integrar Keycloak para OAuth2/JWT.
4.  **Circuit Breaker**: Resilience4j para el fallback de la pasarela de pagos.

---
**Desarrollado para Prueba Técnica Senior Java.**
