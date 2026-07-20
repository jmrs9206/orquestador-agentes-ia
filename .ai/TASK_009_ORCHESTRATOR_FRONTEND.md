# Task Contract: Orchestrator Frontend Views & Execution Logs

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-02`
- Task ID: `task-009`
- Title: Interfaz de Ejecuciones y Visualización de Logs en Tiempo Real
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/orchestration-layer`

## Objective

Desarrollar vistas en Next.js para gestionar agentes, ver la lista de ejecuciones de una tarea, visualizar los logs del terminal en tiempo real y mostrar avisos/modales interactivos cuando una ejecución esté bloqueada esperando aprobación humana.

## Source

- Requirement(s): FR-003, FR-006, FR-008.
- Acceptance criteria: AC-003, AC-006, AC-008.
- ADR/architecture: [ADR-003: Diseño de la Capa de Orquestación](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-003-orchestration-layer.md).

## Preconditions

- Endpoints de ExecutionController disponibles (completado en task-008).

## Scope of work

- Crear vistas secundarias de tareas y ejecuciones bajo el path `/projects/[id]/tasks/[taskId]`.
- Implementar componente `ExecutionConsole` para visualizar el log de consola (con tipografía mono y fondo oscuro).
- Implementar polling o SSE para actualizar el log si la ejecución está en estado `RUNNING`.
- Mostrar un modal destacado o sección de alerta interactiva si el estado es `WAITING_APPROVAL` con los botones de "Aprobar" y "Rechazar".
- Consumir los endpoints REST de aprobación del backend.

## Verification checks

- Crear pruebas unitarias con Vitest para validar que:
  - El modal de "esperando aprobación" se renderiza correctamente cuando el estado de la ejecución es `WAITING_APPROVAL`.
  - Los botones del modal emiten las llamadas correspondientes a la API.
