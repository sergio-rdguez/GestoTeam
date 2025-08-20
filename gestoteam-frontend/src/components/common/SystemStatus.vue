<template>
  <div class="system-status">
    <!-- Estado del Backend -->
    <div class="status-card backend-status" :class="backendStatusClass">
      <div class="status-header">
        <i class="fas fa-server"></i>
        <span class="status-title">Estado del Backend</span>
        <div class="status-indicator" :class="backendStatusClass"></div>
      </div>
      
      <div class="status-details">
        <div class="status-item">
          <span class="label">Estado:</span>
          <span class="value" :class="backendStatusClass">
            {{ backendStatusText }}
          </span>
        </div>
        
        <div v-if="backendStatus.isRunning" class="status-item">
          <span class="label">PID:</span>
          <span class="value">{{ backendStatus.pid }}</span>
        </div>
        
        <div v-if="backendStatus.attempts > 0" class="status-item">
          <span class="label">Intentos:</span>
          <span class="value">{{ backendStatus.attempts }}/{{ backendStatus.maxAttempts }}</span>
        </div>
        
        <div class="status-item">
          <span class="label">Puerto:</span>
          <span class="value">{{ backendStatus.port }}</span>
        </div>
      </div>
      
      <div class="status-actions">
        <button 
          @click="testConnection" 
          class="btn btn-sm btn-outline-primary"
          :disabled="!backendStatus.isRunning"
        >
          <i class="fas fa-plug"></i> Probar Conexión
        </button>
        
        <button 
          @click="restartBackend" 
          class="btn btn-sm btn-outline-warning"
          :disabled="backendStatus.isRunning"
        >
          <i class="fas fa-redo"></i> Reiniciar
        </button>
      </div>
    </div>

    <!-- Estado del Frontend -->
    <div class="status-card frontend-status" :class="frontendStatusClass">
      <div class="status-header">
        <i class="fas fa-desktop"></i>
        <span class="status-title">Estado del Frontend</span>
        <div class="status-indicator" :class="frontendStatusClass"></div>
      </div>
      
      <div class="status-details">
        <div class="status-item">
          <span class="label">Estado:</span>
          <span class="value" :class="frontendStatusClass">
            {{ frontendStatusText }}
          </span>
        </div>
        
        <div v-if="frontendConnection" class="status-item">
          <span class="label">Conexión Backend:</span>
          <span class="value" :class="frontendConnection.success ? 'text-success' : 'text-danger'">
            {{ frontendConnection.success ? 'Conectado' : 'Desconectado' }}
          </span>
        </div>
      </div>
    </div>

    <!-- Errores del Sistema -->
    <div v-if="systemErrors.length > 0" class="status-card error-status">
      <div class="status-header">
        <i class="fas fa-exclamation-triangle text-warning"></i>
        <span class="status-title">Errores del Sistema</span>
        <button @click="clearErrors" class="btn btn-sm btn-outline-secondary">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <div class="error-list">
        <div 
          v-for="(error, index) in systemErrors" 
          :key="index" 
          class="error-item"
        >
          <div class="error-header">
            <span class="error-time">{{ formatTime(error.timestamp) }}</span>
            <span class="error-type">{{ error.type }}</span>
          </div>
          <div class="error-message">{{ error.message }}</div>
          <div v-if="error.details" class="error-details">
            {{ error.details }}
          </div>
        </div>
      </div>
    </div>

    <!-- Acciones del Sistema -->
    <div class="status-card actions-status">
      <div class="status-header">
        <i class="fas fa-tools"></i>
        <span class="status-title">Acciones del Sistema</span>
      </div>
      
      <div class="actions-grid">
        <button @click="openLogsFolder" class="btn btn-outline-info">
          <i class="fas fa-folder-open"></i> Abrir Logs
        </button>
        
        <button @click="generateReport" class="btn btn-outline-secondary">
          <i class="fas fa-file-alt"></i> Generar Reporte
        </button>
        
        <button @click="refreshStatus" class="btn btn-outline-primary">
          <i class="fas fa-sync-alt"></i> Actualizar Estado
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

// Estado del sistema
const backendStatus = ref({
  isRunning: false,
  pid: null,
  attempts: 0,
  maxAttempts: 3,
  port: 8081
});

const frontendConnection = ref(null);
const systemErrors = ref([]);

// Computed properties
const backendStatusClass = computed(() => {
  if (backendStatus.value.isRunning) return 'success';
  if (backendStatus.value.attempts >= backendStatus.value.maxAttempts) return 'danger';
  if (backendStatus.value.attempts > 0) return 'warning';
  return 'secondary';
});

const backendStatusText = computed(() => {
  if (backendStatus.value.isRunning) return 'Ejecutándose';
  if (backendStatus.value.attempts >= backendStatus.value.maxAttempts) return 'Falló';
  if (backendStatus.value.attempts > 0) return 'Reintentando';
  return 'Detenido';
});

const frontendStatusClass = computed(() => {
  if (!frontendConnection.value) return 'secondary';
  return frontendConnection.value.success ? 'success' : 'danger';
});

const frontendStatusText = computed(() => {
  if (!frontendConnection.value) return 'Verificando...';
  return frontendConnection.value.success ? 'Conectado' : 'Desconectado';
});

// Métodos
const refreshStatus = async () => {
  try {
    const response = await window.electronAPI.invoke('get-backend-status');
    if (response.success) {
      backendStatus.value = response.status;
    }
  } catch (error) {
    console.error('Error obteniendo estado del backend:', error);
  }
};

const testConnection = async () => {
  try {
    const result = await window.electronAPI.invoke('test-backend-connection');
    if (result.success) {
      addSystemMessage('success', 'Conexión exitosa', 'El backend responde correctamente');
    } else {
      addSystemMessage('error', 'Error de conexión', result.message);
    }
  } catch (error) {
    addSystemMessage('error', 'Error de conexión', error.message);
  }
};

const restartBackend = async () => {
  try {
    const result = await window.electronAPI.invoke('restart-backend');
    if (result.success) {
      addSystemMessage('info', 'Backend reiniciándose', result.message);
      // Esperar un poco y actualizar estado
      setTimeout(refreshStatus, 3000);
    } else {
      addSystemMessage('error', 'Error reiniciando backend', result.error);
    }
  } catch (error) {
    addSystemMessage('error', 'Error reiniciando backend', error.message);
  }
};

const openLogsFolder = async () => {
  try {
    await window.electronAPI.invoke('open-logs-folder');
  } catch (error) {
    addSystemMessage('error', 'Error abriendo carpeta de logs', error.message);
  }
};

const generateReport = async () => {
  try {
    const report = await window.electronAPI.invoke('get-system-report');
    addSystemMessage('info', 'Reporte generado', 'Se ha generado un reporte del sistema');
    console.log('Reporte del sistema:', report);
  } catch (error) {
    addSystemMessage('error', 'Error generando reporte', error.message);
  }
};

const addSystemMessage = (type, title, message, details = null) => {
  systemErrors.value.unshift({
    type,
    title,
    message,
    details,
    timestamp: new Date()
  });
  
  // Mantener solo los últimos 10 errores
  if (systemErrors.value.length > 10) {
    systemErrors.value = systemErrors.value.slice(0, 10);
  }
};

const clearErrors = () => {
  systemErrors.value = [];
};

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString();
};

// Event listeners
const handleBackendStatus = (event, data) => {
  backendStatus.value = { ...backendStatus.value, ...data };
};

const handleBackendError = (event, data) => {
  addSystemMessage('error', 'Error del Backend', data.reason, 
    `Intento ${data.attempts} de ${data.maxAttempts}`);
};

const handleFrontendConnection = (event, data) => {
  frontendConnection.value = data;
};

// Lifecycle
onMounted(() => {
  // Configurar event listeners
  window.electronAPI.on('backend-status', handleBackendStatus);
  window.electronAPI.on('backend-error', handleBackendError);
  window.electronAPI.on('frontend-backend-connection', handleFrontendConnection);
  
  // Cargar estado inicial
  refreshStatus();
  
  // Actualizar estado cada 10 segundos
  const interval = setInterval(refreshStatus, 10000);
  
  onUnmounted(() => {
    clearInterval(interval);
  });
});

onUnmounted(() => {
  // Limpiar event listeners si es necesario
});
</script>

<style scoped>
.system-status {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.status-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.status-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.status-header i {
  margin-right: 10px;
  font-size: 18px;
}

.status-title {
  font-weight: 600;
  flex: 1;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-left: 10px;
}

.status-indicator.success { background-color: #28a745; }
.status-indicator.warning { background-color: #ffc107; }
.status-indicator.danger { background-color: #dc3545; }
.status-indicator.secondary { background-color: #6c757d; }

.status-details {
  padding: 20px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #f1f3f4;
}

.status-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.status-item .label {
  font-weight: 500;
  color: #6c757d;
}

.status-item .value {
  font-weight: 600;
}

.status-item .value.success { color: #28a745; }
.status-item .value.warning { color: #ffc107; }
.status-item .value.danger { color: #dc3545; }
.status-item .value.secondary { color: #6c757d; }

.status-actions {
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  display: flex;
  gap: 10px;
}

.actions-grid {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.error-list {
  padding: 20px;
}

.error-item {
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 15px;
}

.error-item:last-child {
  margin-bottom: 0;
}

.error-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.error-time {
  font-size: 12px;
  color: #6c757d;
}

.error-type {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  padding: 2px 8px;
  border-radius: 4px;
  background: #dc3545;
  color: white;
}

.error-message {
  font-weight: 500;
  color: #dc3545;
  margin-bottom: 5px;
}

.error-details {
  font-size: 14px;
  color: #6c757d;
  font-style: italic;
}

/* Estados de las tarjetas */
.backend-status.success .status-header { background: #d4edda; }
.backend-status.warning .status-header { background: #fff3cd; }
.backend-status.danger .status-header { background: #f8d7da; }
.backend-status.secondary .status-header { background: #e2e3e5; }

.frontend-status.success .status-header { background: #d4edda; }
.frontend-status.danger .status-header { background: #f8d7da; }
.frontend-status.secondary .status-header { background: #e2e3e5; }

.error-status .status-header { background: #f8d7da; }

/* Responsive */
@media (max-width: 768px) {
  .system-status {
    padding: 10px;
  }
  
  .status-header {
    padding: 12px 15px;
  }
  
  .status-details {
    padding: 15px;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
    padding: 15px;
  }
}
</style>
