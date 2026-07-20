# Orquestador de Agentes IA

## Propósito

Este paquete define **cómo debe comportarse la IA mientras construye el orquestador** y, después, cómo deberá comportarse el propio orquestador cuando cree y mantenga otros proyectos.

No es todavía la implementación del backend del orquestador. Es su **capa de gobierno, contexto y operación**: contratos de agentes, reglas anti-invención, permisos, puertas de aprobación, artefactos obligatorios, flujos de Antigravity y plantillas para cada proyecto.

## Objetivo del producto

Construir una plataforma capaz de gestionar varios proyectos de software mediante agentes de IA especializados. Cada proyecto tendrá:

- contexto, objetivos y criterios de aceptación propios;
- roles habilitados y restricciones específicas;
- repositorio, rama o worktree, sesión y memoria aislados;
- tareas con entradas, salidas y permisos explícitos;
- trazabilidad de decisiones y evidencia de pruebas;
- aprobación humana en las operaciones irreversibles o externas.

El ejecutor inicial será Gemini CLI, ya instalado por el usuario y utilizado desde Antigravity. Se comparte el **binario y la autenticación**, pero no se comparte una sesión activa ni memoria de trabajo entre proyectos.

## Principio rector

> La IA puede proponer, investigar, planificar, implementar y verificar. No puede convertir una suposición en un hecho, inventar evidencia, ampliar el alcance silenciosamente ni aprobar su propio trabajo.

## Estructura

```text
.
├── GEMINI.md
├── README.md
├── .agents/
│   ├── agents.md
│   ├── policies/
│   ├── skills/
│   └── workflows/
├── docs/
└── templates/
```

### Archivos cargados como contexto permanente

`GEMINI.md` importa las reglas críticas de `.agents/policies/` y el registro de roles de `.agents/agents.md`. Estas reglas forman la constitución raíz del workspace.

### Archivos operativos de Antigravity

- `.agents/agents.md`: roles que puede asumir la IA.
- `.agents/skills/*.md`: procedimientos especializados, con entradas, salidas y prohibiciones.
- `.agents/workflows/*.md`: comandos para encadenar roles y puertas de control.

### Plantillas por proyecto

Cada proyecto creado por el orquestador debe recibir una copia de las plantillas necesarias dentro de una carpeta de control, recomendada como `.ai/`:

```text
proyecto/
├── GEMINI.md
├── .ai/
│   ├── PROJECT_CHARTER.md
│   ├── PROJECT_CONSTITUTION.md
│   ├── PROJECT_CONTEXT.md
│   ├── REQUIREMENTS.md
│   ├── ACCEPTANCE_CRITERIA.md
│   ├── ARCHITECTURE.md
│   ├── RISK_REGISTER.md
│   ├── EVIDENCE_LOG.md
│   └── runs/
└── codigo-del-proyecto/
```

## Instalación en el repositorio del orquestador

1. Copiar el contenido de este paquete a la raíz del repositorio del orquestador.
2. Abrir esa raíz como workspace o proyecto de Antigravity.
3. En Gemini CLI, ejecutar `/memory reload` para volver a cargar `GEMINI.md`.
4. En Antigravity, recargar el workspace para que detecte `.agents/agents.md`, skills y workflows.
5. Iniciar con uno de estos comandos:

```text
/nuevo_proyecto "descripción de la idea"
/importar_referencia "URL o ruta del repositorio"
/estado_proyecto <project_id>
/ejecutar_iteracion <project_id>
/auditar_proyecto <project_id>
```

## Secuencia recomendada para el primer MVP

1. Ejecutar `/nuevo_proyecto` para el propio orquestador.
2. Aprobar el `PROJECT_CHARTER.md` y la constitución del proyecto.
3. Ejecutar `/importar_referencia` para registrar el fork de MiroFish como fuente de patrones, no como código a copiar.
4. Diseñar la arquitectura mínima del plano de control.
5. Planificar una iteración corta.
6. Ejecutar tareas de una en una, cada una en un worktree o workspace aislado.
7. Exigir revisión independiente y evidencia antes de marcar una tarea como terminada.

## Qué automatiza este paquete

- separación de roles;
- preparación y validación de contexto;
- análisis de referencias con procedencia y licencia;
- diseño, planificación, implementación y revisión por etapas;
- control de permisos por tarea;
- registro de hechos, suposiciones, decisiones y desconocidos;
- creación de handoffs entre agentes;
- definición de terminado basada en evidencia;
- continuidad de un proyecto sin depender de la memoria conversacional.

## Qué no automatiza todavía

- scheduler persistente;
- base de datos de proyectos y ejecuciones;
- colas de trabajo;
- API del orquestador;
- interfaz web propia;
- adaptador programático de Gemini CLI;
- sandbox físico o contenedores;
- gestión real de secretos;
- creación automática de pull requests;
- despliegues en producción.

Esas capacidades pertenecen al código que se construirá siguiendo estas reglas.

## Regla sobre MiroFish

El fork `jmrs9206/MiroFish` se usa como **fuente de conocimiento arquitectónico**. Sus patrones pueden estudiarse y reimplementarse de forma independiente. No se copiará código, prompts, assets ni configuraciones sin una decisión explícita de licencia y procedencia. El repositorio declara licencia AGPL-3.0, por lo que cualquier reutilización literal debe tratarse como una decisión legal y técnica, no como una acción automática.

## Versionado del paquete

- Versión: `0.2`
- Fecha: `2026-07-14`
- Estado: `BASELINE PARA IMPLEMENTACIÓN DEL MVP`
