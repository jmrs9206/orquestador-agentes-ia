# Project Context

## Context identity

- Project ID: `orquestador-agentes-ia`
- Context version: `0.1`
- Repository root: `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`
- Branch/worktree: `main`
- Commit verified: `NONE`
- Last verified: `2026-07-14`
- Curated by: `@context`

## One-paragraph summary

Inicialización local del orquestador multiproyecto con reglas de gobierno y exclusión de dependencias del blueprint. El proyecto se encuentra en estado de preparación de repositorio local y configuración del control de versiones. Se han copiado las plantillas y reglas de control en la raíz del workspace, y se ha creado un archivo `.gitignore` robusto. La identidad Git global está verificada, pero la autenticación con GitHub CLI requiere acción manual del usuario debido a que `gh` local corresponde a `gitsome` no autenticado.

## Verified facts

| ID | Fact | Source | Verified | Confidence |
|---|---|---|---|---|
| F-001 | El workspace actual no está dentro de ningún repositorio Git superior | `git rev-parse --show-toplevel` | 2026-07-14 | HIGH |
| F-002 | Los archivos del blueprint operativo se han copiado con éxito a la raíz del workspace | `find` / `ls -la` | 2026-07-14 | HIGH |
| F-003 | La identidad de Git global está configurada como Julio Rodríguez (juliorodriguez@vdenergy.es) | `git config user.name` / `user.email` | 2026-07-14 | HIGH |
| F-004 | GitHub CLI en el sistema es la herramienta gitsome, no autenticada | `gh me` / `gh auth status` | 2026-07-14 | HIGH |

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
docs/
templates/
.gitignore
GEMINI.md
MANIFEST.md
README.md
```

### Implemented capabilities

- Ninguna (Fase de definición de gobierno y configuración inicial).

### Known limitations

- No existe repositorio Git local configurado con commits (pendiente de inicialización de la rama local `main` y primer commit).
- No se dispone de autenticación remota en GitHub CLI (gh es gitsome, no autenticado).

## Target state (`TARGET`)

- Repositorio Git local inicializado en la rama `main` con un commit inicial que incluya los archivos de gobierno y del blueprint.
- Repositorio remoto privado creado en GitHub bajo la cuenta del propietario y asociado como remoto `origin`.

## Active constraints

- RUN_ID: `bootstrap-github-2026-07-14`
- Autorización de nivel 3 estricta (no subir secretos, no usar force push, no modificar repos externos).

## Approved decisions

| ADR | Decision | Status | Supersedes |
|---|---|---|---|
| ADR-001 | Licencia como UNKNOWN pendiente de aprobación humana | ACCEPTED | - |

## External systems and contracts

| System | Status | Contract source | Credentials available? | Notes |
|---|---|---|---|---|
| GitHub | CANDIDATE | gh cli | NO | Requiere ejecutar `gh auth login` en el host del usuario |

## Unknowns and conflicts

| ID | Type | Description | Impact | Resolution owner |
|---|---|---|---|---|
| U-001 | UNKNOWN | Autenticación en GitHub CLI (gitsome) | Impide la creación del remoto automáticamente en esta fase | Human Owner |
| U-002 | UNKNOWN | Decisión de Licencia definitiva | Determina las reglas de reutilización legal y distribución | Human Owner |

## Current iteration

- Goal: Bootstrapping local de archivos de gobierno e inicialización del repositorio local.
- Approved tasks: Creación de archivos `.ai/`, configuración de `.gitignore` e inicialización de Git local.

## Required reading by role

| Role | Files to read | Files not needed |
|---|---|---|
| `@director` | `GEMINI.md`, `.agents/policies/` | - |
| `@product` | `.ai/PROJECT_CHARTER.md`, `README.md` | - |

## Context change log

| Version | Date | Change | Evidence |
|---|---|---|---|
| 0.1 | 2026-07-14 | Creación de archivos `.ai/` y configuración de `.gitignore` | `find .ai/` |
