# Arquitectura propuesta del Orquestador de Agentes IA

## 1. Visión

El sistema será una **fábrica multiproyecto de ingeniería asistida por IA**. Recibirá una idea, formalizará el contexto, asignará roles, ejecutará tareas mediante Gemini CLI, exigirá evidencia y conservará memoria verificable para continuar el proyecto en el futuro.

El producto no debe construirse como “un chat que escribe código”. Debe construirse como un sistema de workflow con estado, políticas, artefactos y auditoría.

## 2. Principios de diseño

1. **Artefactos antes que conversación.** El estado vive en archivos y, después, en una base de datos; no en la ventana de chat.
2. **Una tarea, un rol, un contrato.** Cada ejecución tiene objetivo, entradas, rutas y criterio de salida.
3. **Aislamiento por proyecto y run.** Se comparte el ejecutable de Gemini, no la sesión ni el workspace mutable.
4. **Evidencia antes que terminado.** El resultado de herramientas es parte del modelo de datos.
5. **Humano en las puertas de alto impacto.** La IA no se autoautoriza para publicar o desplegar.
6. **Procedencia de conocimiento.** Todo patrón externo conserva URL, commit, licencia y clasificación.
7. **MVP local primero.** No introducir infraestructura distribuida antes de demostrar el flujo básico.

## 3. Arquitectura en tres planos

### 3.1 Plano de control

Responsable de decidir **qué debe ocurrir**.

Componentes:

#### Project Registry

Mantiene identidad y configuración de cada proyecto:

- project ID;
- repositorio y raíces;
- estado;
- constitución;
- contexto vigente;
- branch principal;
- namespace de memoria;
- roles habilitados;
- workflow activo.

#### Workflow Engine

Implementa una máquina de estados persistente. No depende de que una sesión de Gemini permanezca abierta.

Estados iniciales recomendados:

```text
DRAFT
  -> CONTEXT_READY
  -> SPEC_NEEDS_APPROVAL
  -> ARCHITECTURE_NEEDS_APPROVAL
  -> PLANNED
  -> TASK_READY
  -> IN_PROGRESS
  -> NEEDS_REVIEW
  -> NEEDS_QA
  -> NEEDS_APPROVAL
  -> DONE

Estados laterales:
BLOCKED, NEEDS_REWORK, CANCELLED, FAILED
```

#### Policy Engine

Evalúa antes de cada acción:

- rol;
- proyecto;
- herramienta;
- ruta;
- nivel de riesgo;
- aprobación vigente;
- presupuesto o límites;
- política de red;
- política de secretos.

Una política devuelve `ALLOW`, `DENY` o `REQUIRE_APPROVAL` con motivo.

#### Dispatcher

Selecciona el siguiente trabajo elegible. En el MVP puede ser secuencial. Más adelante podrá usar cola y concurrencia limitada.

#### Approval Service

Registra aprobaciones como objetos explícitos:

```text
approval_id
project_id
run_id
action_type
target
scope
approved_by
approved_at
expires_at
status
```

No se aceptan aprobaciones implícitas derivadas de frases antiguas.

#### Audit Log

Registra eventos inmutables o append-only:

- creación de proyecto;
- transición de estado;
- asignación de rol;
- invocación de herramienta;
- cambio de política;
- evidencia;
- aprobación;
- error;
- integración.

### 3.2 Plano de ejecución

Responsable de **realizar trabajo** de forma aislada.

#### Gemini CLI Adapter

Única interfaz del orquestador con Gemini CLI. Debe:

- construir el prompt a partir de contrato y contexto;
- lanzar una ejecución independiente;
- seleccionar modo interactivo o headless;
- capturar stdout, stderr, exit code y metadatos;
- exigir salida estructurada;
- aplicar timeout y cancelación;
- evitar que el modelo cambie de proyecto dentro de la misma ejecución;
- normalizar errores.

Contrato conceptual:

```text
execute(run_spec) -> run_result

run_spec:
  project_id
  run_id
  role
  task_contract
  context_files
  workspace
  allowed_tools
  approval_mode
  timeout

run_result:
  status
  model_response
  tool_events
  changed_files
  evidence
  errors
  usage
```

#### Workspace Manager

Crea y destruye workspaces por run:

- lectura: puede usar checkout limpio o modo plan;
- escritura: worktree o clon aislado;
- pruebas peligrosas: sandbox/contenedor;
- paralelo: un worktree por tarea.

#### Tool Broker

Expone herramientas permitidas al agente:

- filesystem limitado;
- Git limitado;
- shell con política;
- test runner;
- documentación/web;
- integraciones futuras.

El agente no recibe acceso irrestricto al host.

#### Artifact Store

Guarda:

- prompts efectivos;
- respuestas;
- planes;
- diffs;
- logs;
- pruebas;
- reportes;
- handoffs;
- outputs estructurados.

En el MVP puede ser filesystem organizado por `project_id/run_id`.

#### Test and Validation Runner

Ejecuta comandos declarados por el proyecto, no comandos inventados por el modelo. Debe aplicar límites de tiempo, tamaño de salida y recursos.

### 3.3 Plano de conocimiento

Responsable de **lo que el sistema sabe y por qué lo cree**.

#### Project Context Store

Conserva charter, constitución, requisitos, arquitectura, ADR, riesgos, criterios y contexto.

#### Reference Library

Almacena análisis de repositorios y documentación externa. Guarda patrones abstractos separados del código fuente.

#### Evidence Store

Relaciona afirmaciones y estados con evidencia:

```text
claim -> source/evidence -> verification time -> confidence
```

#### Memory Graph

Capacidad posterior inspirada en el uso de grafos de MiroFish. Entidades iniciales:

- Project;
- Requirement;
- Constraint;
- Decision;
- Component;
- Interface;
- Task;
- Run;
- AgentRole;
- Artifact;
- Evidence;
- Risk;
- ReferencePattern.

Relaciones posibles:

```text
REQUIREMENT -> SATISFIED_BY -> COMPONENT
DECISION -> CONSTRAINS -> TASK
TASK -> PRODUCES -> ARTIFACT
EVIDENCE -> VERIFIES -> ACCEPTANCE_CRITERION
PATTERN -> ADAPTED_IN -> COMPONENT
RUN -> EXECUTED_AS -> ROLE
RISK -> MITIGATED_BY -> CONTROL
```

El grafo no sustituye los archivos fuente; los indexa y enlaza.

## 4. Modelo de datos mínimo

### Project

- id;
- name;
- root/repository;
- status;
- context version;
- policy profile;
- active iteration;
- created/updated timestamps.

### Task

- id;
- project ID;
- role;
- objective;
- allowed paths;
- acceptance criteria;
- dependencies;
- status;
- risk level.

### Run

- id;
- task ID;
- session ID;
- workspace/worktree;
- prompt/context hash;
- start/end;
- status;
- exit code;
- usage.

### Artifact

- id;
- type;
- path;
- checksum;
- producer run;
- version;
- status.

### Evidence

- id;
- claim or AC;
- command/method;
- output artifact;
- result;
- environment;
- timestamp.

### Decision

- ADR ID;
- status;
- owner;
- alternatives;
- consequences;
- approval.

## 5. Construcción de contexto

El prompt efectivo de una tarea debe componerse en este orden:

1. políticas globales;
2. constitución del proyecto;
3. rol activo;
4. contrato de tarea;
5. contexto mínimo del proyecto;
6. archivos de referencia necesarios;
7. evidencia o feedback previo de la misma tarea.

No cargar todo el historial del proyecto. El contexto excesivo aumenta contradicciones y mezcla responsabilidades.

## 6. Contrato de salida estructurada

El adaptador debe exigir una salida validable, conceptualmente:

```json
{
  "status": "NEEDS_REVIEW",
  "summary": "...",
  "facts": [],
  "assumptions": [],
  "changed_files": [],
  "commands": [],
  "acceptance_results": [],
  "risks": [],
  "next_gate": "REVIEW"
}
```

Si el JSON es inválido, el run no se considera exitoso aunque el texto parezca convincente.

## 7. Estrategia de concurrencia

### MVP

- un run activo de escritura por proyecto;
- múltiples runs de lectura permitidos;
- worktree por tarea;
- integración secuencial.

### Evolución

- locks por archivo o componente;
- grafo de dependencias;
- cola de ejecución;
- límites de recursos;
- cancelación y reintentos idempotentes;
- integración automatizada solo después de gates.

## 8. Observabilidad

Cada evento debe incluir:

- project ID;
- iteration ID;
- task ID;
- run ID;
- role;
- timestamp;
- tool;
- action;
- result;
- duration;
- evidence link.

Métricas iniciales:

- porcentaje de tareas aceptadas a la primera;
- rework por rol;
- fallos de herramientas;
- tiempo por estado;
- tareas bloqueadas por contexto insuficiente;
- claims corregidos por falta de evidencia;
- coste/tokens cuando estén disponibles.

## 9. Seguridad

- confianza y permisos por proyecto;
- sandbox para código externo;
- secretos fuera del contexto del modelo cuando sea posible;
- red denegada por defecto para ejecuciones;
- aprobación para dependencias y acciones externas;
- logs sin secretos;
- políticas de retención;
- límites de comandos y rutas.

## 10. Límites del MVP

El primer MVP debe demostrar:

1. crear dos proyectos distintos;
2. conservar contextos separados;
3. generar una tarea por proyecto;
4. ejecutar Gemini CLI en worktrees distintos;
5. producir diffs y evidencia;
6. revisar con rol separado;
7. reanudar desde disco;
8. impedir una acción prohibida.

No necesita todavía:

- miles de agentes;
- grafo externo administrado;
- microservicios;
- Kubernetes;
- despliegue autónomo;
- auto-merge;
- planificación económica avanzada.
