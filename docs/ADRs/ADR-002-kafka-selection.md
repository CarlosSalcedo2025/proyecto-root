# ADR-002: Elección de Apache Kafka como Message Broker

## Estado
Aceptado

## Contexto
El sistema debe gestionar flujos de trabajo asíncronos y orientados a eventos (Event-Driven Architecture) para garantizar la escalabilidad y resiliencia ante picos de tráfico. Se requiere un sistema que soporte persistencia de mensajes y capacidad de re-procesamiento.

## Decisión
Se elige **Apache Kafka** como el broker de mensajería principal por las siguientes razones:
1. **Escalabilidad**: Capacidad nativa para manejar grandes volúmenes de datos mediante particionamiento.
2. **Persistencia y Replay**: Los mensajes se guardan en disco, permitiendo que nuevos consumidores (o consumidores que fallaron) puedan leer desde el inicio.
3. **Ecosistema**: Excelente integración con Spring Boot y soporte para patrones complejos como Event Sourcing.
4. **Resiliencia**: Al ser distribuido, garantiza alta disponibilidad.

## Consecuencias
Positivas:
- Alta disponibilidad y tolerancia a fallos.
- Soporte nativo para el patrón Saga coreografiado.
- Desacoplamiento total entre los microservicios de órdenes, pagos y notificaciones.

Negativas:
- Requiere gestión de infraestructura adicional (Zookeeper/Kraft).
- Complejidad en la configuración inicial y gestión de offsets.

## Alternativas Consideradas
1. **RabbitMQ**: Rechazada porque, aunque es excelente para colas simples, Kafka ofrece mejores capacidades de retención de mensajes y replay necesarias para el Event Store.
2. **Redis Pub/Sub**: Rechazada por la falta de persistencia garantizada y limitadas opciones de monitoreo para flujos de misión crítica.
