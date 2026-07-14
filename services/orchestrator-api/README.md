# Spring Boot Backend API

This directory is designated for the Java + Spring Boot backend monolito modular.

## Stack
- Language: Java 21
- Framework: Spring Boot 3.x
- Build Tool: Maven
- Database: MySQL
- Migrations: Flyway or Liquibase
- API Documentation: OpenAPI / Swagger UI
- Testing: JUnit 5, Testcontainers

## Structure (Proposed)
Following a clean modular monolito architecture:
- `src/main/java/com/orquestador/orchestrator`
  - `/domain`: Core entities, values, ports (free of external frameworks).
  - `/application`: Use cases, orchestrating domain logic.
  - `/infrastructure`: Database adapters, sub-process execution adapter, configuration.
  - `/api`: REST controllers and OpenAPI contracts mapping.
