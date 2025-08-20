# Instalación y Uso de NSIS para GestoTeam Desktop

## 📥 Descargar NSIS

NSIS (Nullsoft Scriptable Install System) es necesario para crear el instalador de Windows.

### Enlaces de Descarga:
- **Sitio Oficial**: https://nsis.sourceforge.io/Download
- **Versión Estable**: https://nsis.sourceforge.io/Download/NSIS%203.09/
- **Instalador Windows**: `nsis-3.09-setup.exe`

## 🔧 Instalación

1. **Descargar** el instalador de NSIS
2. **Ejecutar** `nsis-3.09-setup.exe` como administrador
3. **Seguir** el asistente de instalación
4. **Reiniciar** el sistema (recomendado)

### Ubicación por Defecto:
```
C:\Program Files (x86)\NSIS\
```

## ✅ Verificar Instalación

Después de instalar, verifica que NSIS esté disponible:

```bash
# En PowerShell o CMD
"C:\Program Files (x86)\NSIS\makensis.exe" /VERSION
```

Deberías ver algo como:
```
NSIS v3.09
```

## 🚀 Uso con GestoTeam Desktop

### 1. Build Completo (Sin NSIS)
```bash
npm run build
```
Esto creará:
- ✅ Aplicación en `app/`
- ✅ Script NSIS en `dist-electron/installer.nsi`
- ⚠️ Instalador .exe (solo si NSIS está disponible)

### 2. Compilar Instalador (Con NSIS)
```bash
npm run compile-installer
```
Esto creará:
- ✅ Instalador .exe en `dist-electron/`
- ✅ Verificación de archivos y tamaños

## 📁 Estructura de Archivos

```
gestoteam-desktop/
├── app/                           # Aplicación empaquetada
│   ├── gestoteam.jar             # Backend Java
│   ├── frontend/                 # Frontend compilado
│   └── app-config.json          # Configuración
├── dist-electron/                # Archivos de distribución
│   ├── installer.nsi             # Script NSIS generado
│   ├── GestoTeam Desktop Setup X.X.X.exe  # Instalador (si NSIS disponible)
│   └── [archivos del instalador]
└── installer/                    # Scripts NSIS personalizados
    ├── main-installer.nsh        # Lógica principal
    ├── system-requirements.nsh   # Verificaciones del sistema
    ├── check-java.nsh           # Verificación de Java
    └── LICENSE.txt              # Licencia del instalador
```

## 🔍 Verificaciones del Instalador

El instalador NSIS incluye verificaciones automáticas:

### ✅ Sistema Operativo
- Windows 10 o superior
- Arquitectura x64 (64 bits)

### ✅ Java Runtime
- Versión 17 o superior
- Verificación automática en PATH

### ✅ Espacio en Disco
- Mínimo 1GB disponible
- Verificación antes de instalar

### ✅ Permisos
- Instalación per-user (no requiere administrador)
- Verificación de permisos de escritura

## 🛠️ Personalización del Instalador

### Modificar Scripts NSIS

Los archivos en `installer/` se pueden personalizar:

- **main-installer.nsh** - Lógica principal del instalador
- **system-requirements.nsh** - Verificaciones del sistema
- **check-java.nsh** - Verificación de Java

### Recompilar Después de Cambios

```bash
# 1. Hacer cambios en los scripts
# 2. Ejecutar build completo
npm run build

# 3. Compilar instalador
npm run compile-installer
```

## 🚨 Solución de Problemas

### NSIS no encontrado
```
❌ NSIS no encontrado en el sistema
💡 Para crear el instalador, instala NSIS desde: https://nsis.sourceforge.io/Download
```

**Solución:**
1. Instalar NSIS desde el enlace oficial
2. Reiniciar el sistema
3. Ejecutar `npm run compile-installer`

### Error de Compilación NSIS
```
❌ Error en Compilación NSIS: [mensaje de error]
```

**Solución:**
1. Verificar que el script `installer.nsi` existe
2. Verificar que el directorio `app/` existe
3. Ejecutar `npm run build` primero
4. Reintentar `npm run compile-installer`

### Archivos Faltantes
```
❌ Script NSIS no encontrado
💡 Ejecuta primero: npm run build
```

**Solución:**
1. Ejecutar `npm run build` para generar todos los archivos
2. Luego ejecutar `npm run compile-installer`

## 📋 Comandos Útiles

```bash
# Build completo (recomendado para desarrollo)
npm run build

# Solo compilar instalador (requiere NSIS)
npm run compile-installer

# Limpiar build anterior
npm run clean

# Verificar versiones
npm run version:check
```

## 🔄 Flujo de Trabajo Recomendado

### Para Desarrollo:
```bash
npm run build
# Esto crea la aplicación y el script NSIS
# No requiere NSIS instalado
```

### Para Release:
```bash
npm run build
npm run compile-installer
# Esto crea la aplicación, script NSIS e instalador .exe
# Requiere NSIS instalado
```

## 📞 Soporte

Si tienes problemas con NSIS:

1. **Verificar instalación**: `"C:\Program Files (x86)\NSIS\makensis.exe" /VERSION`
2. **Reinstalar NSIS** si es necesario
3. **Verificar PATH** del sistema
4. **Reiniciar** después de la instalación

---

**NSIS** es una herramienta gratuita y de código abierto para crear instaladores de Windows.
