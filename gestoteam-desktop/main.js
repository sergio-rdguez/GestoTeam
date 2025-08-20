const { app, BrowserWindow, ipcMain, dialog } = require('electron');
const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');
const os = require('os');
const { autoUpdater } = require('electron-updater');

// Configuración de puertos
const BACKEND_PORT = 8081;
const FRONTEND_PORT = 8080;

// Variables globales
let mainWindow;
let backendProcess;
let isBackendRunning = false;
let backendStartAttempts = 0;
const MAX_BACKEND_ATTEMPTS = 3;

// Configuración de logging
const LOG_DIR = path.join(app.getPath('userData'), 'logs');
const ERROR_LOG_FILE = path.join(LOG_DIR, 'error.log');
const APP_LOG_FILE = path.join(LOG_DIR, 'app.log');

// Asegurar que existe el directorio de logs
if (!fs.existsSync(LOG_DIR)) {
  fs.mkdirSync(LOG_DIR, { recursive: true });
}

// Función de logging profesional
function logMessage(level, message, data = null) {
  const timestamp = new Date().toISOString();
  const logEntry = {
    timestamp,
    level,
    message,
    data,
    platform: os.platform(),
    arch: os.arch(),
    nodeVersion: process.version,
    electronVersion: process.versions.electron
  };

  const logString = JSON.stringify(logEntry) + '\n';
  
  try {
    // Log principal de la aplicación
    fs.appendFileSync(APP_LOG_FILE, logString);
    
    // Log de errores si es crítico
    if (level === 'ERROR' || level === 'CRITICAL') {
      fs.appendFileSync(ERROR_LOG_FILE, logString);
    }
    
    // También mostrar en consola para desarrollo
    console.log(`[${level}] ${message}`, data || '');
  } catch (error) {
    console.error('Error writing to log:', error);
  }
}

// Función para generar reporte completo del sistema
function generateSystemReport() {
  try {
    const report = {
      timestamp: new Date().toISOString(),
      system: {
        platform: os.platform(),
        arch: os.arch(),
        version: os.release(),
        totalMemory: os.totalmem(),
        freeMemory: os.freemem(),
        cpus: os.cpus().length
      },
      application: {
        version: app.getVersion(),
        electronVersion: process.versions.electron,
        nodeVersion: process.version
      },
      backend: {
        status: isBackendRunning ? 'RUNNING' : 'STOPPED',
        port: BACKEND_PORT,
        startAttempts: backendStartAttempts,
        pid: backendProcess ? backendProcess.pid : null
      },
      logs: {
        appLog: fs.existsSync(APP_LOG_FILE) ? fs.statSync(APP_LOG_FILE).size : 0,
        errorLog: fs.existsSync(ERROR_LOG_FILE) ? fs.statSync(ERROR_LOG_FILE).size : 0
      },
      recentErrors: []
    };

    // Leer últimos errores del log
    if (fs.existsSync(ERROR_LOG_FILE)) {
      try {
        const errorContent = fs.readFileSync(ERROR_LOG_FILE, 'utf8');
        const errorLines = errorContent.trim().split('\n').slice(-10); // Últimos 10 errores
        report.recentErrors = errorLines.map(line => {
          try {
            return JSON.parse(line);
          } catch {
            return { raw: line };
          }
        });
      } catch (error) {
        report.recentErrors = [{ error: 'No se pudieron leer los logs' }];
      }
    }

    return report;
  } catch (error) {
    logMessage('ERROR', 'Error generando reporte del sistema', error.message);
    return { error: 'Error generando reporte', details: error.message };
  }
}

// Función para guardar reporte en archivo
function saveReportToFile(report) {
  try {
    const reportDir = path.join(app.getPath('userData'), 'reports');
    if (!fs.existsSync(reportDir)) {
      fs.mkdirSync(reportDir, { recursive: true });
    }

    const reportFile = path.join(reportDir, `report-${Date.now()}.json`);
    fs.writeFileSync(reportFile, JSON.stringify(report, null, 2));
    
    logMessage('INFO', 'Reporte guardado', { file: reportFile });
    return reportFile;
  } catch (error) {
    logMessage('ERROR', 'Error guardando reporte', error.message);
    return null;
  }
}

// Función para iniciar el backend de forma robusta
function startBackend() {
  if (isBackendRunning) {
    logMessage('WARN', 'Backend ya está ejecutándose');
    return;
  }

  try {
    logMessage('INFO', 'Iniciando backend...', { attempt: backendStartAttempts + 1 });
    
    // Verificar que existe el JAR
    const jarPath = path.join(__dirname, 'app', 'gestoteam-backend.jar');
    if (!fs.existsSync(jarPath)) {
      throw new Error(`JAR no encontrado en: ${jarPath}`);
    }

    // Iniciar proceso del backend
    backendProcess = spawn('java', [
      '-jar', jarPath,
      '--spring.profiles.active=local-client',
      '--server.port=' + BACKEND_PORT
    ], {
      stdio: ['pipe', 'pipe', 'pipe'],
      detached: false
    });

    // Configurar manejo de eventos del proceso
    backendProcess.stdout.on('data', (data) => {
      const output = data.toString().trim();
      logMessage('INFO', 'Backend stdout', output);
      
      // Detectar cuando el backend está listo
      if (output.includes('Started GestoTeamApplication') || output.includes('Tomcat started on port')) {
        isBackendRunning = true;
        backendStartAttempts = 0;
        logMessage('SUCCESS', 'Backend iniciado correctamente', { pid: backendProcess.pid });
        
        // Notificar a la ventana principal
        if (mainWindow && !mainWindow.isDestroyed()) {
          mainWindow.webContents.send('backend-status', { status: 'running', pid: backendProcess.pid });
        }
      }
    });

    backendProcess.stderr.on('data', (data) => {
      const error = data.toString().trim();
      logMessage('ERROR', 'Backend stderr', error);
    });

    backendProcess.on('error', (error) => {
      logMessage('CRITICAL', 'Error iniciando backend', error.message);
      handleBackendError(error);
    });

    backendProcess.on('exit', (code, signal) => {
      logMessage('WARN', 'Backend terminado', { code, signal, pid: backendProcess.pid });
      isBackendRunning = false;
      
      if (code !== 0 && signal !== 'SIGTERM') {
        handleBackendError(new Error(`Backend terminó con código ${code}`));
      }
    });

    // Timeout para detectar si el backend no responde
    setTimeout(() => {
      if (!isBackendRunning) {
        logMessage('ERROR', 'Backend no respondió en tiempo razonable');
        handleBackendError(new Error('Timeout al iniciar backend'));
      }
    }, 30000); // 30 segundos

  } catch (error) {
    logMessage('CRITICAL', 'Error crítico iniciando backend', error.message);
    handleBackendError(error);
  }
}

// Función para manejar errores del backend
function handleBackendError(error) {
  backendStartAttempts++;
  
  logMessage('CRITICAL', 'Error del backend', {
    error: error.message,
    attempts: backendStartAttempts,
    maxAttempts: MAX_BACKEND_ATTEMPTS
  });

  // Generar reporte automático
  const report = generateSystemReport();
  const reportFile = saveReportToFile(report);

  // Mostrar diálogo de error profesional
  if (mainWindow && !mainWindow.isDestroyed()) {
    dialog.showErrorBox(
      'Error del Sistema - GestoTeam',
      `Se ha producido un error en el sistema.\n\n` +
      `Se ha generado un reporte completo para el soporte técnico.\n\n` +
      `Archivo del reporte: ${reportFile}\n\n` +
      `Por favor, envíe este archivo al soporte técnico.\n\n` +
      `La aplicación se cerrará para evitar más problemas.`
    );
  }

  // Intentar reiniciar si no hemos excedido el límite
  if (backendStartAttempts < MAX_BACKEND_ATTEMPTS) {
    logMessage('INFO', 'Reintentando iniciar backend...', { attempt: backendStartAttempts + 1 });
    setTimeout(startBackend, 5000); // Esperar 5 segundos antes de reintentar
  } else {
    logMessage('CRITICAL', 'Máximo de intentos alcanzado, cerrando aplicación');
    app.quit();
  }
}

// Función para detener el backend de forma limpia
function stopBackend() {
  if (backendProcess && !backendProcess.killed) {
    logMessage('INFO', 'Deteniendo backend...');
    
    try {
      // Enviar señal de terminación
      backendProcess.kill('SIGTERM');
      
      // Dar tiempo para cierre limpio
      setTimeout(() => {
        if (backendProcess && !backendProcess.killed) {
          logMessage('WARN', 'Forzando cierre del backend');
          backendProcess.kill('SIGKILL');
        }
      }, 5000);
      
      isBackendRunning = false;
    } catch (error) {
      logMessage('ERROR', 'Error deteniendo backend', error.message);
    }
  }
}

// Función para crear la ventana principal
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 800,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      preload: path.join(__dirname, 'preload.js')
    },
    show: false, // No mostrar hasta que esté listo
    icon: path.join(__dirname, 'assets', 'icon.png'),
    titleBarStyle: 'default',
    resizable: true,
    maximizable: true
  });

  // Cargar la aplicación frontend
  mainWindow.loadURL(`http://localhost:${FRONTEND_PORT}`);

  // Mostrar la ventana cuando esté lista
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    logMessage('INFO', 'Ventana principal mostrada');
  });

  // Manejar cierre de la ventana
  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  // Manejar errores de carga
  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription) => {
    logMessage('ERROR', 'Error cargando frontend', { errorCode, errorDescription });
    
    // Si falla la carga del frontend, verificar el backend
    if (!isBackendRunning) {
      startBackend();
    }
  });

  // Logs de desarrollo
  if (process.env.NODE_ENV === 'development') {
    mainWindow.webContents.openDevTools();
  }
}

// Configuración de auto-updater
function setupAutoUpdater() {
  autoUpdater.logger = require('electron-log');
  autoUpdater.logger.transports.file.level = 'info';

  autoUpdater.on('checking-for-update', () => {
    logMessage('INFO', 'Buscando actualizaciones...');
  });

  autoUpdater.on('update-available', (info) => {
    logMessage('INFO', 'Actualización disponible', info);
    if (mainWindow && !mainWindow.isDestroyed()) {
      mainWindow.webContents.send('update-available', info);
    }
  });

  autoUpdater.on('update-not-available', (info) => {
    logMessage('INFO', 'No hay actualizaciones disponibles', info);
  });

  autoUpdater.on('error', (err) => {
    logMessage('ERROR', 'Error en auto-updater', err.message);
  });

  autoUpdater.on('download-progress', (progressObj) => {
    logMessage('INFO', 'Descargando actualización', progressObj);
  });

  autoUpdater.on('update-downloaded', (info) => {
    logMessage('INFO', 'Actualización descargada', info);
    if (mainWindow && !mainWindow.isDestroyed()) {
      mainWindow.webContents.send('update-downloaded', info);
    }
  });

  // Verificar actualizaciones al iniciar
  setTimeout(() => {
    autoUpdater.checkForUpdates();
  }, 10000); // 10 segundos después del inicio
}

// Eventos de la aplicación
app.whenReady().then(() => {
  logMessage('INFO', 'Aplicación iniciando...');
  
  // Crear ventana principal
  createWindow();
  
  // Iniciar backend
  startBackend();
  
  // Configurar auto-updater
  setupAutoUpdater();
  
  logMessage('SUCCESS', 'Aplicación iniciada correctamente');
});

app.on('window-all-closed', () => {
  logMessage('INFO', 'Todas las ventanas cerradas');
  stopBackend();
  
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

app.on('before-quit', () => {
  logMessage('INFO', 'Aplicación cerrando...');
  stopBackend();
});

app.on('quit', () => {
  logMessage('INFO', 'Aplicación cerrada');
});

// Manejo de errores no capturados
process.on('uncaughtException', (error) => {
  logMessage('CRITICAL', 'Excepción no capturada', error.message);
  
  const report = generateSystemReport();
  saveReportToFile(report);
  
  dialog.showErrorBox(
    'Error Crítico del Sistema',
    `Se ha producido un error crítico en el sistema.\n\n` +
    `Se ha generado un reporte automático.\n\n` +
    `La aplicación se cerrará para evitar más problemas.`
  );
  
  app.quit();
});

process.on('unhandledRejection', (reason, promise) => {
  logMessage('ERROR', 'Promesa rechazada no manejada', { reason: reason?.message || reason });
});

// IPC handlers para comunicación con el renderer
ipcMain.handle('get-system-report', () => {
  return generateSystemReport();
});

ipcMain.handle('save-report', () => {
  const report = generateSystemReport();
  return saveReportToFile(report);
});

ipcMain.handle('get-backend-status', () => {
  return {
    running: isBackendRunning,
    port: BACKEND_PORT,
    attempts: backendStartAttempts,
    pid: backendProcess ? backendProcess.pid : null
  };
});

ipcMain.handle('restart-backend', () => {
  stopBackend();
  setTimeout(startBackend, 2000);
  return { message: 'Reiniciando backend...' };
});

ipcMain.handle('open-logs-folder', () => {
  try {
    const { shell } = require('electron');
    shell.openPath(LOG_DIR);
    return { success: true, path: LOG_DIR };
  } catch (error) {
    logMessage('ERROR', 'Error abriendo carpeta de logs', error.message);
    return { success: false, error: error.message };
  }
});

// Log inicial
logMessage('INFO', 'Proceso principal iniciado', {
  version: app.getVersion(),
  platform: process.platform,
  arch: process.arch
});