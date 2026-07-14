# Roadmap del MVP del Orquestador IA

## Meta del MVP

Demostrar que el sistema puede gestionar dos proyectos diferentes, ejecutar una tarea de código en cada uno con Gemini CLI, conservar aislamiento, exigir revisión y reanudar el trabajo desde artefactos persistentes.

## Fase 0 — Gobierno y contratos

Entregables:

- este paquete Markdown integrado;
- charter del orquestador;
- constitución;
- arquitectura aprobada;
- ADR sobre stack y almacenamiento;
- criterios de aceptación del MVP.

Criterio de salida: la IA puede explicar qué está autorizada a hacer y qué no, y no comienza implementación sin tarea.

## Fase 1 — Registro local de proyectos

Construir:

- CLI o API local para crear/listar/abrir proyectos;
- estructura `.ai/` por proyecto;
- persistencia inicial en SQLite o filesystem versionado;
- IDs y estados;
- validación de rutas.

Criterios:

- crear dos proyectos;
- no sobrescribir uno existente;
- recuperar estado después de reiniciar.

## Fase 2 — Workflow persistente

Construir:

- máquina de estados;
- tareas y runs;
- puertas de aprobación;
- audit log;
- manejo de bloqueos y rework.

Criterios:

- impedir transición inválida;
- registrar quién/qué provocó cada transición;
- reanudar una tarea bloqueada.

## Fase 3 — Gemini CLI Adapter

Construir:

- invocación headless;
- contexto compuesto;
- salida JSON validada;
- timeout, cancelación y errores;
- captura de uso y logs.

Criterios:

- ejecutar una tarea de lectura;
- detectar salida inválida;
- no marcar éxito cuando el proceso falla.

## Fase 4 — Workspace isolation

Construir:

- worktree por tarea de escritura;
- allowed paths;
- diff y estado Git;
- limpieza segura;
- locks básicos por proyecto.

Criterios:

- dos tareas no pisan archivos;
- el agente no puede escribir fuera del workspace;
- un run conserva su evidencia.

## Fase 5 — Roles, revisión y QA

Construir:

- role registry;
- asignación y handoff;
- revisión independiente;
- matriz AC→evidencia;
- definición de terminado automática basada en reglas.

Criterios:

- el autor no puede autoaprobarse;
- una prueba no ejecutada bloquea `DONE` cuando es obligatoria;
- un defecto genera `NEEDS_REWORK`.

## Fase 6 — Biblioteca de referencias

Construir:

- ingesta de repositorios;
- registro de commit/licencia;
- extracción de patrones;
- clasificación ADOPT/ADAPT/EXPERIMENT/REJECT;
- procedencia enlazada a ADR.

Criterios:

- registrar MiroFish sin copiar código;
- rastrear un patrón adoptado a su fuente;
- bloquear reutilización literal sin decisión de licencia.

## Fase 7 — Interfaz de control

Inicialmente puede ser Antigravity + CLI. Una UI propia se justifica cuando existan los flujos estables.

Vista mínima futura:

- proyectos;
- workflow/estado;
- tareas y agentes;
- approvals;
- diffs/evidencia;
- riesgos;
- uso/coste;
- logs.

## Fuera del MVP

- swarm masivo;
- auto-merge;
- deploy autónomo a producción;
- memoria gráfica administrada;
- marketplace de agentes;
- ejecución multiusuario remota;
- facturación;
- Kubernetes;
- optimización automática de prompts.

## Primera iteración recomendada

1. Crear el proyecto del orquestador con las plantillas.
2. Aprobar el charter.
3. Registrar MiroFish como referencia.
4. Crear ADR de stack del MVP.
5. Implementar Project Registry local.
6. Añadir pruebas y evidencia.
7. Revisar y cerrar la iteración sin hacer deploy.
