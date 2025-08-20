const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

class VersionManager {
  constructor() {
    this.projectRoot = path.join(__dirname, '..');
    this.backendPath = path.join(this.projectRoot, 'gestoteam-backend');
    this.frontendPath = path.join(this.projectRoot, 'gestoteam-frontend');
    this.desktopPath = __dirname;
  }

  // Obtener versión del backend desde build.gradle
  getBackendVersion() {
    try {
      const buildGradlePath = path.join(this.backendPath, 'build.gradle');
      if (!fs.existsSync(buildGradlePath)) {
        throw new Error('build.gradle no encontrado');
      }

      const content = fs.readFileSync(buildGradlePath, 'utf8');
      const versionMatch = content.match(/version\s*=\s*['"]([^'"]+)['"]/);
      
      if (!versionMatch) {
        throw new Error('No se pudo extraer la versión del backend');
      }

      return versionMatch[1];
    } catch (error) {
      console.warn('⚠️  No se pudo obtener versión del backend:', error.message);
      return '0.0.1'; // Fallback
    }
  }

  // Obtener versión del frontend desde package.json
  getFrontendVersion() {
    try {
      const packageJsonPath = path.join(this.frontendPath, 'package.json');
      if (!fs.existsSync(packageJsonPath)) {
        throw new Error('package.json del frontend no encontrado');
      }

      const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
      return packageJson.version;
    } catch (error) {
      console.warn('⚠️  No se pudo obtener versión del frontend:', error.message);
      return '0.0.1'; // Fallback
    }
  }

  // Obtener versión del desktop desde package.json
  getDesktopVersion() {
    try {
      const packageJsonPath = path.join(this.desktopPath, 'package.json');
      const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
      return packageJson.version;
    } catch (error) {
      console.warn('⚠️  No se pudo obtener versión del desktop:', error.message);
      return '0.0.1'; // Fallback
    }
  }

  // Obtener nombre del JAR del backend
  getBackendJarName() {
    const version = this.getBackendVersion();
    return `gestoteam-backend-${version}.jar`;
  }

  // Obtener ruta completa del JAR del backend
  getBackendJarPath() {
    const jarName = this.getBackendJarName();
    return path.join(this.backendPath, 'build', 'libs', jarName);
  }

  // Verificar que el JAR existe
  verifyBackendJar() {
    const jarPath = this.getBackendJarPath();
    if (!fs.existsSync(jarPath)) {
      throw new Error(`JAR del backend no encontrado: ${jarPath}`);
    }
    return jarPath;
  }

  // Verificar que el frontend dist existe
  verifyFrontendDist() {
    const distPath = path.join(this.frontendPath, 'dist');
    if (!fs.existsSync(distPath)) {
      throw new Error(`Frontend dist no encontrado: ${distPath}`);
    }
    return distPath;
  }

  // Generar información de versiones
  generateVersionInfo() {
    const info = {
      backend: {
        version: this.getBackendVersion(),
        jarName: this.getBackendJarName(),
        jarPath: this.getBackendJarPath()
      },
      frontend: {
        version: this.getFrontendVersion(),
        distPath: path.join(this.frontendPath, 'dist')
      },
      desktop: {
        version: this.getDesktopVersion()
      },
      timestamp: new Date().toISOString(),
      buildId: `build-${Date.now()}`
    };

    return info;
  }

  // Verificar compatibilidad de versiones
  checkVersionCompatibility() {
    const info = this.generateVersionInfo();
    const backendVersion = info.backend.version;
    const frontendVersion = info.frontend.version;
    const desktopVersion = info.desktop.version;

    console.log('🔍 Verificando compatibilidad de versiones...');
    console.log(`   Backend: ${backendVersion}`);
    console.log(`   Frontend: ${frontendVersion}`);
    console.log(`   Desktop: ${desktopVersion}`);

    // Verificar que las versiones principales coincidan
    const backendMajor = backendVersion.split('.')[0];
    const frontendMajor = frontendVersion.split('.')[0];
    const desktopMajor = desktopVersion.split('.')[0];

    if (backendMajor !== frontendMajor || backendMajor !== desktopMajor) {
      console.warn('⚠️  Advertencia: Las versiones principales no coinciden');
    }

    return info;
  }

  // Actualizar versión del desktop si es necesario
  updateDesktopVersion() {
    const backendVersion = this.getBackendVersion();
    const desktopVersion = this.getDesktopVersion();

    if (backendVersion !== desktopVersion) {
      console.log(`🔄 Actualizando versión del desktop de ${desktopVersion} a ${backendVersion}`);
      
      try {
        const packageJsonPath = path.join(this.desktopPath, 'package.json');
        const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
        
        packageJson.version = backendVersion;
        fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2));
        
        console.log('✅ Versión del desktop actualizada');
        return true;
      } catch (error) {
        console.error('❌ Error actualizando versión del desktop:', error.message);
        return false;
      }
    }

    return false;
  }
}

// Exportar para uso en otros módulos
module.exports = VersionManager;

// Ejecutar si se llama directamente
if (require.main === module) {
  const versionManager = new VersionManager();
  
  try {
    console.log('🚀 Gestor de versiones de GestoTeam');
    console.log('=====================================');
    
    const info = versionManager.checkVersionCompatibility();
    versionManager.updateDesktopVersion();
    
    console.log('\n📋 Información de versiones:');
    console.log(JSON.stringify(info, null, 2));
    
  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}
