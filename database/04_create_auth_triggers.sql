-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Triggers básicos
-- Objetivo: actualizar automáticamente updated_at
-- ============================================================


-- ============================================================
-- 1. FUNCIÓN GENERAL PARA ACTUALIZAR updated_at
-- ============================================================

CREATE OR REPLACE FUNCTION fn_auth_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- 2. TRIGGER PARA auth_users
-- ============================================================

DROP TRIGGER IF EXISTS trg_auth_users_set_updated_at ON auth_users;

CREATE TRIGGER trg_auth_users_set_updated_at
    BEFORE UPDATE ON auth_users
    FOR EACH ROW
    EXECUTE FUNCTION fn_auth_set_updated_at();


-- ============================================================
-- 3. TRIGGER PARA auth_roles
-- ============================================================

DROP TRIGGER IF EXISTS trg_auth_roles_set_updated_at ON auth_roles;

CREATE TRIGGER trg_auth_roles_set_updated_at
    BEFORE UPDATE ON auth_roles
    FOR EACH ROW
    EXECUTE FUNCTION fn_auth_set_updated_at();


-- ============================================================
-- 4. TRIGGER PARA auth_parameters
-- ============================================================

DROP TRIGGER IF EXISTS trg_auth_parameters_set_updated_at ON auth_parameters;

CREATE TRIGGER trg_auth_parameters_set_updated_at
    BEFORE UPDATE ON auth_parameters
    FOR EACH ROW
    EXECUTE FUNCTION fn_auth_set_updated_at();

/* ============================================================
   TRIGGERS: auth_password_recovery_codes
   ============================================================ */

/*
   La tabla auth_password_recovery_codes no requiere trigger de actualización,
   ya que no maneja una columna updated_at.

   Campos controlados:
   - created_at: se asigna automáticamente al crear el registro.
   - expires_at: se calcula desde el backend.
   - used_at: se asigna desde el backend cuando el código es utilizado.
*/

-- ============================================================
-- Select para verificar que los triggers se crearon
-- ============================================================

SELECT
    event_object_table AS table_name,
    trigger_name,
    action_timing,
    event_manipulation,
    action_statement
FROM information_schema.triggers
WHERE trigger_schema = 'public'
  AND trigger_name IN (
                       'trg_auth_users_set_updated_at',
                       'trg_auth_roles_set_updated_at',
                       'trg_auth_parameters_set_updated_at'
    )
ORDER BY event_object_table, trigger_name;

-- ============================================================
-- Prueba rápida del trigger
-- ============================================================

INSERT INTO auth_parameters (
    parameter_group,
    parameter_code,
    parameter_value,
    parameter_description,
    sort_order,
    parameter_status
)
VALUES (
           'TEST_GROUP',
           'TEST_CODE',
           'Valor inicial',
           'Parámetro temporal para probar trigger updated_at.',
           1,
           1  -- parameter_status es SMALLINT: 1 = ACTIVO (antes decía 'ACTIVE' y rompía el script)
       );

-- ============================================================
-- Ahora consulta: Al inicio updated_at debe salir NULL.
-- ============================================================

SELECT
    parameter_id,
    parameter_group,
    parameter_code,
    parameter_value,
    created_at,
    updated_at
FROM auth_parameters
WHERE parameter_group = 'TEST_GROUP';

-- ============================================================
--  Luego actualiza: Ahora updated_at ya debe tener fecha y hora.
-- ============================================================

UPDATE auth_parameters
SET parameter_value = 'Valor actualizado'
WHERE parameter_group = 'TEST_GROUP'
  AND parameter_code = 'TEST_CODE';

-- ============================================================
-- Si quieres borrar el dato de prueba
-- ============================================================

DELETE FROM auth_parameters
WHERE parameter_group = 'TEST_GROUP'
  AND parameter_code = 'TEST_CODE';
