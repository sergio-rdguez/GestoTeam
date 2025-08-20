const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');

console.log('🔍 Verificando Java Runtime embebido...');

// Función para verificar si un archivo es ejecutable
function isExecutable(filePath) {
  try {
    // En Windows, verificar si el archivo existe y tiene permisos de lectura
    if (fs.existsSync(filePath)) {
      const stats = fs.statSync(filePath);
      console.log(`✅ Archivo encontrado: ${filePath}`);
      console.log(`   Tamaño: ${(stats.size / 1024).toFixed(2)} KB`);
      console.log(`   Es archivo: ${stats.isFile()}`);
      console.log(`   Permisos: ${stats.mode.toString(8)}`);
      return true;
    } else {
      console.log(`❌ Archivo no encontrado: ${filePath}`);
      return false;
    }
  } catch (error) {
    console.log(`❌ Error verificando archivo: ${error.message}`);
    return false;
  }
}

// Función para verificar la estructura del Java runtime
function verifyJavaRuntimeStructure() {
  const javaRuntimePath = path.join(__dirname, 'app', 'java-runtime');
  
  if (!fs.existsSync(javaRuntimePath)) {
    console.log('❌ Directorio java-runtime no encontrado');
    return false;
  }
  
  console.log(`✅ Directorio java-runtime encontrado: ${javaRuntimePath}`);
  
  // Verificar estructura básica
  const requiredDirs = ['bin', 'lib', 'conf'];
  const requiredFiles = ['bin/java.exe', 'bin/javaw.exe', 'release'];
  
  for (const dir of requiredDirs) {
    const dirPath = path.join(javaRuntimePath, dir);
    if (fs.existsSync(dirPath)) {
      console.log(`✅ Directorio encontrado: ${dir}`);
    } else {
      console.log(`❌ Directorio faltante: ${dir}`);
      return false;
    }
  }
  
  for (const file of requiredFiles) {
    const filePath = path.join(javaRuntimePath, file);
    if (isExecutable(filePath)) {
      console.log(`✅ Archivo encontrado: ${file}`);
    } else {
      console.log(`❌ Archivo faltante: ${file}`);
      return false;
    }
  }
  
  return true;
}

// Función para probar la ejecución del Java
function testJavaExecution() {
  const javaExePath = path.join(__dirname, 'app', 'java-runtime', 'bin', 'java.exe');
  
  if (!isExecutable(javaExePath)) {
    console.log('❌ No se puede probar la ejecución - java.exe no es ejecutable');
    return false;
  }
  
  console.log('🧪 Probando ejecución de Java embebido...');
  
  return new Promise((resolve) => {
    exec(`"${javaExePath}" -version`, { timeout: 10000 }, (error, stdout, stderr) => {
      if (error) {
        console.log(`❌ Error ejecutando Java: ${error.message}`);
        resolve(false);
      } else {
        console.log(`✅ Java ejecutado correctamente:`);
        console.log(`   Salida: ${stdout.trim()}`);
        if (stderr) {
          console.log(`   Error: ${stderr.trim()}`);
        }
        resolve(true);
      }
    });
  });
}

// Función principal
async function main() {
  console.log('=====================================');
  console.log('VERIFICACIÓN DE JAVA RUNTIME EMBEBIDO');
  console.log('=====================================');
  
  // Verificar estructura
  const structureOk = verifyJavaRuntimeStructure();
  if (!structureOk) {
    console.log('\n❌ La estructura del Java runtime no es correcta');
    process.exit(1);
  }
  
  console.log('\n✅ Estructura del Java runtime verificada correctamente');
  
  // Probar ejecución
  const executionOk = await testJavaExecution();
  if (!executionOk) {
    console.log('\n❌ El Java embebido no se puede ejecutar');
    console.log('\n🔧 SOLUCIONES RECOMENDADAS:');
    console.log('   1. Verificar que el Java runtime se haya descargado correctamente');
    console.log('   2. Verificar que no haya problemas de permisos');
    console.log('   3. Verificar que la arquitectura del Java coincida con el sistema');
    console.log('   4. Reconstruir la aplicación con: npm run build');
    process.exit(1);
  }
  
  console.log('\n🎉 Java Runtime embebido verificado correctamente!');
  console.log('   La aplicación debería funcionar sin problemas.');
}

// Ejecutar verificación
main().catch(console.error);
