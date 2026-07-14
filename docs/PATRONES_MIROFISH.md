# Patrones de MiroFish aplicables y límites de reutilización

## Fuente analizada

- Fork indicado por el usuario: `jmrs9206/MiroFish`.
- Upstream declarado por GitHub: `666ghj/MiroFish`.
- Licencia declarada: AGPL-3.0.
- Uso en este proyecto: referencia de ideas y patrones; no base de código automática.

## Qué hace MiroFish a alto nivel

Su README organiza el producto en una secuencia de:

1. construcción de grafo a partir de materiales semilla;
2. preparación del entorno, entidades, perfiles y configuración;
3. simulación con agentes y actualización temporal de memoria;
4. generación de reportes mediante un agente con herramientas;
5. interacción posterior con agentes y resultados.

La estructura observada separa frontend y backend. El backend contiene APIs de grafo, simulación y reportes, además de servicios especializados para ontología, construcción del grafo, generación de perfiles, configuración, ejecución y reporte.

## Patrones que conviene adoptar como ideas

### 1. Pipeline por etapas con artefactos

**En MiroFish:** una etapa prepara entradas para la siguiente.

**Adaptación:** charter → contexto → arquitectura → tareas → cambios → evidencia → reporte.

**Beneficio:** permite reanudar, auditar y repetir una etapa sin rehacer todo.

### 2. Separación Manager / Runner

**En MiroFish:** la gestión de estado y preparación se separa de la ejecución de simulaciones.

**Adaptación:** Workflow Engine y Dispatcher separados de Gemini CLI Adapter y Workspace Manager.

**Beneficio:** el control sobrevive al fallo de una ejecución concreta.

### 3. Estado explícito y persistente

**En MiroFish:** una simulación tiene estados como created, preparing, ready, running, paused, completed y failed.

**Adaptación:** cada proyecto, tarea y run usa estados definidos, no frases libres.

**Beneficio:** evita que “parece terminado” se convierta en una transición.

### 4. Construcción de contexto estructurado

**En MiroFish:** materiales de entrada se transforman en entidades, relaciones, ontología y memoria.

**Adaptación:** requisitos, decisiones, componentes, tareas y evidencia pueden indexarse como un grafo de conocimiento de ingeniería.

**Beneficio:** recuperación contextual precisa y trazabilidad.

### 5. Perfiles o personas generados desde contexto

**En MiroFish:** entidades se convierten en perfiles de agentes para la simulación.

**Adaptación:** el orquestador crea un contrato de rol por tarea usando restricciones del proyecto, sin inventar una personalidad innecesaria.

**Beneficio:** el mismo modelo puede asumir capacidades distintas con límites distintos.

### 6. Report Agent separado

**En MiroFish:** un agente de reporte analiza el resultado después de la simulación.

**Adaptación:** Reviewer, QA y cierre de iteración operan después del ejecutor y no comparten autoría.

**Beneficio:** reduce autoaprobación y mejora la evidencia.

### 7. Interacción posterior sobre un mundo persistido

**En MiroFish:** el usuario puede explorar agentes y resultados después de ejecutar.

**Adaptación:** el usuario puede preguntar por decisiones, tareas, evidencia y estado desde el proyecto persistido.

## Patrones que no deben heredarse automáticamente

- dominio de predicción o simulación social;
- Twitter/Reddit como entornos;
- miles de agentes;
- OASIS o CAMEL como dependencia obligatoria;
- Zep Cloud como memoria obligatoria;
- un modelo de ontología dinámico para el primer MVP;
- paralelismo de gran escala;
- configuración LLM compatible con OpenAI SDK como única opción;
- almacenamiento de estado en archivos sin control de concurrencia para una versión multiusuario.

## Riesgos observados al trasladar ideas

### Confundir simulación con ejecución real

Los agentes del orquestador modificarán repositorios reales. Las políticas deben ser más estrictas que en un sandbox de simulación.

### Convertir personas en autoridad

Una persona convincente no equivale a permiso. Los permisos deben venir del contrato, no del estilo del prompt.

### Externalizar memoria demasiado pronto

El MVP puede usar Markdown y SQLite antes de introducir un grafo administrado.

### Copiar código AGPL

La licencia declarada obliga a evaluar cualquier reutilización literal. El diseño base evita esa dependencia usando reimplementación de patrones.

## Decisión recomendada

- `ADOPT`: pipeline por etapas, estado explícito, separación manager/runner y agente de reporte.
- `ADAPT`: grafo de conocimiento, perfiles por contexto e interacción posterior.
- `EXPERIMENT`: memoria gráfica cuando existan suficientes proyectos y consultas reales.
- `REJECT FOR MVP`: simulación social, dependencias OASIS/Zep obligatorias y swarm masivo.

## Regla de procedencia

Cada idea derivada de MiroFish debe registrarse en un ADR o análisis de referencia. Si la implementación se parece materialmente a un archivo del repositorio, detener la tarea y evaluar licencia antes de continuar.

## Fuentes

- https://github.com/jmrs9206/MiroFish
- https://github.com/jmrs9206/MiroFish/blob/main/README.md
- https://github.com/jmrs9206/MiroFish/blob/main/backend/pyproject.toml
- https://github.com/jmrs9206/MiroFish/tree/main/backend/app/services
