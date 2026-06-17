/**
 * TABLA DE AUDITORÍA - oldData y newData
 * 
 * Esta tabla define cuándo se deben capturar oldData y newData en los logs de auditoría
 * del Auth Service.
 * 
 * Reglas Generales:
 * - oldData: Datos ANTES del cambio (null si es nuevo registro o sin cambios de datos)
 * - newData: Datos DESPUÉS del cambio (null solo si no hay datos a capturar)
 * - NUNCA incluir: password, passwordHash, recovery_code_hash, jwt, token, api_keys, secrets
 * 
 * ============================================================================
 * ACCIÓN                    | oldData  | newData  | EJEMPLO
 * ============================================================================
 * LOGIN_SUCCESS             | null     | null     | No hay cambio de datos de usuario
 * LOGIN_FAILED              | null     | null     | No hay cambio de datos de usuario
 * PASSWORD_RECOVERY_REQUESTED | null  | null     | Se envía código, sin cambio en usuario
 * PASSWORD_RESET            | null     | cambios  | Contraseña cambió (sin mostrar hash)
 * PASSWORD_CHANGED          | null     | cambios  | Contraseña cambió (sin mostrar hash)
 * 
 * USER_CREATED              | null     | completo | Usuario nuevo + todos sus datos
 * USER_UPDATED              | anterior | nuevo    | Cambios en email, fullName, etc.
 * USER_LOCKED               | anterior | bloqueado| userStatus cambió de 1→2
 * USER_ACTIVATED            | anterior | activo   | userStatus cambió a 1
 * USER_DEACTIVATED          | anterior | inactivo | userStatus cambió de 1→0
 * 
 * ============================================================================
 * EJEMPLOS PRÁCTICOS:
 * ============================================================================
 * 
 * 1. LOGIN_SUCCESS (Sin cambios de datos):
 *    {
 *      "actionType": "LOGIN_SUCCESS",
 *      "oldData": null,
 *      "newData": null,
 *      "ipAddress": "192.168.1.1",
 *      "userAgent": "Mozilla/5.0..."
 *    }
 * 
 * 2. USER_CREATED (Nuevo usuario):
 *    {
 *      "actionType": "USER_CREATED",
 *      "oldData": null,
 *      "newData": {
 *        "userId": 5,
 *        "username": "juan.perez",
 *        "email": "juan@example.com",
 *        "fullName": "Juan Pérez",
 *        "userStatus": 1
 *      },
 *      "ipAddress": "192.168.1.1",
 *      "userAgent": "Mozilla/5.0..."
 *    }
 * 
 * 3. USER_UPDATED (Cambio de datos):
 *    {
 *      "actionType": "USER_UPDATED",
 *      "oldData": {
 *        "userId": 5,
 *        "username": "juan.perez",
 *        "email": "juan@example.com",
 *        "fullName": "Juan Pérez",
 *        "userStatus": 1
 *      },
 *      "newData": {
 *        "userId": 5,
 *        "username": "juan.perez",
 *        "email": "juan.nuevo@example.com",
 *        "fullName": "Juan Carlos Pérez",
 *        "userStatus": 1
 *      },
 *      "ipAddress": "192.168.1.1",
 *      "userAgent": "Mozilla/5.0..."
 *    }
 * 
 * 4. USER_LOCKED (Cambio de estado):
 *    {
 *      "actionType": "USER_LOCKED",
 *      "oldData": {
 *        "userId": 5,
 *        "username": "juan.perez",
 *        "email": "juan@example.com",
 *        "fullName": "Juan Pérez",
 *        "userStatus": 1
 *      },
 *      "newData": {
 *        "userId": 5,
 *        "username": "juan.perez",
 *        "email": "juan@example.com",
 *        "fullName": "Juan Pérez",
 *        "userStatus": 2
 *      },
 *      "ipAddress": "192.168.1.1",
 *      "userAgent": "Mozilla/5.0..."
 *    }
 * 
 * 5. PASSWORD_RESET (Sin cambio de datos personales):
 *    {
 *      "actionType": "PASSWORD_RESET",
 *      "oldData": null,
 *      "newData": null,
 *      "ipAddress": "192.168.1.1",
 *      "userAgent": "Mozilla/5.0..."
 *    }
 *    NOTA: No se guardan detalles de contraseña en oldData/newData por seguridad
 * 
 */

