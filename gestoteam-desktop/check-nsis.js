const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

console.log('🔍 Diagnóstico de NSIS para GestoTeam Desktop');
console.log('=============================================\n');

// Función para ejecutar comandos de forma segura
function runCommand(command, description) {
  try {
    const result = execSync(command, { stdio: 'pipe', encoding: 'utf8' });
    return { success: true, output: result.trim() };
  } catch (error) {
    return { success: false, error: error.message };
  }
}

// Función para verificar paths específicos
function checkSpecificPaths() {
  console.log('📁 Verificando ubicaciones de instalación...');
  
  const possiblePaths = [
    'C:\\Program Files (x86)\\NSIS\\makensis.exe',
    'C:\\Program Files\\NSIS\\makensis.exe',
    'C:\\NSIS\\makensis.exe'
  ];
  
  let foundPaths = [];
  
  possiblePaths.forEach(nsisPath => {
    try {
      if (fs.existsSync(nsisPath)) {
        foundPaths.push(nsisPath);
        console.log(`✅ ${nsisPath}`);
      } else {
        console.log(`❌ ${nsisPath}`);
      }
    } catch (error) {
      console.log(`❌ ${nsisPath} (error: ${error.message})`);
    }
  });
  
  return foundPaths;
}

// Función para verificar PATH del sistema
function checkSystemPATH() {
  console.log('\n🔍 Verificando PATH del sistema...');
  
  // Verificar con 'where'
  const whereResult = runCommand('where makensis.exe', 'Búsqueda con where');
  if (whereResult.success) {
    console.log('✅ makensis.exe encontrado en PATH:');
    whereResult.output.split('\n').forEach(path => {
      console.log(`   ${path}`);
    });
    return true;
  } else {
    console.log('❌ makensis.exe no encontrado en PATH');
    return false;
  }
}

// Función para verificar versión de NSIS
function checkNSISVersion() {
  console.log('\n🔍 Verificando versión de NSIS...');
  
  const versionResult = runCommand('makensis.exe /VERSION', 'Verificación de versión');
  if (versionResult.success) {
    console.log(`✅ NSIS disponible: ${versionResult.output}`);
    return true;
  } else {
    console.log('❌ No se pudo verificar la versión de NSIS');
    console.log(`   Error: ${versionResult.error}`);
    return false;
  }
}

// Función para verificar permisos
function checkPermissions() {
  console.log('\n🔐 Verificando permisos...');
  
  const testPaths = [
    'C:\\Program Files (x86)\\NSIS\\',
    'C:\\Program Files\\NSIS\\'
  ];
  
  testPaths.forEach(testPath => {
    try {
      if (fs.existsSync(testPath)) {
        // Intentar leer el directorio
        fs.readdirSync(testPath);
        console.log(`✅ ${testPath} - Acceso de lectura OK`);
      }
    } catch (error) {
      console.log(`❌ ${testPath} - Error de acceso: ${error.message}`);
    }
  });
}

// Función para mostrar recomendaciones
function showRecommendations(hasNSIS) {
  console.log('\n💡 RECOMENDACIONES');
  console.log('==================');
  
  if (hasNSIS) {
    console.log('✅ NSIS está disponible en el sistema');
    console.log('🚀 Puedes ejecutar: npm run compile-installer');
  } else {
    console.log('❌ NSIS no está disponible');
    console.log('');
    console.log('🔧 Para instalar NSIS:');
    console.log('   1. Descarga desde: https://nsis.sourceforge.io/Download');
    console.log('   2. Ejecuta el instalador como administrador');
    console.log('   3. Reinicia el sistema');
    console.log('   4. Ejecuta este script nuevamente');
    console.log('');
    console.log('💡 Alternativas:');
    console.log('   • Usar solo: npm run build (crea script NSIS)');
    console.log('   • Instalar NSIS en una ubicación personalizada');
    console.log('   • Agregar NSIS al PATH del sistema');
  }
}

// Función principal
function main() {
  try {
    // Verificar paths específicos
    const foundPaths = checkSpecificPaths();
    
    // Verificar PATH del sistema
    const inPATH = checkSystemPATH();
    
    // Verificar versión
    const hasVersion = checkNSISVersion();
    
    // Verificar permisos
    checkPermissions();
    
    // Resumen
    console.log('\n📊 RESUMEN DEL DIAGNÓSTICO');
    console.log('============================');
    console.log(`📁 NSIS instalado: ${foundPaths.length > 0 ? '✅ Sí' : '❌ No'}`);
    console.log(`🔍 En PATH: ${inPATH ? '✅ Sí' : '❌ No'}`);
    console.log(`📋 Versión disponible: ${hasVersion ? '✅ Sí' : '❌ No'}`);
    
    if (foundPaths.length > 0) {
      console.log(`📍 Ubicaciones encontradas: ${foundPaths.length}`);
      foundPaths.forEach(path => {
        console.log(`   • ${path}`);
      });
    }
    
    // Mostrar recomendaciones
    const hasNSIS = foundPaths.length > 0 || inPATH || hasVersion;
    showRecommendations(hasNSIS);
    
    // Estado final
    if (hasNSIS) {
      console.log('\n🎉 NSIS está listo para usar con GestoTeam Desktop!');
    } else {
      console.log('\n⚠️ NSIS no está disponible. Instala NSIS para crear instaladores.');
    }
    
  } catch (error) {
    console.error('\n❌ Error durante el diagnóstico:', error.message);
    process.exit(1);
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { main };
