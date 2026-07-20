# Task Contract: Orchestrator REST API & Human Gate

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-02`
- Task ID: `task-008`
- Title: API REST de Ejecuciones y Control Humano de Riesgo
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/orchestration-layer`

## Objective

Exponer endpoints HTTP para administrar agentes, tareas y ejecuciones. Incorporar la lógica del "Human Gate" que detiene comandos que contengan patrones de riesgo (ej. `git push`, `deploy`) poniéndolos en estado `WAITING_APPROVAL` hasta que un humano los autorice.

## Source

- Requirement(s): FR-003, FR-004, FR-007, FR-008.
- Acceptance criteria: AC-003, AC-004, AC-007, AC-008.
- ADR/architecture: [ADR-003: Diseño de la Capa de Orquestación](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-003-orchestration-layer.md).

## Preconditions

- CliRunnerService implementado (completado en task-007).

## Scope of work

- Crear `ExecutionController` con los endpoints:
  - `POST /api/tasks/{id}/executions`: registra e inicia una nueva ejecución de agente.
  - `GET /api/executions/{id}`: obtiene los detalles y fragmento del log de salida.
  - `POST /api/executions/{id}/approve`: aprueba una ejecución en pausa `WAITING_APPROVAL`.
  - `POST /api/executions/{id}/reject`: rechaza la ejecución bloqueando su inicio.
- Crear lógica en `ExecutionService` que analice la línea de comandos antes de su invocación en el subproceso:
  - Si coincide con patrones de riesgo (`git push`, `npm publish`, `deploy`), se detiene y se guarda con estado `WAITING_APPROVAL`.
- Implementar la regla de validación de revisión cruzada: impedir que una tarea pase a `DONE` si el asignado (`assigneeId`) y el revisor (`reviewerId`) de la tarea son el mismo agente.

## Verification checks

- Crear pruebas MockMvc en el backend para validar:
  - Que un comando peligroso se registre como `WAITING_APPROVAL`.
  - Que los endpoints de `/approve` y `/reject` actualicen el estado y desencadenen el inicio (o detención) del proceso.
  - Que la auto-aprobación de tareas retorne error `400 Bad Request`.
