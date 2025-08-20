const { contextBridge, ipcRenderer } = require('electron');

// Exponer APIs seguras al renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  // Métodos principales
  invoke: (channel, ...args) => ipcRenderer.invoke(channel, ...args),
  send: (channel, ...args) => ipcRenderer.send(channel, ...args),
  
  // Sistema y reportes
  getSystemReport: () => ipcRenderer.invoke('get-system-report'),
  
  // Estado del backend
  getBackendStatus: () => ipcRenderer.invoke('get-backend-status'),
  restartBackend: () => ipcRenderer.invoke('restart-backend'),
  testBackendConnection: () => ipcRenderer.invoke('test-backend-connection'),
  
  // Sistema de archivos
  openLogsFolder: () => ipcRenderer.invoke('open-logs-folder'),
  
  // Eventos del sistema
  onBackendStatus: (callback) => ipcRenderer.on('backend-status', callback),
  onBackendError: (callback) => ipcRenderer.on('backend-error', callback),
  onFrontendBackendConnection: (callback) => ipcRenderer.on('frontend-backend-connection', callback),
  onUpdateAvailable: (callback) => ipcRenderer.on('update-available', callback),
  onUpdateDownloaded: (callback) => ipcRenderer.on('update-downloaded', callback),
  onUpdateDownloadProgress: (callback) => ipcRenderer.on('update-download-progress', callback),
  
  // Remover listeners
  removeAllListeners: (channel) => ipcRenderer.removeAllListeners(channel)
});

// Log de inicialización
console.log('Preload script cargado correctamente');
