const fs = require('fs');
const path = require('path');

// Función para verificar que el directorio build tenga los archivos necesarios
function verifyBuildDirectory() {
  const buildDir = path.join(__dirname, 'build');
  const installerNshPath = path.join(buildDir, 'installer.nsh');
  
  console.log('🔍 Verificando directorio build...');
  
  if (!fs.existsSync(buildDir)) {
    console.log('⚠️  Directorio build no existe, creando...');
    fs.mkdirSync(buildDir, { recursive: true });
  }
  
  // Verificar si existe installer.nsh
  if (!fs.existsSync(installerNshPath)) {
    console.log('⚠️  installer.nsh no encontrado, creando básico...');
    const basicNsh = `!macro customInstall
  ; Aquí puedes agregar instalaciones personalizadas si es necesario
!macroend`;
    fs.writeFileSync(installerNshPath, basicNsh);
    console.log('✅ installer.nsh básico creado');
  } else {
    console.log('✅ installer.nsh encontrado');
  }
  
  console.log('ℹ️  Usando icono por defecto de Electron (sin icono personalizado)');
}

// Función principal
function main() {
  console.log('🚀 Preparando build de GestoTeam Desktop...');
  
  try {
    // Solo verificar directorio build
    verifyBuildDirectory();
    
    console.log('✅ Preparación completada');
    console.log('📦 Continuando con el build usando icono por defecto...');
    
  } catch (error) {
    console.error('❌ Error durante la preparación:', error.message);
    console.log('⚠️  Continuando con configuración por defecto...');
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { verifyBuildDirectory, main };
