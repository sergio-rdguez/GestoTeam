<template>
  <div class="system-status">
    <div class="status-header">
      <h3>Estado del Sistema</h3>
      <div class="status-indicator" :class="backendStatusClass">
        <span class="status-dot"></span>
        {{ backendStatusText }}
      </div>
    </div>

    <div class="status-details">
      <div class="status-item">
        <span class="label">Backend:</span>
        <span class="value" :class="backendStatusClass">
          {{ backendStatusText }}
        </span>
      </div>
      
      <div class="status-item" v-if="backendStatus.pid">
        <span class="label">PID:</span>
        <span class="value">{{ backendStatus.pid }}</span>
      </div>
      
      <div class="status-item">
        <span class="label">Puerto:</span>
        <span class="value">{{ backendStatus.port }}</span>
      </div>
      
      <div class="status-item" v-if="backendStatus.attempts > 0">
        <span class="label">Intentos:</span>
        <span class="value warning">{{ backendStatus.attempts }}</span>
      </div>
    </div>

    <div class="status-actions">
      <button 
        @click="restartBackend" 
        :disabled="isRestarting"
        class="btn btn-secondary"
      >
        <span v-if="isRestarting">Reiniciando...</span>
        <span v-else>Reiniciar Backend</span>
      </button>
      
      <button @click="generateReport" class="btn btn-primary">
        Generar Reporte
      </button>
      
      <button @click="openLogsFolder" class="btn btn-outline">
        Abrir Logs
      </button>
    </div>

    <!-- Notificaciones del sistema -->
    <div v-if="systemNotifications.length > 0" class="system-notifications">
      <div 
        v-for="notification in systemNotifications" 
        :key="notification.id"
        class="notification"
        :class="notification.type"
      >
        <span class="notification-icon">
          {{ getNotificationIcon(notification.type) }}
        </span>
        <span class="notification-message">{{ notification.message }}</span>
        <button @click="dismissNotification(notification.id)" class="dismiss-btn">
          ×
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SystemStatus',
  data() {
    return {
      backendStatus: {
        running: false,
        port: 8081,
        attempts: 0,
        pid: null
      },
      isRestarting: false,
      systemNotifications: [],
      notificationId: 0
    };
  },
  computed: {
    backendStatusClass() {
      if (this.backendStatus.running) return 'success';
      if (this.backendStatus.attempts > 0) return 'warning';
      return 'error';
    },
    backendStatusText() {
      if (this.backendStatus.running) return 'Funcionando';
      if (this.backendStatus.attempts > 0) return 'Reintentando';
      return 'Detenido';
    }
  },
  mounted() {
    this.initializeSystemStatus();
    this.setupEventListeners();
  },
  beforeUnmount() {
    this.cleanupEventListeners();
  },
  methods: {
    async initializeSystemStatus() {
      try {
        if (window.electronAPI) {
          this.backendStatus = await window.electronAPI.getBackendStatus();
        }
      } catch (error) {
        console.error('Error obteniendo estado del sistema:', error);
        this.addNotification('error', 'No se pudo obtener el estado del sistema');
      }
    },

    setupEventListeners() {
      if (window.electronAPI) {
        // Escuchar cambios en el estado del backend
        window.electronAPI.onBackendStatus((event, status) => {
          this.backendStatus = status;
          
          if (status.status === 'running') {
            this.addNotification('success', 'Backend iniciado correctamente');
          } else if (status.status === 'error') {
            this.addNotification('error', 'Error en el backend');
          }
        });

        // Escuchar actualizaciones disponibles
        window.electronAPI.onUpdateAvailable((event, info) => {
          this.addNotification('info', `Actualización disponible: v${info.version}`);
        });

        // Escuchar actualizaciones descargadas
        window.electronAPI.onUpdateDownloaded((event, info) => {
          this.addNotification('success', 'Actualización descargada, se instalará al reiniciar');
        });
      }
    },

    cleanupEventListeners() {
      if (window.electronAPI) {
        window.electronAPI.removeAllListeners('backend-status');
        window.electronAPI.removeAllListeners('update-available');
        window.electronAPI.removeAllListeners('update-downloaded');
      }
    },

    async restartBackend() {
      if (!window.electronAPI) {
        this.addNotification('error', 'API del sistema no disponible');
        return;
      }

      this.isRestarting = true;
      this.addNotification('info', 'Reiniciando backend...');

      try {
        const result = await window.electronAPI.restartBackend();
        this.addNotification('success', result.message);
        
        // Actualizar estado después de un breve delay
        setTimeout(() => {
          this.initializeSystemStatus();
        }, 3000);
      } catch (error) {
        this.addNotification('error', 'Error al reiniciar el backend');
        console.error('Error reiniciando backend:', error);
      } finally {
        this.isRestarting = false;
      }
    },

    async generateReport() {
      if (!window.electronAPI) {
        this.addNotification('error', 'API del sistema no disponible');
        return;
      }

      try {
        this.addNotification('info', 'Generando reporte del sistema...');
        
        const reportFile = await window.electronAPI.saveReport();
        
        if (reportFile) {
          this.addNotification('success', `Reporte generado: ${reportFile}`);
        } else {
          this.addNotification('error', 'Error al generar el reporte');
        }
      } catch (error) {
        this.addNotification('error', 'Error al generar el reporte');
        console.error('Error generando reporte:', error);
      }
    },

    openLogsFolder() {
      if (window.electronAPI) {
        // Enviar comando para abrir la carpeta de logs
        window.electronAPI.openLogsFolder();
      }
    },

    addNotification(type, message) {
      const notification = {
        id: ++this.notificationId,
        type,
        message,
        timestamp: new Date()
      };

      this.systemNotifications.push(notification);

      // Auto-remover notificaciones después de 5 segundos
      setTimeout(() => {
        this.dismissNotification(notification.id);
      }, 5000);
    },

    dismissNotification(id) {
      const index = this.systemNotifications.findIndex(n => n.id === id);
      if (index > -1) {
        this.systemNotifications.splice(index, 1);
      }
    },

    getNotificationIcon(type) {
      const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
      };
      return icons[type] || 'ℹ️';
    }
  }
};
</script>

<style scoped>
.system-status {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-header h3 {
  margin: 0;
  color: #495057;
  font-size: 1.2rem;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #6c757d;
}

.status-indicator.success .status-dot {
  background: #28a745;
}

.status-indicator.warning .status-dot {
  background: #ffc107;
}

.status-indicator.error .status-dot {
  background: #dc3545;
}

.status-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.status-item .label {
  font-size: 0.9rem;
  color: #6c757d;
  font-weight: 500;
}

.status-item .value {
  font-size: 1rem;
  font-weight: 600;
  color: #495057;
}

.status-item .value.success {
  color: #28a745;
}

.status-item .value.warning {
  color: #ffc107;
}

.status-item .value.error {
  color: #dc3545;
}

.status-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #545b62;
}

.btn-outline {
  background: transparent;
  color: #6c757d;
  border: 1px solid #6c757d;
}

.btn-outline:hover:not(:disabled) {
  background: #6c757d;
  color: white;
}

.system-notifications {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  position: relative;
}

.notification.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.notification.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.notification.warning {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
}

.notification.info {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.notification-icon {
  font-size: 1.1rem;
}

.notification-message {
  flex: 1;
}

.dismiss-btn {
  background: none;
  border: none;
  color: inherit;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.dismiss-btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .status-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .status-details {
    grid-template-columns: 1fr;
  }
  
  .status-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
