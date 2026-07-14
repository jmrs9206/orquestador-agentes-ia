# ADR-002: Diseño del Módulo Project Registry (Registro de Proyectos)

- Status: `PROPOSED`
- Date: `2026-07-14`
- Project ID: `orquestador-agentes-ia`
- Decision owners: `@architect` / `Propietario Humano`
- Supersedes: `none`

## Context

El primer incremento técnico del orquestador es el **Project Registry**, un módulo que cataloga y gestiona lógicamente los diferentes proyectos locales. Debemos definir el modelo de datos de un proyecto, la máquina de estados permitida, las validaciones de entrada, los endpoints REST expuestos y el esquema de base de datos MySQL correspondiente.

## Decision drivers

- **Consistencia de estado:** Transiciones de estado explícitas y validadas por el backend.
- **Aislamiento físico:** Control estricto de la ruta local del repositorio para evitar fugas de contexto.
- **Borrado lógico (Archivado):** Los proyectos no deben eliminarse físicamente de la base de datos para preservar la trazabilidad histórica de ejecuciones y auditorías.
- **Validación robusta:** Comprobación de que la ruta local existe en el host antes de confirmar el registro.

## Diseño propuesto

### 1. Modelo de datos de un Proyecto

- `id` (VARCHAR(36), PK): UUID generado en el backend.
- `key` (VARCHAR(50), UNIQUE): Clave corta y amigable para el CLI/URLs (ej. `mi-proyecto-web`).
- `name` (VARCHAR(100)): Nombre descriptivo del proyecto.
- `description` (TEXT): Breve resumen del propósito.
- `status` (VARCHAR(20)): Estado actual del ciclo de vida.
- `repositoryPath` (VARCHAR(255)): Ruta absoluta en el disco del host.
- `defaultBranch` (VARCHAR(50)): Rama Git por defecto (por defecto `main`).
- `contextPath` (VARCHAR(255)): Carpeta de control de agentes (por defecto `.ai`).
- `createdAt` (TIMESTAMP)
- `updatedAt` (TIMESTAMP)
- `archivedAt` (TIMESTAMP, NULL): Fecha de archivado (indica borrado lógico).

### 2. Máquina de Estados y Transiciones Permitidas

Los estados iniciales y sus transiciones son rígidos:

```text
       +-------+
       | DRAFT |
       +---+---+
           |
           v
      +----+---+     (pause)      +--------+
      | ACTIVE | -------------->  | PAUSED |
      +----+---+ <--------------  +----+---+
        |    ^                         |
        |    | (unblock)               |
        v    |                         |
      +-+----+--+                      |
      | BLOCKED |                      |
      +----+----+                      |
           |                           |
           +------------+--------------+
                        |
                        v
                   +----+----+
                   |ARCHIVED |
                   +---------+
```

#### Reglas de Transición de Estados:
*   **De DRAFT a:**
    *   `ACTIVE`: Transición estándar cuando el proyecto se configura inicialmente.
    *   `ARCHIVED`: Borrado lógico directo antes de entrar en producción.
*   **De ACTIVE a:**
    *   `PAUSED`: Pausa manual del orquestador.
    *   `BLOCKED`: Bloqueo automático por incidentes de seguridad (ej. detección de secretos staged o violación de políticas).
    *   `ARCHIVED`: Archivado del proyecto.
*   **De PAUSED a:**
    *   `ACTIVE`: Reanudación.
    *   `ARCHIVED`: Archivado.
*   **De BLOCKED a:**
    *   `ACTIVE`: Tras resolución manual por parte del operador humano.
    *   `ARCHIVED`: Archivado.
*   **ARCHIVED (Estado Final):** No se permiten transiciones salientes en el ciclo de vida del MVP. Cualquier intento de ejecutar tareas en un proyecto `ARCHIVED` o `BLOCKED` será rechazado.

### 3. Endpoints de la API REST

Los endpoints serán documentados formalmente usando OpenAPI v3:

*   `POST /api/projects`
    *   *Propósito:* Registrar un proyecto.
    *   *Entrada:* JSON con `key`, `name`, `description`, `repositoryPath`, `defaultBranch`, `contextPath`.
    *   *Validación:* Validar campos requeridos, verificar que `repositoryPath` existe localmente en el host y que `key` es alfanumérica única.
    *   *Salida:* `201 Created` con el proyecto en estado `DRAFT`.
*   `GET /api/projects`
    *   *Propósito:* Listar proyectos. Admite filtros como query params (ej. `?includeArchived=false`).
    *   *Salida:* `200 OK` con un array JSON de proyectos.
*   `GET /api/projects/{id}`
    *   *Propósito:* Consultar un proyecto específico por ID o por `key`.
    *   *Salida:* `200 OK` o `404 Not Found`.
*   `PATCH /api/projects/{id}`
    *   *Propósito:* Actualizar metadatos permitidos.
    *   *Entrada:* JSON parcial (`name`, `description`, `defaultBranch`).
    *   *Salida:* `200 OK` con el proyecto actualizado.
*   `PUT /api/projects/{id}/archive`
    *   *Propósito:* Archivar el proyecto (borrado lógico).
    *   *Salida:* `200 OK` con el estado actualizado a `ARCHIVED` y `archivedAt` registrado.

### 4. Esquema de Base de Datos MySQL

Esquema que se creará mediante migraciones controladas (Flyway o Liquibase):

```sql
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(36) PRIMARY KEY,
    `key` VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    repository_path VARCHAR(255) NOT NULL,
    default_branch VARCHAR(50) NOT NULL DEFAULT 'main',
    context_path VARCHAR(255) NOT NULL DEFAULT '.ai',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    archived_at TIMESTAMP NULL,
    INDEX idx_project_status (status),
    INDEX idx_project_key (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## Consequences

### Positive
- **Validaciones de negocio robustas:** El motor de Spring Boot interceptará e impedirá cualquier transición inválida o ruta local falsa.
- **Trazabilidad:** Al no borrar filas de la tabla `projects`, mantenemos la integridad referencial con los registros históricos de tareas y logs de ejecuciones.

### Negative
- **Aislamiento en Host local:** El backend de Java deberá tener permisos del sistema operativo para verificar la existencia del directorio Git (`repositoryPath`) en la máquina host, lo que requiere configurar adecuadamente los límites de permisos de Docker si el backend se ejecuta dockerizado.

## Approval

`PENDING` (Requiere aprobación del Propietario Humano para iniciar implementación)
