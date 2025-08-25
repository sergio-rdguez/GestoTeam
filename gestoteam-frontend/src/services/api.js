import axios from 'axios';
import { getToken } from './auth';
import authService from './auth';
import router from '@/router';
import config from '@/config';

const api = axios.create({
  baseURL: config.api.baseURL,
  timeout: config.api.timeout,
});

api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Manejar errores de autenticación
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.error('Error de autenticación:', error.response.status, error.response.data);
      
      // Limpiar el estado de autenticación y redirigir al login
      authService.logout();
      
      // Solo redirigir si no estamos ya en la página de login
      if (router.currentRoute.value.name !== 'Login') {
        router.push({ name: 'Login' });
      }
    }
    return Promise.reject(error);
  }
);

export default api;