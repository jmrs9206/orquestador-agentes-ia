# Task Contract: Frontend Project Registry UI

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-01`
- Task ID: `task-004`
- Title: Implementación de la Interfaz del Project Registry en Next.js
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/project-registry`

## Objective

Desarrollar las páginas en el frontend Next.js para listar los proyectos registrados y un formulario para registrar un proyecto nuevo consumiendo los endpoints de la API REST del backend.

## Source

- Requirement(s): FR-001, FR-009 (Listar y registrar proyectos mediante interfaz de usuario).
- Acceptance criteria: Criterios de UI y UX del primer incremento.
- ADR/architecture: [docs/adr/ADR-001-stack-tecnologico.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md) y [docs/adr/ADR-002-project-registry.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md).

## Preconditions

- [ ] Task `task-003` (inicialización de Next.js) completada y marcada como DONE.
- [ ] Task `task-002` (API REST backend completada) en estado DONE o con API funcional en el host.
- [ ] Workspace isolated (`/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`)
- [ ] Dependencies available (Node.js 24, npm 11)
- [ ] No approval gate pending

## Inputs to read

- [docs/adr/ADR-002-project-registry.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md)
- [packages/contracts/openapi.yaml](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/packages/contracts/openapi.yaml)

## Allowed write paths

- `apps/web/`

## Prohibited paths

- `services/orchestrator-api/`
- `.agents/`

## Expected change

- Crear la pantalla principal de listado de proyectos (por ejemplo, en `apps/web/src/app/page.tsx` o similar):
  - Consumir el endpoint `GET /api/projects` para listar los proyectos activos.
  - Mostrar estados de: **Cargando** (skeleton/spinner), **Vacío** (mensaje amigable si no hay proyectos registrados), y **Error** (si el backend no responde).
  - Incluir botones para ver detalles o archivar un proyecto llamando a `PUT /api/projects/{id}/archive`.
- Crear el formulario de registro de proyectos:
  - Campos: Clave corta (`key`), Nombre (`name`), Descripción (`description`), Ruta absoluta local (`repositoryPath`), defaultBranch (opcional), contextPath (opcional).
  - Validar los campos en cliente y enviar petición `POST /api/projects`.
  - Manejar errores estructurados enviados por el backend (ej. mostrar mensaje si la ruta física del repositorio no existe).
- No implementar gestores de estado globales (ej. Redux o Zustand) a menos que se justifique. Utilizar estado de React local (`useState`, `useEffect`).

## Explicit non-goals

- Implementar lógica compleja de paginación o filtros de búsqueda avanzados en esta etapa.

## Acceptance criteria

| AC | Expected evidence |
|---|---|
| AC-4.1 | El listado de proyectos renderiza correctamente los datos devueltos por la API de Spring Boot. |
| AC-4.2 | El formulario de registro envía los datos correctos en formato JSON y maneja con éxito las respuestas `201 Created` y `400 Bad Request`. |
| AC-4.3 | La compilación `npm run build` en `apps/web` finaliza correctamente sin errores. |

## Mandatory checks

| Check | Command or method | Required? |
|---|---|---|
| Lint check | `npm run lint` | yes |
| Type check | `npx tsc --noEmit` | yes |
| Test execution | `npm run test` | yes |

## Dependency policy

- New dependencies allowed: `NO` (utilizar fetch nativo o bibliotecas de cliente simples incluidas en la inicialización).

## Approval

- Planner: `PENDING`
- Human gate: `PENDING`
