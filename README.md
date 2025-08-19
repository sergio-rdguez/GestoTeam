# 🏆 GestoTeam

**GestoTeam** es una aplicación profesional para la gestión integral de equipos de fútbol, disponible en **versión web** y **versión desktop** para Windows.

## 🚀 Características Principales

- **👥 Gestión de Jugadores**: Registro completo con estadísticas, posiciones y historial
- **⚽ Gestión de Partidos**: Control de convocatorias, resultados y estadísticas
- **🏗️ Gestión de Equipos**: Administración de plantillas y roles
- **📊 Estadísticas Avanzadas**: Análisis detallado de rendimiento
- **🔄 Gestión de Rivales**: Base de datos de equipos contrarios
- **📱 Multiplataforma**: Web responsive + Aplicación desktop Windows

## 🏗️ Arquitectura del Proyecto

```
GestoTeam/
├── 🖥️ gestoteam-backend/          # API REST Spring Boot
├── 🌐 gestoteam-frontend/         # Frontend Vue.js
├── 💻 gestoteam-desktop/          # Aplicación Electron Windows
├── 📚 docs/                       # Documentación técnica
├── 📦 releases/                   # Instaladores para clientes
└── 🔄 .github/workflows/          # CI/CD automático
```

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** + **Spring Boot 3.3.0**
- **Spring Security** + **JWT**
- **Spring Data JPA** + **Hibernate**
- **H2 Database** (desktop) + **PostgreSQL** (web)
- **Flyway** para migraciones
- **Gradle** para build

### Frontend
- **Vue.js 3** + **Vue Router**
- **Bootstrap 5** para UI
- **Axios** para API calls
- **FontAwesome** para iconos

### Desktop
- **Electron 28** para aplicación Windows
- **electron-builder** para instaladores NSIS
- **electron-updater** para auto-actualizaciones

## 📋 Requisitos del Sistema

### Para Desarrollo
- **Java 17** o superior
- **Node.js 18** o superior
- **npm** o **yarn**
- **Git**

### Para Clientes Desktop
- **Windows 10/11** (64-bit)
- **4GB RAM** mínimo
- **500MB** espacio en disco

## 🚀 Instalación y Desarrollo

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/gestoteam.git
cd gestoteam
```

### 2. Backend (Spring Boot)
```bash
cd gestoteam-backend
./gradlew bootRun
```
**Puerto**: 8081 (desktop) / 8080 (web)

### 3. Frontend (Vue.js)
```bash
cd gestoteam-frontend
npm install
npm run serve
```
**Puerto**: 3000

### 4. Desktop (Electron)
```bash
cd gestoteam-desktop
npm install
npm run dev
```

## 📦 Build y Release

### Build Automático
El proyecto incluye **GitHub Actions** que se ejecutan automáticamente al crear un tag:

```bash
# 1. Actualizar versión en package.json
# 2. Commit y push
git add .
git commit -m "release: v1.0.0"
git push

# 3. Crear tag (esto dispara el build automático)
git tag v1.0.0
git push origin v1.0.0
```

### Build Manual
```bash
# Backend
cd gestoteam-backend
./gradlew clean build

# Frontend
cd gestoteam-frontend
npm run build

# Desktop
cd gestoteam-desktop
npm run build
```

## 🔧 Configuración

### Perfiles de Spring Boot
- **`local-client`**: Para aplicación desktop (H2 + puerto 8081)
- **`dev`**: Para desarrollo web (H2 + puerto 8080)
- **`prod`**: Para producción web (PostgreSQL + puerto 8080)

### Variables de Entorno
```bash
# Backend
SPRING_PROFILES_ACTIVE=local-client
SERVER_PORT=8081

# Frontend
VUE_APP_API_URL=http://localhost:8081/api
```

## 📚 Documentación

- **📖 [Guía de Usuario](docs/user-guide.md)** - Para clientes finales
- **🔧 [Guía de Desarrollo](docs/development.md)** - Para desarrolladores
- **🚀 [Guía de Deployment](docs/deployment.md)** - Para producción
- **📦 [Guía de Release](docs/release.md)** - Para crear versiones

## 🔄 Flujo de Trabajo

1. **Desarrollo** → Rama `develop`
2. **Testing** → Pull Request a `main`
3. **Release** → Tag `v*` → Build automático
4. **Distribución** → GitHub Releases con instaladores

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es **privado y confidencial**. Todos los derechos reservados.

## 📞 Soporte

- **📧 Email**: soporte@gestoteam.com
- **🐛 Issues**: [GitHub Issues](https://github.com/tu-usuario/gestoteam/issues)
- **📖 Documentación**: [docs/](docs/)

---

## 🎯 **Resumen para Clientes**

**GestoTeam Desktop** es una aplicación **completa y profesional** que incluye:

✅ **Todo integrado** (backend + frontend)  
✅ **Base de datos local** (sin internet)  
✅ **Actualizaciones automáticas**  
✅ **Instalador profesional** para Windows  
✅ **Soporte técnico** incluido  

**Para instalar**: Descargar el `.exe` del último release y ejecutar.

---

**Desarrollado con ❤️ por GestoTeam AI**  
*Gestiona tu equipo de fútbol de forma profesional*
