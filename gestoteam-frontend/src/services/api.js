import axios from "axios";
import { getAudit } from "./audit";
import { EventBus } from "@/utils/EventBus";

const apiClient = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para incluir el Audit
apiClient.interceptors.request.use((config) => {
  const audit = getAudit();
  config.headers["Audit"] = audit; 
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
