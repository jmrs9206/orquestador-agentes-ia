# Operación con Gemini CLI y Antigravity

## Enfoque

Antigravity se usará como superficie visual y gestor de proyectos/agentes. Gemini CLI será el ejecutor terminal y, más adelante, la interfaz programática del orquestador.

## Contexto raíz con GEMINI.md

Gemini CLI carga archivos `GEMINI.md` jerárquicamente. Este paquete usa esa capacidad para mantener reglas globales y proyecto-específicas.

La raíz del orquestador contiene un `GEMINI.md` que importa:

- constitución;
- fuentes de verdad;
- política anti-invención;
- seguridad;
- aislamiento;
- definición de terminado;
- registro de roles.

Cada proyecto generado debe tener su propio `GEMINI.md`, basado en `templates/PROJECT_GEMINI.md`, que importe solo su constitución y contexto.

Para inspeccionar el contexto activo:

```text
/memory show
```

Después de editar reglas:

```text
/memory reload
```

## Antigravity

La carpeta `.agents/` contiene:

- `agents.md` para las personas/roles;
- `skills/` para procedimientos;
- `workflows/` para comandos de orquestación.

El proyecto o workspace de Antigravity debe apuntar a la raíz correcta. Un proyecto de Antigravity puede incluir varias carpetas, pero el orquestador debe mantener una raíz autorizada por proyecto y no usar esa flexibilidad para mezclar contextos.

## Modos de Gemini CLI

### Planificación

Usar para:

- investigación;
- cartografía del repo;
- especificación;
- arquitectura;
- revisión inicial.

La planificación debe ser de solo lectura o limitarse a artefactos de plan.

### Ejecución interactiva

Útil durante el desarrollo manual del MVP y cuando el usuario necesita intervenir.

### Ejecución headless

Útil para el adaptador del orquestador:

```text
gemini -p "<prompt>" --output-format json
```

La implementación debe capturar y validar la salida. Para eventos progresivos se puede evaluar JSONL/streaming, pero no es requisito del primer incremento.

### Sandbox

Usar al analizar o ejecutar código no confiable. El sandbox reduce riesgo, pero no sustituye las políticas del orquestador.

### Worktrees

Para tareas paralelas o aislamiento de cambios, cada sesión de escritura debe usar un worktree propio. Gemini CLI dispone de soporte experimental, pero el orquestador también puede gestionar worktrees directamente con Git para no depender de una opción experimental.

## Identidad de una invocación

Cada llamada a Gemini debe incluir en el prompt y en variables del adaptador:

- project ID;
- task ID;
- run ID;
- rol;
- workspace;
- rutas permitidas;
- criterios de aceptación;
- estado esperado de salida.

## Prompt efectivo recomendado

```text
SYSTEM POLICIES
<políticas globales>

PROJECT CONSTITUTION
<reglas del proyecto>

ACTIVE ROLE
<contrato del rol>

TASK CONTRACT
<objetivo, alcance, rutas, criterios>

MINIMUM CONTEXT
<hechos y decisiones relevantes>

OUTPUT SCHEMA
<JSON requerido>
```

No concatenar conversaciones completas ni documentos no relevantes.

## Gestión de sesiones

- Una sesión pertenece a un solo proyecto.
- Reanudar solo si el workspace y la tarea son compatibles.
- Guardar checkpoint antes de una decisión significativa.
- No usar una sesión interactiva como base de memoria permanente.
- Al cerrar, producir handoff y evidencia.

## Aprobación de herramientas

Configuración recomendada para el desarrollo:

- modo normal/default para edición;
- plan para análisis;
- no usar modo de autoaprobación total;
- sandbox para código externo;
- aprobación humana adicional en el propio workflow para acciones de nivel 3.

## Observabilidad del adaptador

Guardar por run:

- versión de Gemini CLI;
- modelo seleccionado;
- hash del contexto;
- prompt o referencia segura al prompt;
- inicio y fin;
- exit code;
- stdout/stderr;
- salida estructurada;
- archivos modificados;
- uso/tokens si están disponibles;
- error normalizado.

## Comandos iniciales de operación manual

```text
/nuevo_proyecto "Construir el MVP del orquestador"
/importar_referencia "https://github.com/jmrs9206/MiroFish.git" <project_id>
/estado_proyecto <project_id>
/ejecutar_iteracion <project_id>
/auditar_proyecto <project_id>
```

## Fuentes oficiales consultadas

- https://geminicli.com/docs/cli/gemini-md/
- https://geminicli.com/docs/cli/tutorials/memory-management/
- https://geminicli.com/docs/cli/headless/
- https://geminicli.com/docs/cli/git-worktrees/
- https://geminicli.com/docs/cli/sandbox/
- https://geminicli.com/docs/cli/settings/
- https://codelabs.developers.google.com/autonomous-ai-developer-pipelines-antigravity
- https://codelabs.developers.google.com/getting-started-google-antigravity
