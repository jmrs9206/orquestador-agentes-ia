# Reference Analysis

## Identity

- Reference name: `MiroFish`
- URL/path: `https://github.com/jmrs9206/MiroFish.git`
- Owner/upstream: `jmrs9206 / 666ghj`
- Branch: `main`
- Commit/version: `UNKNOWN`
- License: `AGPL-3.0`
- Analyzed on: `2026-07-14`
- Target project: `orquestador-agentes-ia`

## Purpose of analysis

Extraer patrones arquitectónicos para el diseño del orquestador multiproyecto. La investigación se centra en la estructuración de flujos por etapas (pipeline), el aislamiento de roles (Report Agent/Reviewer), el almacenamiento de contexto y estado persistente, y la separación entre la coordinación del flujo (Manager) y la ejecución del agente (Runner).

## Verified structure and stack

Basado en la documentación y la estructura conceptual de MiroFish:
- Separación de Frontend y Backend.
- Backend en Python (FastAPI/Flask) con servicios estructurados para ontología, construcción de perfiles, simulación e interacción.
- Almacenamiento y memoria (conceptualizado con grafos y servicios externos como Zep Cloud/OASIS/CAMEL).

## Main workflow

1. **Construcción del Grafo/Contexto:** Carga y estructuración de materiales de entrada.
2. **Preparación:** Generación de perfiles de agentes, ontología y parámetros de configuración.
3. **Simulación:** Ejecución de agentes interactuando entre sí con paso del tiempo simulado.
4. **Reporte:** Un agente de informes externo analiza los logs y resultados de la simulación.
5. **Exploración:** Interfaz para que el usuario explore el estado final e interactúe con los agentes post-simulación.

## Patterns

| ID | Pattern | Evidence | Classification | Rationale |
|---|---|---|---|---|
| RP-001 | Pipeline de ejecución por etapas | `docs/PATRONES_MIROFISH.md` | `ADOPT` | Permite estructurar el desarrollo del software en fases independientes (Contexto -> Arquitectura -> Tareas -> Pruebas -> Evidencia -> Reporte). |
| RP-002 | Separación Manager / Runner | `docs/PATRONES_MIROFISH.md` | `ADOPT` | El motor de workflows (Manager) es independiente del ejecutor del agente (Runner), aislando fallos de red o APIs. |
| RP-003 | Estado explícito y persistente | `docs/PATRONES_MIROFISH.md` | `ADOPT` | El ciclo de vida del proyecto y las tareas usa transiciones de estado explícitas (DRAFT, NEEDS_APPROVAL, RUNNING, completed, failed) en lugar de confirmaciones implícitas. |
| RP-004 | Construcción de contexto estructurado | `docs/PATRONES_MIROFISH.md` | `ADAPT` | Los requisitos y decisiones aprobadas se indexan y estructuran para inyectarse de forma eficiente en los prompts. |
| RP-005 | Perfiles desde contexto | `docs/PATRONES_MIROFISH.md` | `ADAPT` | Se generan perfiles temporales de agentes con restricciones y capacidades asociadas a una tarea, sin sobrecargar con personalidades innecesarias. |
| RP-006 | Agente de Reporte separado | `docs/PATRONES_MIROFISH.md` | `ADOPT` | La revisión y validación de la evidencia es realizada por un rol independiente (QA o Reviewer) distinto del implementador de la tarea. |
| RP-007 | Interacción posterior sobre datos | `docs/PATRONES_MIROFISH.md` | `ADAPT` | El usuario final puede consultar decisiones históricas, logs y evidencias almacenadas mediante consultas naturales. |

## Ideas versus literal reuse

| Item | Abstract idea | Literal code needed? | License impact | Decision |
|---|---|---:|---|---|
| Pipeline por etapas con artefactos | Dividir la orquestación en etapas con entradas y salidas de archivos claras | NO | Ninguno | `ADOPT` |
| Separación Manager / Runner | Estructurar la lógica de control separada del cliente LLM / Gemini CLI | NO | Ninguno | `ADOPT` |
| Agente de revisión cruzada | Implementar un flujo donde el autor de un cambio no sea el que lo aprueba | NO | Ninguno | `ADOPT` |
| Lógica del Backend de MiroFish | Lógica específica de simulación social y grafos de ontología dinámica | NO | AGPL-3.0 (Obligaría a abrir el código del orquestador si se copia) | `REJECT` |

## Risks and anti-patterns

- **Confundir simulación con ejecución:** A diferencia de una simulación social, las acciones del orquestador ocurren sobre repositorios y archivos del sistema de forma real. Se requiere un nivel de autorización estricto para evitar destrucciones accidentales.
- **Herencia prematura de dependencias complejas:** La memoria gráfica de MiroFish utiliza bases de datos de grafos avanzadas o Zep Cloud. El primer MVP del orquestador debe usar persistencia simple en archivos Markdown y SQLite para evitar sobreingeniería.
- **Vulneración de licencia AGPL-3.0:** Copiar fragmentos literales de código o prompts estructurados de MiroFish contaminaría legalmente el proyecto. Todo el código debe escribirse desde cero.

## Dependencies not inherited automatically

- Frameworks de agentes (`OASIS`, `CAMEL`).
- Memoria externa persistente (`Zep Cloud`).

## Unknowns

- Cómo maneja MiroFish la concurrencia en la escritura de archivos y si hay colas de trabajo cuando varios agentes operan simultáneamente.

## Recommendation

Se recomienda adoptar las ideas conceptuales de **pipeline por etapas**, **separación de roles ejecutor/revisor** y **separación de control/ejecución**. Para el diseño del MVP, se propone rechazar la inclusión de frameworks de agentes complejos y ontologías dinámicas, decantándose por un diseño ligero utilizando Node.js/TypeScript o Python (según selección de stack) con persistencia en Markdown y SQLite local.

## Sources inspected

- [docs/PATRONES_MIROFISH.md](file:///home/jmrs/Documentos/PROYECTOS/JMRS/orquestadorIA/docs/PATRONES_MIROFISH.md)
