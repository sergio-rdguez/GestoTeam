import axios from 'axios';
import { getAudit } from './audit';

const apiClient = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para incluir el Audit
apiClient.interceptors.request.use((config) => {
  const audit = getAudit();
  config.headers['Audit'] = audit; // Añade el Audit al encabezado
  return config;
});

export default apiClient;
