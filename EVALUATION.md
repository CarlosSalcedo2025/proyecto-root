# EVALUATION.md - Autoevaluación de la Prueba Técnica

## 1. Funcionalidades Completadas
- [x] **Gestión de Órdenes**: Creación reactiva, consulta por ID, **listado por cliente con paginación** y cancelación con validación de dominio.
- [x] **Arquitectura Orientada a Eventos**: Flujo asíncrono completo (`OrderCreated` -> `InventoryValidated` -> `PaymentProcessed` -> `Shipped` -> `Delivered`).
- [x] **Respeto a Clean Architecture**: Separación estricta de capas Domain, Application e Infrastructure con inversión de dependencias.
- [x] **Programación Reactiva**: Uso de Spring WebFlux, Project Reactor y R2DBC. Manejo de backpressure natural de flujos reactivos.
- [x] **Event Sourcing & Auditoría**: Auditoría de eventos en MongoDB con **Correlación ID** para trazabilidad total.
- [x] **Observabilidad**: Logs estructurados en **JSON** y Health Checks con Spring Boot Actuator.
- [x] **Idempotencia**: Implementada en todos los consumidores de Kafka para evitar duplicados.
- [x] **Documentación**: ADRs, Diagramas Mermaid (C4, ER, Sequence, State), Especificaciones OpenAPI y Colección de Postman.

## 2. Funcionalidades Pendientes / Mejoras
- **Circuit Breaker**: Se considera para mejorar la resiliencia entre servicios.
- **Tests de Integración con Testcontainers**: Pendientes de implementación tras completar el core.
- **API Gateway**: Integración de un punto único de entrada.

## 3. Decisiones con más tiempo
- Implementaría el **Pattern Outbox** para garantizar la atomicidad entre la DB R2DBC y Kafka.
- Añadiría seguridad con **Spring Security (OAuth2/JWT)**.
- Implementaría **Kafka Streams** para analíticas en tiempo real de las órdenes.

## 4. Desafíos enfrentados
- **Propagación del Contexto Reactivo**: Mantener el `Correlation ID` a través de hilos y saltos asíncronos de Kafka requirió el uso de `Mono.deferContextual`.
- **Saga en Coreografía**: Manejar el estado de la orden de forma distribuida sin un orquestador central requiere que cada servicio sea consciente de su rol y de la idempotencia.

## 5. Trade-offs realizados
- **Simulación de Inventario**: Por simplicidad se simuló el éxito de inventario dentro del Order Service, aunque en un entorno real sería un microservicio aparte. 
- **Coreografía vs Orquestación**: Se priorizó la coreografía para mantener el sistema altamente reactivo y evitar el punto único de falla de un orquestador, a cambio de una mayor complejidad en el diagrama de flujo.
