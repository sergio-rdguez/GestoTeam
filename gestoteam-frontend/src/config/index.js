const config = {
  // Configuración de la API
  api: {
    baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8081/api',
    timeout: 10000,
  },
  
  // Configuración de autenticación
  auth: {
    tokenKey: 'authToken',
    refreshTokenKey: 'refreshToken',
    tokenExpiryThreshold: 5 * 60 * 1000, // 5 minutos antes de expirar
  },
  
  // Configuración de la aplicación
  app: {
    name: 'GestoTeam',
    version: '1.0.0',
    defaultRoute: '/dashboard',
    loginRoute: '/login',
  },
  
  // Configuración de paginación
  pagination: {
    defaultPageSize: 20,
    pageSizeOptions: [10, 20, 50, 100],
  },
  
  // Configuración de notificaciones
  notifications: {
    defaultDuration: 5000,
    maxNotifications: 5,
  }
};

export default config;
