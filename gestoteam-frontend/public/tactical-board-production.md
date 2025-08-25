# 🏭 Configuración de Producción - Pizarra Táctica

## Descripción

Este documento describe la configuración optimizada para producción de la pizarra táctica, que incluye mejoras de autenticación, manejo de errores y optimizaciones de rendimiento.

## ✨ Características de Producción

### 🔐 Autenticación Robusta
- **Múltiples estrategias de obtención de tokens**: localStorage, sessionStorage, cookies, URL parameters
- **Detección automática de entornos**: Desarrollo vs Producción
- **Sistema de reintentos automático**: Hasta 3 reintentos con delay incremental
- **Manejo inteligente de errores**: 401, 403, errores de red

### 🌐 Configuración Automática
- **URL de API automática**: Se adapta al hostname del servidor
- **Protocolo automático**: HTTP/HTTPS según configuración del servidor
- **Fallbacks inteligentes**: Múltiples estrategias de conexión

### 📊 Monitoreo y Debugging
- **Logs condicionales**: Solo en desarrollo, silencioso en producción
- **Verificación de salud de API**: Endpoint `/health` para monitoreo
- **Verificación de conectividad**: Estado completo del sistema
- **Métricas de rendimiento**: Tiempo de respuesta y reintentos

## 🚀 Implementación

### 1. **Configuración Automática de Entorno**

```javascript
// Se detecta automáticamente
const isProduction = window.location.hostname !== 'localhost' && 
                    window.location.hostname !== '127.0.0.1';

// API URL se adapta automáticamente
const baseURL = isProduction ? 
    `${protocol}//${hostname}/api` : 
    'http://localhost:8081/api';
```

### 2. **Estrategias de Autenticación**

1. **localStorage del frontend principal** (iframe/parent)
2. **localStorage de ventana abierta** (window.opener)
3. **localStorage local** (fallback)
4. **sessionStorage** (temporal)
5. **Cookies** (persistente)
6. **URL parameters** (SSO/redirección)

### 3. **Manejo de Errores**

- **Errores 401/403**: Reintento automático de autenticación
- **Errores de red**: Notificaciones visuales y reintentos
- **Tokens expirados**: Limpieza automática y notificación
- **Fallbacks**: Múltiples estrategias de recuperación

## 📋 Configuración de Producción

### Variables de Entorno

```javascript
const PRODUCTION_CONFIG = {
    enableDebugLogs: false,      // Logs silenciosos
    showAuthWarnings: true,      // Advertencias visibles
    autoRetryAuth: true,         // Reintentos automáticos
    maxRetries: 3                // Máximo de reintentos
};
```

### Endpoints de Verificación

- **`/api/health`**: Estado de salud de la API
- **`/api/enums/exercises/categories`**: Verificación de autenticación
- **`/api/exercises`**: Carga de ejercicios
- **`/api/files/exercises/{id}/image`**: Subida de imágenes

## 🔧 Uso en Producción

### 1. **Despliegue**

```bash
# El tactical board se adapta automáticamente al entorno
# No requiere configuración manual
```

### 2. **Monitoreo**

```javascript
// Verificar estado del sistema
const connectivity = await tacticalBoardAPI.checkConnectivity();
console.log('Estado:', connectivity);
```

### 3. **Debugging**

```javascript
// En desarrollo: logs detallados
// En producción: logs silenciosos, solo errores críticos
```

## 🛡️ Seguridad

### Tokens JWT
- **Validación automática**: Verificación de formato y longitud
- **Limpieza automática**: Eliminación de tokens expirados
- **Reintentos seguros**: Sin exponer información sensible

### Comunicación
- **PostMessage seguro**: Comunicación con ventanas padre
- **CORS handling**: Manejo de restricciones de origen
- **Fallbacks seguros**: Múltiples estrategias sin comprometer seguridad

## 📈 Rendimiento

### Optimizaciones
- **Lazy loading**: Servicios se cargan solo cuando son necesarios
- **Caching inteligente**: Tokens y configuraciones en memoria
- **Reintentos eficientes**: Delay incremental para evitar spam
- **Logs condicionales**: Sin overhead en producción

### Métricas
- **Tiempo de respuesta**: Monitoreo de latencia de API
- **Tasa de éxito**: Seguimiento de reintentos y fallos
- **Estado de autenticación**: Verificación continua de tokens

## 🚨 Solución de Problemas

### Error: "No hay token de autenticación"

**Causas posibles:**
1. Usuario no ha iniciado sesión
2. Token expirado
3. Problemas de CORS entre ventanas
4. Configuración incorrecta del backend

**Soluciones:**
1. Verificar login del usuario
2. Revisar consola del navegador
3. Usar botón "Reintentar" en advertencia
4. Verificar configuración de CORS del backend

### Error: "HTTP error! status: 403"

**Causas posibles:**
1. Token sin permisos suficientes
2. Endpoint protegido
3. Token expirado
4. Problemas de autorización en backend

**Soluciones:**
1. Verificar permisos del usuario
2. Revisar logs del backend
3. Verificar configuración de roles
4. Contactar administrador del sistema

## 📞 Soporte

### Logs de Debugging
- **Desarrollo**: Logs detallados en consola
- **Producción**: Solo errores críticos
- **Verificación**: Usar archivo de prueba incluido

### Herramientas de Diagnóstico
- **Archivo de prueba**: `test-tactical-board.html`
- **Verificación de conectividad**: Método `checkConnectivity()`
- **Estado de autenticación**: Método `checkAuthStatus()`

## 🔄 Actualizaciones

### Versión Actual
- **Configuración**: Automática y robusta
- **Autenticación**: Múltiples estrategias
- **Producción**: Optimizada y segura
- **Debugging**: Herramientas completas

### Próximas Mejoras
- **Métricas avanzadas**: Dashboard de monitoreo
- **Notificaciones push**: Alertas en tiempo real
- **Cache distribuido**: Mejoras de rendimiento
- **Analytics**: Seguimiento de uso
