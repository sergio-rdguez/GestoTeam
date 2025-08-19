# 🔧 Guía de Desarrollo - GestoTeam

## 🎯 Visión General

**GestoTeam** es una aplicación multiplataforma para gestión de equipos de fútbol, desarrollada con tecnologías modernas y arquitectura modular.

## 🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │    Desktop      │
│   (Vue.js)      │◄──►│  (Spring Boot)  │◄──►│   (Electron)    │
│                 │    │                 │    │                 │
│   Puerto: 3000  │    │ Puerto: 8080/81 │    │ Puerto: 8081    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Navegador     │    │   PostgreSQL    │    │   H2 Database   │
│   (Web App)     │    │   (Producción)  │    │   (Local)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Tecnologías por Capa

#### Frontend (Vue.js 3)
- **Framework**: Vue.js 3 + Composition API
- **Routing**: Vue Router 4
- **UI Framework**: Bootstrap 5
- **HTTP Client**: Axios
- **State Management**: Vuex (futuro)
- **Build Tool**: Vue CLI

#### Backend (Spring Boot 3)
- **Framework**: Spring Boot 3.3.0
- **Java Version**: 17
- **Security**: Spring Security + JWT
- **Data Access**: Spring Data JPA + Hibernate
- **Database**: H2 (desktop) + PostgreSQL (web)
- **Build Tool**: Gradle

#### Desktop (Electron)
- **Runtime**: Electron 28
- **Build Tool**: electron-builder
- **Auto-update**: electron-updater
- **Packaging**: NSIS (Windows)

## 🚀 Configuración del Entorno de Desarrollo

### Prerrequisitos
```bash
# Java
java -version  # Debe ser 17 o superior

# Node.js
node --version  # Debe ser 18 o superior
npm --version   # Debe ser 9 o superior

# Git
git --version
```

### Instalación del Entorno
```bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/gestoteam.git
cd gestoteam

# 2. Configurar backend
cd gestoteam-backend
./gradlew bootRun

# 3. Configurar frontend
cd ../gestoteam-frontend
npm install
npm run serve

# 4. Configurar desktop
cd ../gestoteam-desktop
npm install
npm run dev
```

## 📁 Estructura del Proyecto

### Backend (gestoteam-backend/)
```
src/main/java/com/gestoteam/
├── config/           # Configuraciones de Spring
├── controller/       # Controladores REST
├── dto/             # Data Transfer Objects
├── enums/           # Enumeraciones
├── exception/       # Manejo de excepciones
├── model/           # Entidades JPA
├── repository/      # Repositorios de datos
├── service/         # Lógica de negocio
└── util/            # Utilidades

src/main/resources/
├── application.yml              # Configuración base
├── application-local-client.yml # Desktop (H2 + puerto 8081)
├── application-dev.yml          # Desarrollo (H2 + puerto 8080)
├── application-prod.yml         # Producción (PostgreSQL + puerto 8080)
└── db/migration/                # Migraciones Flyway
```

### Frontend (gestoteam-frontend/)
```
src/
├── components/       # Componentes reutilizables
│   ├── base/        # Componentes base (Button, Input, etc.)
│   ├── common/      # Componentes comunes (DataTable, etc.)
│   └── layout/      # Componentes de layout
├── pages/           # Páginas de la aplicación
├── services/        # Servicios de API
├── utils/           # Utilidades
├── router/          # Configuración de rutas
└── assets/          # Recursos estáticos
```

### Desktop (gestoteam-desktop/)
```
├── build/           # Recursos de build
├── src/             # Código fuente (main.js)
├── package.json     # Configuración del proyecto
└── README.md        # Documentación
```

## 🔧 Configuración de Desarrollo

### Perfiles de Spring Boot

#### local-client (Desktop)
```yaml
spring:
  profiles:
    active: local-client
  
  datasource:
    url: jdbc:h2:file:./gestoteamdb
    username: sa
    password: 
  
server:
  port: 8081
```

#### dev (Desarrollo Web)
```yaml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:h2:file:./gestoteamdb
    username: sa
    password: 
  
server:
  port: 8080
```

#### prod (Producción Web)
```yaml
spring:
  profiles:
    active: prod
  
  datasource:
    url: jdbc:postgresql://localhost:5432/gestoteam
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
server:
  port: 8080
```

### Variables de Entorno Frontend
```bash
# .env.development
VUE_APP_API_URL=http://localhost:8080/api
VUE_APP_MODE=development

# .env.production
VUE_APP_API_URL=https://api.gestoteam.com/api
VUE_APP_MODE=production
```

## 🚀 Flujo de Desarrollo

### 1. Desarrollo de Features
```bash
# Crear rama de feature
git checkout -b feature/nueva-funcionalidad

# Desarrollar cambios
# ... código ...

# Commit y push
git add .
git commit -m "feat: nueva funcionalidad"
git push origin feature/nueva-funcionalidad
```

### 2. Testing
```bash
# Backend tests
cd gestoteam-backend
./gradlew test

# Frontend tests (futuro)
cd ../gestoteam-frontend
npm run test

# Desktop tests (futuro)
cd ../gestoteam-desktop
npm run test
```

### 3. Pull Request
- Crear PR a `main`
- Revisar código
- Ejecutar tests
- Aprobar y merge

### 4. Release
```bash
# Actualizar versión
# Commit y push
git add .
git commit -m "release: v1.0.0"
git push

# Crear tag (dispara GitHub Actions)
git tag v1.0.0
git push origin v1.0.0
```

## 📦 Build y Deployment

### Build Local
```bash
# Backend
cd gestoteam-backend
./gradlew clean build

# Frontend
cd ../gestoteam-frontend
npm run build

# Desktop
cd ../gestoteam-desktop
npm run build
```

### Build Automático (GitHub Actions)
El workflow `.github/workflows/build-desktop.yml` se ejecuta automáticamente:

1. **Build Backend**: Compila JAR con perfil `local-client`
2. **Build Frontend**: Genera archivos estáticos
3. **Prepare Desktop**: Copia JAR y frontend a desktop
4. **Build Desktop**: Crea instalador NSIS
5. **Release**: Publica en GitHub Releases

## 🧪 Testing

### Backend Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class PlayerServiceTest {
    
    @Autowired
    private PlayerService playerService;
    
    @Test
    void shouldCreatePlayer() {
        // Test implementation
    }
}
```

### Frontend Testing (Futuro)
```javascript
import { mount } from '@vue/test-utils'
import PlayerForm from '@/components/PlayerForm.vue'

describe('PlayerForm', () => {
  test('renders player form correctly', () => {
    const wrapper = mount(PlayerForm)
    expect(wrapper.find('form').exists()).toBe(true)
  })
})
```

## 🔍 Debugging

### Backend Debugging
```bash
# Ejecutar con debug
./gradlew bootRun --debug-jvm

# Conectar debugger en puerto 5005
```

### Frontend Debugging
```bash
# Ejecutar con Vue DevTools
npm run serve

# Abrir DevTools en navegador
```

### Desktop Debugging
```bash
# Ejecutar con DevTools
npm run dev

# DevTools se abren automáticamente
```

## 📊 Monitoreo y Logs

### Logging Backend
```yaml
logging:
  level:
    com.gestoteam: DEBUG
    org.springframework: INFO
    org.hibernate: WARN
  
  file:
    name: logs/gestoteam.log
```

### Logging Frontend
```javascript
// Configurar logging
console.log('Debug info:', data)
console.warn('Warning:', message)
console.error('Error:', error)
```

### Logging Desktop
```javascript
// Logs se guardan en %APPDATA%\GestoTeam\logs\
console.log('[Desktop] Info:', message)
console.error('[Desktop] Error:', error)
```

## 🔒 Seguridad

### JWT Configuration
```yaml
gestoteam:
  jwt:
    secret: ${JWT_SECRET:default-secret}
    expiration: 86400000  # 24 horas
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

## 📚 Recursos y Referencias

### Documentación Oficial
- [Spring Boot 3.x](https://spring.io/projects/spring-boot)
- [Vue.js 3](https://vuejs.org/)
- [Electron](https://www.electronjs.org/)
- [H2 Database](https://www.h2database.com/)

### Herramientas de Desarrollo
- **IDE**: IntelliJ IDEA, VS Code
- **Database**: H2 Console, pgAdmin
- **API Testing**: Postman, Insomnia
- **Git**: GitHub Desktop, GitKraken

### Patrones de Diseño
- **Backend**: Repository Pattern, Service Layer, DTO Pattern
- **Frontend**: Component Composition, Service Layer
- **Desktop**: Main Process + Renderer Process

## 🚨 Solución de Problemas Comunes

### Backend No Inicia
```bash
# Verificar puerto
netstat -ano | findstr :8081

# Verificar Java
java -version

# Verificar Gradle
./gradlew --version
```

### Frontend No Se Conecta
```bash
# Verificar API URL
# Verificar CORS
# Verificar backend ejecutándose
```

### Desktop No Inicia
```bash
# Verificar Node.js
node --version

# Verificar dependencias
npm install

# Verificar logs
# %APPDATA%\GestoTeam\logs\
```

## 🔮 Roadmap y Futuro

### Versión 1.1
- [ ] Tests unitarios completos
- [ ] Tests de integración
- [ ] CI/CD mejorado
- [ ] Documentación API

### Versión 1.2
- [ ] Sincronización en la nube
- [ ] Aplicación móvil
- [ ] Reportes avanzados
- [ ] Integración con APIs externas

### Versión 2.0
- [ ] Microservicios
- [ ] Kubernetes deployment
- [ ] Machine Learning para estadísticas
- [ ] Real-time updates

---

## 🎯 **Resumen para Desarrolladores**

**GestoTeam** es un proyecto **profesional y escalable** que requiere:

✅ **Conocimientos sólidos** de Spring Boot y Vue.js  
✅ **Buenas prácticas** de desarrollo y testing  
✅ **Arquitectura limpia** y mantenible  
✅ **Documentación completa** y actualizada  
✅ **CI/CD automatizado** para releases  

**¡Únete al equipo de desarrollo y ayuda a crear la mejor aplicación de gestión de equipos de fútbol!** 🚀
