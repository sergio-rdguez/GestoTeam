import { reactive, readonly } from 'vue';
import api from './api';
import router from '@/router';
import config from '@/config';

// Estado reactivo y privado para la autenticación
const state = reactive({
  user: null,
  token: localStorage.getItem(config.auth.tokenKey) || null,
  isAuthenticated: false,
});

// Servicio de autenticación refactorizado
const authService = {
  // Exponemos el estado como de solo lectura para los componentes
  state: readonly(state),

  /**
   * Valida si el token actual es válido
   * @returns {Promise<boolean>}
   */
  async validateToken() {
    if (!state.token) {
      return false;
    }
    
    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${state.token}`;
      const response = await api.get('/auth/profile');
      return response.status === 200;
    } catch (error) {
      console.error("Token inválido:", error);
      return false;
    }
  },

  /**
   * Intenta autenticar al usuario al iniciar la aplicación
   * si existe un token.
   */
  async checkAuth() {
    if (!state.token) {
      return false;
    }
    
    try {
      api.defaults.headers.common['Authorization'] = `Bearer ${state.token}`;
      const response = await api.get('/auth/profile'); 
      state.user = response.data;
      state.isAuthenticated = true;
      return true;
    } catch (error) {
      console.error("Fallo de auto-login:", error);
      // Si el token es inválido o expirado, limpiar el estado
      if (error.response?.status === 401 || error.response?.status === 403) {
        this.logout();
      }
      return false;
    }
  },

  /**
   * Inicia sesión, guarda el token y actualiza el estado.
   * @param {object} credentials - { email, password }
   */
  async login(credentials) {
    const response = await api.post('/auth/login', credentials);
    const { token } = response.data;

    localStorage.setItem(config.auth.tokenKey, token);
    state.token = token;
    
    await this.checkAuth();
  },

  /**
   * Registra un nuevo usuario en el sistema.
   * @param {object} credentials - { username, password }
   */
  async register(credentials) {
    await api.post('/auth/register', credentials);
  },

  /**
   * Cierra la sesión, limpia todo y redirige.
   */
  logout() {
    localStorage.removeItem(config.auth.tokenKey);
    delete api.defaults.headers.common['Authorization'];
    
    state.token = null;
    state.user = null;
    state.isAuthenticated = false;
    
    // Usamos el router para una navegación más limpia
    router.push({ name: 'Login' });
  },

  /**
   * Verifica si el usuario está autenticado y tiene un token válido
   * @returns {boolean}
   */
  isUserAuthenticated() {
    return state.isAuthenticated && state.token !== null;
  }
};

export default authService;

// Función auxiliar para que otros módulos (como el interceptor de axios)
// puedan obtener el token actual sin acoplarse al estado interno
export function getToken() {
  return localStorage.getItem(config.auth.tokenKey);
}