-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Datos iniciales
-- Objetivo: insertar parámetros y roles base del sistema
-- ============================================================


-- ============================================================
-- 1. PARÁMETROS: ESTADOS DE USUARIO
-- 0 = INACTIVO, 1 = ACTIVO, 2 = BLOQUEADO
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES
    ('USER_STATUS', 'ACTIVE', '1', 'Usuario habilitado para iniciar sesión y operar en el sistema.', 1, 1),
    ('USER_STATUS', 'INACTIVE', '0', 'Usuario deshabilitado administrativamente. No puede iniciar sesión.', 2, 1),
    ('USER_STATUS', 'LOCKED', '2', 'Usuario bloqueado por seguridad, intentos fallidos o decisión administrativa.', 3, 1)
    ON CONFLICT (parameter_group, parameter_code) DO NOTHING;


-- ============================================================
-- 2. PARÁMETROS: ESTADOS DE ROL
-- 0 = INACTIVO, 1 = ACTIVO
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES
    ('ROLE_STATUS', 'ACTIVE', '1', 'Rol disponible para ser asignado a usuarios.', 1, 1),
    ('ROLE_STATUS', 'INACTIVE', '0', 'Rol deshabilitado. No debe asignarse a nuevos usuarios.', 2, 1)
    ON CONFLICT (parameter_group, parameter_code) DO NOTHING;


-- ============================================================
-- 3. PARÁMETROS: ESTADOS DE ASIGNACIÓN DE ROL
-- 0 = INACTIVO, 1 = ACTIVO
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES
    ('ASSIGNMENT_STATUS', 'ACTIVE', '1', 'Asignación de rol vigente para el usuario.', 1, 1),
    ('ASSIGNMENT_STATUS', 'INACTIVE', '0', 'Asignación de rol deshabilitada sin eliminar historial.', 2, 1)
    ON CONFLICT (parameter_group, parameter_code) DO NOTHING;


-- ============================================================
-- 4. PARÁMETROS: TIPOS DE DOCUMENTO
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES
    ('DOCUMENT_TYPE', 'DNI', 'DNI', 'Documento Nacional de Identidad utilizado en Perú.', 1, 1),
    ('DOCUMENT_TYPE', 'CE', 'Carnet de Extranjería', 'Documento de identidad para persona extranjera.', 2, 1),
    ('DOCUMENT_TYPE', 'RUC', 'RUC', 'Registro Único de Contribuyentes para empresas o personas con actividad tributaria.', 3, 1),
    ('DOCUMENT_TYPE', 'PASSPORT', 'Pasaporte', 'Documento internacional de identificación personal.', 4, 1)
    ON CONFLICT (parameter_group, parameter_code) DO NOTHING;


-- ============================================================
-- 5. PARÁMETROS: TIPOS DE ACCIÓN DE AUDITORÍA
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES
    ('AUDIT_ACTION_TYPE', 'LOGIN_SUCCESS', 'LOGIN_SUCCESS', 'Evento registrado cuando un usuario inicia sesión correctamente.', 1, 1),
    ('AUDIT_ACTION_TYPE', 'LOGIN_FAILED', 'LOGIN_FAILED', 'Evento registrado cuando ocurre un intento de inicio de sesión incorrecto.', 2, 1),
    ('AUDIT_ACTION_TYPE', 'USER_CREATED', 'USER_CREATED', 'Evento registrado cuando se crea un nuevo usuario.', 3, 1),
    ('AUDIT_ACTION_TYPE', 'USER_UPDATED', 'USER_UPDATED', 'Evento registrado cuando se modifica información de un usuario.', 4, 1),
    ('AUDIT_ACTION_TYPE', 'USER_LOCKED', 'USER_LOCKED', 'Evento registrado cuando una cuenta es bloqueada por seguridad o administración.', 5, 1),
    ('AUDIT_ACTION_TYPE', 'PASSWORD_CHANGED', 'PASSWORD_CHANGED', 'Evento registrado cuando un usuario cambia o restablece su contraseña.', 6, 1),
    ('AUDIT_ACTION_TYPE', 'ROLE_CREATED', 'ROLE_CREATED', 'Evento registrado cuando se crea un nuevo rol.', 7, 1),
    ('AUDIT_ACTION_TYPE', 'ROLE_ASSIGNED', 'ROLE_ASSIGNED', 'Evento registrado cuando se asigna un rol a un usuario.', 8, 1),
    ('AUDIT_ACTION_TYPE', 'ROLE_REMOVED', 'ROLE_REMOVED', 'Evento registrado cuando se desactiva o retira un rol asignado a un usuario.', 9, 1)
    ON CONFLICT (parameter_group, parameter_code) DO NOTHING;


/* ============================================================
   SEED DATA: auth_password_recovery_codes
   ============================================================ */

/*
   No se insertan datos iniciales en auth_password_recovery_codes.

   Esta tabla se llena dinámicamente cuando un usuario solicita
   recuperación de contraseña desde el endpoint:

   POST /api/v1/auth/password/recovery/request
*/

-- ============================================================
-- 6. ROLES BASE DEL SISTEMA
-- 0 = INACTIVO, 1 = ACTIVO
-- ============================================================

INSERT INTO auth_roles (
    role_code,
    role_name,
    role_description,
    role_status
)
VALUES
    (
        'ADMIN',
        'Administrador',
        'Usuario con acceso administrativo al sistema. Puede gestionar usuarios, roles, parámetros y revisar auditoría.',
        1
    ),
    (
        'SUPERVISOR',
        'Supervisor',
        'Usuario encargado de supervisar operadores, revisar rutas, validar registros y consultar información operativa.',
        1
    ),
    (
        'OPERATOR',
        'Operador',
        'Usuario encargado de usar el APK para registrar visitas, inventario, recaudación e incidencias de máquinas.',
        1
    )
    ON CONFLICT (role_code) DO NOTHING;