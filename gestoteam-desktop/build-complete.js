const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const VersionManager = require('./version-manager');

console.log('🚀 Iniciando build completo de GestoTeam Desktop...');

// Inicializar gestor de versiones
const versionManager = new VersionManager();

// Función para ejecutar comandos de forma segura
function runCommand(command, description, cwd = process.cwd()) {
  console.log(`📋 ${description}...`);
  try {
    execSync(command, { stdio: 'inherit', cwd: cwd });
    console.log(`✅ ${description} completado`);
  } catch (error) {
    console.error(`❌ Error en ${description}:`, error.message);
    process.exit(1);
  }
}

// Función para recompilar el backend
function rebuildBackend() {
  console.log('🔧 Recompilando backend...');
  
  const backendPath = path.join(__dirname, '..', 'gestoteam-backend');
  
  try {
    // Verificar que existe el directorio del backend
    if (!fs.existsSync(backendPath)) {
      throw new Error(`Directorio del backend no encontrado: ${backendPath}`);
    }
    
    // Limpiar build anterior
    console.log('🧹 Limpiando build anterior del backend...');
    if (fs.existsSync(path.join(backendPath, 'build'))) {
      fs.rmSync(path.join(backendPath, 'build'), { recursive: true, force: true });
    }
    
    // Recompilar con Gradle wrapper
    console.log('🏗️  Compilando backend con Gradle wrapper...');
    const gradleCommand = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
    runCommand(`${gradleCommand} clean build -x test`, 'Compilación del backend', backendPath);
    
    // Verificar que se generó el JAR
    const jarPath = versionManager.verifyBackendJar();
    console.log(`✅ Backend recompilado: ${path.basename(jarPath)}`);
    
  } catch (error) {
    console.error('❌ Error recompilando backend:', error.message);
    process.exit(1);
  }
}

// Función para recompilar el frontend
function rebuildFrontend() {
  console.log('🌐 Recompilando frontend...');
  
  const frontendPath = path.join(__dirname, '..', 'gestoteam-frontend');
  
  try {
    // Verificar que existe el directorio del frontend
    if (!fs.existsSync(frontendPath)) {
      throw new Error(`Directorio del frontend no encontrado: ${frontendPath}`);
    }
    
    // Limpiar build anterior
    console.log('🧹 Limpiando build anterior del frontend...');
    if (fs.existsSync(path.join(frontendPath, 'dist'))) {
      fs.rmSync(path.join(frontendPath, 'dist'), { recursive: true, force: true });
    }
    
    // Instalar dependencias si es necesario
    if (!fs.existsSync(path.join(frontendPath, 'node_modules'))) {
      console.log('📦 Instalando dependencias del frontend...');
      runCommand('npm install', 'Instalación de dependencias', frontendPath);
    }
    
    // Recompilar con npm
    console.log('🏗️  Compilando frontend con npm...');
    runCommand('npm run build', 'Compilación del frontend', frontendPath);
    
    // Verificar que se generó el dist
    const distPath = versionManager.verifyFrontendDist();
    console.log(`✅ Frontend recompilado: ${distPath}`);
    
  } catch (error) {
    console.error('❌ Error recompilando frontend:', error.message);
    process.exit(1);
  }
}

// Función para verificar Java del sistema
function checkSystemJava() {
  console.log('☕ Verificando Java del sistema...');
  
  try {
    const { execSync } = require('child_process');
    execSync('java -version', { stdio: 'pipe' });
    console.log('✅ Java del sistema encontrado');
    return true;
  } catch (error) {
    console.log('⚠️ Java del sistema no encontrado');
    console.log('💡 La aplicación requiere Java 17+ para funcionar');
    console.log('📥 Descargar desde: https://adoptium.net/');
    return false;
  }
}

// Función para verificar que existen los archivos necesarios
function verifyRequiredFiles() {
  console.log('🔍 Verificando archivos requeridos...');
  
  try {
    // Verificar versiones y compatibilidad
    const versionInfo = versionManager.checkVersionCompatibility();
    
    // Verificar que el JAR existe
    const jarPath = versionManager.verifyBackendJar();
    console.log(`✅ Backend JAR encontrado: ${path.basename(jarPath)}`);
    
    // Verificar que el frontend dist existe
    const distPath = versionManager.verifyFrontendDist();
    console.log(`✅ Frontend dist encontrado: ${distPath}`);
    
    return versionInfo;
  } catch (error) {
    console.error(`❌ Error verificando archivos: ${error.message}`);
    process.exit(1);
  }
}

// Función para preparar la estructura de la aplicación
function prepareAppStructure() {
  console.log('📁 Preparando estructura de la aplicación...');
  
  // Crear directorio app si no existe
  const appDir = path.join(__dirname, 'app');
  if (!fs.existsSync(appDir)) {
    fs.mkdirSync(appDir, { recursive: true });
  }
  
  console.log('✅ Estructura de directorios creada');
}

// Función para copiar el backend JAR
function copyBackendJar() {
  console.log('📦 Copiando backend JAR...');
  
  const sourceJar = versionManager.getBackendJarPath();
  const targetJar = path.join(__dirname, 'app/gestoteam.jar');
  
  try {
    fs.copyFileSync(sourceJar, targetJar);
    console.log(`✅ Backend JAR copiado: ${path.basename(sourceJar)} -> ${path.basename(targetJar)}`);
  } catch (error) {
    console.error('❌ Error copiando backend JAR:', error.message);
    process.exit(1);
  }
}

// Función para copiar el frontend compilado
function copyFrontendDist() {
  console.log('📦 Copiando frontend compilado...');
  
  const sourceDir = versionManager.verifyFrontendDist();
  const targetDir = path.join(__dirname, 'app/frontend');
  
  try {
    // Eliminar directorio destino si existe
    if (fs.existsSync(targetDir)) {
      fs.rmSync(targetDir, { recursive: true, force: true });
    }
    
    // Copiar todo el contenido del dist
    fs.cpSync(sourceDir, targetDir, { recursive: true });
    console.log('✅ Frontend compilado copiado correctamente');
  } catch (error) {
    console.error('❌ Error copiando frontend:', error.message);
    process.exit(1);
  }
}

// Función para copiar archivos de la aplicación de forma inteligente
function copyAppFiles() {
  console.log('📦 Copiando archivos de la aplicación...');
  
  const appDir = path.join(__dirname, 'app');
  const outputDir = path.join(__dirname, 'dist-electron');
  
  try {
    // Verificar qué archivos existen realmente
    const appFiles = fs.readdirSync(appDir);
    console.log('📋 Archivos encontrados en app/:', appFiles);
    
    // Copiar solo los archivos que existen
    appFiles.forEach(file => {
      const sourcePath = path.join(appDir, file);
      const targetPath = path.join(outputDir, file);
      
      if (fs.statSync(sourcePath).isDirectory()) {
        // Es un directorio, copiarlo completo
        if (fs.existsSync(targetPath)) {
          fs.rmSync(targetPath, { recursive: true, force: true });
        }
        fs.cpSync(sourcePath, targetPath, { recursive: true });
        console.log(`📁 Directorio copiado: ${file}/`);
      } else {
        // Es un archivo, copiarlo
        fs.copyFileSync(sourcePath, targetPath);
        console.log(`📄 Archivo copiado: ${file}`);
      }
    });
    
    console.log('✅ Archivos de la aplicación copiados correctamente');
    
  } catch (error) {
    console.error('❌ Error copiando archivos de la aplicación:', error.message);
    process.exit(1);
  }
}

// Función para crear archivo de configuración de la aplicación
function createAppConfig(versionInfo) {
  console.log('⚙️  Creando configuración de la aplicación...');
  
  const config = {
    version: versionInfo.desktop.version,
    buildDate: new Date().toISOString(),
    buildId: versionInfo.buildId,
    backend: {
      version: versionInfo.backend.version,
      jarPath: './gestoteam.jar',
      port: 8081,
      profile: 'desktop'
    },
    frontend: {
      version: versionInfo.frontend.version,
      path: './frontend',
      port: 8080
    },
    compatibility: {
      backendVersion: versionInfo.backend.version,
      frontendVersion: versionInfo.frontend.version,
      desktopVersion: versionInfo.desktop.version
    }
  };
  
  const configPath = path.join(__dirname, 'app/app-config.json');
  fs.writeFileSync(configPath, JSON.stringify(config, null, 2));
  
  console.log('✅ Configuración de la aplicación creada');
}

// Función para crear instalador NSIS personalizado
function createCustomInstaller(versionInfo) {
  console.log('📦 Creando instalador NSIS personalizado...');
  
  const installerDir = path.join(__dirname, 'installer');
  const outputDir = path.join(__dirname, 'dist-electron');
  const appDir = path.join(__dirname, 'app');
  
  try {
    // Verificar que existe el directorio app
    if (!fs.existsSync(appDir)) {
      throw new Error('Directorio app no encontrado. Ejecute build-complete.js primero.');
    }
    
    // Crear directorio de salida si no existe
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }
    
    // Copiar archivos del instalador al directorio de salida
    const installerFiles = [
      'main-installer.nsh',
      'system-requirements.nsh',
      'check-java.nsh',
      'LICENSE.txt'
    ];
    
    installerFiles.forEach(file => {
      const sourcePath = path.join(installerDir, file);
      const targetPath = path.join(outputDir, file);
      fs.copyFileSync(sourcePath, targetPath);
      console.log(`📋 Copiado: ${file}`);
    });
    
    console.log('📋 Archivos del instalador copiados');
    
    // Ahora usar electron-builder para crear la aplicación real
    console.log('🔨 Compilando aplicación Electron con electron-builder...');
    
    try {
      // Ejecutar electron-builder
      runCommand('npx electron-builder --win --x64', 'Compilación Electron', __dirname);
      console.log('✅ Aplicación Electron compilada exitosamente');
      
      // Verificar que se creó el ejecutable
      const exeFiles = fs.readdirSync(outputDir).filter(file => 
        file.endsWith('.exe') && file.includes('GestoTeam Desktop')
      );
      
      if (exeFiles.length > 0) {
        console.log('✅ Ejecutable encontrado:', exeFiles[0]);
      } else {
        console.log('⚠️ No se encontró el ejecutable después de la compilación');
      }
      
    } catch (error) {
      console.log('⚠️ Error durante la compilación Electron, pero los archivos están listos');
      console.log('💡 Puedes compilar manualmente con: npx electron-builder --win --x64');
    }
    
  } catch (error) {
    console.error('❌ Error creando instalador NSIS:', error.message);
    console.log('⚠️ Continuando sin instalador...');
  }
}

// Función para encontrar NSIS de forma robusta
function findNSIS() {
  const possiblePaths = [
    'C:\\Program Files (x86)\\NSIS\\makensis.exe',
    'C:\\Program Files\\NSIS\\makensis.exe',
    'C:\\NSIS\\makensis.exe',
    'makensis.exe' // Solo si está en PATH
  ];
  
  // Verificar cada path
  for (const nsisPath of possiblePaths) {
    try {
      if (nsisPath === 'makensis.exe') {
        // Verificar si está en PATH
        const { execSync } = require('child_process');
        execSync('makensis.exe /VERSION', { stdio: 'pipe' });
        return { found: true, path: nsisPath, type: 'PATH' };
      } else if (fs.existsSync(nsisPath)) {
        return { found: true, path: nsisPath, type: 'INSTALLED' };
      }
    } catch (error) {
      // Continuar con el siguiente path
    }
  }
  
  // Verificar en PATH de forma más robusta
  try {
    const { execSync } = require('child_process');
    const result = execSync('where makensis.exe', { stdio: 'pipe', encoding: 'utf8' });
    if (result.trim()) {
      return { found: true, path: 'makensis.exe', type: 'PATH' };
    }
  } catch (error) {
    // makensis.exe no está en PATH
  }
  
  return { found: false, path: null, type: null };
}

// Función para crear script NSIS
function createNSISScript(versionInfo) {
  return `!include "MUI2.nsh"

# Configuración básica del instalador
Name "GestoTeam Desktop"
OutFile "GestoTeam Desktop Setup ${versionInfo.desktop.version}.exe"
InstallDir "$LOCALAPPDATA\\Programs\\GestoTeam Desktop"

# Instalación per-user (no requiere permisos de administrador)
RequestExecutionLevel user

# Páginas del instalador
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "LICENSE.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

# Páginas de desinstalación
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Idiomas
!insertmacro MUI_LANGUAGE "Spanish"

# Sección de instalación
Section "GestoTeam Desktop" SecMain
  SetOutPath "$INSTDIR"
  
  DetailPrint "🚀 Iniciando instalación de GestoTeam Desktop..."
  
  # Crear directorios
  CreateDirectory "$SMPROGRAMS\\GestoTeam Desktop"
  CreateDirectory "$INSTDIR\\logs"
  CreateDirectory "$INSTDIR\\data"
  CreateDirectory "$INSTDIR\\temp"
  CreateDirectory "$INSTDIR\\uploads"
  
  # Copiar archivos de la aplicación (desde el directorio actual)
  DetailPrint "📦 Copiando archivos de la aplicación..."
  
  # Copiar archivos principales
  File "gestoteam.jar"
  File "app-config.json"
  
  # Copiar directorio frontend
  SetOutPath "$INSTDIR\\frontend"
  File /r "frontend\\*.*"
  
  # Volver al directorio principal
  SetOutPath "$INSTDIR"
  
  # Copiar directorio java-runtime si existe
  File /r "java-runtime\\*.*"
  
  # Crear accesos directos básicos
  DetailPrint "🔗 Creando accesos directos..."
  CreateShortCut "$SMPROGRAMS\\GestoTeam Desktop\\GestoTeam Desktop.lnk" "$INSTDIR\\gestoteam.jar"
  CreateShortCut "$DESKTOP\\GestoTeam Desktop.lnk" "$INSTDIR\\gestoteam.jar"
  
  # Registrar desinstalador
  WriteUninstaller "$INSTDIR\\Uninstall.exe"
  WriteRegStr HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "DisplayName" "GestoTeam Desktop"
  WriteRegStr HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "UninstallString" '"$INSTDIR\\Uninstall.exe"'
  WriteRegStr HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "DisplayIcon" "$INSTDIR\\gestoteam.jar"
  WriteRegStr HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "Publisher" "GestoTeam"
  WriteRegStr HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "DisplayVersion" "${versionInfo.desktop.version}"
  WriteRegDWORD HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "NoModify" 1
  WriteRegDWORD HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop" "NoRepair" 1
  
  DetailPrint "✅ Instalación completada exitosamente"
SectionEnd

# Sección de desinstalación
Section "Uninstall"
  DetailPrint "🗑️ Desinstalando GestoTeam Desktop..."
  
  # Eliminar archivos y directorios
  RMDir /r "$INSTDIR\\logs"
  RMDir /r "$INSTDIR\\data"
  RMDir /r "$INSTDIR\\temp"
  RMDir /r "$INSTDIR\\uploads"
  RMDir /r "$INSTDIR\\frontend"
  RMDir /r "$INSTDIR\\java-runtime"
  Delete "$INSTDIR\\gestoteam.jar"
  Delete "$INSTDIR\\app-config.json"
  Delete "$INSTDIR\\Uninstall.exe"
  RMDir "$INSTDIR"
  
  # Eliminar accesos directos
  Delete "$SMPROGRAMS\\GestoTeam Desktop\\GestoTeam Desktop.lnk"
  RMDir "$SMPROGRAMS\\GestoTeam Desktop"
  Delete "$DESKTOP\\GestoTeam Desktop.lnk"
  
  # Eliminar registro
  DeleteRegKey HKCU "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\GestoTeam Desktop"
  DeleteRegKey HKCU "Software\\GestoTeam Desktop"
  
  DetailPrint "✅ Desinstalación completada"
SectionEnd

# Función de inicialización
Function .onInit
  DetailPrint "🚀 Iniciando instalador de GestoTeam Desktop..."
FunctionEnd

# Función de finalización
Function .onInstSuccess
  DetailPrint "🎉 Instalación completada exitosamente"
FunctionEnd`;
}

// Función para preparar Java embebido
function prepareEmbeddedJava() {
  console.log('☕ Preparando Java embebido...');
  
  const javaSourceDir = path.join(__dirname, 'java-runtime');
  const javaTargetDir = path.join(__dirname, 'app', 'java-runtime');
  
  try {
    // Verificar si ya existe Java embebido
    if (fs.existsSync(javaTargetDir)) {
      console.log('✅ Java embebido ya existe');
      return;
    }
    
    // Crear directorio de destino
    if (!fs.existsSync(path.dirname(javaTargetDir))) {
      fs.mkdirSync(path.dirname(javaTargetDir), { recursive: true });
    }
    
    // Si no hay Java embebido, mostrar instrucciones
    if (!fs.existsSync(javaSourceDir)) {
      console.log('⚠️ No se encontró Java embebido en java-runtime/');
      console.log('💡 Para incluir Java embebido:');
      console.log('   1. Descarga Java 17 desde: https://adoptium.net/');
      console.log('   2. Crea la carpeta: java-runtime/');
      console.log('   3. Copia el contenido de la instalación de Java');
      console.log('   4. Ejecuta build-complete.js de nuevo');
      console.log('');
      console.log('📋 Estructura esperada:');
      console.log('   java-runtime/');
      console.log('   ├── bin/');
      console.log('   │   ├── java.exe');
      console.log('   │   └── ...');
      console.log('   ├── lib/');
      console.log('   └── ...');
      return;
    }
    
    // Copiar Java embebido
    console.log('📁 Copiando Java embebido...');
    copyDirectoryRecursive(javaSourceDir, javaTargetDir);
    console.log('✅ Java embebido preparado');
    
  } catch (error) {
    console.error('❌ Error preparando Java embebido:', error.message);
    console.log('⚠️ Continuando sin Java embebido...');
  }
}

// Función para copiar directorios recursivamente
function copyDirectoryRecursive(source, target) {
  if (!fs.existsSync(target)) {
    fs.mkdirSync(target, { recursive: true });
  }
  
  const items = fs.readdirSync(source);
  
  for (const item of items) {
    const sourcePath = path.join(source, item);
    const targetPath = path.join(target, item);
    
    if (fs.statSync(sourcePath).isDirectory()) {
      copyDirectoryRecursive(sourcePath, targetPath);
    } else {
      fs.copyFileSync(sourcePath, targetPath);
    }
  }
}

// Función para limpiar directorios de build anteriores
function cleanPreviousBuilds() {
  console.log('🧹 Limpiando builds anteriores...');
  
  const directoriesToClean = ['app', 'dist-electron'];
  
  for (const dir of directoriesToClean) {
    const dirPath = path.join(__dirname, dir);
    if (fs.existsSync(dirPath)) {
      try {
        fs.rmSync(dirPath, { recursive: true, force: true });
        console.log(`✅ Directorio ${dir} eliminado`);
      } catch (error) {
        console.log(`⚠️ No se pudo eliminar ${dir}: ${error.message}`);
      }
    } else {
      console.log(`ℹ️ Directorio ${dir} no existe, continuando...`);
    }
  }
}

// Función principal
function main() {
  try {
    console.log('🔍 Iniciando build completo...');
    
    // Limpiar builds anteriores
    cleanPreviousBuilds();
    
    // Verificar Java del sistema
    checkSystemJava();
    
    // Recompilar backend y frontend
    rebuildBackend();
    rebuildFrontend();

    // Verificar archivos requeridos y obtener información de versiones
    const versionInfo = verifyRequiredFiles();
    
    // Actualizar versión del desktop si es necesario
    versionManager.updateDesktopVersion();
    
    // Preparar estructura
    prepareAppStructure();
    
    // Preparar Java embebido
    prepareEmbeddedJava();
    
    // Copiar recursos
    copyBackendJar();
    copyFrontendDist();
    
    // Crear configuración
    createAppConfig(versionInfo);
    
    // Crear instalador con electron-builder
    createCustomInstaller(versionInfo);
    
    console.log('\n🎉 Build completo completado exitosamente!');
    console.log(`📦 Versión: ${versionInfo.desktop.version}`);
    console.log(`🔧 Backend: ${versionInfo.backend.version}`);
    console.log(`🌐 Frontend: ${versionInfo.frontend.version}`);
    console.log('\n📋 Archivos generados en:');
    console.log(`   - Aplicación: app/`);
    console.log(`   - Distribución: dist-electron/`);
    console.log('\n🚀 La aplicación está lista para usar!');
    
  } catch (error) {
    console.error('❌ Error durante el build completo:', error.message);
    process.exit(1);
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { main };

