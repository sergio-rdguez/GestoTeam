export const authService = {
    isAuthenticated: () => {
      const user = localStorage.getItem('audit_user');
      return user !== null; 
    },
  
    /**
     * Guarda el usuario autenticado en localStorage.
     * @param {string} username - Nombre de usuario autenticado
     */
    login: (username) => {
      localStorage.setItem('audit_user', username);
    },
  
    /**
     * Elimina el estado de autenticación.
     */
    logout: () => {
      localStorage.removeItem('audit_user');
    },
  
    /**
     * Obtiene el usuario actualmente autenticado.
     * @returns {string|null} - El nombre del usuario o null si no está autenticado
     */
    getUser: () => {
      return localStorage.getItem('audit_user');
    },
  };
  