-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Comentarios de tablas y columnas
-- ============================================================


-- ============================================================
-- TABLA: auth_users
-- ============================================================

COMMENT ON TABLE auth_users IS
'Tabla principal de usuarios del sistema VENDING.COM S.A.C. Almacena las cuentas individuales que pueden acceder al sistema, incluyendo credenciales hasheadas, datos básicos del usuario, estado de acceso y trazabilidad de creación o modificación.';

COMMENT ON COLUMN auth_users.user_id IS
'Identificador único del usuario. Es generado automáticamente por PostgreSQL y no debe enviarse desde Postman, frontend o aplicación móvil.';

COMMENT ON COLUMN auth_users.username IS
'Nombre de usuario utilizado para iniciar sesión en el sistema. Debe ser único. Ejemplo: operador01, supervisor01, admin.';

COMMENT ON COLUMN auth_users.email IS
'Correo electrónico del usuario. Puede utilizarse para login, recuperación de cuenta, notificaciones o identificación del usuario. Debe ser único.';

COMMENT ON COLUMN auth_users.password_hash IS
'Contraseña del usuario almacenada como hash seguro. Nunca debe guardarse la contraseña en texto plano.';

COMMENT ON COLUMN auth_users.full_name IS
'Nombre completo del usuario. Se muestra en la aplicación, reportes, auditoría y pantallas administrativas.';

COMMENT ON COLUMN auth_users.phone_number IS
'Número telefónico o celular del usuario. Sirve como dato de contacto operativo o administrativo.';

COMMENT ON COLUMN auth_users.document_type IS
'Tipo de documento del usuario. Ejemplo: DNI, CE, RUC o PASSPORT. Este valor debe estar controlado por el grupo DOCUMENT_TYPE de la tabla auth_parameters.';

COMMENT ON COLUMN auth_users.document_number IS
'Número de documento de identidad del usuario. Permite identificar administrativamente a la persona registrada.';

COMMENT ON COLUMN auth_users.user_status IS
'Estado actual del usuario dentro del sistema. Ejemplo: ACTIVE, INACTIVE o LOCKED. Este valor debe estar controlado por el grupo USER_STATUS de auth_parameters.';

COMMENT ON COLUMN auth_users.last_login_at IS
'Fecha y hora del último inicio de sesión exitoso del usuario. Sirve para control de acceso y monitoreo de actividad.';

COMMENT ON COLUMN auth_users.created_by_user_id IS
'Usuario que creó este registro. Apunta a auth_users.user_id. Puede ser NULL para el primer usuario administrador creado por inicialización del sistema.';

COMMENT ON COLUMN auth_users.updated_by_user_id IS
'Usuario que realizó la última modificación del registro. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_users.created_at IS
'Fecha y hora en la que se creó el usuario en el sistema.';

COMMENT ON COLUMN auth_users.updated_at IS
'Fecha y hora de la última modificación del usuario.';


-- ============================================================
-- TABLA: auth_roles
-- ============================================================

COMMENT ON TABLE auth_roles IS
'Tabla que almacena los roles o perfiles generales del sistema. Define el nivel de acceso de cada usuario, como ADMIN, SUPERVISOR u OPERATOR.';

COMMENT ON COLUMN auth_roles.role_id IS
'Identificador único del rol. Es generado automáticamente por PostgreSQL.';

COMMENT ON COLUMN auth_roles.role_code IS
'Código técnico único del rol. Ejemplo: ADMIN, SUPERVISOR, OPERATOR. Se usa internamente en backend y reglas de seguridad.';

COMMENT ON COLUMN auth_roles.role_name IS
'Nombre visible del rol para mostrar en pantallas administrativas. Ejemplo: Administrador, Supervisor, Operador.';

COMMENT ON COLUMN auth_roles.role_description IS
'Descripción funcional del rol. Explica qué responsabilidad o nivel de acceso representa dentro del sistema.';

COMMENT ON COLUMN auth_roles.role_status IS
'Estado del rol. Ejemplo: ACTIVE o INACTIVE. Este valor debe estar controlado por el grupo ROLE_STATUS de auth_parameters.';

COMMENT ON COLUMN auth_roles.created_by_user_id IS
'Usuario que creó el rol. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_roles.updated_by_user_id IS
'Usuario que modificó el rol por última vez. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_roles.created_at IS
'Fecha y hora en la que se creó el rol.';

COMMENT ON COLUMN auth_roles.updated_at IS
'Fecha y hora de la última modificación del rol.';


-- ============================================================
-- TABLA: auth_user_roles
-- ============================================================

COMMENT ON TABLE auth_user_roles IS
'Tabla intermedia que relaciona usuarios con roles. Permite que un usuario tenga uno o más roles activos dentro del sistema sin duplicar información.';

COMMENT ON COLUMN auth_user_roles.user_role_id IS
'Identificador único de la relación usuario-rol. Es generado automáticamente por PostgreSQL.';

COMMENT ON COLUMN auth_user_roles.user_id IS
'Usuario al que se le asigna el rol. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_user_roles.role_id IS
'Rol asignado al usuario. Apunta a auth_roles.role_id.';

COMMENT ON COLUMN auth_user_roles.assigned_by_user_id IS
'Usuario administrador o supervisor que asignó el rol. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_user_roles.assignment_status IS
'Estado de la asignación del rol. Ejemplo: ACTIVE o INACTIVE. Permite desactivar una asignación sin eliminar historial.';

COMMENT ON COLUMN auth_user_roles.assigned_at IS
'Fecha y hora en la que se asignó el rol al usuario.';


-- ============================================================
-- TABLA: auth_parameters
-- ============================================================

COMMENT ON TABLE auth_parameters IS
'Tabla genérica de parámetros controlados del módulo AUTH. Permite administrar catálogos como estados de usuario, estados de rol, tipos de documento, tipos de acción de auditoría y otros valores reutilizables sin crear muchas tablas pequeñas.';

COMMENT ON COLUMN auth_parameters.parameter_id IS
'Identificador único del parámetro del módulo AUTH. Es generado automáticamente por PostgreSQL.';

COMMENT ON COLUMN auth_parameters.parameter_group IS
'Grupo al que pertenece el parámetro. Ejemplo: USER_STATUS, ROLE_STATUS, DOCUMENT_TYPE, AUDIT_ACTION_TYPE, ASSIGNMENT_STATUS.';

COMMENT ON COLUMN auth_parameters.parameter_code IS
'Código técnico del parámetro. Ejemplo: ACTIVE, INACTIVE, LOCKED, DNI, LOGIN_SUCCESS, USER_CREATED.';

COMMENT ON COLUMN auth_parameters.parameter_value IS
'Valor visible del parámetro que puede mostrarse en la aplicación. Ejemplo: Activo, Bloqueado, Documento Nacional de Identidad.';

COMMENT ON COLUMN auth_parameters.parameter_description IS
'Descripción funcional del parámetro. Explica para qué sirve el código dentro del sistema.';

COMMENT ON COLUMN auth_parameters.sort_order IS
'Orden de visualización del parámetro en listas, combos o catálogos del frontend.';

COMMENT ON COLUMN auth_parameters.parameter_status IS
'Estado del parámetro. Ejemplo: ACTIVE o INACTIVE. Permite ocultar o deshabilitar valores sin eliminarlos.';

COMMENT ON COLUMN auth_parameters.created_by_user_id IS
'Usuario que creó el parámetro. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_parameters.updated_by_user_id IS
'Usuario que modificó el parámetro por última vez. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_parameters.created_at IS
'Fecha y hora en la que se creó el parámetro.';

COMMENT ON COLUMN auth_parameters.updated_at IS
'Fecha y hora de la última modificación del parámetro.';


-- ============================================================
-- TABLA: auth_audit_logs
-- ============================================================

COMMENT ON TABLE auth_audit_logs IS
'Tabla de auditoría del módulo AUTH. Registra eventos importantes de seguridad y trazabilidad, como inicios de sesión, intentos fallidos, creación de usuarios, cambios de contraseña y asignación de roles.';

COMMENT ON COLUMN auth_audit_logs.audit_log_id IS
'Identificador único del evento de auditoría. Usa BIGINT porque la tabla puede crecer bastante con el tiempo.';

COMMENT ON COLUMN auth_audit_logs.affected_user_id IS
'Usuario afectado por la acción registrada. Puede ser NULL cuando el evento no aplica a un usuario específico. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_audit_logs.action_type IS
'Tipo de acción realizada. Ejemplo: LOGIN_SUCCESS, LOGIN_FAILED, USER_CREATED, USER_UPDATED, ROLE_ASSIGNED, PASSWORD_CHANGED.';

COMMENT ON COLUMN auth_audit_logs.affected_table_name IS
'Nombre de la tabla afectada por el evento. Ejemplo: auth_users, auth_roles, auth_user_roles.';

COMMENT ON COLUMN auth_audit_logs.affected_record_id IS
'Identificador del registro afectado por la acción. Permite ubicar qué registro fue creado, modificado o relacionado.';

COMMENT ON COLUMN auth_audit_logs.action_description IS
'Descripción clara del evento ocurrido. Sirve para auditoría funcional y revisión administrativa.';

COMMENT ON COLUMN auth_audit_logs.old_data IS
'Datos anteriores al cambio almacenados en formato JSONB. Permite revisar qué información existía antes de la modificación.';

COMMENT ON COLUMN auth_audit_logs.new_data IS
'Datos nuevos después del cambio almacenados en formato JSONB. Permite revisar el resultado de la modificación.';

COMMENT ON COLUMN auth_audit_logs.ip_address IS
'Dirección IP desde donde se ejecutó la acción. Soporta IPv4 e IPv6.';

COMMENT ON COLUMN auth_audit_logs.user_agent IS
'Navegador, aplicación móvil, dispositivo o cliente desde donde se ejecutó la acción.';

COMMENT ON COLUMN auth_audit_logs.executed_by_user_id IS
'Usuario que ejecutó la acción registrada. Puede ser NULL para procesos automáticos o intentos no autenticados. Apunta a auth_users.user_id.';

COMMENT ON COLUMN auth_audit_logs.executed_at IS
'Fecha y hora exacta en la que ocurrió el evento de auditoría.';

/* ============================================================
   COMENTARIOS: auth_password_recovery_codes
   ============================================================ */

COMMENT ON TABLE auth_password_recovery_codes IS
'Tabla que almacena códigos temporales de recuperación de contraseña enviados al correo del usuario.';

COMMENT ON COLUMN auth_password_recovery_codes.recovery_code_id IS
'Identificador único del código de recuperación de contraseña.';

COMMENT ON COLUMN auth_password_recovery_codes.user_id IS
'Identificador del usuario dueño del correo que solicitó la recuperación de contraseña.';

COMMENT ON COLUMN auth_password_recovery_codes.email IS
'Correo electrónico al cual se envía el código de recuperación. Debe corresponder a un usuario registrado.';

COMMENT ON COLUMN auth_password_recovery_codes.code_hash IS
'Código de recuperación almacenado de forma hasheada. No se guarda el código en texto plano por seguridad.';

COMMENT ON COLUMN auth_password_recovery_codes.used IS
'Indica si el código de recuperación ya fue utilizado. TRUE significa usado, FALSE significa pendiente.';

COMMENT ON COLUMN auth_password_recovery_codes.expires_at IS
'Fecha y hora en la que expira el código de recuperación. Después de este tiempo el código ya no será válido.';

COMMENT ON COLUMN auth_password_recovery_codes.created_at IS
'Fecha y hora en la que se generó el código de recuperación.';

COMMENT ON COLUMN auth_password_recovery_codes.used_at IS
'Fecha y hora en la que el código fue utilizado correctamente para restablecer la contraseña.';

COMMENT ON COLUMN auth_password_recovery_codes.attempts IS
'Cantidad de intentos fallidos realizados para validar el código de recuperación.';

COMMENT ON COLUMN auth_password_recovery_codes.max_attempts IS
'Cantidad máxima de intentos permitidos para validar el código antes de invalidarlo.';-- ============================================================
-- Select para verificar que las tablas se crearon
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
-- Select para verificar columnas, tipos y si son obligatorias
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
-- Select para verificar comentarios de tablas
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
-- Select para verificar comentarios de columnas
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
-- Select para verificar relaciones FK
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
