# EVALUATION.md - Autoevaluación de la Prueba Técnica

## 1. Funcionalidades Completadas
- [x] **Gestión de Órdenes**: Creación reactiva, consulta por ID, listado por cliente y cancelación con validación de estado.
- [x] **Arquitectura Orientada a Eventos**: Flujo asíncrono implementado con Kafka (`OrderCreated` -> `PaymentProcessed` -> `UpdateOrderStatus`).
- [x] **Respeto a Clean Architecture**: Separación estricta de capas Domain, Application e Infrastructure.
- [x] **Programación Reactiva**: Uso de Spring WebFlux, Project Reactor (Mono/Flux) y R2DBC en todo el flujo.
- [x] **Event Sourcing**: Auditoría de eventos almacenada en MongoDB en los servicios de Órdenes y Notificaciones.
- [x] **Infraestructura**: Docker Compose funcional con Postgres, MongoDB y Kafka.
- [x] **Documentación**: README completo y 3 ADRs detallando decisiones clave.

## 2. Funcionalidades Pendientes / Mejoras
- **Paginación Real**: El listado de órdenes actualmente devuelve todos los registros del cliente; falta integrar `Pageable` con R2DBC para mayor eficiencia.
- **Circuit Breaker**: No se implementó Resilience4j por tiempo, pero se considera para mejorar la resiliencia entre servicios.
- **Tests de Integración Exhaustivos**: Se requiere configurar Testcontainers para pruebas con Kafka y Postgres reales.
- **Seguridad**: Falta integración con OAuth2/JWT para proteger los endpoints.

## 3. Decisiones con más tiempo
- Implementaría un **API Gateway** (Spring Cloud Gateway) para centralizar el acceso y manejo de rate limiting.
- Añadiría el **Pattern Outbox** para garantizar que los eventos se publiquen solo si la transacción en la base de datos es exitosa.
- Profundizaría en el modelado de **DDD** (Agregados y Invariantes) para el catálogo de productos.

## 4. Desafíos enfrentados
- **R2DBC Relationships**: La gestión de relaciones Uno a Muchos (Orden -> Items) en R2DBC no es tan directa como en JPA, lo que requirió una gestión manual de `collectList` y flujos paralelos.
- **Kafka Serialization**: Configurar la serialización/deserialización JSON de manera que los objetos complejos se transmitan correctamente entre servicios sin compartir el modelo de clases exacto.

## 5. Trade-offs realizados
- **Coreing vs Orchestración**: Se eligió Coreografía para las Sagas para mantener el sistema reactivo y evitar un orquestador central que pudiera ser un cuello de botella, a pesar de que la trazabilidad es un poco más compleja de implementar.
