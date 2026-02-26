# ADR-0001: Implementación de Clean Architecture para Microservicios

## Estado
Aceptado

## Contexto
El sistema requiere un alto grado de mantenibilidad, testabilidad y desacoplamiento de los frameworks externos (Spring Boot, Kafka, R2DBC). Dada la naturaleza de la prueba técnica para un rol Senior, es fundamental demostrar la capacidad de separar la lógica de negocio de la infraestructura.

## Decisión
Se implementará Clean Architecture (Arquitectura Hexagonal) con la siguiente estructura de paquetes por microservicio:
1. `domain`: Entidades de negocio, Agregados, Value Objects y Puertos (Interfaces de salida). Cero dependencias de Spring u otros frameworks de infraestructura.
2. `application`: Casos de uso dirigidos por el negocio e interfaces de entrada (Puertos internos).
3. `infrastructure`: Adaptadores (Controladores REST, Repositorios R2DBC/MongoDB, Productores/Consumidores Kafka). Aquí reside toda la configuración de Spring.

## Consecuencias
Positivas:
- Lógica de negocio protegida de cambios tecnológicos.
- Test unitarios de dominio sin necesidad de mocks complejos de infraestructura.
- Escalabilidad y claridad en la evolución del sistema.

Negativas:
- Mayor número de clases y boilerplate inicial debido al mapping entre capas.
- Curva de aprendizaje superior para el equipo.

## Alternativas Consideradas
1. **Layered Architecture (Traditional Controller-Service-Repository)**: Rechazada por el alto acoplamiento que genera con el framework y la dificultad de probar la lógica de negocio de forma aislada.
