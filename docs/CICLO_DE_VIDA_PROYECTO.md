# Ciclo de vida de un proyecto gestionado por agentes

## 1. Registro

Entrada: idea del usuario.

Salida:

- project ID;
- charter;
- constitución;
- repositorio o ubicación;
- propietario;
- estado `NEEDS_APPROVAL`.

No se escribe código.

## 2. Descubrimiento

Los agentes inspeccionan:

- repositorio actual;
- documentación;
- referencias aprobadas;
- restricciones;
- dependencias;
- riesgos.

Salida: contexto verificable. Los huecos permanecen como `UNKNOWN`.

## 3. Especificación

`@product` formaliza requisitos y criterios de aceptación.

Puerta: aprobación humana de alcance, no objetivos y criterios principales.

## 4. Arquitectura

`@architect` separa estado actual, opciones y estado objetivo. Produce ADR.

Puerta: aprobación humana de decisiones de alto impacto.

## 5. Planificación

`@planner` crea tareas con un resultado principal. Solo las tareas sin bloqueos pasan a `READY`.

## 6. Preparación de ejecución

El Director:

- selecciona tarea;
- crea run ID;
- crea worktree;
- asigna rol;
- carga contexto mínimo;
- evalúa políticas.

## 7. Implementación

Un agente ejecutor modifica solo rutas autorizadas y produce evidencia. Termina en `NEEDS_REVIEW`, nunca en `DONE`.

## 8. Revisión independiente

`@reviewer` compara contrato, diff y evidencia. Puede aprobar para QA, solicitar cambios o bloquear.

## 9. QA y seguridad

`@qa` valida criterios. `@security` interviene según riesgo. Un fallo crea rework con un nuevo contrato o una extensión controlada del existente.

## 10. Cierre de tarea

La tarea pasa a `DONE` cuando:

- criterios pasan;
- evidencia existe;
- revisión es favorable;
- no hay gates pendientes para el alcance de la tarea.

Merge, push y deploy no son parte implícita de `DONE`.

## 11. Cierre de iteración

Se genera reporte consolidado. El usuario decide si integra, replanifica o cancela.

## 12. Continuidad

Para reanudar:

1. leer estado Git;
2. cargar `.ai/`;
3. revisar último handoff;
4. validar runs abiertos;
5. reconstruir contexto;
6. elegir siguiente transición válida.

No reanudar desde “lo que recuerdo de la conversación”.

## Puertas humanas obligatorias

Como mínimo:

- aprobación del charter;
- aprobación de arquitectura significativa;
- uso de secretos o datos reales;
- dependencia que cambia licencia o superficie operativa;
- migración destructiva;
- push/merge/release;
- despliegue externo;
- gasto o recurso cloud;
- aceptación de riesgo alto.

## Manejo de errores

### Fallo de herramienta

Registrar error real, estado `BLOCKED` o reintento limitado. No inventar la salida esperada.

### Salida inválida del modelo

No aplicar cambios. Solicitar corrección estructurada dentro del mismo run o iniciar uno nuevo.

### Contaminación de contexto

Cancelar run, descartar workspace si es necesario, reconstruir contexto y registrar incidente.

### Conflicto Git

No resolver automáticamente si altera lógica o decisiones. Crear tarea de integración.

### Aprobación caducada

Volver a `NEEDS_APPROVAL`.

## Artefactos por etapa

| Etapa | Artefacto obligatorio |
|---|---|
| Registro | PROJECT_CHARTER, PROJECT_CONSTITUTION |
| Descubrimiento | PROJECT_CONTEXT, REFERENCE_ANALYSIS |
| Especificación | REQUIREMENTS, ACCEPTANCE_CRITERIA |
| Arquitectura | ARCHITECTURE, ADR, RISK_REGISTER |
| Planificación | TASK_CONTRACT |
| Ejecución | EVIDENCE_LOG, diff, HANDOFF |
| Validación | matriz AC→evidencia |
| Cierre | ITERATION_REPORT, memoria actualizada |
