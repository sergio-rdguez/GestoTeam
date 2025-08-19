# GestoTeam Desktop

Aplicación de escritorio para GestoTeam que permite gestionar equipos de fútbol de forma local.

## 🚀 Características

- **Frontend + Backend integrados** en una sola aplicación
- **Base de datos local H2**
- **Sin conexión a internet**
- **Actualizaciones automáticas**
- **Instalador NSIS para Windows**

## 📋 Requisitos

- **Windows 10/11** (64-bit)
- **4GB RAM** mínimo
- **500MB** de espacio en disco

## 🛠️ Desarrollo

```bash
# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Build de producción
npm run build
```

## 🚀 Release

Para crear una nueva versión:

1. Actualizar versión en `package.json`
2. Crear tag: `git tag v1.0.0`
3. Push: `git push origin v1.0.0`
4. GitHub Actions creará automáticamente el release con el instalador

## 📦 Configuración

- **Backend**: Puerto 8081
- **Frontend**: Puerto 8080 (dentro de Electron)
- **Base de datos**: `%APPDATA%\GestoTeam\data\`
- **Logs**: `%APPDATA%\GestoTeam\logs\`

## 📦 Instalación

1. Descargar `GestoTeam-Setup-v1.0.0.exe` del release
2. Ejecutar el instalador
3. Seguir el asistente
4. La aplicación se instala automáticamente

## 🔄 Actualizaciones

La aplicación verifica automáticamente actualizaciones cada hora y notifica cuando hay una nueva versión disponible.

---

**GestoTeam Desktop** - Gestión local de equipos de fútbol.
