# Task Contract: Orchestrator CLI Process Runner

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-02`
- Task ID: `task-007`
- Title: Servicio de Ejecución de Procesos Locales (CLI Runner)
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/orchestration-layer`

## Objective

Crear un servicio de backend capaz de lanzar comandos CLI de forma asíncrona mediante subprocesos locales, capturar su salida en archivos de logs de la ruta `.ai/logs/` y limpiar las variables de entorno para evitar leaks de secretos del sistema.

## Source

- Requirement(s): FR-005, FR-006.
- Acceptance criteria: AC-005, AC-006.
- ADR/architecture: [ADR-003: Diseño de la Capa de Orquestación](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-003-orchestration-layer.md).

## Preconditions

- Tablas de persistencia de ejecuciones inicializadas (completado en task-006).

## Scope of work

- Crear `CliRunnerService` en la capa de infraestructura/aplicación.
- Utilizar `ProcessBuilder` para inicializar el subproceso:
  - Limpiar el entorno heredado (`environment().clear()`).
  - Solo inyectar un conjunto mínimo configurable (ej. `PATH`, `JAVA_HOME`, `NODE_ENV`, `GEMINI_API_KEY`).
  - Redireccionar error stream a stdout (`redirectErrorStream(true)`).
- Capturar la salida del proceso y guardarla de forma asíncrona en un archivo local `.log` en la carpeta `.ai/logs/` del proyecto.
- Retornar promesas/futures de Spring para realizar el seguimiento del ciclo de vida (`RUNNING`, `SUCCESS`, `FAILED`) y rellenar el código de salida (`exitCode`).

## Verification checks

- Crear pruebas unitarias e integración en el backend para validar que:
  - Un comando simple (ej. `echo "hello"`) se ejecuta y se escribe en el archivo log.
  - El entorno del subproceso no tiene acceso a variables confidenciales del host del sistema.
  - El código de salida y estados se actualizan correctamente al finalizar el proceso.
