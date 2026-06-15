-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Consultas de validación
-- ============================================================


-- ============================================================
-- 1. Ver tablas creadas del módulo AUTH
-- ============================================================

SELECT
    table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN (
                     'auth_users',
                     'auth_roles',
                     'auth_user_roles',
                     'auth_parameters',
                     'auth_audit_logs'
    )
ORDER BY table_name;


-- ============================================================
-- 2. Ver columnas, tipos de datos y obligatoriedad
-- ============================================================

SELECT
    table_name,
    ordinal_position,
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
                     'auth_users',
                     'auth_roles',
                     'auth_user_roles',
                     'auth_parameters',
                     'auth_audit_logs'
    )
ORDER BY table_name, ordinal_position;


-- ============================================================
-- 3. Ver comentarios de tablas
-- ============================================================

SELECT
    c.relname AS table_name,
    obj_description(c.oid) AS table_comment
FROM pg_class c
         JOIN pg_namespace n
              ON n.oid = c.relnamespace
WHERE n.nspname = 'public'
  AND c.relkind = 'r'
  AND c.relname IN (
                    'auth_users',
                    'auth_roles',
                    'auth_user_roles',
                    'auth_parameters',
                    'auth_audit_logs'
    )
ORDER BY c.relname;


-- ============================================================
-- 4. Ver comentarios de columnas
-- ============================================================

SELECT
    c.relname AS table_name,
    a.attname AS column_name,
    col_description(c.oid, a.attnum) AS column_comment
FROM pg_class c
         JOIN pg_namespace n
              ON n.oid = c.relnamespace
         JOIN pg_attribute a
              ON a.attrelid = c.oid
WHERE n.nspname = 'public'
  AND c.relkind = 'r'
  AND a.attnum > 0
  AND NOT a.attisdropped
  AND c.relname IN (
                    'auth_users',
                    'auth_roles',
                    'auth_user_roles',
                    'auth_parameters',
                    'auth_audit_logs'
    )
ORDER BY c.relname, a.attnum;


-- ============================================================
-- 5. Ver llaves foráneas
-- ============================================================

SELECT
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS referenced_table,
    ccu.column_name AS referenced_column,
    tc.constraint_name
FROM information_schema.table_constraints tc
         JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name = kcu.constraint_name
                  AND tc.table_schema = kcu.table_schema
         JOIN information_schema.constraint_column_usage ccu
              ON ccu.constraint_name = tc.constraint_name
                  AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
  AND tc.table_name IN (
                        'auth_users',
                        'auth_roles',
                        'auth_user_roles',
                        'auth_parameters',
                        'auth_audit_logs'
    )
ORDER BY tc.table_name, kcu.column_name;


-- ============================================================
-- 6. Ver índices
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


-- ============================================================
-- 7. Ver triggers
-- ============================================================

SELECT
    event_object_table AS table_name,
    trigger_name,
    action_timing,
    event_manipulation,
    action_statement
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table, trigger_name;


-- ============================================================
-- 8. Ver registros por tabla
-- ============================================================

SELECT 'auth_users' AS table_name, COUNT(*) AS total FROM auth_users
UNION ALL
SELECT 'auth_roles' AS table_name, COUNT(*) AS total FROM auth_roles
UNION ALL
SELECT 'auth_user_roles' AS table_name, COUNT(*) AS total FROM auth_user_roles
UNION ALL
SELECT 'auth_parameters' AS table_name, COUNT(*) AS total FROM auth_parameters
UNION ALL
SELECT 'auth_audit_logs' AS table_name, COUNT(*) AS total FROM auth_audit_logs
ORDER BY table_name;


-- ============================================================
-- 9. Ver usuarios
-- ============================================================

SELECT
    user_id,
    username,
    email,
    full_name,
    phone_number,
    document_type,
    document_number,
    user_status,
    last_login_at,
    created_by_user_id,
    updated_by_user_id,
    created_at,
    updated_at
FROM auth_users
ORDER BY user_id;


-- ============================================================
-- 10. Ver roles
-- ============================================================

SELECT
    role_id,
    role_code,
    role_name,
    role_description,
    role_status,
    created_by_user_id,
    updated_by_user_id,
    created_at,
    updated_at
FROM auth_roles
ORDER BY role_id;


-- ============================================================
-- 11. Ver usuarios con roles
-- ============================================================

SELECT
    ur.user_role_id,
    u.user_id,
    u.username,
    u.full_name,
    r.role_id,
    r.role_code,
    r.role_name,
    ur.assignment_status,
    ur.assigned_by_user_id,
    ur.assigned_at
FROM auth_user_roles ur
         INNER JOIN auth_users u
                    ON u.user_id = ur.user_id
         INNER JOIN auth_roles r
                    ON r.role_id = ur.role_id
ORDER BY ur.user_role_id;


-- ============================================================
-- 12. Ver parámetros
-- ============================================================

SELECT
    parameter_id,
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status,
    created_by_user_id,
    updated_by_user_id,
    created_at,
    updated_at
FROM auth_parameters
ORDER BY parameter_group, sort_order;


-- ============================================================
-- 13. Ver auditoría
-- ============================================================

SELECT
    audit_log_id,
    affected_user_id,
    action_type,
    affected_table_name,
    affected_record_id,
    action_description,
    old_data,
    new_data,
    ip_address,
    user_agent,
    executed_by_user_id,
    executed_at
FROM auth_audit_logs
ORDER BY audit_log_id DESC;



-- ==========================================
-- RESUMEN DE REGISTROS POR TABLA
-- ==========================================
SELECT 'auth_users' AS table_name, COUNT(*) AS total FROM auth_users
UNION ALL
SELECT 'auth_roles' AS table_name, COUNT(*) AS total FROM auth_roles
UNION ALL
SELECT 'auth_user_roles' AS table_name, COUNT(*) AS total FROM auth_user_roles
UNION ALL
SELECT 'auth_parameters' AS table_name, COUNT(*) AS total FROM auth_parameters
UNION ALL
SELECT 'auth_audit_logs' AS table_name, COUNT(*) AS total FROM auth_audit_logs
ORDER BY table_name;

/* ============================================================
   VALIDACIÓN: auth_password_recovery_codes
   ============================================================ */

SELECT
    recovery_code_id,
    user_id,
    email,
    code_hash,
    used,
    expires_at,
    created_at,
    used_at,
    attempts,
    max_attempts
FROM auth_password_recovery_codes
ORDER BY recovery_code_id DESC;