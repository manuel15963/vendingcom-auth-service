

-- ============================================================
-- VENDING.COM S.A.C
-- Base de datos: vendingcom_db
-- Módulo: AUTH - Autenticación y gestión de accesos
-- ============================================================

CREATE DATABASE vendingcom_db;



CREATE TABLE IF NOT EXISTS auth_users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    document_type VARCHAR(20),
    document_number VARCHAR(20),
    user_status SMALLINT NOT NULL DEFAULT 1,
    last_login_at TIMESTAMP,
    created_by_user_id INTEGER,
    updated_by_user_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT uq_auth_users_username UNIQUE (username),
    CONSTRAINT uq_auth_users_email UNIQUE (email),

    CONSTRAINT chk_auth_users_user_status
    CHECK (user_status IN (0, 1, 2)),

    CONSTRAINT fk_auth_users_created_by
    FOREIGN KEY (created_by_user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT fk_auth_users_updated_by
    FOREIGN KEY (updated_by_user_id)
    REFERENCES auth_users(user_id)
    );


CREATE TABLE IF NOT EXISTS auth_roles (
    role_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    role_description VARCHAR(255),
    -- 0 = INACTIVO, 1 = ACTIVO
    role_status SMALLINT NOT NULL DEFAULT 1,
    created_by_user_id INTEGER,
    updated_by_user_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT uq_auth_roles_role_code UNIQUE (role_code),

    CONSTRAINT chk_auth_roles_role_status
    CHECK (role_status IN (0, 1)),

    CONSTRAINT fk_auth_roles_created_by
    FOREIGN KEY (created_by_user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT fk_auth_roles_updated_by
    FOREIGN KEY (updated_by_user_id)
    REFERENCES auth_users(user_id)
    );

CREATE TABLE IF NOT EXISTS auth_user_roles (
    user_role_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    assigned_by_user_id INTEGER,
    -- 0 = INACTIVO, 1 = ACTIVO
    assignment_status SMALLINT NOT NULL DEFAULT 1,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_auth_user_roles_user
    FOREIGN KEY (user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT fk_auth_user_roles_role
    FOREIGN KEY (role_id)
    REFERENCES auth_roles(role_id),

    CONSTRAINT fk_auth_user_roles_assigned_by
    FOREIGN KEY (assigned_by_user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT uq_auth_user_roles_user_role
    UNIQUE (user_id, role_id),

    CONSTRAINT chk_auth_user_roles_assignment_status
    CHECK (assignment_status IN (0, 1))
    );

CREATE TABLE IF NOT EXISTS auth_parameters (
    parameter_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parameter_group VARCHAR(50) NOT NULL,
    parameter_code VARCHAR(50) NOT NULL,
    parameter_value VARCHAR(100) NOT NULL,
    parameter_description VARCHAR(255),
    sort_order INTEGER NOT NULL DEFAULT 1,
      -- 0 = INACTIVO, 1 = ACTIVO
    parameter_status SMALLINT NOT NULL DEFAULT 1,

    created_by_user_id INTEGER,
    updated_by_user_id INTEGER,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT uq_auth_parameters_group_code
    UNIQUE (parameter_group, parameter_code),

    CONSTRAINT chk_auth_parameters_parameter_status
    CHECK (parameter_status IN (0, 1)),

    CONSTRAINT fk_auth_parameters_created_by
    FOREIGN KEY (created_by_user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT fk_auth_parameters_updated_by
    FOREIGN KEY (updated_by_user_id)
    REFERENCES auth_users(user_id)
    );


CREATE TABLE IF NOT EXISTS auth_audit_logs (
    audit_log_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    affected_user_id INTEGER,
    action_type VARCHAR(50) NOT NULL,
    affected_table_name VARCHAR(50),
    affected_record_id INTEGER,
    action_description TEXT,
    old_data JSONB,
    new_data JSONB,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    executed_by_user_id INTEGER,
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_auth_audit_logs_affected_user
    FOREIGN KEY (affected_user_id)
    REFERENCES auth_users(user_id),

    CONSTRAINT fk_auth_audit_logs_executed_by
    FOREIGN KEY (executed_by_user_id)
    REFERENCES auth_users(user_id)
    );

/* ============================================================
   TABLA: auth_password_recovery_codes
   DESCRIPCIÓN:
   Almacena códigos temporales para recuperación de contraseña.
   Se utiliza cuando un usuario olvida su contraseña y solicita
   un código de verificación por correo electrónico.
   ============================================================ */

CREATE TABLE IF NOT EXISTS auth_password_recovery_codes (
    recovery_code_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    email VARCHAR(120) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    attempts INTEGER NOT NULL DEFAULT 0,
    max_attempts INTEGER NOT NULL DEFAULT 3,

    CONSTRAINT fk_auth_password_recovery_user
    FOREIGN KEY (user_id)
    REFERENCES auth_users(user_id)
    );
