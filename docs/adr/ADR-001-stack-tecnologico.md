# ADR-001: Selección del Stack Tecnológico Definitivo para el Orquestador

- Status: `ACCEPTED`
- Date: `2026-07-14`
- Project ID: `orquestador-agentes-ia`
- Decision owners: `Propietario Humano` / `@architect`
- Supersedes: `ADR-002: Selección del Stack Tecnológico para el MVP` (Python/SQLite anterior)

## Context

El desarrollo del "Orquestador de Agentes IA" requiere un entorno robusto, tipado, con límites claros de responsabilidad y fácil de verificar en integración. El propietario humano ha tomado una decisión de stack tecnológico definitiva y estructurada que sustituye la propuesta inicial de Python + SQLite. Este documento formaliza dicha decisión y establece las directrices arquitectónicas obligatorias.

## Decision drivers

- **Seguridad e integridad de datos:** Persistencia relacional estricta usando MySQL con control estricto de esquema a través de migraciones versionadas.
- **Tipado estricto y mantenibilidad:** Uso de Java y TypeScript en backend y frontend respectivamente, asegurando contratos bien definidos.
- **Límites de dominio claros:** El backend Java es la autoridad única sobre las reglas de negocio, workflows y estados de los agentes, manteniendo al frontend libre de lógica de dominio.
- **Entorno local reproducible:** Configuración unificada mediante Docker Compose y contenedores locales.
- **Validación robusta:** Pruebas integrales con Testcontainers (MySQL real) y Playwright (E2E).

## Stack tecnológico aprobado

### Frontend
- **Framework:** Next.js (React, TypeScript, Tailwind CSS).
- **Responsabilidad:** Presentación visual, renderizado responsivo y consumo de la API REST del backend.
- **Restricción:** Queda terminantemente prohibida la duplicación de reglas de dominio o autorización en el frontend.

### Backend
- **Framework:** Java + Spring Boot (gestión con Maven).
- **Arquitectura:** Monolito modular. Límites claros entre dominio, aplicación, infraestructura y API REST.
- **Responsabilidad:** Autoridad única sobre proyectos, agentes, roles, tareas, workflows, ejecuciones, aprobaciones y auditoría.
- **Documentación:** Contratos HTTP documentados con OpenAPI.

### Datos
- **Motor:** MySQL.
- **Gestión:** Migraciones versionadas (Flyway o Liquibase). Prohibida la modificación manual del esquema fuera de las migraciones.
- **Pruebas:** Uso obligatorio de Testcontainers con MySQL real en las pruebas de integración (prohibido sustituir por bases de datos en memoria como H2).

### Entorno local
- **Orquestación:** Docker Compose (`infrastructure/compose.yaml`).
- **Configuración:** Variables de entorno vía `.env` local (basado en un `.env.example` libre de secretos reales).

### Pruebas
- **Backend:** JUnit para pruebas unitarias e integración.
- **Frontend:** Vitest o Jest para TypeScript.
- **End-to-End (E2E):** Playwright para flujos completos del sistema.

## Monorepo directory map

```text
orquestador-agentes-ia/
├── apps/
│   └── web/                    # Next.js Frontend
├── services/
│   └── orchestrator-api/       # Spring Boot Backend (Maven)
├── packages/
│   ├── contracts/              # Esquemas y contratos compartidos (OpenAPI)
│   └── ui/                     # UI components (si son necesarios)
├── infrastructure/
│   ├── docker/                 # Dockerfiles de soporte
│   └── compose.yaml            # Configuración de Docker Compose
├── docs/
│   └── adr/                    # Registro de Decisiones de Arquitectura (ADR)
├── .agents/                    # Reglas de gobierno de agentes
├── .ai/                        # Contexto, requisitos y tareas del proyecto
├── GEMINI.md
└── README.md
```

## Consequences

### Positive
- **Tipado completo:** Contratos consistentes entre backend (Java) y frontend (TypeScript) reduciendo errores de integración.
- **Auditoría de datos real:** Al probar las integraciones con MySQL real vía Testcontainers, se detectarán problemas de sintaxis SQL y concurrencia antes del despliegue.
- **Modularidad:** El monorepo permite empaquetar contratos y componentes de manera coherente, facilitando la mantenibilidad.

### Negative
- **Complejidad del entorno:** Requiere Docker instalado y activo localmente para ejecutar pruebas de integración y levantar el entorno.
- **Tiempo de inicio:** Mayor tiempo de compilación y empaquetado inicial (Maven + Java + NPM build) en comparación con un stack scriptado.

## References

- Directivas explícitas de stack tecnológico aprobadas por el Propietario Humano el 2026-07-14.

## Approval

`APPROVED` por el Propietario Humano (2026-07-14)
