# Project Context

## Context identity

- Project ID: `orquestador-agentes-ia`
- Context version: `0.6`
- Repository root: `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`
- Branch/worktree: `feature/project-registry`
- Commit verified: `41a1343cd4083308e6ffbf8921940b99b99a74c5`
- Last verified: `2026-07-14`
- Curated by: `@context`

## One-paragraph summary

Inicialización local y remota del orquestador multiproyecto con reglas de gobierno y exclusión de dependencias del blueprint. El repositorio Git local se inicializó correctamente en la rama `main` con el commit inicial de gobierno. El repositorio remoto en GitHub fue creado y enlazado, y la rama principal fue publicada con éxito. Las políticas y el charter han recibido aprobación humana explícita como línea base inicial.

## Verified facts

| F-001 | El workspace actual no está dentro de ningún repositorio Git superior | `git rev-parse --show-toplevel` | 2026-07-14 | HIGH |
| F-002 | Los archivos del blueprint operativo se han copiado con éxito a la raíz del workspace | `find` / `ls -la` | 2026-07-14 | HIGH |
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
- Backend Spring Boot completamente inicializado, estructurado de forma modular (dominio, aplicación, infraestructura, api), con persistencia MySQL, Flyway migrations y Testcontainers.
- Endpoints REST `/api/projects` expuestos para crear, listar, detallar y archivar proyectos locales con validaciones y máquina de estados.
- Frontend Next.js con TypeScript, Tailwind CSS y Vitest configurado con panel de visualización, filtros y formulario funcional.
- Configuración de Dockerfiles multi-stage y compose.yaml validados para despliegue local de la composición.

### Known limitations

- El orquestador cuenta con el registro de proyectos locales, pero carece de la orquestación real de agentes, tareas y workflows en esta iteración inicial.

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

- Goal: Diseño y planificación del motor de Orquestación y Ejecución de Agentes (task-006 a task-010).
- Approved tasks: Modelado de persistencia de agentes y ejecuciones, motor CLI asíncrono seguro, human gate de detención de comandos riesgosos, y consolas de logs en frontend.

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
