# GestoTeam Desktop

Aplicación de escritorio para GestoTeam que integra el backend Java y el frontend Vue.js en una sola aplicación Electron.

## 🚀 Build Unificado

El sistema de build está completamente unificado y automatizado. Un solo comando recompila todo:

- **Backend Java** (Gradle)
- **Frontend Vue.js** (npm)
- **Aplicación Electron** (empaquetado)
- **Instalador NSIS** (Windows)

## 📋 Requisitos

- **Java 17+** - Para el backend
- **Node.js 18+** - Para el frontend y desktop
- **NSIS** (opcional) - Para crear el instalador Windows

## 🔨 Comandos de Build

### Build Completo (Recomendado)
```bash
npm run build
# o
npm run build:local
# o
npm run build:professional
```

**Todos los comandos hacen lo mismo ahora** - recompilan backend, frontend y crean el instalador.

### Compilar Instalador (Opcional)
```bash
npm run compile-installer
```

**Requiere NSIS instalado** - Crea el instalador .exe desde el script NSIS generado.

### Diagnóstico de NSIS
```bash
npm run check-nsis
```

**Herramienta de diagnóstico** - Verifica el estado de NSIS en el sistema y proporciona recomendaciones.

### Limpiar Build
```bash
npm run clean
```

### Verificar Versiones
```bash
npm run version:check
```

## 🏗️ Proceso de Build

1. **Recompila Backend** - Ejecuta `./gradlew clean build` en `gestoteam-backend/`
2. **Recompila Frontend** - Ejecuta `npm run build` en `gestoteam-frontend/`
3. **Prepara Aplicación** - Copia JAR y dist a `app/`
4. **Crea Instalador** - Genera script NSIS y compila instalador
5. **Empaqueta Todo** - Crea estructura final en `dist-electron/`

## 📁 Estructura de Salida

```
gestoteam-desktop/
├── app/                           # Aplicación empaquetada
│   ├── gestoteam.jar             # Backend Java
│   ├── frontend/                 # Frontend compilado
│   └── app-config.json          # Configuración
└── dist-electron/                # Archivos de distribución
    ├── installer.nsi             # Script NSIS
    ├── GestoTeam Desktop Setup X.X.X.exe  # Instalador (si NSIS disponible)
    └── [archivos del instalador]
```

## 🎯 GitHub Actions

El workflow `.github/workflows/build-desktop.yml` se ejecuta automáticamente:

- **En cada tag** (`v*`) - Crea release automático
- **Manual** - Permite construir versión específica
- **Windows** - Ejecuta en runner Windows con Java 17 y Node.js 18

## 🔧 Configuración

### Perfil Desktop
La aplicación usa el perfil `desktop` del backend:
- Puerto: 8081
- Base de datos: H2 embebida
- Archivos: Almacenamiento local

### Perfil Frontend
El frontend se sirve desde el directorio `app/frontend/`:
- Puerto: 8080
- Modo: Producción optimizado

## 🚀 Desarrollo Local

```bash
# Iniciar en modo desarrollo
npm run dev

# Iniciar aplicación normal
npm start
```

## 📦 Instalación

### Con Instalador (Recomendado)
1. Ejecutar `GestoTeam Desktop Setup X.X.X.exe`
2. Seguir el asistente de instalación
3. La aplicación se instala en `%LOCALAPPDATA%\Programs\GestoTeam Desktop`

### Portable
1. Extraer `gestoteam-desktop-app.zip`
2. Ejecutar `GestoTeam Desktop.exe`

## 🔍 Solución de Problemas

### Java no encontrado
```
⚠️ Java del sistema no encontrado
💡 La aplicación requiere Java 17+ para funcionar
📥 Descargar desde: https://adoptium.net/
```

### NSIS no disponible
```
⚠️ NSIS no encontrado, creando solo el script
💡 Para crear el instalador, instale NSIS y ejecute manualmente:
   "C:\Program Files (x86)\NSIS\makensis.exe" "ruta\al\installer.nsi"
```

**Solución:** Después de instalar NSIS, ejecuta:
```bash
npm run compile-installer
```

**Diagnóstico:** Para verificar el estado de NSIS:
```bash
npm run check-nsis
```

### Error de Build
1. Verificar que Java 17+ esté instalado
2. Verificar que Node.js 18+ esté instalado
3. Ejecutar `npm run clean` y reintentar
4. Verificar que los directorios `gestoteam-backend/` y `gestoteam-frontend/` existan

## 📝 Notas de Versión

- **v0.0.1** - Build unificado implementado
- Sistema de versiones automático
- Instalador NSIS personalizado
- GitHub Actions automatizado
- Soporte para releases automáticos

## 🤝 Contribución

1. Hacer cambios en el código
2. Actualizar versiones si es necesario
3. Crear tag: `git tag vX.X.X`
4. Push: `git push --tags`
5. GitHub Actions creará el release automáticamente

## 📄 Licencia

UNLICENSED - GestoTeam AI
