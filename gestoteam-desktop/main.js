const { app, BrowserWindow, ipcMain, dialog } = require('electron');
const { spawn } = require('child_process');
const path = require('path');
const fs = require('fs');
const os = require('os');
// const { autoUpdater } = require('electron-updater'); // Desactivado temporalmente

// ============================================================================
// CONFIGURACIÓN GLOBAL
// ============================================================================
const BACKEND_PORT = 8081;
const FRONTEND_PORT = 8080;
const BACKEND_STARTUP_TIMEOUT = 30000; // 30 segundos
const BACKEND_HEALTH_CHECK_INTERVAL = 5000; // 5 segundos

// Variables globales
let mainWindow;
let backendProcess;
let isBackendRunning = false;
let backendStartAttempts = 0;
let backendHealthCheckInterval;
let backendStartTime;
const MAX_BACKEND_ATTEMPTS = 1; // Reducido a 1 intento

// ============================================================================
// SISTEMA DE LOGGING PROFESIONAL
// ============================================================================
const getDataDir = () => {
  if (process.env.NODE_ENV === 'development') {
    return app.getPath('userData');
  }
  
  if (process.resourcesPath) {
    return path.join(process.resourcesPath, '..');
  }
  
  return app.getPath('userData');
};

const DATA_DIR = getDataDir();
const LOG_DIR = path.join(DATA_DIR, 'logs');
const ERROR_LOG_FILE = path.join(LOG_DIR, 'error.log');
const APP_LOG_FILE = path.join(LOG_DIR, 'app.log');

// Crear directorio de logs
try {
  if (!fs.existsSync(LOG_DIR)) {
    fs.mkdirSync(LOG_DIR, { recursive: true });
  }
} catch (error) {
  console.error('Error creando directorio de logs:', error.message);
}

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
    fs.appendFileSync(APP_LOG_FILE, logString);
    
    if (level === 'ERROR' || level === 'CRITICAL') {
      fs.appendFileSync(ERROR_LOG_FILE, logString);
    }
    
    console.log(`[${level}] ${message}`, data || '');
  } catch (error) {
    console.error('Error escribiendo log:', error.message);
  }
}

// ============================================================================
// VERIFICACIONES DEL SISTEMA
// ============================================================================
function diagnoseEmbeddedJava() {
  const embeddedJava = path.join(process.resourcesPath, 'java-runtime', 'bin', 'java.exe');
  
  if (!fs.existsSync(embeddedJava)) {
    return { exists: false, executable: false, error: 'Archivo no encontrado' };
  }
  
  try {
    const { execSync } = require('child_process');
    execSync(`"${embeddedJava}" -version`, { 
      stdio: 'pipe', 
      timeout: 5000,
      windowsHide: true 
    });
    return { exists: true, executable: true, error: null };
  } catch (error) {
    return { exists: true, executable: false, error: error.message };
  }
}

function checkSystemRequirements() {
  logMessage('INFO', 'Verificando requisitos del sistema...');
  
  const checks = {
    java: false,
    jar: false,
    directories: false
  };
  
  // Verificar Java del sistema
  try {
    // Intentar encontrar Java en PATH
    const javaCheck = spawn('java', ['-version']);
    javaCheck.on('error', () => {
      logMessage('WARN', 'Java no encontrado en PATH del sistema');
    });
    javaCheck.on('exit', (code) => {
      if (code === 0) {
        checks.java = true;
        logMessage('INFO', 'Java del sistema encontrado, usando java');
      }
    });
    
    // Verificar Java embebido (más confiable)
    const embeddedJava = path.join(process.resourcesPath, 'java-runtime', 'bin', 'java.exe');
    if (fs.existsSync(embeddedJava)) {
      const diagnosis = diagnoseEmbeddedJava();
      if (diagnosis.executable) {
        checks.java = true;
        logMessage('INFO', 'Java embebido encontrado y ejecutable, usando java-runtime', { path: embeddedJava });
      } else {
        logMessage('WARN', 'Java embebido encontrado pero no ejecutable', { 
          path: embeddedJava, 
          error: diagnosis.error 
        });
      }
    }
    
    // Verificar Java en directorios comunes de Windows
    const commonJavaPaths = [
      'C:\\Program Files\\Java\\jdk-17\\bin\\java.exe',
      'C:\\Program Files\\Java\\jre-17\\bin\\java.exe',
      'C:\\Program Files (x86)\\Java\\jdk-17\\bin\\java.exe',
      'C:\\Program Files (x86)\\Java\\jre-17\\bin\\java.exe',
      'C:\\Program Files\\Java\\jdk-11\\bin\\java.exe',
      'C:\\Program Files\\Java\\jre-11\\bin\\java.exe',
      'C:\\Program Files (x86)\\Java\\jdk-11\\bin\\java.exe',
      'C:\\Program Files (x86)\\Java\\jre-11\\bin\\java.exe'
    ];
    
    for (const javaPath of commonJavaPaths) {
      if (fs.existsSync(javaPath)) {
        checks.java = true;
        logMessage('INFO', 'Java encontrado en directorio común', { path: javaPath });
        break;
      }
    }
    
  } catch (error) {
    logMessage('ERROR', 'Error verificando Java:', error.message);
  }
  
  // Verificar JAR del backend
  const jarPath = findBackendJar();
  checks.jar = !!jarPath;
  logMessage('INFO', `JAR del backend encontrado: ${checks.jar}`, { path: jarPath });
  
  // Verificar directorios
  try {
    const userHome = process.env.USERPROFILE || process.env.HOME;
    const gestoteamDir = path.join(userHome, '.gestoteam');
    if (!fs.existsSync(gestoteamDir)) {
      fs.mkdirSync(gestoteamDir, { recursive: true });
    }
    checks.directories = true;
    logMessage('INFO', 'Directorios del backend creados/verificados');
  } catch (error) {
    checks.directories = false;
    logMessage('ERROR', 'Error creando directorios del backend:', error.message);
  }
  
  return checks;
}

function findBackendJar() {
  const possiblePaths = [
    path.join(__dirname, 'app', 'gestoteam.jar'),
    path.join(__dirname, 'app', 'backend', 'gestoteam-backend.jar'),
    path.join(process.resourcesPath, 'gestoteam.jar'),
    path.join(process.resourcesPath, 'app', 'gestoteam.jar'),
    path.join(process.resourcesPath, 'app', 'backend', 'gestoteam-backend.jar'),
    path.join(process.resourcesPath, '..', 'app', 'gestoteam.jar'),
    path.join(process.resourcesPath, '..', 'app', 'backend', 'gestoteam-backend.jar')
  ];
  
  for (const jarPath of possiblePaths) {
    if (fs.existsSync(jarPath)) {
      return jarPath;
    }
  }
  
  return null;
}

// ============================================================================
// VERIFICACIÓN DE CONECTIVIDAD DEL BACKEND
// ============================================================================
function testBackendConnectivity() {
  return new Promise((resolve) => {
    const net = require('net');
    const client = new net.Socket();
    
    client.connect(BACKEND_PORT, '127.0.0.1', () => {
      client.destroy();
      resolve({ success: true, message: `Puerto ${BACKEND_PORT} accesible` });
    });
    
    client.on('error', (err) => {
      resolve({ success: false, message: `Puerto ${BACKEND_PORT} no accesible: ${err.message}` });
    });
    
    client.setTimeout(5000, () => {
      client.destroy();
      resolve({ success: false, message: `Timeout conectando al puerto ${BACKEND_PORT}` });
    });
  });
}

function performBackendHealthCheck() {
  if (!isBackendRunning) return;
  
  testBackendConnectivity().then(result => {
    if (!result.success) {
      logMessage('ERROR', 'Backend no responde a health check:', result.message);
      handleBackendFailure('Backend no responde a verificación de salud');
    }
  });
}

// ============================================================================
// GESTIÓN DEL BACKEND
// ============================================================================
function startBackend() {
  if (isBackendRunning) {
    logMessage('WARN', 'Backend ya está ejecutándose');
    return;
  }

  try {
    logMessage('INFO', 'Iniciando backend...', { attempt: backendStartAttempts + 1 });
    
    // Verificar requisitos
    const systemChecks = checkSystemRequirements();
    if (!systemChecks.jar) {
      throw new Error('JAR del backend no encontrado');
    }
    
    const jarPath = findBackendJar();
    
    // Determinar ruta de Java (embebido primero, luego búsqueda automática)
    const getJavaPath = () => {
          // 1. PRIMERO: Verificar Java embebido (más confiable)
    const embeddedJava = path.join(process.resourcesPath, 'java-runtime', 'bin', 'java.exe');
    if (fs.existsSync(embeddedJava)) {
      // Verificar que sea ejecutable
      try {
        const { execSync } = require('child_process');
        execSync(`"${embeddedJava}" -version`, { 
          stdio: 'pipe', 
          timeout: 5000,
          windowsHide: true 
        });
        logMessage('INFO', 'Java embebido encontrado y ejecutable, usando java-runtime', { path: embeddedJava });
        return embeddedJava;
      } catch (error) {
        logMessage('WARN', 'Java embebido encontrado pero no ejecutable, intentando con alternativas', { 
          path: embeddedJava, 
          error: error.message 
        });
      }
    }
      
      // 2. SEGUNDO: Verificar si 'java' está en PATH del sistema
      try {
        const { execSync } = require('child_process');
        execSync('java -version', { stdio: 'pipe' });
        logMessage('INFO', 'Java encontrado en PATH del sistema, usando java');
        return 'java';
      } catch (error) {
        logMessage('WARN', 'Java no encontrado en PATH del sistema');
      }
      
      // 3. TERCERO: Verificar JAVA_HOME (más profesional)
      const javaHome = process.env.JAVA_HOME;
      if (javaHome) {
        const javaExePath = path.join(javaHome, 'bin', 'java.exe');
        if (fs.existsSync(javaExePath)) {
          logMessage('INFO', 'Java encontrado en JAVA_HOME', { 
            javaHome: javaHome,
            javaPath: javaExePath 
          });
          // Si JAVA_HOME tiene espacios, intentar usar 'java' del PATH
          if (javaHome.includes(' ')) {
            logMessage('WARN', 'JAVA_HOME tiene espacios, intentando usar java del PATH');
            try {
              const { execSync } = require('child_process');
              execSync('java -version', { stdio: 'pipe' });
              logMessage('INFO', 'Usando java del PATH para evitar problemas con espacios');
              return 'java';
            } catch (error) {
              logMessage('ERROR', 'No se puede usar java del PATH, JAVA_HOME tiene espacios');
            }
          }
          return javaExePath;
        } else {
          logMessage('WARN', 'JAVA_HOME configurado pero java.exe no encontrado', { 
            javaHome: javaHome,
            expectedPath: javaExePath 
          });
        }
      }
      
      // 4. CUARTO: Buscar en directorios estándar de Windows
      const commonJavaPaths = [
        'C:\\Program Files\\Java\\jdk-17\\bin\\java.exe',
        'C:\\Program Files\\Java\\jre-17\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jdk-17\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jre-17\\bin\\java.exe',
        'C:\\Program Files\\Java\\jdk-11\\bin\\java.exe',
        'C:\\Program Files\\Java\\jre-11\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jdk-11\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jre-11\\bin\\java.exe',
        'C:\\Program Files\\Java\\jdk-21\\bin\\java.exe',
        'C:\\Program Files\\Java\\jre-21\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jdk-21\\bin\\java.exe',
        'C:\\Program Files (x86)\\Java\\jre-21\\bin\\java.exe'
      ];
      
      for (const javaPath of commonJavaPaths) {
        if (fs.existsSync(javaPath)) {
          logMessage('INFO', 'Java encontrado en directorio estándar', { 
            path: javaPath 
          });
          // Si la ruta tiene espacios, intentar usar 'java' del PATH
          if (javaPath.includes(' ')) {
            logMessage('WARN', 'Ruta de Java tiene espacios, intentando usar java del PATH');
            try {
              const { execSync } = require('child_process');
              execSync('java -version', { stdio: 'pipe' });
              logMessage('INFO', 'Usando java del PATH para evitar problemas con espacios');
              return 'java';
            } catch (error) {
              logMessage('ERROR', 'No se puede usar java del PATH, ruta tiene espacios');
            }
          }
          return javaPath;
        }
      }
      
      // 5. QUINTO: Último intento - verificar si 'java' está en PATH (redundante pero seguro)
      try {
        const { execSync } = require('child_process');
        execSync('java -version', { stdio: 'pipe' });
        logMessage('INFO', 'Java encontrado en PATH del sistema (último intento), usando java');
        return 'java';
      } catch (error) {
        logMessage('ERROR', 'Java no encontrado en ninguna ubicación', { 
          javaHome: javaHome,
          embeddedPath: embeddedJava,
          commonPaths: commonJavaPaths,
          pathError: error.message 
        });
        
        // Mostrar error crítico al usuario con instrucciones claras
        showJavaNotFoundError(javaHome);
        return null;
      }
    };
    
    const javaPath = getJavaPath();
    
    // Si no se encontró Java, salir
    if (!javaPath) {
      logMessage('CRITICAL', 'No se puede iniciar backend sin Java');
      return;
    }
    
    const javaArgs = [
      '-jar', jarPath,
      '--spring.profiles.active=desktop',
      '--server.port=' + BACKEND_PORT,
      '--logging.level.com.gestoteam=INFO',
      '--logging.level.org.springframework=WARN'
    ];
    
    logMessage('INFO', 'Ejecutando backend', { 
      java: javaPath, 
      jar: jarPath, 
      args: javaArgs,
      workingDir: path.dirname(jarPath)
    });
    
    // Función para intentar ejecutar con un Java específico
    const tryExecuteJava = (javaPathToTry, attemptNumber = 1) => {
      try {
            // Para Java embebido, usar exec en lugar de spawn para evitar problemas de permisos
    if (javaPathToTry.includes('java-runtime')) {
      logMessage('INFO', 'Usando Java embebido con exec para evitar problemas de permisos');
      
      // Usar ruta absoluta completa para evitar problemas de permisos
      const javaCommand = `"${javaPathToTry}" -jar "${jarPath}" --spring.profiles.active=desktop --server.port=${BACKEND_PORT} --logging.level.com.gestoteam=INFO --logging.level.org.springframework=WARN`;
      
      logMessage('INFO', 'Comando Java embebido', { command: javaCommand });
      
      const process = require('child_process').exec(javaCommand, {
        cwd: path.dirname(jarPath),
        windowsHide: true,
        env: { ...process.env, PATH: process.env.PATH }
      });
          
          backendStartTime = Date.now();
          logMessage('INFO', 'Proceso del backend iniciado con exec', { pid: process.pid });
          
          // Configurar manejo de eventos
          process.stdout.on('data', (data) => {
            const output = data.toString().trim();
            logMessage('INFO', 'Backend stdout', output);
            
            // Detectar cuando el backend está listo
            if (output.includes('Started GestoTeamApplication') || 
                output.includes('Tomcat started on port') ||
                output.includes('Started application')) {
              isBackendRunning = true;
              backendStartAttempts = 0;
              backendProcess = process;
              logMessage('SUCCESS', 'Backend iniciado correctamente', { 
                pid: process.pid,
                startupTime: Date.now() - backendStartTime
              });
              
              // Iniciar health checks
              startBackendHealthChecks();
              
              // Notificar a la ventana principal
              if (mainWindow && !mainWindow.isDestroyed()) {
                mainWindow.webContents.send('backend-status', { 
                  status: 'running', 
                  pid: process.pid 
                });
              }
            }
          });
          
          process.stderr.on('data', (data) => {
            const error = data.toString().trim();
            logMessage('ERROR', 'Backend stderr', error);
          });
          
          process.on('error', (error) => {
            logMessage('ERROR', 'Error en proceso del backend (exec)', error);
            
            // Si falló con Java embebido, intentar con Java del sistema
            if (attemptNumber === 1) {
              logMessage('INFO', 'Java embebido falló, intentando con Java del sistema...');
              tryExecuteJava('java', 2);
            } else {
              handleBackendFailure('Error en proceso del backend');
            }
          });
          
          process.on('exit', (code, signal) => {
            if (code !== 0) {
              logMessage('ERROR', 'Backend terminó con código de salida (exec)', { code, signal });
              
              // Si falló con Java embebido, intentar con Java del sistema
              if (attemptNumber === 1) {
                logMessage('INFO', 'Java embebido falló, intentando con Java del sistema...');
                tryExecuteJava('java', 2);
              } else {
                handleBackendFailure('Backend terminó inesperadamente');
              }
            }
          });
          
          return process;
          
        } else {
          // Para Java del sistema, usar spawn como antes
          const process = spawn(javaPathToTry, javaArgs, {
            stdio: ['pipe', 'pipe', 'pipe'],
            detached: false,
            cwd: path.dirname(jarPath)
          });
          
          backendStartTime = Date.now();
          logMessage('INFO', 'Proceso del backend iniciado con spawn', { pid: process.pid });
          
          // Configurar manejo de eventos
          process.stdout.on('data', (data) => {
            const output = data.toString().trim();
            logMessage('INFO', 'Backend stdout', output);
            
            // Detectar cuando el backend está listo
            if (output.includes('Started GestoTeamApplication') || 
                output.includes('Tomcat started on port') ||
                output.includes('Started application')) {
              isBackendRunning = true;
              backendStartAttempts = 0;
              backendProcess = process;
              logMessage('SUCCESS', 'Backend iniciado correctamente', { 
                pid: process.pid,
                startupTime: Date.now() - backendStartTime
              });
              
              // Iniciar health checks
              startBackendHealthChecks();
              
              // Notificar a la ventana principal
              if (mainWindow && !mainWindow.isDestroyed()) {
                mainWindow.webContents.send('backend-status', { 
                  status: 'running', 
                  pid: process.pid 
                });
              }
            }
          });
          
          process.stderr.on('data', (data) => {
            const error = data.toString().trim();
            logMessage('ERROR', 'Backend stderr', error);
          });
          
          process.on('error', (error) => {
            logMessage('ERROR', 'Error en proceso del backend (spawn)', error);
            handleBackendFailure('Error en proceso del backend');
          });
          
          process.on('exit', (code, signal) => {
            if (code !== 0) {
              logMessage('ERROR', 'Backend terminó con código de salida (spawn)', { code, signal });
              handleBackendFailure('Backend terminó inesperadamente');
            }
          });
          
          return process;
        }
        
      } catch (error) {
        logMessage('ERROR', 'Error iniciando proceso del backend', error);
        
        // Si es el primer intento y falló con Java embebido, intentar con Java del sistema
        if (attemptNumber === 1 && javaPathToTry.includes('java-runtime')) {
          logMessage('INFO', 'Java embebido falló, intentando con Java del sistema...');
          return tryExecuteJava('java', 2);
        } else {
          throw error;
        }
      }
    };
    
    // Intentar ejecutar con el Java seleccionado
    backendProcess = tryExecuteJava(javaPath);
    
    // Timeout para detectar si el backend no responde
    setTimeout(() => {
      if (!isBackendRunning) {
        logMessage('ERROR', 'Backend no respondió en tiempo razonable');
        handleBackendFailure('Timeout al iniciar backend');
      }
    }, BACKEND_STARTUP_TIMEOUT);

  } catch (error) {
    logMessage('CRITICAL', 'Error crítico iniciando backend', error.message);
    handleBackendFailure(`Error crítico: ${error.message}`);
  }
}

function startBackendHealthChecks() {
  if (backendHealthCheckInterval) {
    clearInterval(backendHealthCheckInterval);
  }
  
  backendHealthCheckInterval = setInterval(performBackendHealthCheck, BACKEND_HEALTH_CHECK_INTERVAL);
  logMessage('INFO', 'Health checks del backend iniciados');
}

function stopBackendHealthChecks() {
  if (backendHealthCheckInterval) {
    clearInterval(backendHealthCheckInterval);
    backendHealthCheckInterval = null;
    logMessage('INFO', 'Health checks del backend detenidos');
  }
}

function stopBackend() {
  if (backendProcess) {
    logMessage('INFO', 'Deteniendo backend...');
    backendProcess.kill('SIGTERM');
    
    // Forzar cierre si no responde
    setTimeout(() => {
      if (backendProcess && !backendProcess.killed) {
        backendProcess.kill('SIGKILL');
        logMessage('WARN', 'Backend forzado a cerrar con SIGKILL');
      }
    }, 5000);
    
    isBackendRunning = false;
    stopBackendHealthChecks();
  }
}

// ============================================================================
// MANEJO DE ERRORES DEL BACKEND
// ============================================================================
function handleBackendFailure(reason) {
  backendStartAttempts++;
  
  logMessage('CRITICAL', 'Fallo del backend', {
    reason,
    attempts: backendStartAttempts,
    maxAttempts: MAX_BACKEND_ATTEMPTS
  });

  // Mostrar error en pantalla
  if (mainWindow && !mainWindow.isDestroyed()) {
    mainWindow.webContents.send('backend-error', {
      reason,
      attempts: backendStartAttempts,
      maxAttempts: MAX_BACKEND_ATTEMPTS
    });
  }

  // Reintentar si no hemos excedido el máximo
  if (backendStartAttempts < MAX_BACKEND_ATTEMPTS) {
    logMessage('INFO', 'Reintentando en 5 segundos...');
    setTimeout(() => {
      startBackend();
    }, 5000);
  } else {
    logMessage('CRITICAL', 'Máximo de intentos alcanzado');
    showBackendFailureDialog(reason);
  }
}

function showBackendFailureDialog(reason) {
  if (mainWindow && !mainWindow.isDestroyed()) {
    dialog.showErrorBox(
      'Error Crítico del Sistema - GestoTeam',
      `El backend no pudo iniciarse correctamente.\n\n` +
      `Razón: ${reason}\n\n` +
      `Se han realizado ${backendStartAttempts} intentos fallidos.\n\n` +
      `La aplicación puede no funcionar correctamente.\n\n` +
      `Revisa los logs para más detalles.`
    );
  }
}

// ============================================================================
// VERIFICACIÓN DEL FRONTEND
// ============================================================================
function verifyFrontendConnection() {
  setTimeout(() => {
    if (mainWindow && !mainWindow.isDestroyed()) {
      mainWindow.webContents.executeJavaScript(`
        // Verificar si el frontend puede conectarse al backend
        fetch('http://localhost:${BACKEND_PORT}/api/health')
          .then(response => {
            if (response.ok) {
              console.log('✅ Frontend conectado al backend correctamente');
              window.electronAPI.send('frontend-backend-connection', { success: true });
            } else {
              console.log('⚠️ Backend responde pero con error:', response.status);
              window.electronAPI.send('frontend-backend-connection', { 
                success: false, 
                error: 'HTTP ' + response.status 
              });
            }
          })
          .catch(error => {
            console.log('❌ Frontend no puede conectar al backend:', error.message);
            window.electronAPI.send('frontend-backend-connection', { 
              success: false, 
              error: error.message 
            });
          });
      `);
    }
  }, 5000); // Esperar 5 segundos después de que se cargue el frontend
}

// ============================================================================
// VENTANA PRINCIPAL
// ============================================================================
// Función para mostrar error de Java no encontrado
function showJavaNotFoundError(javaHome) {
  const { dialog } = require('electron');
  
  let message = 'GestoTeam Desktop requiere Java para funcionar.\n\n';
  message += '🔧 Soluciones:\n';
  message += '1. Instalar Java 17 o superior desde: https://adoptium.net/\n';
  message += '2. Asegurarse de que Java esté en el PATH del sistema\n';
  message += '3. Reiniciar la aplicación después de instalar Java\n\n';
  message += '📋 Detalles técnicos:\n';
  
  if (javaHome) {
    message += `• JAVA_HOME configurado: ${javaHome}\n`;
  }
  message += '• Java embebido no está disponible\n';
  message += '• Java del sistema no se encuentra en PATH\n';
  message += '• Directorios verificados: Program Files\\Java, Program Files (x86)\\Java\n';
  message += '• La aplicación no puede iniciar el backend\n\n';
  message += '💡 Después de instalar Java, reinicia la aplicación.';

  dialog.showErrorBox(
    '❌ Java Runtime No Encontrado',
    message
  );
  
  // No salir inmediatamente, dar tiempo al usuario de leer el mensaje
  setTimeout(() => {
    app.quit();
  }, 10000); // 10 segundos
}

function createWindow() {
  logMessage('INFO', 'Creando ventana principal...');
  
  let mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 800,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      preload: path.join(__dirname, 'preload.js'),
      webSecurity: false, // Permitir peticiones HTTP locales
      allowRunningInsecureContent: true // Permitir contenido HTTP
    },
    show: false,
    icon: path.join(__dirname, 'assets', 'icon.ico')
  });

  // Cargar frontend
  const frontendPath = path.join(__dirname, 'app', 'frontend', 'index.html');
  if (fs.existsSync(frontendPath)) {
    mainWindow.loadFile(frontendPath);
    logMessage('INFO', 'Frontend cargado desde archivo local', { path: frontendPath });
  } else {
    logMessage('ERROR', 'Frontend no encontrado', { path: frontendPath });
    mainWindow.loadURL('http://localhost:' + FRONTEND_PORT);
  }

  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    logMessage('SUCCESS', 'Ventana principal creada y mostrada');
    
    // Verificar conectividad del frontend
    verifyFrontendConnection();
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// ============================================================================
// EVENTOS DE LA APLICACIÓN
// ============================================================================
app.whenReady().then(() => {
  logMessage('INFO', 'Aplicación iniciando...');
  
  // Crear ventana principal
  createWindow();
  
  // Iniciar backend con delay
  setTimeout(() => {
    logMessage('INFO', 'Iniciando backend...');
    startBackend();
  }, 2000);
  
  // Configurar auto-updater
  // setupAutoUpdater(); // Desactivado temporalmente
  
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

// ============================================================================
// MANEJO DE ERRORES NO CAPTURADOS
// ============================================================================
process.on('uncaughtException', (error) => {
  logMessage('CRITICAL', 'Excepción no capturada', error.message);
  
  dialog.showErrorBox(
    'Error Crítico del Sistema',
    `Se ha producido un error crítico en el sistema.\n\n` +
    `Error: ${error.message}\n\n` +
    `La aplicación se cerrará para evitar más problemas.`
  );
  
  app.quit();
});

process.on('unhandledRejection', (reason, promise) => {
  logMessage('ERROR', 'Promesa rechazada no manejada', { reason: reason?.message || reason });
});

// ============================================================================
// AUTO-UPDATER
// ============================================================================
function setupAutoUpdater() {
  // autoUpdater.logger = require('electron-log'); // Desactivado temporalmente
  // autoUpdater.logger.transports.file.level = 'info'; // Desactivado temporalmente
  
  // autoUpdater.on('checking-for-update', () => { // Desactivado temporalmente
  //   logMessage('INFO', 'Verificando actualizaciones...'); // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // autoUpdater.on('update-available', (info) => { // Desactivado temporalmente
  //   logMessage('INFO', 'Actualización disponible', info); // Desactivado temporalmente
  //   if (mainWindow && !mainWindow.isDestroyed()) { // Desactivado temporalmente
  //     mainWindow.webContents.send('update-available', info); // Desactivado temporalmente
  //   } // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // autoUpdater.on('update-not-available', (info) => { // Desactivado temporalmente
  //   logMessage('INFO', 'No hay actualizaciones disponibles', info); // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // autoUpdater.on('error', (err) => { // Desactivado temporalmente
  //   logMessage('ERROR', 'Error en auto-updater', err.message); // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // autoUpdater.on('download-progress', (progressObj) => { // Desactivado temporalmente
  //   if (mainWindow && !mainWindow.isDestroyed()) { // Desactivado temporalmente
  //     mainWindow.webContents.send('update-download-progress', progressObj); // Desactivado temporalmente
  //   } // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // autoUpdater.on('update-downloaded', (info) => { // Desactivado temporalmente
  //   logMessage('INFO', 'Actualización descargada', info); // Desactivado temporalmente
  //   if (mainWindow && !mainWindow.isDestroyed()) { // Desactivado temporalmente
  //     mainWindow.webContents.send('update-downloaded', info); // Desactivado temporalmente
  //   } // Desactivado temporalmente
  // }); // Desactivado temporalmente

  // Verificar actualizaciones al iniciar // Desactivado temporalmente
  // setTimeout(() => { // Desactivado temporalmente
  //   autoUpdater.checkForUpdates(); // Desactivado temporalmente
  // }, 10000); // Desactivado temporalmente
}

// ============================================================================
// IPC HANDLERS
// ============================================================================
ipcMain.handle('get-system-report', () => {
  return {
    backend: {
      isRunning: isBackendRunning,
      pid: backendProcess ? backendProcess.pid : null,
      attempts: backendStartAttempts,
      port: BACKEND_PORT
    },
    system: {
      platform: process.platform,
      arch: process.arch,
      nodeVersion: process.version,
      electronVersion: process.versions.electron
    },
    logs: {
      appLog: APP_LOG_FILE,
      errorLog: ERROR_LOG_FILE
    }
  };
});

ipcMain.handle('restart-backend', () => {
  try {
    logMessage('INFO', 'Reiniciando backend...');
    stopBackend();
    
    setTimeout(() => {
      startBackend();
    }, 1000);
    
    return { success: true, message: 'Backend reiniciándose...' };
  } catch (error) {
    logMessage('ERROR', 'Error reiniciando backend', error.message);
    return { success: false, error: error.message };
  }
});

ipcMain.handle('get-backend-status', () => {
  return {
    success: true,
    status: {
      isRunning: isBackendRunning,
      pid: backendProcess ? backendProcess.pid : null,
      attempts: backendStartAttempts,
      port: BACKEND_PORT
    }
  };
});

ipcMain.handle('test-backend-connection', async () => {
  return await testBackendConnectivity();
});

// ============================================================================
// HANDLERS PARA MENSAJES SEND (NO INVOKE)
// ============================================================================
ipcMain.on('user-register', (event, userData) => {
  logMessage('INFO', 'Registro de usuario recibido', userData);
  // Aquí podrías manejar el registro del usuario
  // Por ahora solo logueamos el evento
});

ipcMain.on('user-login', (event, credentials) => {
  logMessage('INFO', 'Login de usuario recibido', credentials);
  // Aquí podrías manejar el login del usuario
  // Por ahora solo logueamos el evento
});

ipcMain.on('backend-command', (event, command) => {
  logMessage('INFO', 'Comando del backend recibido', command);
  // Aquí podrías manejar comandos específicos del backend
  // Por ahora solo logueamos el evento
});

// ============================================================================
// INICIALIZACIÓN
// ============================================================================
logMessage('INFO', 'Proceso principal iniciado', {
  version: app.getVersion(),
  platform: process.platform,
  arch: process.arch
});