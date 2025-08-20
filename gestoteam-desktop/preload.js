const { contextBridge, ipcRenderer } = require('electron');

// Exponer APIs seguras al renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  // Sistema y reportes
  getSystemReport: () => ipcRenderer.invoke('get-system-report'),
  saveReport: () => ipcRenderer.invoke('save-report'),
  
  // Estado del backend
  getBackendStatus: () => ipcRenderer.invoke('get-backend-status'),
  restartBackend: () => ipcRenderer.invoke('restart-backend'),
  
  // Sistema de archivos
  openLogsFolder: () => ipcRenderer.invoke('open-logs-folder'),
  
  // Eventos del sistema
  onBackendStatus: (callback) => ipcRenderer.on('backend-status', callback),
  onUpdateAvailable: (callback) => ipcRenderer.on('update-available', callback),
  onUpdateDownloaded: (callback) => ipcRenderer.on('update-downloaded', callback),
  
  // Remover listeners
  removeAllListeners: (channel) => ipcRenderer.removeAllListeners(channel)
});

// Log de inicialización
console.log('Preload script cargado correctamente');
