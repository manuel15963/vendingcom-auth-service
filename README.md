# vendingcom-auth-service

Microservicio de **autenticación y gestión de accesos** del sistema de máquinas expendedoras de **VENDING.COM S.A.C**. Emite los tokens JWT que consumen los demás microservicios y centraliza usuarios, roles, recuperación de contraseña y auditoría.

## Stack

- **Java 17** + **Spring Boot 3.5**
- **Spring WebFlux** (reactivo) + **R2DBC** (PostgreSQL)
- **Spring Security** + **JWT** (jjwt)
- **Resend** (correo transaccional de recuperación)
- **Arquitectura hexagonal** (puertos y adaptadores)
- Documentación con **Swagger / OpenAPI**

## Arquitectura

```
domain/          → modelos puros (AuthUser, AuthRole...) y excepciones de negocio
application/     → casos de uso, servicios, DTOs y puertos (interfaces)
infrastructure/  → adaptadores REST (controllers), persistencia R2DBC, correo
util/            → seguridad (JWT), auditoría, contexto de request
```

## Roles

| Código | Descripción |
|--------|-------------|
| `ADMIN` | Gestiona usuarios, roles y revisa auditoría |
| `SUPERVISOR` | Consulta usuarios, roles e información operativa |
| `OPERATOR` | Usa la APK en campo (visitas, inventario, recaudación) |

## Endpoints principales

| Método | Ruta | Acceso |
|--------|------|--------|
| `POST` | `/api/v1/auth/login` | Público |
| `GET` | `/api/v1/auth/me` | Autenticado (datos frescos de BD) |
| `PATCH` | `/api/v1/auth/password/me` | Autenticado |
| `PATCH` | `/api/v1/auth/password/users/{userId}` | ADMIN (reset de contraseña) |
| `POST` | `/api/v1/auth/password/recovery/request` | Público |
| `POST` | `/api/v1/auth/password/recovery/confirm` | Público |
| `GET/POST/PUT/PATCH/DELETE` | `/api/v1/auth/users/**` | ADMIN / SUPERVISOR según método |
| `GET` | `/api/v1/auth/roles/**` | ADMIN / SUPERVISOR |
| `GET` | `/api/v1/auth/audit-logs/**` | ADMIN |

Documentación interactiva: `/<SPRINGDOC_SWAGGER_UI_PATH>` (Swagger UI).

## Seguridad

- Contraseñas y códigos de recuperación **hasheados** (BCrypt); nunca en texto plano.
- Política de contraseña: **mínimo 8 caracteres, con al menos una letra y un número**.
- **Bloqueo temporal automático**: tras 5 intentos de login fallidos, la cuenta se bloquea 15 minutos.
- **Auditoría**: el "quién ejecutó" cada acción se deriva del **JWT autenticado**, no del cuerpo de la petición.
- Recuperación de contraseña con expiración, máximo de intentos y *cooldown* anti-spam.

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `PORT` | Puerto del servicio |
| `SPRING_APPLICATION_NAME` | Nombre de la app |
| `SPRING_R2DBC_URL` / `_USERNAME` / `_PASSWORD` | Conexión R2DBC a PostgreSQL |
| `SPRING_SQL_INIT_MODE` | `always` para ejecutar los scripts de `database/` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos separados por coma |
| `JWT_SECRET` | Clave de firma del JWT (≥ 256 bits) |
| `JWT_EXPIRATION_MINUTES` | Vigencia del token |
| `MAIL_FROM` / `RESEND_API_KEY` | Envío de correos con Resend |
| `PASSWORD_RECOVERY_EXPIRATION_MINUTES` / `_MAX_ATTEMPTS` / `_COOLDOWN_MINUTES` | Parámetros de recuperación |
| `MANAGEMENT_HEALTH_R2DBC_ENABLED` / `_MAIL_ENABLED` | Health checks |
| `LOGGING_LEVEL_R2DBC` / `_APP` | Niveles de log |
| `SPRINGDOC_*` | Rutas y orden de Swagger |

## Base de datos

Los scripts viven en `database/` y se ejecutan en orden:

1. `01_create_auth_tables.sql` — tablas
2. `02_create_auth_comments.sql` — comentarios
3. `03_create_auth_indexes.sql` — índices
4. `04_create_auth_triggers.sql` — triggers (`updated_at`)
5. `05_insert_auth_seed_data.sql` — parámetros y roles base

## Ejecutar en local

```bash
# Configura las variables de entorno (ver tabla) y luego:
./mvnw spring-boot:run
```

Build del artefacto:

```bash
./mvnw clean package
```
