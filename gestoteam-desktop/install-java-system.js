const { execSync, spawn } = require('child_process');
const fs = require('fs');
const path = require('path');
const https = require('https');
const { createWriteStream } = require('fs');

console.log('🚀 Instalador automático de Java para GestoTeam...');

// Configuración de Java a instalar
const JAVA_CONFIG = {
  version: '17.0.12',
  url: 'https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe',
  filename: 'jdk-17_windows-x64_bin.exe',
  installPath: 'C:\\Program Files\\Java\\jdk-17',
  silentInstall: true
};

// Función para verificar si Java ya está instalado
function isJavaInstalled() {
  try {
    const result = execSync('java -version', { 
      stdio: 'pipe',
      timeout: 5000,
      windowsHide: true
    });
    console.log('✅ Java ya está instalado en el sistema:');
    console.log(result.toString());
    return true;
  } catch (error) {
    return false;
  }
}

// Función para descargar archivo
function downloadFile(url, filename) {
  return new Promise((resolve, reject) => {
    console.log(`📥 Descargando Java desde: ${url}`);
    
    const file = createWriteStream(filename);
    const request = https.get(url, (response) => {
      if (response.statusCode === 200) {
        response.pipe(file);
        file.on('finish', () => {
          file.close();
          console.log(`✅ Descarga completada: ${filename}`);
          resolve(filename);
        });
      } else {
        reject(new Error(`Error HTTP: ${response.statusCode}`));
      }
    });
    
    request.on('error', (err) => {
      reject(err);
    });
    
    file.on('error', (err) => {
      reject(err);
    });
  });
}

// Función para instalar Java
function installJava(installerPath) {
  return new Promise((resolve, reject) => {
    console.log('🔧 Instalando Java...');
    
    const installArgs = [
      '/s', // Instalación silenciosa
      '/v', // Variables de instalación
      `INSTALLDIR=${JAVA_CONFIG.installPath}`,
      'ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJarFileRunWith,FeatureJavaHome',
      'AUTO_UPDATE=0', // No actualizaciones automáticas
      'EULA=1' // Aceptar licencia
    ];
    
    const installer = spawn(installerPath, installArgs, {
      stdio: 'pipe',
      windowsHide: true
    });
    
    installer.stdout.on('data', (data) => {
      console.log(`Instalador: ${data.toString().trim()}`);
    });
    
    installer.stderr.on('data', (data) => {
      console.log(`Error instalador: ${data.toString().trim()}`);
    });
    
    installer.on('close', (code) => {
      if (code === 0) {
        console.log('✅ Java instalado correctamente');
        resolve();
      } else {
        reject(new Error(`Instalador terminó con código: ${code}`));
      }
    });
    
    installer.on('error', (error) => {
      reject(error);
    });
  });
}

// Función para configurar variables de entorno
function configureEnvironment() {
  try {
    console.log('⚙️ Configurando variables de entorno...');
    
    // Configurar JAVA_HOME
    const javaHome = JAVA_CONFIG.installPath;
    execSync(`setx JAVA_HOME "${javaHome}"`, { 
      stdio: 'pipe',
      windowsHide: true
    });
    console.log(`✅ JAVA_HOME configurado: ${javaHome}`);
    
    // Agregar Java al PATH
    const javaBin = path.join(javaHome, 'bin');
    const currentPath = process.env.PATH || '';
    
    if (!currentPath.includes(javaBin)) {
      const newPath = `${javaBin};${currentPath}`;
      execSync(`setx PATH "${newPath}"`, { 
        stdio: 'pipe',
        windowsHide: true
      });
      console.log('✅ Java agregado al PATH del sistema');
    } else {
      console.log('✅ Java ya está en el PATH');
    }
    
    return true;
  } catch (error) {
    console.log(`❌ Error configurando variables de entorno: ${error.message}`);
    return false;
  }
}

// Función para verificar instalación
function verifyInstallation() {
  try {
    console.log('🔍 Verificando instalación...');
    
    // Verificar que el ejecutable existe
    const javaExe = path.join(JAVA_CONFIG.installPath, 'bin', 'java.exe');
    if (!fs.existsSync(javaExe)) {
      throw new Error('java.exe no encontrado después de la instalación');
    }
    
    // Verificar que se puede ejecutar
    const result = execSync(`"${javaExe}" -version`, { 
      stdio: 'pipe',
      timeout: 10000,
      windowsHide: true
    });
    
    console.log('✅ Verificación exitosa:');
    console.log(result.toString());
    return true;
  } catch (error) {
    console.log(`❌ Error en verificación: ${error.message}`);
    return false;
  }
}

// Función principal
async function main() {
  try {
    console.log('=====================================');
    console.log('INSTALADOR AUTOMÁTICO DE JAVA');
    console.log('=====================================');
    
    // 1. Verificar si ya está instalado
    if (isJavaInstalled()) {
      console.log('\n🎉 Java ya está instalado y funcionando!');
      return;
    }
    
    // 2. Descargar instalador
    console.log('\n📥 Descargando Java...');
    const installerPath = await downloadFile(JAVA_CONFIG.url, JAVA_CONFIG.filename);
    
    // 3. Instalar Java
    console.log('\n🔧 Instalando Java...');
    await installJava(installerPath);
    
    // 4. Configurar variables de entorno
    console.log('\n⚙️ Configurando sistema...');
    if (!configureEnvironment()) {
      throw new Error('No se pudieron configurar las variables de entorno');
    }
    
    // 5. Verificar instalación
    console.log('\n🔍 Verificando instalación...');
    if (!verifyInstallation()) {
      throw new Error('La instalación no se verificó correctamente');
    }
    
    // 6. Limpiar archivos temporales
    try {
      fs.unlinkSync(installerPath);
      console.log('🧹 Archivos temporales eliminados');
    } catch (error) {
      console.log('⚠️ No se pudieron eliminar archivos temporales');
    }
    
    console.log('\n🎉 Java instalado y configurado correctamente!');
    console.log('\n💡 IMPORTANTE:');
    console.log('   - Reinicia la aplicación para que los cambios surtan efecto');
    console.log('   - Si tienes problemas, reinicia el sistema');
    console.log('   - Java está configurado en: ' + JAVA_CONFIG.installPath);
    
  } catch (error) {
    console.log(`\n❌ Error durante la instalación: ${error.message}`);
    console.log('\n🔧 SOLUCIONES MANUALES:');
    console.log('   1. Descarga Java manualmente desde: https://adoptium.net/');
    console.log('   2. Instala Java 17 o superior');
    console.log('   3. Configura JAVA_HOME y PATH manualmente');
    console.log('   4. Reinicia el sistema');
    
    process.exit(1);
  }
}

// Ejecutar instalador
main();
