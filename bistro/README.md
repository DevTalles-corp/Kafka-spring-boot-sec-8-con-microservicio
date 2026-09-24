# Bistro

API REST para el ciclo de vida de reservas de un restaurante.

## Requisitos

- JDK 21
- Maven 3.9+

## Arranque en modo desarrollo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

El perfil `dev` utiliza H2 en memoria y carga mesas de ejemplo (capacidades 2, 4, 6 y 8).

## Ejecutar tests

```bash
mvn test
```

## Documentación de la API

Con la aplicación en ejecución:

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

## Escenarios de validación

Ver [`specs/001-reservation-lifecycle/quickstart.md`](specs/001-reservation-lifecycle/quickstart.md).
