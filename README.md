# PROYECTO: Sistema de Gestión de Órdenes E-commerce

Este proyecto implementa un sistema distribuido para la gestión de órdenes, siguiendo una arquitectura orientada a eventos, programación reactiva y Clean Architecture.

## Arquitectura

Se ha implementado **Clean Architecture (Hexagonal)** para asegurar el desacoplamiento entre la lógica de negocio y la infraestructura.

### Tecnologías
- **Java 21**
- **Spring Boot 3.4.3**
- **Spring WebFlux** (Programación Reactiva)
- **Apache Kafka** (Broker de Mensajería)
- **PostgreSQL + R2DBC** (Persistencia Transaccional)
- **MongoDB Reactive** (Event Store y Auditoría)
- **Docker & Docker Compose**

### Microservicios
1. **Order Service (8080)**: Gestión de ciclo de vida de órdenes.
2. **Payment Service (8081)**: Simulación de pagos asíncronos.
3. **Notification Service (8082)**: Registro de eventos y auditoría.

## Cómo ejecutar

1. Clona el repositorio.
2. Asegúrate de tener Docker instalado.
3. Ejecuta el entorno de infraestructura:
   ```bash
   docker-compose up -d
   ```
4. Ejecuta cada microservicio:
   ```bash
   mvn spring-boot:run
   ```

## Endpoints Principales

### Order Service
- `POST /api/v1/orders`: Crear orden.
- `GET /api/v1/orders/{id}`: Consultar orden.
- `GET /api/v1/orders?customerId=X&page=0&size=10`: Listar órdenes (con paginación).
- `PATCH /api/v1/orders/{id}/cancel`: Cancelar orden.
- `GET /api/v1/orders/{id}/events`: Historial de eventos (Auditoría/Event Sourcing).

### Payment Service
- `GET /api/v1/payments/{orderId}`: Consultar estado del pago.
- `POST /api/v1/payments/{orderId}/retry`: Reintentar pago fallido.

### Notification Service
- `GET /api/v1/notifications?orderId=X`: Historial de notificaciones enviadas.


## Documentación adicional
- **ADRs**: Ubicados en `docs/ADRs/`.
- **Diagramas (Mermaid)**: Ubicados en `docs/architecture/` (Componentes, Flujo, Estados, DB).
- **Postman**: Colección disponible en `docs/api/postman-collection.json`.

