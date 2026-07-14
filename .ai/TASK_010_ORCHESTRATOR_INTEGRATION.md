# Task Contract: Orchestrator Integration & E2E Tests

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-02`
- Task ID: `task-010`
- Title: Pruebas Integrales de Orquestación y Puerta Humana
- Status: `DRAFT`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `none`
- Branch/worktree: `feature/orchestration-layer`

## Objective

Escribir pruebas de integración end-to-end (E2E) con Playwright para verificar que la interfaz y el backend coordinan correctamente el ciclo de vida de ejecuciones, bloqueando comandos de riesgo y permitiendo su reanudación tras la aprobación del usuario.

## Source

- Requirement(s): FR-005, FR-008.
- Acceptance criteria: AC-005, AC-008.
- ADR/architecture: [ADR-003: Diseño de la Capa de Orquestación](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-003-orchestration-layer.md).

## Preconditions

- Todas las tareas anteriores (task-006 a task-009) implementadas.

## Scope of work

- Crear archivo `apps/web/e2e/orchestrator-execution.spec.ts` para pruebas Playwright.
- Programar el flujo de prueba E2E:
  - Crear un proyecto y una tarea.
  - Lanzar un comando seguro (ej. `echo "hello"`) y verificar que se ejecuta y muestra logs en el panel.
  - Lanzar un comando de riesgo (ej. `git push`) y verificar que la consola de la UI muestra el aviso "Esperando Aprobación Humana".
  - Hacer clic en "Aprobar" y verificar que el comando finaliza con código de salida y cambia de estado en la UI.
- Configurar el flujo de ejecución en local con Docker Compose.

## Verification checks

- Ejecutar las pruebas unitarias y las nuevas pruebas E2E asegurando que no hay regresiones y todo el flujo integrado se valida con éxito.
