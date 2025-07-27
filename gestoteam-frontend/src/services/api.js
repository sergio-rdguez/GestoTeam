import axios from "axios";
import { EventBus } from "@/utils/EventBus";

const apiClient = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para incluir el Token JWT en cada petición
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  
  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`; 
  }
  
  return config;
});


apiClient.interceptors.response.use(
  (response) => response, 
  (error) => {
    const message = error.response?.data?.message || "Ocurrió un error inesperado";

    // Emitir error al EventBus
    EventBus.emit("error", { message });

    // Rechazar la promesa para que el error se pueda manejar en caso necesario
    return Promise.reject(error);
  }
);

export default apiClient;