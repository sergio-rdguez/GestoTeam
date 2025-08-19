# 📦 Guía de Release - GestoTeam

Esta guía te explica cómo crear y publicar releases de **GestoTeam** de forma profesional y automatizada.

## 🎯 ¿Qué es un Release?

Un **release** es una versión oficial de la aplicación que se distribuye a los clientes. Incluye:

- ✅ **Instalador Windows** (.exe)
- ✅ **Notas de cambios** detalladas
- ✅ **Número de versión** único
- ✅ **Fecha de publicación** oficial
- ✅ **Compatibilidad** verificada

## 🚀 Proceso Automático de Release

### 1. **Trigger Automático**
Los releases se crean **automáticamente** cuando:
- Se crea un **tag** con formato `v*` (ej: `v1.0.0`)
- Se hace **push** del tag a GitHub

### 2. **GitHub Actions Workflow**
El archivo `.github/workflows/build-desktop.yml` se ejecuta automáticamente:

```yaml
on:
  push:
    tags:
      - 'v*'  # Dispara en cualquier tag v*
```

### 3. **Pasos Automatizados**
1. **Build Backend** → Compila JAR con perfil `local-client`
2. **Build Frontend** → Genera archivos estáticos
3. **Prepare Desktop** → Copia JAR y frontend a desktop
4. **Build Desktop** → Crea instalador NSIS
5. **Create Release** → Publica en GitHub Releases

## 🔧 Crear un Release Manualmente

### Paso 1: Preparar el Código
```bash
# 1. Asegurar que todo está commiteado
git add .
git commit -m "feat: preparar release v1.0.0"
git push origin main

# 2. Verificar que los tests pasan
cd gestoteam-backend && ./gradlew test
cd ../gestoteam-frontend && npm run build
cd ../gestoteam-desktop && npm run build
```

### Paso 2: Actualizar Versiones
```bash
# 1. Actualizar versión en package.json del desktop
cd gestoteam-desktop
# Editar package.json: "version": "1.0.0"

# 2. Actualizar versión en package.json del frontend
cd ../gestoteam-frontend
# Editar package.json: "version": "1.0.0"

# 3. Commit de versiones
git add .
git commit -m "chore: actualizar versiones a v1.0.0"
git push origin main
```

### Paso 3: Crear Tag y Release
```bash
# 1. Crear tag local
git tag v1.0.0

# 2. Push del tag (esto dispara GitHub Actions)
git push origin v1.0.0

# 3. Verificar que GitHub Actions se ejecuta
# Ve a: https://github.com/tu-usuario/gestoteam/actions
```

## 📋 Checklist de Release

### ✅ **Antes del Release**
- [ ] **Tests pasan** en todas las plataformas
- [ ] **Build local** funciona correctamente
- [ ] **Documentación** está actualizada
- [ ] **CHANGELOG.md** está actualizado
- [ ] **Versiones** están actualizadas en package.json
- [ ] **Dependencias** están actualizadas
- [ ] **Logs** están limpios

### ✅ **Durante el Release**
- [ ] **Tag** creado con formato correcto (`v1.0.0`)
- [ ] **GitHub Actions** se ejecuta correctamente
- [ ] **Build** se completa sin errores
- [ ] **Instalador** se genera correctamente
- **Release** se publica automáticamente

### ✅ **Después del Release**
- [ ] **Descargar** instalador para testing
- [ ] **Verificar** instalación en Windows limpio
- [ ] **Probar** funcionalidades principales
- [ ] **Comunicar** a clientes sobre nueva versión
- [ ] **Monitorear** feedback y issues

## 🏷️ Convenciones de Versionado

### **Semantic Versioning (SemVer)**
```
MAJOR.MINOR.PATCH
  1   .  0   .  0
```

- **MAJOR** (1): Cambios incompatibles con versiones anteriores
- **MINOR** (0): Nuevas funcionalidades compatibles
- **PATCH** (0): Correcciones de bugs compatibles

### **Ejemplos de Versiones**
```bash
# Nueva funcionalidad
v1.1.0    # Nueva feature, compatible

# Corrección de bug
v1.0.1    # Bug fix, compatible

# Cambio mayor
v2.0.0    # Cambio incompatible

# Pre-release
v1.0.0-alpha.1    # Alpha testing
v1.0.0-beta.1     # Beta testing
v1.0.0-rc.1       # Release candidate
```

## 📝 Notas de Release

### **Estructura Recomendada**
```markdown
# GestoTeam Desktop v1.0.0

## 🎉 Nuevas Funcionalidades
- Gestión completa de jugadores
- Sistema de convocatorias
- Estadísticas avanzadas del equipo

## 🔧 Mejoras
- Interfaz más intuitiva
- Rendimiento optimizado
- Mejor gestión de memoria

## 🐛 Correcciones
- Corregido bug en exportación de datos
- Solucionado problema de inicio lento

## 📋 Requisitos del Sistema
- Windows 10/11 (64-bit)
- 4GB RAM mínimo
- 500MB espacio en disco

## 📥 Descarga
- [GestoTeam-Desktop-Setup-1.0.0.exe](link-al-instalador)

## 🔄 Actualización
- Los usuarios existentes recibirán notificación automática
- La actualización se descarga e instala automáticamente
```

## 🚨 Solución de Problemas

### **GitHub Actions Falla**
```bash
# 1. Verificar logs en Actions
# 2. Corregir errores en el código
# 3. Hacer commit de correcciones
# 4. Eliminar tag anterior
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# 5. Crear nuevo tag
git tag v1.0.0
git push origin v1.0.0
```

### **Build Falla Localmente**
```bash
# 1. Limpiar builds anteriores
cd gestoteam-backend && ./gradlew clean
cd ../gestoteam-frontend && npm run clean
cd ../gestoteam-desktop && npm run clean

# 2. Verificar dependencias
cd gestoteam-backend && ./gradlew --refresh-dependencies
cd ../gestoteam-frontend && npm ci
cd ../gestoteam-desktop && npm ci

# 3. Reintentar build
cd gestoteam-desktop && npm run build
```

### **Instalador No Se Genera**
```bash
# 1. Verificar electron-builder
npm list electron-builder

# 2. Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install

# 3. Verificar configuración en package.json
# Asegurar que "build" y "win" están configurados
```

## 📊 Monitoreo del Release

### **Métricas a Seguir**
- **Descargas** del instalador
- **Instalaciones exitosas** vs fallidas
- **Feedback** de usuarios
- **Issues** reportados
- **Crash reports** (si aplica)

### **Herramientas de Monitoreo**
- **GitHub Insights** - Estadísticas del repositorio
- **GitHub Releases** - Descargas y feedback
- **GitHub Issues** - Problemas reportados
- **Logs de la aplicación** - Errores en producción

## 🔮 Mejoras Futuras

### **Automatización Avanzada**
- [ ] **Tests automáticos** antes del release
- [ ] **Validación de calidad** del código
- [ ] **Notificaciones** automáticas a clientes
- [ ] **Rollback automático** si hay problemas

### **Distribución Mejorada**
- [ ] **CDN** para descargas más rápidas
- [ ] **Torrent** para archivos grandes
- [ ] **Actualizaciones delta** (solo cambios)
- [ ] **Distribución por regiones**

---

## 🎯 **Resumen del Proceso de Release**

**GestoTeam** tiene un proceso de release **completamente automatizado**:

✅ **Trigger automático** con tags  
✅ **Build completo** de todas las plataformas  
✅ **Generación automática** del instalador  
✅ **Publicación automática** en GitHub Releases  
✅ **Notificaciones automáticas** a usuarios  

**¡Solo necesitas crear un tag y GitHub Actions hace todo el trabajo!** 🚀

---

**GestoTeam AI** - *Releases profesionales, automatizados y confiables*
