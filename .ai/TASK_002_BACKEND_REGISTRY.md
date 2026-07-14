# Task Contract: Backend Project Registry REST API

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-01`
- Task ID: `task-002`
- Title: Implementación de la API REST del Project Registry
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/project-registry`

## Objective

Implementar los endpoints REST del Project Registry en Spring Boot, incluyendo las validaciones de negocio del ciclo de vida de los proyectos y las transiciones de estado descritas en [ADR-002: Diseño del Módulo Project Registry](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md).

## Source

- Requirement(s): FR-001, FR-002, FR-007, FR-009, NFR-001.
- Acceptance criteria: AC-001, AC-002, AC-004, AC-009 de `.ai/ACCEPTANCE_CRITERIA.md`.
- ADR/architecture: [docs/adr/ADR-002-project-registry.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md).

## Preconditions

- [ ] Task `task-001` (inicialización de Spring Boot y MySQL) completada y marcada como DONE.
- [ ] Workspace isolated (`/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`)
- [ ] Dependencies available (Java 21, Maven)
- [ ] No approval gate pending

## Inputs to read

- [docs/adr/ADR-002-project-registry.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md)
- [packages/contracts/openapi.yaml](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/packages/contracts/openapi.yaml)

## Allowed write paths

- `services/orchestrator-api/`

## Prohibited paths

- `apps/web/`
- `.agents/`

## Expected change

- Crear el controlador `ProjectController` mapeado a `/api/projects` implementando los métodos de OpenAPI:
  - `POST /api/projects` (Crear proyecto en estado `DRAFT`, verificar existencia del path local en el host, validar formato de `key` y que no esté duplicado).
  - `GET /api/projects` (Listar proyectos con filtro opcional de archivados).
  - `GET /api/projects/{id}` (Obtener detalles de un proyecto).
  - `PATCH /api/projects/{id}` (Actualizar name, description, defaultBranch).
  - `PUT /api/projects/{id}/archive` (Establecer estado a `ARCHIVED` y colocar fecha archivedAt).
- Implementar la lógica del servicio `ProjectService` para validar que las transiciones de estado inválidas (ej. `DRAFT -> PAUSED`, `ARCHIVED -> ACTIVE`) se rechacen lanzando excepciones de negocio.
- Configurar el manejador global de excepciones para retornar la estructura `ErrorResponse` definida en OpenAPI en caso de fallos.
- Crear pruebas unitarias que testeen las validaciones de transiciones de estados e inexistencia de rutas.
- Configurar e integrar `springdoc-openapi` para auto-generar la documentación de OpenAPI en `/v3/api-docs` y `/swagger-ui.html`.

## Explicit non-goals

- Implementar lógica de autenticación o seguridad de roles (Spring Security).

## Acceptance criteria

| AC | Expected evidence |
|---|---|
| AC-2.1 | Ejecutar peticiones HTTP de prueba a `/api/projects` responde conforme a las especificaciones y códigos de retorno de OpenAPI. |
| AC-2.2 | Intentar transicionar de un estado bloqueado o archivado a uno inválido devuelve un error HTTP `400 Bad Request` estructurado. |
| AC-2.3 | La documentación OpenAPI es accesible localmente al levantar el servicio en `/v3/api-docs`. |

## Mandatory checks

| Check | Command or method | Required? |
|---|---|---|
| Linter/Formatting | Checkstyle/Spotless | yes |
| Tests execution | `mvn test` | yes |

## Dependency policy

- New dependencies allowed: `YES_WITHIN_LIST` (`springdoc-openapi-starter-webmvc-ui` para auto-documentación).

## Approval

- Planner: `PENDING`
- Human gate: `PENDING`
