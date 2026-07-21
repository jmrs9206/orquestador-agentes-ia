# Project Context

## Context identity

- Project ID: `orquestador-agentes-ia`
- Context version: `0.7`
- Repository root: `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`
- Branch/worktree: `feature/orchestration-layer`
- Commit verified: `d6e8574cd4083308e6ffbf8921940b99b99a74c5`
- Last verified: `2026-07-14`
- Curated by: `@context`

## One-paragraph summary

Inicialización local y remota del orquestador multiproyecto con reglas de gobierno. El repositorio Git local se inicializó correctamente en la rama `main` con el commit inicial de gobierno. El repositorio remoto en GitHub fue creado y enlazado, y la rama principal fue publicada con éxito. Las políticas y el charter han recibido aprobación humana explícita como línea base inicial.

## Verified facts

| F-001 | El workspace actual no está dentro de ningún repositorio Git superior | `git rev-parse --show-toplevel` | 2026-07-14 | HIGH |
| F-002 | Los archivos del marco operativo se han copiado con éxito a la raíz del workspace | `find` / `ls -la` | 2026-07-14 | HIGH |
| F-003 | La identidad de Git global está configurada como Julio Rodríguez (juliorodriguez@vdenergy.es) | `git config user.name` / `user.email` | 2026-07-14 | HIGH |
| F-004 | El repositorio remoto está enlazado a origin y sincronizado con el commit local inicial | `git ls-remote origin` / `git remote -v` | 2026-07-14 | HIGH |
| F-005 | El repositorio remoto es público por autorización explícita del propietario humano | Historial de chat / verificación manual | 2026-07-14 | HIGH |

## Current state (`CURRENT`)

### Repository map

```text
.agents/
  agents.md
  policies/
  skills/
  workflows/
.ai/
  PROJECT_CHARTER.md
  PROJECT_CONSTITUTION.md
  PROJECT_CONTEXT.md
  REQUIREMENTS.md
  ACCEPTANCE_CRITERIA.md
  HANDOFF.md
  TASK_001_BACKEND_INIT.md
  TASK_002_BACKEND_REGISTRY.md
  TASK_003_FRONTEND_INIT.md
  TASK_004_FRONTEND_REGISTRY.md
  TASK_005_INTEGRATION_E2E.md
  TASK_006_ORCHESTRATOR_DOMAIN.md
  TASK_007_ORCHESTRATOR_RUNNER.md
  TASK_008_ORCHESTRATOR_REST.md
  TASK_009_ORCHESTRATOR_FRONTEND.md
  TASK_010_ORCHESTRATOR_INTEGRATION.md
apps/
  web/                          # Next.js Frontend App
docs/
  adr/
    ADR-001-stack-tecnologico.md
    ADR-002-project-registry.md
infrastructure/
  compose.yaml                  # Docker Compose configuration
packages/
  contracts/                    # API shared contracts (openapi.yaml)
services/
  orchestrator-api/             # Spring Boot Backend API
templates/
.env.example
.gitignore
GEMINI.md
MANIFEST.md
README.md
```

### Implemented capabilities

- Estructura de gobierno y plantillas inicializadas localmente y subidas al remoto `origin`.
- Rama `main` configurada como rama predeterminada local y remota.
- Backend Spring Boot modular con persistencia MySQL, Flyway migrations y Testcontainers.
- API REST para registrar proyectos, agentes, tareas y ejecuciones con máquina de estados y validación de reglas de negocio.
- CLI Process Runner asíncrono con saneamiento y limpieza del entorno de variables de host para ejecuciones seguras.
- Human Gate y validación cruzada: intercepta comandos de riesgo y bloquea auto-aprobación del revisor de tareas.
- Panel Next.js / React (Workspace Console) con visor de logs en tiempo real, tabulador de agentes/tareas y banner interactivo de seguridad del Human Gate.
- Configuración de Dockerfiles multi-stage y compose.yaml validados para despliegue local de la composición.

### Known limitations

- El orquestador cuenta con persistencia y ejecución CLI manual, pero carece de la integración real de la API de Gemini (IA) para ejecución automatizada de tareas en base a prompts.
- No se han codificado los validadores físicos de filesystem (aislamiento absoluto de rutas de proyectos) ni la auditoría automatizada en base a contratos generados en disco.

## Target state (`TARGET`)

- Creación e inicialización del proyecto Spring Boot y Next.js en el monorepo.
- Implementación y validación del backend del Project Registry (API REST y persistencia MySQL).
- Desarrollo de la interfaz gráfica mínima en Next.js para listar y registrar proyectos.
- Validación mediante JUnit (Testcontainers MySQL) y Playwright (E2E).

## Active constraints

- RUN_ID: `bootstrap-github-2026-07-14`
- Autorización de nivel 3 estricta (no subir secretos, no usar force push, no modificar repos externos).

## Approved decisions

| ADR | Decision | Status | Supersedes |
|---|---|---|---|
| ADR-001 | Selección de stack definitivo (Java, Next.js, MySQL, Maven) | ACCEPTED | ADR-002 anterior |
| ADR-002 | Diseño del Project Registry (Estados y API REST) | PROPOSED | - |
| ADR-003 | Diseño de la Capa de Orquestación y Ejecución de Agentes | PROPOSED | - |

## External systems and contracts

| GitHub | CONFIRMED | git remote | YES | Repositorio remoto público enlazado y accesible |

## Unknowns and conflicts

| U-001 | UNKNOWN | Decisión de Licencia definitiva | Determina las reglas de reutilización legal y distribución | Human Owner |

## Current iteration

- Goal: Diseño e implementación del motor de agentes IA (Gemini SDK), aislamiento físico del filesystem y auditoría de tareas (task-011 a task-016).
- Approved tasks: Configuración del cliente Gemini API, validadores de path traversal para directorios de trabajo, generación dinámica de TASK_CONTRACT y EVIDENCE_LOG, y controles de roles de aprobación.

## Required reading by role

| Role | Files to read | Files not needed |
|---|---|---|
| `@director` | `GEMINI.md`, `.agents/policies/` | - |
| `@product` | `.ai/PROJECT_CHARTER.md`, `README.md` | - |

## Context change log

| Version | Date | Change | Evidence |
|---|---|---|---|
| 0.1 | 2026-07-14 | Creación de archivos `.ai/` y configuración de `.gitignore` | `find .ai/` |
| 0.2 | 2026-07-14 | Inicialización de Git local, configuración de remoto público y push exitoso | `git status` / `git ls-remote` |
| 0.3 | 2026-07-14 | Aprobación de documentos de gobierno e inicio de fase de arquitectura | Aprobación en chat |
| 0.4 | 2026-07-14 | Redefinición a stack Java/Next.js, creación de estructura de carpetas, ADRs y tareas del Project Registry | `git status` |
| 0.5 | 2026-07-14 | Implementación de las tareas task-001 a task-005, compilación y pruebas exitosas | `mvn clean test` y `npm run test` |
| 0.6 | 2026-07-14 | Planificación y diseño de la Capa de Orquestación (ADR-003, task-006 a task-010) | `git status` |
| 0.7 | 2026-07-14 | Implementación del motor de orquestación, Human Gate, interfaz gráfica de consola y pase de pruebas unitarias/E2E | `npx playwright test` |
