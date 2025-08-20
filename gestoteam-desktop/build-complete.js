const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const VersionManager = require('./version-manager');

console.log('🚀 Iniciando build completo de GestoTeam Desktop...');

// Inicializar gestor de versiones
const versionManager = new VersionManager();

// Función para ejecutar comandos de forma segura
function runCommand(command, description) {
  console.log(`📋 ${description}...`);
  try {
    execSync(command, { stdio: 'inherit', cwd: process.cwd() });
    console.log(`✅ ${description} completado`);
  } catch (error) {
    console.error(`❌ Error en ${description}:`, error.message);
    process.exit(1);
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
  
  // Crear directorio app/backend
  const backendDir = path.join(appDir, 'backend');
  if (!fs.existsSync(backendDir)) {
    fs.mkdirSync(backendDir, { recursive: true });
  }
  
  // Crear directorio app/frontend
  const frontendDir = path.join(appDir, 'frontend');
  if (!fs.existsSync(frontendDir)) {
    fs.mkdirSync(frontendDir, { recursive: true });
  }
  
  console.log('✅ Estructura de directorios creada');
}

// Función para copiar el backend JAR
function copyBackendJar() {
  console.log('📦 Copiando backend JAR...');
  
  const sourceJar = versionManager.getBackendJarPath();
  const targetJar = path.join(__dirname, 'app/backend/gestoteam-backend.jar');
  
  try {
    fs.copyFileSync(sourceJar, targetJar);
    console.log(`✅ Backend JAR copiado: ${path.basename(sourceJar)}`);
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

// Función para crear archivo de configuración de la aplicación
function createAppConfig(versionInfo) {
  console.log('⚙️  Creando configuración de la aplicación...');
  
  const config = {
    version: versionInfo.desktop.version,
    buildDate: new Date().toISOString(),
    buildId: versionInfo.buildId,
    backend: {
      version: versionInfo.backend.version,
      jarPath: './backend/gestoteam-backend.jar',
      port: 8081,
      profile: 'local-client'
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

// Función para limpiar archivos innecesarios
function cleanupUnnecessaryFiles() {
  console.log('🧹 Limpiando archivos innecesarios...');
  
  const filesToRemove = [
    'app/build-icon.js',
    'app/build-local.ps1',
    'app/create-installer.ps1',
    'app/README.md'
  ];
  
  for (const file of filesToRemove) {
    const filePath = path.join(__dirname, file);
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
      console.log(`🗑️  Eliminado: ${file}`);
    }
  }
  
  console.log('✅ Limpieza completada');
}

// Función para crear archivo de metadatos del build
function createBuildMetadata(versionInfo) {
  console.log('📋 Creando metadatos del build...');
  
  const metadata = {
    buildInfo: versionInfo,
    buildScript: 'build-complete.js',
    electronVersion: require('./package.json').devDependencies.electron,
    nodeVersion: process.version,
    platform: process.platform,
    arch: process.arch,
    timestamp: new Date().toISOString()
  };
  
  const metadataPath = path.join(__dirname, 'app/build-metadata.json');
  fs.writeFileSync(metadataPath, JSON.stringify(metadata, null, 2));
  
  console.log('✅ Metadatos del build creados');
}

// Función principal
function main() {
  try {
    console.log('🔍 Iniciando verificación de versiones...');
    
    // Verificar archivos requeridos y obtener información de versiones
    const versionInfo = verifyRequiredFiles();
    
    // Actualizar versión del desktop si es necesario
    versionManager.updateDesktopVersion();
    
    // Preparar estructura
    prepareAppStructure();
    
    // Copiar recursos
    copyBackendJar();
    copyFrontendDist();
    
    // Crear configuración y metadatos
    createAppConfig(versionInfo);
    createBuildMetadata(versionInfo);
    
    // Limpiar archivos innecesarios
    cleanupUnnecessaryFiles();
    
    console.log('\n🎉 Preparación completada exitosamente!');
    console.log(`📦 Versión: ${versionInfo.desktop.version}`);
    console.log(`🔧 Backend: ${versionInfo.backend.version}`);
    console.log(`🌐 Frontend: ${versionInfo.frontend.version}`);
    console.log('\n📋 Ahora puedes ejecutar: npm run build:complete');
    
  } catch (error) {
    console.error('❌ Error durante la preparación:', error.message);
    process.exit(1);
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { main };
