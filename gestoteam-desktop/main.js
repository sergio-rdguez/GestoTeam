const { app, BrowserWindow, ipcMain, dialog, shell } = require('electron');
const path = require('path');
const fs = require('fs');
const { spawn } = require('child_process');
const { autoUpdater } = require('electron-updater');

// Configuración del auto-updater
autoUpdater.autoDownload = false;
autoUpdater.autoInstallOnAppQuit = true;

let backendProcess;
let mainWindow;

// Configuración de directorios
const userDataPath = app.getPath('userData');
const appDataPath = path.join(userDataPath, 'data');
const backupsPath = path.join(userDataPath, 'backups');
const jarPath = path.join(__dirname, 'app', 'gestoteam.jar');
const BACKEND_PORT = 8081;

// Crear directorios necesarios
function createDirectories() {
  const dirs = [appDataPath, backupsPath];
  dirs.forEach(dir => {
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
      console.log(`Directorio creado: ${dir}`);
    }
  });
}

function createWindow() {
  console.log('[Electron Main] Creando la ventana principal...');
  
  // Crear ventana principal
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 800,
    icon: path.join(__dirname, 'build', 'icon.ico'),
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      enableRemoteModule: true
    },
    show: false, // No mostrar hasta que esté listo
    titleBarStyle: 'default',
    autoHideMenuBar: false
  });

  // Cargar el frontend
  mainWindow.loadFile(path.join(__dirname, 'dist/index.html'));
  console.log('[Electron Main] Contenido de la ventana cargado.');
  
  // Mostrar la ventana cuando esté lista
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    // Solo abrir DevTools en desarrollo
    if (process.env.NODE_ENV === 'development') {
      mainWindow.webContents.openDevTools();
    }
  });

  // Manejar enlaces externos
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });

  // Prevenir navegación a URLs externas
  mainWindow.webContents.on('will-navigate', (event, navigationUrl) => {
    const parsedUrl = new URL(navigationUrl);
    if (parsedUrl.origin !== 'file://') {
      event.preventDefault();
      shell.openExternal(navigationUrl);
    }
  });
}

// Funciones de manejo de actualizaciones
function setupAutoUpdater() {
  // Verificar actualizaciones al iniciar
  autoUpdater.checkForUpdates();

  // Eventos del auto-updater
  autoUpdater.on('checking-for-update', () => {
    console.log('Verificando actualizaciones...');
  });

  autoUpdater.on('update-available', (info) => {
    console.log('Actualización disponible:', info);
    dialog.showMessageBox(mainWindow, {
      type: 'info',
      title: 'Actualización disponible',
      message: `Hay una nueva versión disponible (${info.version}). ¿Deseas descargarla ahora?`,
      detail: 'La actualización incluye mejoras y correcciones de seguridad.',
      buttons: ['Sí, descargar', 'Más tarde'],
      defaultId: 0,
      cancelId: 1
    }).then((result) => {
      if (result.response === 0) {
        autoUpdater.downloadUpdate();
      }
    });
  });

  autoUpdater.on('update-not-available', () => {
    console.log('No hay actualizaciones disponibles');
  });

  autoUpdater.on('error', (err) => {
    console.error('Error en el auto-updater:', err);
    dialog.showErrorBox('Error de actualización', 
      'No se pudo verificar las actualizaciones. La aplicación continuará funcionando normalmente.');
  });

  autoUpdater.on('download-progress', (progressObj) => {
    console.log('Descargando actualización:', progressObj.percent + '%');
  });

  autoUpdater.on('update-downloaded', (info) => {
    console.log('Actualización descargada:', info);
    dialog.showMessageBox(mainWindow, {
      type: 'info',
      title: 'Actualización descargada',
      message: 'La actualización se ha descargado correctamente.',
      detail: 'Se instalará automáticamente cuando cierres la aplicación.',
      buttons: ['Reiniciar ahora', 'Más tarde'],
      defaultId: 0,
      cancelId: 1
    }).then((result) => {
      if (result.response === 0) {
        autoUpdater.quitAndInstall();
      }
    });
  });
}

// Iniciar el backend
function startBackend() {
  console.log(`[Electron Main] Lanzando el backend desde: ${jarPath}`);
  
  // Verificar que el JAR existe
  if (!fs.existsSync(jarPath)) {
    console.error('ERROR: No se encontró el archivo gestoteam.jar');
    dialog.showErrorBox('Error de inicio', 
      'No se pudo encontrar el archivo del backend. Por favor, reinstala la aplicación.');
    app.quit();
    return;
  }

  // Iniciar el backend con el perfil local-client
  backendProcess = spawn('java', [
    '-jar', 
    jarPath, 
    '--spring.profiles.active=local-client',
    `--gestoteam.app.data-path=${appDataPath}`,
    `--gestoteam.app.backup-path=${backupsPath}`
  ]);

  backendProcess.stdout.on('data', (data) => {
    console.log(`Backend stdout: ${data}`);
  });

  backendProcess.stderr.on('data', (data) => {
    console.error(`Backend stderr: ${data}`);
  });

  backendProcess.on('close', (code) => {
    console.log(`El proceso del backend se ha cerrado con el código ${code}`);
    if (code !== 0) {
      dialog.showErrorBox('Error del backend', 
        'El backend se ha cerrado inesperadamente. La aplicación se cerrará.');
      app.quit();
    }
  });

  backendProcess.on('error', (err) => {
    console.error('Error al iniciar el backend:', err);
    dialog.showErrorBox('Error de inicio', 
      'No se pudo iniciar el backend. Verifica que Java esté instalado.');
    app.quit();
  });
}

// Manejar eventos de la aplicación
app.whenReady().then(() => {
  console.log('[Electron Main] La aplicación está lista (ready).');
  
  // Crear directorios necesarios
  createDirectories();
  
  // Iniciar el backend
  startBackend();
  
  // Crear la ventana principal
  createWindow();
  
  // Configurar el auto-updater
  setupAutoUpdater();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    console.log('[Electron Main] Todas las ventanas cerradas. Saliendo de la aplicación.');
    if (backendProcess) {
      backendProcess.kill();
    }
    app.quit();
  }
});

app.on('before-quit', () => {
  console.log('[Electron Main] Evento before-quit. Matando el proceso del backend.');
  if (backendProcess) {
    backendProcess.kill();
  }
});

// Manejar eventos de IPC
ipcMain.handle('get-app-version', () => {
  return app.getVersion();
});

ipcMain.handle('get-user-data-path', () => {
  return userDataPath;
});

ipcMain.handle('open-external', async (event, url) => {
  await shell.openExternal(url);
});