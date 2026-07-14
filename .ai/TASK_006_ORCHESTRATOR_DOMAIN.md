# Task Contract: Orchestrator Domain & JPA Entities

## Identity

- Project ID: `orquestador-agentes-ia`
- Iteration ID: `it-02`
- Task ID: `task-006`
- Title: Modelado de Agentes, Tareas y Ejecuciones en Persistencia
- Status: `DRAFT`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `none`
- Branch/worktree: `feature/orchestration-layer`

## Objective

Modelar e implementar en base de datos las entidades de persistencia y puertos de dominio de `Agent`, `Task` y `Execution` con sus respectivas migraciones de Flyway.

## Source

- Requirement(s): FR-003, FR-004, FR-006.
- Acceptance criteria: AC-003, AC-004, AC-006.
- ADR/architecture: [ADR-003: Diseño de la Capa de Orquestación](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/adr/ADR-003-orchestration-layer.md).

## Preconditions

- La tabla `projects` debe estar creada (completado en it-01).

## Scope of work

- Crear la migración SQL `V2__create_orchestration_tables.sql` en `services/orchestrator-api/src/main/resources/db/migration/` que defina:
  - Tabla `agents` (`id`, `name`, `role`, `system_prompt`, `model_name`, `temperature`).
  - Tabla `tasks` (`id`, `project_id`, `title`, `description`, `status`, `assignee_id`, `reviewer_id`, `created_at`).
  - Tabla `executions` (`id`, `task_id`, `agent_id`, `status`, `command_line`, `log_file_path`, `exit_code`, `started_at`, `finished_at`).
- Definir los enums de dominio `TaskStatus` y `ExecutionStatus`.
- Crear las clases de dominio y de persistencia JPA correspondientes.
- Configurar y mapear las claves foráneas de relaciones y restricciones de unicidad.
- Crear interfaces de repositorios para `AgentRepository`, `TaskRepository` y `ExecutionRepository`.
- Implementar los adaptadores de infraestructura para persistencia de datos.

## Verification checks

- Crear pruebas de integración en `ProjectRepositoryTest.java` (o clase similar) con Testcontainers para verificar que las tablas `agents`, `tasks` y `executions` se crean correctamente y permiten transacciones CRUD de prueba.
