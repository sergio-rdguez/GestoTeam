import axios from 'axios';
import { notificationService } from './notificationService';
// Importamos directamente el servicio, no el default export
import authService from './auth';

const api = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api',
});

// El interceptor de request ya no necesita tocar el token, auth.js se encarga.

api.interceptors.response.use(
    response => response,
    error => {
        const message = error.response?.data?.message || error.message || 'Ocurrió un error inesperado.';
        
        // Si el error es 401 o 403, es un problema de autenticación/autorización.
        // Delegamos al authService para que centralice la lógica de logout.
        if (error.response?.status === 401 || error.response?.status === 403) {
            // No mostramos notificación de error para estos casos, el logout es suficiente feedback.
            authService.logout();
        } else {
            // Para todos los demás errores, sí mostramos una notificación.
            notificationService.showError(message);
        }

        return Promise.reject(error);
    }
);

export default api;