# Acceptance Criteria

## Metadata

- Project ID: `orquestador-agentes-ia`
- Scope: `project`
- Version: `0.1`
- Status: `DRAFT`

## Criteria

| ID | Linked requirement | Given / precondition | When / action | Then / expected result | Evidence method | Status |
|---|---|---|---|---|---|---|
| AC-001 | FR-001 | Un proyecto nuevo con ID único no registrado | Se invoca la inicialización del proyecto | Se crean los directorios y archivos de gobierno locales correspondientes | manual inspection | PENDING |
| AC-002 | FR-002 | Proyectos A y B registrados con directorios de trabajo aislados | Un agente en Proyecto A intenta acceder al directorio del Proyecto B | La lectura/escritura es bloqueada y se registra un error de acceso | test | PENDING |
| AC-003 | FR-003 | Tarea registrada que requiere el rol `@reviewer` | Un agente con rol `@product` intenta aprobar la tarea | El orquestador rechaza la ejecución indicando violación de permisos de rol | test | PENDING |
| AC-004 | FR-004 | Tarea asignada en la planificación de iteración | Se inicia la ejecución de la tarea | Se genera el archivo `TASK_CONTRACT.md` con entradas y salidas validadas | manual inspection | PENDING |
| AC-005 | FR-005 | Entrada enviada a un agente para procesamiento | Se ejecuta Gemini CLI en el backend | La salida se captura de forma estructurada en formato Markdown o JSON | manual inspection | PENDING |
| AC-006 | FR-006 | Tarea finalizada por el agente de ejecución | Se cierra el ciclo de trabajo de la tarea | Se genera un `EVIDENCE_LOG.md` que incluye los logs de ejecución, los diffs de archivos y el resultado de las pruebas | manual inspection | PENDING |
| AC-007 | FR-007 | Un agente ha realizado cambios en el código de una tarea | Se solicita la revisión independiente de los cambios | El orquestador bloquea que el mismo agente actúe como `@reviewer` de sus propios cambios | test | PENDING |
| AC-008 | FR-008 | Comando externo o irreversible (como push o creación de recurso remoto) planificado | Se ejecuta la acción en el flujo de trabajo | El orquestador se detiene, muestra el comando exacto, su impacto y solicita confirmación interactiva del usuario antes de proceder | manual inspection | PENDING |
| AC-009 | FR-009 | Un proyecto previo tiene configuraciones y decisiones particulares aprobadas | Se inicializa un nuevo proyecto en el orquestador | El nuevo proyecto arranca sin heredar configuración o historial de decisiones del proyecto anterior | manual inspection | PENDING |

## Quality gates

- [ ] Error behavior defined (comportamiento de aislamiento de archivos y errores de roles).
- [ ] Boundary cases defined (intento de anidación de directorios o reescritura).
- [ ] Security-sensitive behavior defined (bloqueo de secretos y control de comandos externos).
- [ ] Accessibility criteria defined when applicable (interfaz de comandos del orquestador legible).
- [ ] Performance target has a measurement method when applicable (tiempos de respuesta en llamadas a Gemini CLI).
- [ ] No criterion depends on an undefined external system (dependencia estricta de Gemini CLI autenticado localmente).

## Evidence mapping

| AC | Run | Command/test | Artifact | Result | Reviewer |
|---|---|---|---|---|---|
| AC-001 | - | - | - | - | - |
| AC-002 | - | - | - | - | - |
| AC-003 | - | - | - | - | - |
| AC-004 | - | - | - | - | - |
| AC-005 | - | - | - | - | - |
| AC-006 | - | - | - | - | - |
| AC-007 | - | - | - | - | - |
| AC-008 | - | - | - | - | - |
| AC-009 | - | - | - | - | - |

## Notes

Los criterios de aceptación marcados como PENDING requieren el desarrollo inicial de la arquitectura del orquestador y la suite de pruebas automatizadas para ser verificados en runtime.
