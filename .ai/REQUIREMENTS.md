# Requirements

## Metadata

- Project ID: `orquestador-agentes-ia`
- Version: `0.1`
- Status: `APPROVED`
- Last updated: `2026-07-14`

## Requirement rules

- Every requirement has a source.
- Proposed requirements are not binding until approved.
- Requirements describe needed outcomes, not arbitrary implementation choices.
- Changes after approval require versioning and impact analysis.

## Functional requirements

| ID | Requirement | Priority | Source | Status | Acceptance IDs |
|---|---|---|---|---|---|
| FR-001 | Registrar y gestionar varios proyectos de software de forma centralizada | MUST | USER | PROPOSED | AC-001 |
| FR-002 | Mantener contexto, memoria y archivos aislados por `project_id` | MUST | USER | PROPOSED | AC-002 |
| FR-003 | Asignar agentes con roles y permisos explícitos en cada flujo | MUST | USER | PROPOSED | AC-003 |
| FR-004 | Convertir requisitos en contratos de tarea con entradas y salidas explícitas | MUST | USER | PROPOSED | AC-004 |
| FR-005 | Ejecutar Gemini CLI con contexto mínimo estructurado y capturar salida | MUST | USER | PROPOSED | AC-005 |
| FR-006 | Mantener registros de estados, logs, diffs y evidencias por ejecución | MUST | USER | PROPOSED | AC-006 |
| FR-007 | Impedir que un agente apruebe su propio trabajo (requerir revisión cruzada) | MUST | USER | PROPOSED | AC-007 |
| FR-008 | Detener acciones externas o irreversibles (push, deploy, recursos) para solicitar autorización humana | MUST | USER | PROPOSED | AC-008 |
| FR-009 | Crear nuevos proyectos limpios sin mezcla de decisiones históricas de otros proyectos | MUST | USER | PROPOSED | AC-009 |

## Non-functional requirements

| ID | Attribute | Requirement | Measurement | Source | Status |
|---|---|---|---|---|---|
| NFR-001 | Security / Privacy | Aislamiento físico de los directorios de trabajo locales de cada proyecto | Comprobación de que no se cruzan rutas de filesystem | USER | PROPOSED |
| NFR-002 | Security | Comprobación automatizada de secretos locales antes de realizar commits en el orquestador | Cero secretos subidos al repositorio | USER | PROPOSED |

## Constraints mistaken for requirements

| ID | Preference | Owner | Decision needed |
|---|---|---|---|
| P-001 | Usar base de datos relacional SQLite para el almacenamiento de logs del orquestador | Architect | yes |

## Open questions

| ID | Question | Why it matters | Safe default | Owner | Status |
|---|---|---|---|---|---|
| Q-001 | ¿Cómo se gestionará la concurrencia entre agentes trabajando en distintas tareas del mismo proyecto? | Afecta al diseño del backend y a la consistencia del repositorio | Ejecución secuencial de tareas por proyecto | Architect | OPEN |

## Change log

- 2026-07-14: Redacción inicial del borrador de requisitos para el MVP.
