# Project Charter

## Metadata

- Project ID: `orquestador-agentes-ia`
- Project name: `Orquestador de Agentes IA`
- Owner: `Propietario Humano`
- Repository: `/home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA`
- Status: `NEEDS_APPROVAL`
- Version: `0.1`
- Last verified: `2026-07-14`
- Approved by: `PENDING`

## Problem

Actualmente, el desarrollo de software mediante múltiples agentes de IA carece de un marco riguroso de control que aísle contextos, defina de manera explícita los roles y permisos, y mantenga evidencia verificable de las pruebas y decisiones de cada ejecución. Es necesario construir un orquestador multiproyecto que controle el ciclo de vida de los agentes de forma controlada y segura, utilizando Gemini CLI como ejecutor inicial y Antigravity como superficie de operación.

## Intended users

| User/persona | Need | Source | Status |
|---|---|---|---|
| Desarrollador / Operador | Ejecutar y coordinar agentes de IA de forma estructurada con contexto aislado, control de permisos e historial verificable | USER | CONFIRMED |

## Desired outcome

Un entorno seguro y predecible para que los agentes de IA colaboren en el desarrollo de software bajo un gobierno claro, reduciendo al mínimo la invención de hechos, las mezclas de contexto y las acciones destructivas o no autorizadas.

## In scope

Objetivos del primer MVP:
1. Registrar y gestionar varios proyectos de manera independiente.
2. Mantener contexto, memoria y archivos aislados por `project_id`.
3. Asignar agentes con roles y permisos explícitos.
4. Convertir requisitos en contratos de tarea verificables.
5. Ejecutar Gemini CLI con contexto mínimo y salida estructurada.
6. Mantener estados, logs, diffs, pruebas y evidencia por ejecución.
7. Impedir que un agente apruebe su propio trabajo.
8. Detener acciones externas o irreversibles sin autorización humana explícita.
9. Crear proyectos nuevos sin mezclar decisiones de otros proyectos.

## Out of scope / non-goals

El primer MVP excluye explícitamente:
- Despliegue autónomo a producción.
- Auto-merge sin intervención humana.
- Swarm masivo de agentes.
- Orquestación mediante Kubernetes.
- Facturación y control de costes integrados.
- Marketplace de agentes.
- Creación automática de recursos cloud.
- Modificación automática de cuentas externas.
- Publicación automática de paquetes de software.
- Ejecución destructiva de archivos o repositorios.
- Almacenamiento local o remoto de secretos/claves.

## Success criteria

| ID | Criterion | Measurement | Target | Evidence owner |
|---|---|---|---|---|
| SC-001 | Aislamiento estricto de proyectos | Comprobación de que las ejecuciones de un `project_id` no acceden a datos o directorios de otro | Cero fugas de contexto | `@qa` |
| SC-002 | Control de aprobación humana | Intentos de realizar acciones restringidas (como push o creación de repos) sin aprobación son bloqueados | Bloqueo al 100% | `@qa` |
| SC-003 | Generación de evidencias | Toda ejecución completada guarda un registro detallado de logs, diffs y pruebas ejecutadas | Evidencia por tarea | `@reviewer` |

## Constraints

- Technology: Gemini CLI (ejecutor inicial), Antigravity (superficie de operación), Git local y remoto.
- Time: RUN_ID `bootstrap-github-2026-07-14`.
- Budget: UNKNOWN.
- Security/privacy: Aislamiento absoluto de contextos por proyecto. Prohibido guardar secretos.
- Legal/licensing: License decision: UNKNOWN / pendiente de aprobación humana.
- Operations: Ejecución en entorno Linux local mediante CLI.
- Compatibility: UNKNOWN.

## Assumptions

| ID | Assumption | Impact if false | Verification plan | Status |
|---|---|---|---|---|
| AS-001 | Gemini CLI y GitHub CLI están instalados y autenticados en el entorno del usuario | No se pueden ejecutar las herramientas ni crear repositorios remotos | Comprobar disponibilidad de comandos en la fase de inicialización | `VERIFIED` |

## Risks

- Link to `RISK_REGISTER.md` (no creado en el primer MVP, a definir en arquitectura).

## References

- Repositorio de referencia conceptual (sin copia de código ni compatibilidad declarada): `https://github.com/jmrs9206/MiroFish.git`

## Approval gate

- [ ] Problem approved
- [ ] Scope approved
- [ ] Non-goals approved
- [ ] Success criteria approved
- [ ] Constraints reviewed

Approval statement: `PENDING`
