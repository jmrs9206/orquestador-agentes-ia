# Task Contract: Backend Initialization

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-01`
- Task ID: `task-001`
- Title: Inicialización de Spring Boot Backend y Migraciones MySQL
- Status: `DONE`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/project-registry`

## Objective

Inicializar el esqueleto del backend en Java con Spring Boot y Maven, y configurar el soporte de base de datos MySQL con migraciones controladas por Flyway/Liquibase para la tabla `projects`.

## Source

- Requirement(s): FR-001, FR-006, FR-009 (Estructura inicial backend, MySQL y persistencia).
- Acceptance criteria: AC-001 de `.ai/ACCEPTANCE_CRITERIA.md`.
- ADR/architecture: [ADR-001: Selección del Stack Tecnológico](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md) y [ADR-002: Diseño del Módulo Project Registry](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md).

## Preconditions

- [ ] Workspace isolated (`/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`)
- [ ] Dependencies available (Java 21, Maven, Docker active)
- [ ] No approval gate pending

## Inputs to read

- [docs/adr/ADR-001-stack-tecnologico.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-001-stack-tecnologico.md)
- [docs/adr/ADR-002-project-registry.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-002-project-registry.md)

## Allowed write paths

- `services/orchestrator-api/`

## Prohibited paths

- `apps/web/`
- `.agents/`
- `templates/`

## Expected change

- Crear el proyecto Maven Spring Boot en `services/orchestrator-api/` con las dependencias: `Spring Web`, `Spring Data JPA`, `MySQL Driver`, `Flyway Core` (o Liquibase), `Validation`, y `Lombok`.
- Crear el script de migración SQL inicial (ej. `V1__create_projects_table.sql`) en `src/main/resources/db/migration/` definiendo el esquema de la tabla `projects`.
- Configurar `application-dev.yml` para apuntar a MySQL en local.
- Crear la entidad JPA `Project` y su repositorio `ProjectRepository` en el paquete de dominio/infraestructura correspondiente.
- Crear una prueba de integración utilizando `Testcontainers` para validar que la migración se ejecuta con éxito y se pueden insertar/leer proyectos en una base de datos MySQL real.

## Explicit non-goals

- Implementar lógica del controlador REST o endpoints HTTP.
- Implementar validaciones de transiciones de estado complejas de negocio.

## Acceptance criteria

| AC | Expected evidence |
|---|---|
| AC-1.1 | Ejecutar `mvn clean test` en `services/orchestrator-api` compila el proyecto y pasa todas las pruebas con éxito. |
| AC-1.2 | Las pruebas de integración levantan un contenedor MySQL real con Testcontainers, aplican las migraciones y verifican la persistencia de un objeto `Project`. |

## Mandatory checks

| Check | Command or method | Required? |
|---|---|---|
| Compilation check | `mvn clean compile` | yes |
| Test execution | `mvn test` | yes |

## Dependency policy

- New dependencies allowed: `YES_WITHIN_LIST` (Spring Boot Starters, MySQL Driver, Flyway/Liquibase, Testcontainers MySQL).

## Approval

- Planner: `PENDING`
- Human gate: `PENDING`
