# ADR-005: MongoDB como Event Store para Auditoría y Trazabilidad

## Estatus
Aceptado

## Contexto
En una arquitectura orientada a eventos, es crítico tener una fuente de verdad histórica de todas las transacciones y cambios de estado que han ocurrido. Aunque el estado actual de las órdenes reside en PostgreSQL, necesitamos una bitácora inmutable de eventos que permita reconstruir la historia de una orden o realizar auditorías técnicas.

## Decisión
Se ha seleccionado **MongoDB** como base de datos para el almacenamiento de eventos (Event Store) y logs de auditoría en el `notification-service`.

### Justificación
1. **Esquema Flexible**: Los eventos de dominio tienen estructuras variables (payloads diferentes para `OrderCreated` vs `PaymentFailed`). El modelo de documentos de MongoDB se adapta naturalmente a esta variabilidad sin necesidad de migraciones complejas.
2. **Alta Velocidad de Escritura**: MongoDB está optimizado para ingestas rápidas de datos, lo cual es ideal para capturar flujos de eventos en tiempo real provenientes de Kafka.
3. **Escalabilidad**: Facilita el crecimiento horizontal mediante sharding si el volumen de eventos crece exponencialmente.
4. **Consultas de Auditoría**: Permite realizar consultas ricas sobre los campos de los eventos para debugging o reportes de negocio.

## Consecuencias
- **Persistencia Políglota**: El sistema ahora depende de dos tipos de bases de datos (SQL y NoSQL), aumentando la complejidad operativa.
- **Consistencia Eventual**: La sincronización entre PostgreSQL y MongoDB se realiza a través de eventos, por lo que puede haber un desfase milimétrico entre el estado "vivo" y el histórico.
