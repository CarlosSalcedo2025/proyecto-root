# ADR-003: Estrategia de Manejo de Errores y Compensación (Saga Pattern)

## Estado
Aceptado

## Contexto
En un entorno distribuido, las transacciones que involucran múltiples microservicios (Órdenes, Pagos, Inventario) no pueden ser atómicas (ACID) de manera tradicional. Se necesita garantizar la consistencia eventual sin bloquear los servicios.

## Decisión
Se implementará el **Saga Pattern coreografiado** (Choreography):
1. Cada servicio publica un evento tras completar su tarea local.
2. Los demás servicios escuchan y deciden si actúan.
3. En caso de fallo (ej. Pago fallido), se emiten **eventos de compensación** para revertir estados previos (ej. Cancelar orden o liberar inventario).

## Consecuencias
Positivas:
- Sin punto único de fallo (sin orquestador central).
- Acoplamiento mínimo entre servicios.
- Escalabilidad horizontal sencilla.

Negativas:
- Trazabilidad del flujo completo más compleja de monitorear.
- Riesgo de dependencias cíclicas si no se diseña correctamente.

## Alternativas Consideradas
1. **Saga Orchestrated**: Rechazada para evitar un acoplamiento excesivo a un orquestador centralizado, prefiriendo la naturaleza reactiva de la coreografía para esta prueba técnica.
2. **2PC (Two-Phase Commit)**: Rechazada por ser bloqueante y no escalar adecuadamente en sistemas distribuidos modernos.
