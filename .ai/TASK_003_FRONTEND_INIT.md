# Task Contract: Frontend Initialization

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-01`
- Task ID: `task-003`
- Title: Inicialización de Next.js Frontend con TypeScript y Tailwind CSS
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/project-registry`

## Objective

Inicializar el esqueleto del frontend en `apps/web` utilizando Next.js (App Router), TypeScript, Tailwind CSS, y configurar la suite de pruebas unitarias (Vitest/Jest) y linting.

## Source

- Requirement(s): FR-001 (Estructura frontend inicial y tecnologías).
- Acceptance criteria: Calidad y guías de frontend de la directiva.
- ADR/architecture: [docs/adr/ADR-001-stack-tecnologico.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md).

## Preconditions

- [ ] Workspace isolated (`/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`)
- [ ] Dependencies available (Node.js 24, npm 11)
- [ ] No approval gate pending

## Inputs to read

- [docs/adr/ADR-001-stack-tecnologico.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md)

## Allowed write paths

- `apps/web/`

## Prohibited paths

- `services/orchestrator-api/`
- `.agents/`

## Expected change

- Inicializar el proyecto Next.js en `apps/web` utilizando `create-next-app` o de forma manual configurando:
  - App Router
  - TypeScript
  - Tailwind CSS
  - ESLint
- Configurar la suite de pruebas unitarias con `Vitest` o `Jest` y `@testing-library/react` para validar el renderizado básico.
- Configurar los scripts de npm para:
  - `npm run dev`: Iniciar el servidor Next.js en modo desarrollo.
  - `npm run build`: Compilar la aplicación Next.js para producción.
  - `npm run lint`: Ejecutar comprobaciones de ESLint.
  - `npm run test`: Ejecutar pruebas con Vitest/Jest.
- Asegurar que la build de Next.js compila de forma correcta sin errores.

## Explicit non-goals

- Implementar lógica de conexión o consumo de la API REST real.
- Crear componentes o vistas de negocio complejas.

## Acceptance criteria

| AC | Expected evidence |
|---|---|
| AC-3.1 | Ejecutar `npm run build` en `apps/web` compila con éxito la aplicación sin errores de tipado o linting. |
| AC-3.2 | Ejecutar `npm run test` corre pruebas unitarias básicas y reporta PASS. |

## Mandatory checks

| Check | Command or method | Required? |
|---|---|---|
| Linter check | `npm run lint` | yes |
| Type check | `npx tsc --noEmit` | yes |
| Test execution | `npm run test` | yes |
| Build execution | `npm run build` | yes |

## Dependency policy

- New dependencies allowed: `YES_WITHIN_LIST` (Next.js, React, React DOM, Tailwind, TypeScript, ESLint, Vitest, Testing Library).

## Approval

- Planner: `PENDING`
- Human gate: `PENDING`
