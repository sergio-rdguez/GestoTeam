/**
 * Configuración simplificada para la pizarra táctica
 * Este archivo proporciona solo la funcionalidad básica de exportación de imagen
 */

// Configuración de la API (solo para futuras funcionalidades)
const API_CONFIG = {
    // Detectar automáticamente el entorno
    baseURL: (() => {
        const hostname = window.location.hostname;
        const protocol = window.location.protocol;
        
        // Desarrollo local
        if (hostname === 'localhost' || hostname === '127.0.0.1') {
            return 'http://localhost:8081/api';
        }
        
        // Producción - usar el mismo host
        return `${protocol}//${hostname}/api`;
    })(),
    timeout: 10000
};

// Detectar si estamos en producción
const isProduction = window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1';

// Clase para manejar notificaciones simples
class NotificationManager {
    showNotification(message, type = 'info') {
        try {
            // Intentar usar el sistema de notificaciones del frontend
            if (window.parent && window.parent.showNotification) {
                window.parent.showNotification(message, type);
            } else if (window.opener && window.opener.showNotification) {
                window.opener.showNotification(message, type);
            } else {
                // Fallback a alert
                this.showAlert(message, type);
            }
        } catch (error) {
            console.log('Error mostrando notificación:', error);
            this.showAlert(message, type);
        }
    }

    showAlert(message, type) {
        const alertClass = type === 'error' ? 'alert-danger' : 
                          type === 'success' ? 'alert-success' : 
                          type === 'warning' ? 'alert-warning' : 'alert-info';
        
        // Crear elemento de alerta temporal
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.parentNode.removeChild(alertDiv);
            }
        }, 5000);
    }
}

// Función para cargar los servicios del frontend (simplificada)
async function loadFrontendServices() {
    try {
        // Intentar cargar servicios del frontend principal
        if (window.parent && window.parent !== window) {
            try {
                if (window.parent.exerciseService) {
                    window.exerciseService = window.parent.exerciseService;
                    console.log('Servicios del frontend cargados desde parent');
                }
            } catch (e) {
                console.log('No se puede acceder a los servicios del parent (CORS)');
            }
        } else if (window.opener && window.opener !== window) {
            try {
                if (window.opener.exerciseService) {
                    window.exerciseService = window.opener.exerciseService;
                    console.log('Servicios del frontend cargados desde opener');
                }
            } catch (e) {
                console.log('No se puede acceder a los servicios del opener (CORS)');
            }
        } else {
            console.log('Servicios del frontend no disponibles, usando implementación local');
        }
    } catch (error) {
        console.log('Error cargando servicios del frontend:', error);
    }
}

// Función para obtener token de autenticación (simplificada)
function getAuthToken() {
    try {
        // Intentar obtener token del localStorage del frontend principal
        if (window.parent && window.parent.localStorage) {
            return window.parent.localStorage.getItem('token') || 
                   window.parent.localStorage.getItem('authToken');
        } else if (window.opener && window.opener.localStorage) {
            return window.opener.localStorage.getItem('token') || 
                   window.opener.localStorage.getItem('authToken');
        } else {
            // Fallback al localStorage local
            return localStorage.getItem('token') || 
                   localStorage.getItem('authToken');
        }
    } catch (error) {
        console.log('Error obteniendo token:', error);
        return localStorage.getItem('token') || localStorage.getItem('authToken');
    }
}

// Función para mostrar notificaciones (simplificada)
function showNotification(message, type = 'info') {
    if (window.notificationManager) {
        window.notificationManager.showNotification(message, type);
    } else {
        alert(message);
    }
}

// Inicializar servicios cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        loadFrontendServices();
        initializeServices();
    });
} else {
    loadFrontendServices();
    initializeServices();
}

// Función para inicializar todos los servicios
function initializeServices() {
    // Crear instancia del gestor de notificaciones
    window.notificationManager = new NotificationManager();
    
    console.log('Servicios del tactical board inicializados correctamente');
}

// Exportar configuración simplificada
window.tacticalBoardConfig = {
    loadFrontendServices,
    getAuthToken,
    showNotification,
    notificationManager: window.notificationManager
};
