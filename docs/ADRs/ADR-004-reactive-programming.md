# ADR-004: Adopción de Programación Reactiva (Project Reactor & WebFlux)

## Estatus
Aceptado

## Contexto
El sistema de gestión de órdenes debe ser capaz de manejar picos de tráfico intensos durante campañas de e-commerce sin degradar significativamente el rendimiento o agotar los recursos del servidor (threads). El modelo tradicional thread-per-request de Spring MVC puede verse limitado bajo alta concurrencia debido al bloqueo de I/O en llamadas a base de datos y mensajería.

## Decisión
Se ha decidido adoptar el stack reactivo de Spring (**Spring WebFlux**) junto con **Project Reactor** como backbone de la aplicación.

### Justificación
1. **Escalabilidad Vertical**: Permite manejar miles de conexiones simultáneas con un número mínimo de hilos, reduciendo el overhead de cambio de contexto.
2. **I/O No Bloqueante**: El uso de drivers reactivos (**R2DBC** para PostgreSQL y drivers reactivos para MongoDB/Kafka) garantiza que los hilos del CPU no se queden esperando respuestas de red o disco.
3. **Backpressure**: Reactor proporciona mecanismos nativos para manejar el flujo de datos y evitar que los consumidores se vean abrumados por los productores.
4. **Resiliencia**: Facilita la implementación de operadores de reintento, timeouts y fallbacks de manera declarativa.

## Consecuencias
- **Curva de Aprendizaje**: El paradigma funcional/reactivo es más complejo que el imperativo tradicional y requiere un cambio de mentalidad en el equipo de desarrollo.
- **Debugging**: El stacktrace en aplicaciones reactivas es menos lineal y más difícil de seguir sin herramientas de auditoría adecuadas.
- **Librerías**: Todas las dependencias deben ser compatibles con el modelo no bloqueante para no romper la cadena reactiva.
