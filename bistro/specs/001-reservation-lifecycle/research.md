# Research: Ciclo de Vida de una Reserva

Consolidación de Phase 0. Resuelve todos los "NEEDS CLARIFICATION" del Technical Context en [plan.md](plan.md). Formato por decisión: Decision / Rationale / Alternatives considered.

## R1 — Stack y versiones

- **Decision**: Java 21 (LTS), Spring Boot 4.1.0, Maven.
- **Rationale**: Java 21 es la LTS recomendada para proyectos nuevos. Maven es el estándar en entornos de curso/enseñanza. La versión de Spring Boot se verificó contra Maven Central al momento de implementar (2026-08-18): **la última estable publicada es 4.1.0** y el starter `spring-boot-starter-webmvc` existe en el repositorio central. Se usa `spring-boot-starter-parent` 4.1.0.
- **Alternatives considered**: Spring Boot 3.5.3 (línea anterior, fin de soporte OSS cercano; descartado por requerimiento del proyecto de usar 4.x); Java 25 LTS (viable, pero 21 tiene más material y es conservador); Gradle (soportado, sin ventaja decisiva para este proyecto).

## R2 — Dependencias mínimas

- **Decision**: `spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-flyway`, MapStruct 1.6.3 + Lombok 1.18.38, H2 2.4.240 (dev/test), PostgreSQL 42.7.11 + Flyway (prod), springdoc-openapi-starter-webmvc-ui 3.1.0 (documentación), `spring-boot-webmvc-test` para `@AutoConfigureMockMvc`.
- **Rationale**: Cobertura mínima del stack constitucional (web, JPA, Bean Validation, MapStruct). En Spring Boot 4.x el starter web se llama `spring-boot-starter-webmvc`. Flyway dejó de estar en `spring-boot-autoconfigure` y requiere el starter `spring-boot-starter-flyway`. Los tests web con MockMvc requieren el artefacto `spring-boot-webmvc-test` y el paquete `org.springframework.boot.webmvc.test.autoconfigure`. Para MapStruct + Lombok, el procesador de Lombok DEBE declararse antes que el de MapStruct en la configuración de annotation processors (gotcha conocido). springdoc-openapi-starter-webmvc-ui 3.1.0 es la versión compatible con Spring Boot 4.1.x. Versiones concretas verificadas en Maven Central.
- **Alternatives considered**: springdoc-openapi 2.8.x (no compatible con Boot 4.x); Testcontainers (recomendado en general para pruebas con BD real; no disponible en este entorno sin Docker, por lo que el test de concurrencia usa H2 con pool de una sola conexión, ver R4); JUnit 5 + AssertJ como base de testing (elección estándar).
- **Nota sobre Flyway en Boot 4.x**: En Spring Boot 4.x la auto-configuración de Flyway se extrajo a `spring-boot-starter-flyway`. Además, el `FlywayMigrationInitializer` no garantiza por sí solo que JPA espere a que Flyway termine; se añadió un `BeanFactoryPostProcessor` (`FlywayJpaDependencyConfig`) para establecer `entityManagerFactory` como dependiente de `flywayInitializer` en entornos donde Flyway está habilitado. Esto evita que Hibernate `validate` falle porque las tablas aún no existen.

## R3 — Manejo de errores: ProblemDetail (RFC 7807 / RFC 9457)

- **Decision**: `@RestControllerAdvice` propio que extiende `ResponseEntityExceptionHandler`, produciendo respuestas `ProblemDetail` para validación (Bean Validation), 404 de reserva inexistente y demás excepciones del dominio. Mensajes de error orientados al cliente en español (constitución).
- **Rationale**: La constitución exige explícitamente `@RestControllerAdvice` + `ProblemDetail`. Un advice propio permite controlar títulos/mensajes en español y normalizar el `ProblemDetail.errors[]` de validación. Spring Boot 4 incluye además un `ProblemDetailsExceptionHandler` auto-configurado que se activa con `spring.mvc.problemdetails.enabled=true`; se mantiene el advice propio para cumplir la constitución y se desactiva/coexiste la auto-configuración para evitar doble manejo.
- **Alternatives considered**: Confiar únicamente en el handler auto-configurado de Boot 4 (no cumple el mandato literal de `@RestControllerAdvice` propio); devolver errores sin `ProblemDetail` (viola la constitución).

## R4 — Concurrencia: evitar doble asignación de mesa por franja

- **Decision**: **Bloqueo pesimista (`SELECT ... FOR UPDATE` vía `@Lock(PESSIMISTIC_WRITE)`)** sobre la fila de la mesa candidata + **constraint `UNIQUE(assigned_table_id, reservation_time)`** como backstop en BD.
- **Rationale**: El flujo es "buscar mesa libre → asignar". El riesgo está entre el chequeo y el insert (read-then-write). Bloqueando la fila de la mesa ANTES de verificar disponibilidad, dos transacciones concurrentes para la misma mesa se serializan: la segunda ve la mesa ocupada al re-evaluar y resuelve con otra mesa o con REJECTED (Edge Case 4 de la spec). Para baja contención (un restaurante), es el patrón recomendado por las fuentes ("practical default"), correcto bajo contención realista y sin reintentos del cliente. El constraint único es la última línea de defensa contra doble booking incluso si una ruta de código omitiera el lock. El flujo completo (evaluar disponibilidad + crear reserva) vive en un único método `@Transactional` en el servicio.
- **Nota sobre tests de concurrencia**: El bloqueo pesimista funciona correctamente en PostgreSQL. En H2 in-memory el lock a nivel de fila no se comporta de la misma manera con múltiples conexiones; para el test de concurrencia con H2 se fuerza un pool de una sola conexión (`spring.datasource.hikari.maximum-pool-size=1`) y se solicita un tamaño de grupo (partySize 8) que solo puede ser satisfecho por una única mesa, garantizando que solo una reserva se confirme.
- **Alternatives considered**:
  - **Optimistic locking con `@Version`**: no aplica a conflictos de *inserción* (dos reservas nuevas para la misma mesa+slot); la versión protege actualizaciones sobre una misma fila, no inserts concurrentes.
  - **Solo constraint único como fuente de verdad**: correcto a nivel BD, pero una violación de constraint deja la transacción Hibernate en rollback-only y obliga a reintentar en transacciones nuevas para "probar la siguiente mesa", complicando el flujo y el mapeo a REJECTED limpio.
  - **Lock sobre filas de reservation**: no hay filas que lockear antes del primer insert para esa mesa+slot.
- **Nota PENDING**: FR-006 (nacer PENDING, terminar CONFIRMED/REJECTED) se satisface en el ciclo de vida del dominio: la solicitud nace como PENDING conceptual y la transacción atómica persiste directamente el estado terminal. PENDING nunca es observable como fila persistida (no hay ventana de observación en un flujo síncrono e inmediato). El enum `ReservationStatus` contiene los tres estados (no existe ningún otro), cumpliendo la letra y el espíritu de FR-006.
- **Nota franja**: slot = `reservationTime` (fecha + hora exacta, sin duración), según Assumption 1. `LocalDateTime` sin zona + zona local del restaurante documentada en configuración (Assumption 3).

## R5 — Datos y arranque

- **Decision**: Perfil `dev`/test con H2 in-memory (modo PGSQL) y perfil `prod` con PostgreSQL 16 vía docker-compose + Flyway para el esquema. Seed de mesas de ejemplo (dev) para que la decisión de disponibilidad sea comprobable sin feature de gestión de mesas.
- **Rationale**: Zero-fricción para validar el flujo (quickstart sin Docker), manteniendo PostgreSQL como destino real de producción. Flyway garantiza el esquema (incluidos los constraints) de forma reproducible.
- **Alternatives considered**: Testcontainers como única estrategia de testing (más fiel a producción pero exige Docker en todos los entornos; se documenta como alternativa recomendada para el test de integración de concurrencia); HSQLDB (misma fricción que H2 sin ventaja).

## R6 — Testing

- **Decision**: JUnit 5 + Spring Boot Test + AssertJ; test de integración de concurrencia disparando N solicitudes simultáneas a la misma mesa+slot (ver [quickstart.md](quickstart.md)), verificando que solo una confirma.
- **Rationale**: El criterio de éxito SC-003 (nunca dos reservas CONFIRMED en la misma mesa+slot) es el riesgo central y debe probarse con concurrencia real. El resto del flujo se cubre con tests de servicio (estados, asignación, rechazo) y de API (contratos).
- **Alternatives considered**: Solo unit tests de servicio (no demostraría el comportamiento concurrente); Testcontainers para el test de concurrencia (alternativa documentada).

## R7 — Estructura de paquetes (enmienda constitucional, v1.3.0)

- **Decision**: Dentro de cada módulo SOLO existen los sub-packages `controller`, `service`, `repository` y `model`. Distribución de responsabilidades según la constitución vigente:
  - `controller`: adaptadores HTTP (controllers), DTOs de request/response y mappers MapStruct.
  - `service`: lógica de negocio y orquestación.
  - `repository`: interfaces de acceso a datos (Spring Data).
  - `model`: entidades JPA y enums del dominio.
  - `shared` (excepciones/config) es cross-cutting y NO es un módulo de dominio.
- **Rationale**: La enmienda al Principio I lo impone de forma literal: "exactamente `controller`, `service`, `repository` y `model`... NO usar `domain`, `application`, `api`, `infrastructure` ni capas de arquitectura hexagonal". Es una convención de naming, no un cambio de responsabilidades: las mismas responsabilidades de capas se conservan, reubicadas en los cuatro paquetes permitidos. El `shared` no es un módulo de negocio (no es reservations/tables/notifications), por lo que no aplica la restricción de paquetes internos.
- **Alternatives considered**: Arquitectura hexagonal (domain/application/infrastructure) — la estructura del primer borrador del plan; se descarta por violar la enmienda; estructura inicial de la enmienda sin `model` (entidades en `repository`) — se descarta porque la enmienda vigente (v1.3.0) introdujo el paquete `model` para entidades y enums; mantener entidades en `domain` (viola la enmienda); mover el mapper a un paquete `mapper` (viola "exactamente controller/service/repository/model"; se ubica en `controller` por ser la capa de presentación).
- **Nota gobernanza**: la enmienda fue versionada (v1.3.0) y el header de la constitución quedó actualizado. El cumplimiento se evalúa contra el texto vigente.
