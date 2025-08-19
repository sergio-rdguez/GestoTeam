const { app, BrowserWindow } = require('electron');
const path = require('path');
const { spawn } = require('child_process');

let backendProcess;

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1280,
    height: 720,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js') // Lo crearemos si es necesario
    },
    icon: path.join(__dirname, 'icon.png') // Añadiremos un icono más adelante
  });

  // Carga el frontend de Vue (que estará en la carpeta 'dist')
  mainWindow.loadFile(path.join(__dirname, 'dist', 'index.html'));
  
  // Opcional: Abre las herramientas de desarrollo
  // mainWindow.webContents.openDevTools();
}

function startBackend() {
    const jarPath = path.join(__dirname, 'app', 'gestoteam.jar');
    
    // Usamos el perfil 'local-client' que creamos
    backendProcess = spawn('java', ['-jar', jarPath, '--spring.profiles.active=local-client']);

    backendProcess.stdout.on('data', (data) => {
        console.log(`Backend stdout: ${data}`);
    });

    backendProcess.stderr.on('data', (data) => {
        console.error(`Backend stderr: ${data}`);
    });

    backendProcess.on('close', (code) => {
        console.log(`El proceso del backend ha terminado con código ${code}`);
    });
}

app.whenReady().then(() => {
  startBackend();
  createWindow();

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') {
    // Si tenemos un proceso de backend, lo terminamos
    if (backendProcess) {
        backendProcess.kill();
    }
    app.quit();
  }
});

app.on('before-quit', () => {
    if (backendProcess) {
        backendProcess.kill();
    }
});