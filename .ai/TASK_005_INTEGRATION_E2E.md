# Task Contract: E2E Integration & Docker Compose

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-01`
- Task ID: `task-005`
- Title: Integración del Entorno Local y Pruebas End-to-End con Playwright
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/project-registry`

## Objective

Configurar y validar la integración completa del entorno local dockerizado de desarrollo (`mysql`, `backend` y `frontend`) mediante Docker Compose, e implementar pruebas end-to-end automatizadas con Playwright que verifiquen el ciclo de vida del Project Registry (crear, listar y archivar).

## Source

- Requirement(s): FR-001, FR-009, NFR-001.
- Acceptance criteria: AC-001, AC-008, AC-009 de `.ai/ACCEPTANCE_CRITERIA.md`.
- ADR/architecture: [docs/adr/ADR-001-stack-tecnologico.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md) y [infrastructure/compose.yaml](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/infrastructure/compose.yaml).

## Preconditions

- [ ] Task `task-002` (API REST Backend) y Task `task-004` (Frontend UI) completadas y marcadas como DONE.
- [ ] Workspace isolated (`/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`)
- [ ] Dependencies available (Docker, Docker Compose, Node.js, npm, Java 21)
- [ ] No approval gate pending

## Inputs to read

- [infrastructure/compose.yaml](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/infrastructure/compose.yaml)
- [.env.example](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/.env.example)

## Allowed write paths

- `infrastructure/`
- `apps/web/` (configuración de Playwright y carpeta de pruebas `e2e/`)
- `.env`

## Prohibited paths

- `services/orchestrator-api/src/main` (excepto logs de ejecución y evidencias)
- `.agents/`

## Expected change

- Asegurar que la configuración de `infrastructure/compose.yaml` levanta correctamente:
  - MySQL (`mysql:8.0.36`) en puerto local 3306.
  - Spring Boot `orchestrator-api` expuesto en puerto 8080.
  - Next.js `web-app` expuesto en puerto 3000.
- Crear el archivo Dockerfile para compilar y servir la API en `services/orchestrator-api/Dockerfile` (con compilación multi-stage basada en OpenJDK 21 y Maven).
- Crear el archivo Dockerfile para compilar y servir Next.js en `apps/web/Dockerfile` (con compilación multi-stage basada en Node.js).
- Configurar `Playwright` en el frontend (`apps/web/playwright.config.ts`) y escribir una prueba de integración E2E en `apps/web/e2e/project-registry.spec.ts` que simule las siguientes interacciones de usuario:
  1. Abrir la página principal, verificar estado "vacío".
  2. Rellenar y enviar el formulario de registro con datos correctos de proyecto (creando una carpeta temporal en el host para simular que existe el repositorio).
  3. Comprobar que el proyecto aparece en la lista principal en estado `DRAFT`.
  4. Intentar crear otro proyecto con la misma clave y validar el rechazo.
  5. Hacer clic en "Archivar" y verificar que el proyecto cambia su estado a `ARCHIVED` y desaparece de la vista activa predeterminada.
- Ejecutar y documentar la salida de la prueba E2E de Playwright.

## Explicit non-goals

- Desplegar el entorno local a producción o configurar orquestadores externos como Kubernetes.
- Almacenar contraseñas o credenciales reales en `compose.yaml` (usar interpolación de variables de entorno basadas en `.env.example`).

## Acceptance criteria

| AC | Expected evidence |
|---|---|
| AC-5.1 | Ejecutar `docker compose up --build -d` en `infrastructure/` levanta los tres contenedores y el endpoint `/api/projects` responde con éxito. |
| AC-5.2 | Las pruebas de Playwright E2E (`npx playwright test`) pasan con éxito simulando el ciclo completo en un entorno integrado limpio. |

## Mandatory checks

| Check | Command or method | Required? |
|---|---|---|
| Compose execution | `docker compose ps` | yes |
| Playwright test execution | `npx playwright test` | yes |

## Dependency policy

- New dependencies allowed: `YES_WITHIN_LIST` (`playwright` y dependencias de test en Next.js).

## Approval

- Planner: `PENDING`
- Human gate: `PENDING`
