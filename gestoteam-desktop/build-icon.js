const fs = require('fs');
const path = require('path');

// Función para verificar y preparar el icono
function prepareIcon() {
  const buildDir = path.join(__dirname, 'build');
  const iconPath = path.join(buildDir, 'icon.ico');
  
  console.log('🔍 Verificando configuración del icono...');
  console.log('📁 Directorio build:', buildDir);
  console.log('🎨 Ruta del icono:', iconPath);
  
  // Verificar si existe el directorio build
  if (!fs.existsSync(buildDir)) {
    console.log('⚠️  Directorio build no encontrado, creando...');
    fs.mkdirSync(buildDir, { recursive: true });
  }
  
  // Verificar si existe el icono
  if (fs.existsSync(iconPath)) {
    const stats = fs.statSync(iconPath);
    console.log('✅ Icono encontrado:', iconPath);
    console.log('📏 Tamaño:', (stats.size / 1024).toFixed(2), 'KB');
    
    // Verificar que el archivo no esté corrupto
    if (stats.size > 1000) { // Más de 1KB
      console.log('✅ Icono válido, usando icono personalizado');
      return true;
    } else {
      console.log('⚠️  Icono muy pequeño, puede estar corrupto');
      // Eliminar icono corrupto
      fs.unlinkSync(iconPath);
      console.log('🗑️  Icono corrupto eliminado');
    }
  } else {
    console.log('❌ Icono no encontrado');
  }
  
  console.log('ℹ️  Usando icono por defecto de Electron');
  return false;
}

// Función para crear un icono simple si es necesario
function createSimpleIcon() {
  const buildDir = path.join(__dirname, 'build');
  const iconPath = path.join(buildDir, 'icon.ico');
  
  // Solo crear si no existe
  if (!fs.existsSync(iconPath)) {
    console.log('🔄 Creando icono placeholder...');
    
    try {
      // Crear un archivo de icono mínimo (esto es solo un marcador)
      // En la práctica, electron-builder usará el icono por defecto
      fs.writeFileSync(iconPath, '');
      console.log('✅ Icono placeholder creado');
    } catch (error) {
      console.log('⚠️  No se pudo crear icono placeholder:', error.message);
    }
  }
}

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
}

// Función principal
function main() {
  console.log('🚀 Preparando build de GestoTeam Desktop...');
  
  try {
    // Verificar directorio build
    verifyBuildDirectory();
    
    // Preparar icono
    const iconExists = prepareIcon();
    
    if (!iconExists) {
      createSimpleIcon();
    }
    
    console.log('✅ Preparación del icono completada');
    console.log('📦 Continuando con el build...');
    
  } catch (error) {
    console.error('❌ Error durante la preparación:', error.message);
    console.log('⚠️  Continuando con configuración por defecto...');
  }
}

// Ejecutar si se llama directamente
if (require.main === module) {
  main();
}

module.exports = { prepareIcon, createSimpleIcon, verifyBuildDirectory, main };
