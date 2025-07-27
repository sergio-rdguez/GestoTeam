export const authService = {
  /**
   * Cierra la sesión del usuario eliminando el token.
   */
  logout: () => {
    localStorage.removeItem('jwt_token');
  },

  /**
   * Comprueba si el usuario está autenticado.
   * @returns {boolean} - True si existe un token, false en caso contrario.
   */
  isAuthenticated: () => {
    const token = localStorage.getItem('jwt_token');
    return !!token; 
  },

  /**
   * Obtiene el token JWT del almacenamiento.
   * @returns {string|null} - El token o null si no existe.
   */
  getToken: () => {
    return localStorage.getItem('jwt_token');
  },
};

export default authService;