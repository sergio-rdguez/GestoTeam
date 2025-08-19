import { reactive, readonly } from 'vue';
import api from './api';
import router from '@/router';

// Estado reactivo y privado para la autenticación
const state = reactive({
  user: null,
  token: localStorage.getItem('authToken') || null,
  isAuthenticated: false,
});

// Servicio de autenticación refactorizado
const authService = {
  // Exponemos el estado como de solo lectura para los componentes
  state: readonly(state),

  /**
   * Intenta autenticar al usuario al iniciar la aplicación
   * si existe un token.
   */
  async checkAuth() {
    if (state.token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${state.token}`;
      try {
        const response = await api.get('/auth/profile'); 
        state.user = response.data;
        state.isAuthenticated = true;
      } catch (error) {
        console.error("Fallo de auto-login:", error);
        this.logout();
      }
    }
  },

  /**
   * Inicia sesión, guarda el token y actualiza el estado.
   * @param {object} credentials - { email, password }
   */
  async login(credentials) {
  const response = await api.post('/auth/login', credentials);
  const { token } = response.data;

  localStorage.setItem('authToken', token);
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
    localStorage.removeItem('authToken');
    delete api.defaults.headers.common['Authorization'];
    
    state.token = null;
    state.user = null;
    state.isAuthenticated = false;
    
    // Usamos el router para una navegación más limpia
    router.push({ name: 'Login' });
  },
};

export default authService;

// Función auxiliar para que otros módulos (como el interceptor de axios)
// puedan obtener el token actual sin acoplarse al estado interno
export function getToken() {
  return localStorage.getItem('authToken');
}