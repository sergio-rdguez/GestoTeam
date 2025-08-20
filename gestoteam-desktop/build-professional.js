const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const VersionManager = require('./version-manager');

console.log('🚀 Iniciando build profesional de GestoTeam Desktop...');

// Inicializar gestor de versiones
const versionManager = new VersionManager();

// Función para ejecutar comandos de forma segura
function runCommand(command, description, options = {}) {
  console.log(`📋 ${description}...`);
  try {
    execSync(command, { 
      stdio: 'inherit', 
      cwd: process.cwd(),
      ...options
    });
    console.log(`✅ ${description} completado`);
  } catch (error) {
    console.error(`❌ Error en ${description}:`, error.message);
    if (options.continueOnError) {
      console.log('⚠️  Continuando con el siguiente paso...');
      return false;
    }
    process.exit(1);
  }
  return true;
}

// Función para preparar el build
function prepareBuild() {
  console.log('🔧 Preparando build...');
  
  try {
    // Verificar versiones
    const versionInfo = versionManager.checkVersionCompatibility();
    
    // Actualizar versión del desktop si es necesario
    versionManager.updateDesktopVersion();
    
    // Ejecutar script de preparación
    runCommand('node build-complete.js', 'Preparación de recursos');
    
    return versionInfo;
  } catch (error) {
    console.error('❌ Error preparando build:', error.message);
    process.exit(1);
  }
}

// Función para limpiar builds anteriores
function cleanPreviousBuilds() {
  console.log('🧹 Limpiando builds anteriores...');
  
  const dirsToClean = ['dist-electron', 'dist'];
  
  for (const dir of dirsToClean) {
    if (fs.existsSync(dir)) {
      try {
        fs.rmSync(dir, { recursive: true, force: true });
        console.log(`🗑️  Eliminado: ${dir}`);
      } catch (error) {
        console.warn(`⚠️  No se pudo eliminar ${dir}:`, error.message);
      }
    }
  }
  
  console.log('✅ Limpieza completada');
}

// Función para crear build con electron-builder
function buildWithElectronBuilder(versionInfo) {
  console.log('🔨 Creando build con electron-builder...');
  
  const buildCommand = 'npm run build';
  
  try {
    runCommand(buildCommand, 'Build con electron-builder');
    
    // Verificar que se creó el instalador
    const installerPath = path.join(__dirname, 'dist-electron', `GestoTeam-Setup-${versionInfo.desktop.version}.exe`);
    if (fs.existsSync(installerPath)) {
      const stats = fs.statSync(installerPath);
      const sizeMB = (stats.size / (1024 * 1024)).toFixed(2);
      console.log(`✅ Instalador creado: ${path.basename(installerPath)} (${sizeMB} MB)`);
    } else {
      console.warn('⚠️  Instalador no encontrado, verificando archivos generados...');
      listGeneratedFiles();
    }
    
  } catch (error) {
    console.error('❌ Error en build con electron-builder:', error.message);
    console.log('🔄 Intentando build alternativo con electron-packager...');
    buildWithElectronPackager(versionInfo);
  }
}

// Función para crear build con electron-packager como fallback
function buildWithElectronPackager(versionInfo) {
  console.log('🔨 Creando build con electron-packager (fallback)...');
  
  const packagerCommand = 'npm run build:simple';
  
  try {
    runCommand(packagerCommand, 'Build con electron-packager');
    
    // Verificar que se creó la aplicación
    const appPath = path.join(__dirname, 'dist-electron', 'GestoTeam-win32-x64', 'GestoTeam.exe');
    if (fs.existsSync(appPath)) {
      console.log('✅ Aplicación portable creada con electron-packager');
    }
    
  } catch (error) {
    console.error('❌ Error en build con electron-packager:', error.message);
    process.exit(1);
  }
}

// Función para listar archivos generados
function listGeneratedFiles() {
  console.log('📋 Archivos generados:');
  
  const distDir = path.join(__dirname, 'dist-electron');
  if (fs.existsSync(distDir)) {
    try {
      const files = fs.readdirSync(distDir, { recursive: true });
      files.forEach(file => {
        if (typeof file === 'string') {
          console.log(`   📁 ${file}`);
        }
      });
    } catch (error) {
      console.warn('⚠️  No se pudieron listar los archivos:', error.message);
    }
  }
}

// Función para crear ZIP portable
function createPortableZip(versionInfo) {
  console.log('📦 Creando ZIP portable...');
  
  const sourceDir = path.join(__dirname, 'dist-electron', 'GestoTeam-win32-x64');
  const zipPath = path.join(__dirname, 'dist-electron', `GestoTeam-Desktop-v${versionInfo.desktop.version}-Portable.zip`);
  
  if (!fs.existsSync(sourceDir)) {
    console.warn('⚠️  Directorio de la aplicación no encontrado, saltando creación de ZIP');
    return;
  }
  
  try {
    // Usar PowerShell para crear ZIP
    const zipCommand = `Compress-Archive -Path "${sourceDir}\\*" -DestinationPath "${zipPath}" -Force`;
    runCommand(`powershell -Command "${zipCommand}"`, 'Creación de ZIP portable', { continueOnError: true });
    
    if (fs.existsSync(zipPath)) {
      const stats = fs.statSync(zipPath);
      const sizeMB = (stats.size / (1024 * 1024)).toFixed(2);
      console.log(`✅ ZIP portable creado: ${path.basename(zipPath)} (${sizeMB} MB)`);
    }
  } catch (error) {
    console.warn('⚠️  No se pudo crear ZIP portable:', error.message);
  }
}

// Función para generar reporte del build
function generateBuildReport(versionInfo) {
  console.log('📋 Generando reporte del build...');
  
  const report = {
    buildInfo: {
      timestamp: new Date().toISOString(),
      version: versionInfo.desktop.version,
      buildId: versionInfo.buildId,
      platform: process.platform,
      arch: process.arch,
      nodeVersion: process.version
    },
    versions: {
      backend: versionInfo.backend.version,
      frontend: versionInfo.frontend.version,
      desktop: versionInfo.desktop.version
    },
    generatedFiles: [],
    buildStatus: 'SUCCESS'
  };
  
  // Listar archivos generados
  const distDir = path.join(__dirname, 'dist-electron');
  if (fs.existsSync(distDir)) {
    try {
      const files = fs.readdirSync(distDir, { recursive: true });
      report.generatedFiles = files.filter(f => typeof f === 'string');
    } catch (error) {
      console.warn('⚠️  No se pudieron listar archivos generados:', error.message);
    }
  }
  
  // Guardar reporte
  const reportPath = path.join(__dirname, 'dist-electron', 'build-report.json');
  fs.writeFileSync(reportPath, JSON.stringify(report, null, 2));
  
  console.log('✅ Reporte del build generado');
  return report;
}

// Función principal
function main() {
  try {
    console.log('🔍 Iniciando proceso de build profesional...');
    
    // Limpiar builds anteriores
    cleanPreviousBuilds();
    
    // Preparar build
    const versionInfo = prepareBuild();
    
    console.log(`\n📦 Iniciando build para versión: ${versionInfo.desktop.version}`);
    console.log(`🔧 Backend: ${versionInfo.backend.version}`);
    console.log(`🌐 Frontend: ${versionInfo.frontend.version}`);
    
    // Crear build
    buildWithElectronBuilder(versionInfo);
    
    // Crear ZIP portable
    createPortableZip(versionInfo);
    
    // Generar reporte
    const report = generateBuildReport(versionInfo);
    
    console.log('\n🎉 Build profesional completado exitosamente!');
    console.log(`📁 Archivos generados en: dist-electron/`);
    console.log(`📋 Reporte del build: dist-electron/build-report.json`);
    
    // Mostrar resumen
    if (report.generatedFiles.length > 0) {
      console.log('\n📋 Archivos generados:');
      report.generatedFiles.forEach(file => {
        console.log(`   📁 ${file}`);
      });
    }
    
  } catch (error) {
    console.error('❌ Error durante el build profesional:', error.message);
    process.exit(1);
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { main };
