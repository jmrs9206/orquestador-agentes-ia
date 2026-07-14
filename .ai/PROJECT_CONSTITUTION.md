# Project Constitution

## Metadata

- Project ID: `orquestador-agentes-ia`
- Status: `APPROVED`
- Version: `0.1`
- Approved by: `Propietario Humano`
- Effective from: `2026-07-14`

## Purpose of this constitution

Define project-specific permissions and restrictions. Global policies remain applicable unless this document is stricter.

## Allowed repositories and roots

| Root | Purpose | Read | Write | Notes |
|---|---|---:|---:|---|
| `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA` | Workspace root for the orchestrator development | yes | yes | Main project codebase |

## Prohibited paths

- Any paths outside the workspace directory `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`, except system temp directories for scratch executions or user App Data directory `/home/jmrs/.gemini/antigravity-cli`.

## Allowed roles

- `@director` (Coordinación y control del flujo)
- `@product` (Definición de requisitos y charter)
- `@context` (Verificación de hechos y estado)
- `@architect` (Diseño de la estructura y stack técnico)
- `@planner` (Organización de iteraciones y tareas)
- `@qa` (Validación y pruebas)
- `@reviewer` (Revisión de cambios antes de integrar)

## Project-specific role restrictions

- Ningún agente puede aprobar su propio trabajo o marcar sus propias tareas como completadas sin revisión independiente de otro agente o confirmación humana.
- Prohibida la copia literal o adaptación directa de código de `MiroFish` sin aprobación.

## Technology constraints

- Required: Git, Gemini CLI, Antigravity.
- Allowed: Official GitHub CLI (`gh`) for repository and remote setup (if authenticated).
- Prohibited: Modifying global Git configuration, installing global system packages, using non-standard libraries without approval.
- Versions pinned by: UNKNOWN
- Package manager: UNKNOWN

## Data classification

| Data type | Classification | Storage allowed | External transmission | Notes |
|---|---|---|---|---|
| Secrets, API keys, Credentials | PROHIBITED | NO | NO | Jamás almacenar en el repositorio |
| Código fuente y documentación | INTERNAL | YES | YES | Solo al repositorio remoto autorizado |
| Datos del proyecto gestionado | SENSITIVE | YES | NO | Aislamiento estricto por proyecto |

## Actions requiring human approval

- adding production dependencies;
- changing public API;
- schema migration;
- push/merge/release;
- external deployment;
- use of secrets;
- cloud resource or cost;
- destructive data action;
- license-affecting reuse.

## Testing requirements

- Mandatory commands: Git verification commands (`git status`, checking for secrets in staged files).
- Minimum environments: Local environment.
- Required security checks: Secret detection in staged files.
- Required evidence: Git status and branch output.

## Git policy

- Default branch: `main`
- Branch naming: `main` (para bootstrap), prefixed branches (e.g. `feat/`, `fix/`) for subsequent tasks.
- Worktree policy: UNKNOWN (PROPOSAL: isolated worktrees for concurrent task execution).
- Commit policy: Structured commit messages (e.g., `chore: ...`, `feat: ...`, `fix: ...`).
- Merge policy: Pull Requests with review gate, no force push.

## Definition of done additions

- Criterios de aceptación verificados mediante comandos de comprobación.
- Evidencia adjuntada en el log de ejecuciones.

## Exceptions

| Exception | Scope | Owner | Expiry | Approval |
|---|---|---|---|---|
| Ninguna | - | - | - | - |
