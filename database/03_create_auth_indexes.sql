-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Índices para optimización de consultas
-- ============================================================


-- ============================================================
-- 1. ÍNDICES PARA auth_users
-- ============================================================

-- Búsqueda rápida de usuario por username sin importar mayúsculas/minúsculas.
-- Útil para login: operador01, Operador01, OPERADOR01.
CREATE INDEX IF NOT EXISTS idx_auth_users_username_lower
    ON auth_users (LOWER(username));

-- Búsqueda rápida por email sin importar mayúsculas/minúsculas.
-- Útil para login o recuperación de cuenta.
CREATE INDEX IF NOT EXISTS idx_auth_users_email_lower
    ON auth_users (LOWER(email));

-- Búsqueda por número de documento.
-- Útil cuando el administrador busca usuarios por DNI, CE o RUC.
CREATE INDEX IF NOT EXISTS idx_auth_users_document
    ON auth_users (document_type, document_number);

-- Filtro por estado del usuario.
-- Útil para listar usuarios activos, inactivos o bloqueados.
CREATE INDEX IF NOT EXISTS idx_auth_users_status
    ON auth_users (user_status);

-- Consultas de trazabilidad: quién creó registros.
CREATE INDEX IF NOT EXISTS idx_auth_users_created_by
    ON auth_users (created_by_user_id);

-- Consultas de trazabilidad: quién modificó registros.
CREATE INDEX IF NOT EXISTS idx_auth_users_updated_by
    ON auth_users (updated_by_user_id);


-- ============================================================
-- 2. ÍNDICES PARA auth_roles
-- ============================================================

-- Búsqueda rápida por código de rol.
-- Ejemplo: ADMIN, SUPERVISOR, OPERATOR.
CREATE INDEX IF NOT EXISTS idx_auth_roles_code_lower
    ON auth_roles (LOWER(role_code));

-- Filtro por estado del rol.
-- Útil para listar roles activos o inactivos.
CREATE INDEX IF NOT EXISTS idx_auth_roles_status
    ON auth_roles (role_status);

-- Consultas de trazabilidad: quién creó roles.
CREATE INDEX IF NOT EXISTS idx_auth_roles_created_by
    ON auth_roles (created_by_user_id);

-- Consultas de trazabilidad: quién modificó roles.
CREATE INDEX IF NOT EXISTS idx_auth_roles_updated_by
    ON auth_roles (updated_by_user_id);


-- ============================================================
-- 3. ÍNDICES PARA auth_user_roles
-- ============================================================

-- Permite obtener rápido todos los roles de un usuario.
-- Muy usado al iniciar sesión para saber qué rol tiene.
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_user_id
    ON auth_user_roles (user_id);

-- Permite obtener todos los usuarios asociados a un rol.
-- Ejemplo: listar todos los operadores.
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_role_id
    ON auth_user_roles (role_id);

-- Permite filtrar roles activos o inactivos asignados a usuarios.
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_assignment_status
    ON auth_user_roles (assignment_status);

-- Permite consultar quién asignó roles.
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_assigned_by
    ON auth_user_roles (assigned_by_user_id);

-- Índice compuesto para validar rápidamente roles activos por usuario.
-- Útil para login y autorización.
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_user_status
    ON auth_user_roles (user_id, assignment_status);


-- ============================================================
-- 4. ÍNDICES PARA auth_parameters
-- ============================================================

-- Búsqueda de parámetros por grupo.
-- Ejemplo: traer todos los USER_STATUS o DOCUMENT_TYPE.
CREATE INDEX IF NOT EXISTS idx_auth_parameters_group
    ON auth_parameters (parameter_group);

-- Búsqueda de parámetros activos por grupo.
-- Útil para combos o catálogos del frontend.
CREATE INDEX IF NOT EXISTS idx_auth_parameters_group_status_order
    ON auth_parameters (parameter_group, parameter_status, sort_order);

-- Búsqueda directa por grupo y código.
-- Aunque ya existe UNIQUE, este índice ayuda a dejar clara la intención de consulta.
CREATE INDEX IF NOT EXISTS idx_auth_parameters_group_code
    ON auth_parameters (parameter_group, parameter_code);

-- Consultas de trazabilidad: quién creó parámetros.
CREATE INDEX IF NOT EXISTS idx_auth_parameters_created_by
    ON auth_parameters (created_by_user_id);

-- Consultas de trazabilidad: quién modificó parámetros.
CREATE INDEX IF NOT EXISTS idx_auth_parameters_updated_by
    ON auth_parameters (updated_by_user_id);


-- ============================================================
-- 5. ÍNDICES PARA auth_audit_logs
-- ============================================================

-- Consulta de auditoría por usuario afectado.
-- Ejemplo: ver todo lo que ocurrió sobre un usuario.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_affected_user
    ON auth_audit_logs (affected_user_id);

-- Consulta de auditoría por usuario que ejecutó la acción.
-- Ejemplo: ver qué cambios hizo un administrador.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_executed_by
    ON auth_audit_logs (executed_by_user_id);

-- Consulta por tipo de acción.
-- Ejemplo: LOGIN_FAILED, LOGIN_SUCCESS, USER_CREATED.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_action_type
    ON auth_audit_logs (action_type);

-- Consulta por fecha.
-- Útil para reportes de auditoría por rango de tiempo.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_executed_at
    ON auth_audit_logs (executed_at);

-- Consulta combinada por acción y fecha.
-- Útil para revisar eventos recientes de un tipo específico.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_action_date
    ON auth_audit_logs (action_type, executed_at);

-- Consulta por tabla y registro afectado.
-- Útil para ver el historial de un registro específico.
CREATE INDEX IF NOT EXISTS idx_auth_audit_logs_table_record
    ON auth_audit_logs (affected_table_name, affected_record_id);

/* ============================================================
   ÍNDICES: auth_password_recovery_codes
   ============================================================ */

/* Índice para buscar códigos por correo electrónico */
CREATE INDEX IF NOT EXISTS idx_recovery_codes_email
    ON auth_password_recovery_codes(email);

/* Índice para buscar códigos por usuario */
CREATE INDEX IF NOT EXISTS idx_recovery_codes_user_id
    ON auth_password_recovery_codes(user_id);

/* Índice para buscar códigos activos no usados y no vencidos */
CREATE INDEX IF NOT EXISTS idx_recovery_codes_active
    ON auth_password_recovery_codes(email, used, expires_at, attempts, max_attempts);

/* Índice para limpiar códigos vencidos o usados */
CREATE INDEX IF NOT EXISTS idx_recovery_codes_expiration_used
    ON auth_password_recovery_codes(expires_at, used);

-- ============================================================
-- 5. Select para verificar que se crearon los índices
-- ============================================================

SELECT
    tablename AS table_name,
    indexname AS index_name,
    indexdef AS index_definition
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
                    'auth_users',
                    'auth_roles',
                    'auth_user_roles',
                    'auth_parameters',
                    'auth_audit_logs'
    )
ORDER BY tablename, indexname;