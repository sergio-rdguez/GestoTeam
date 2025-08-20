const { execSync, spawn } = require('child_process');
const fs = require('fs');
const path = require('path');
const os = require('os');

console.log('🔍 Verificando Java del sistema...');

// Función para verificar si Java está disponible
function checkJavaSystem() {
  try {
    // Intentar ejecutar java -version
    const result = execSync('java -version', { 
      stdio: 'pipe',
      timeout: 10000,
      windowsHide: true
    });
    
    console.log('✅ Java encontrado en el sistema:');
    console.log(result.toString());
    return true;
  } catch (error) {
    console.log('❌ Java no encontrado en PATH del sistema');
    return false;
  }
}

// Función para buscar Java en directorios comunes
function findJavaInCommonPaths() {
  const commonPaths = [
    'C:\\Program Files\\Java\\jdk-17\\bin\\java.exe',
    'C:\\Program Files\\Java\\jre-17\\bin\\java.exe',
    'C:\\Program Files\\Java\\jdk-11\\bin\\java.exe',
    'C:\\Program Files\\Java\\jre-11\\bin\\java.exe',
    'C:\\Program Files\\Java\\jdk-21\\bin\\java.exe',
    'C:\\Program Files\\Java\\jre-21\\bin\\java.exe',
    'C:\\Program Files (x86)\\Java\\jdk-17\\bin\\java.exe',
    'C:\\Program Files (x86)\\Java\\jre-17\\bin\\java.exe'
  ];
  
  for (const javaPath of commonPaths) {
    if (fs.existsSync(javaPath)) {
      console.log(`✅ Java encontrado en: ${javaPath}`);
      return javaPath;
    }
  }
  
  console.log('❌ Java no encontrado en directorios comunes');
  return null;
}

// Función para verificar JAVA_HOME
function checkJavaHome() {
  const javaHome = process.env.JAVA_HOME;
  if (javaHome) {
    const javaExe = path.join(javaHome, 'bin', 'java.exe');
    if (fs.existsSync(javaExe)) {
      console.log(`✅ JAVA_HOME configurado: ${javaHome}`);
      console.log(`   Java ejecutable: ${javaExe}`);
      return javaExe;
    } else {
      console.log(`⚠️ JAVA_HOME configurado pero java.exe no encontrado: ${javaHome}`);
    }
  } else {
    console.log('❌ JAVA_HOME no configurado');
  }
  return null;
}

// Función para probar ejecución de Java
function testJavaExecution(javaPath) {
  try {
    if (javaPath === 'java') {
      // Java está en PATH
      const result = execSync('java -version', { 
        stdio: 'pipe',
        timeout: 10000,
        windowsHide: true
      });
      console.log('✅ Java del PATH ejecutado correctamente');
      return true;
    } else {
      // Java está en ruta específica
      const result = execSync(`"${javaPath}" -version`, { 
        stdio: 'pipe',
        timeout: 10000,
        windowsHide: true
      });
      console.log('✅ Java de ruta específica ejecutado correctamente');
      return true;
    }
  } catch (error) {
    console.log(`❌ Error ejecutando Java: ${error.message}`);
    return false;
  }
}

// Función principal
function main() {
  console.log('=====================================');
  console.log('VERIFICACIÓN DE JAVA DEL SISTEMA');
  console.log('=====================================');
  
  let javaAvailable = false;
  let javaPath = null;
  
  // 1. Verificar Java en PATH
  if (checkJavaSystem()) {
    javaAvailable = true;
    javaPath = 'java';
  }
  
  // 2. Verificar JAVA_HOME
  if (!javaAvailable) {
    const javaHomePath = checkJavaHome();
    if (javaHomePath) {
      javaAvailable = true;
      javaPath = javaHomePath;
    }
  }
  
  // 3. Buscar en directorios comunes
  if (!javaAvailable) {
    const commonPath = findJavaInCommonPaths();
    if (commonPath) {
      javaAvailable = true;
      javaPath = commonPath;
    }
  }
  
  // 4. Probar ejecución
  if (javaAvailable && javaPath) {
    if (testJavaExecution(javaPath)) {
      console.log('\n🎉 Java del sistema está funcionando correctamente!');
      console.log(`   Ruta: ${javaPath}`);
      console.log('\n💡 RECOMENDACIÓN:');
      console.log('   - Usar Java del sistema en lugar del embebido');
      console.log('   - Configurar JAVA_HOME si no está configurado');
      console.log('   - Agregar Java al PATH del sistema');
    } else {
      console.log('\n❌ Java encontrado pero no se puede ejecutar');
      console.log('   Posibles causas:');
      console.log('   - Permisos insuficientes');
      console.log('   - Dependencias faltantes');
      console.log('   - Java corrupto');
    }
  } else {
    console.log('\n❌ Java no encontrado en el sistema');
    console.log('\n🔧 SOLUCIONES:');
    console.log('   1. Instalar Java 11, 17 o 21 desde Oracle o OpenJDK');
    console.log('   2. Configurar JAVA_HOME');
    console.log('   3. Agregar Java al PATH del sistema');
    console.log('   4. Usar el instalador automático de Java');
  }
}

// Ejecutar verificación
main();
