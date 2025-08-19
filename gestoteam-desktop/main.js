const { app, BrowserWindow } = require('electron');
const path = require('path');
const { spawn } = require('child_process');

let backendProcess;

function createWindow() {
  console.log('[Electron Main] Creando la ventana principal...');
  const mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    webPreferences: {
      // preload: path.join(__dirname, 'preload.js'), // No lo usamos por ahora
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  mainWindow.loadFile(path.join(__dirname, 'dist/index.html'));
  console.log('[Electron Main] Contenido de la ventana cargado.');
  
  // ¡Línea clave! Abrimos las DevTools para ver los errores del frontend.
  mainWindow.webContents.openDevTools();
}

console.log('[Electron Main] Iniciando el proceso principal de Electron.');

const jarPath = path.join(__dirname, 'app', 'gestoteam.jar');
console.log(`[Electron Main] Lanzando el backend desde: ${jarPath}`);

backendProcess = spawn('java', ['-jar', jarPath, '--spring.profiles.active=local-client']);

backendProcess.stdout.on('data', (data) => {
  console.log(`Backend stdout: ${data}`);
});

backendProcess.stderr.on('data', (data) => {
  console.error(`Backend stderr: ${data}`);
});

backendProcess.on('close', (code) => {
  console.log(`El proceso del backend se ha cerrado con el código ${code}`);
});

app.whenReady().then(() => {
  console.log('[Electron Main] La aplicación está lista (ready).');
  createWindow();

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