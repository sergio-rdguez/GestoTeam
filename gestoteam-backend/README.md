# GestoTeam Backend

## 📋 Descripción

GestoTeam es una aplicación backend desarrollada en **Spring Boot 3.3.0** para la gestión integral de equipos de fútbol amateur. El sistema permite administrar equipos, jugadores, partidos, estadísticas y más, proporcionando una API REST completa con autenticación JWT y documentación automática.

## 🚀 Características Principales

- **Gestión de Equipos**: Crear, editar y administrar equipos de fútbol
- **Gestión de Jugadores**: Administrar plantillas, fichas y estadísticas individuales
- **Gestión de Partidos**: Programar partidos, registrar resultados y estadísticas
- **Sistema de Autenticación**: JWT con Spring Security
- **API REST**: Endpoints bien documentados con Swagger/OpenAPI
- **Base de Datos**: Soporte para PostgreSQL (producción) y H2 (desarrollo)
- **Auditoría**: Seguimiento automático de fechas de creación y modificación
- **Testing**: Cobertura de pruebas con JaCoCo

## 🛠️ Tecnologías Utilizadas

### Core
- **Java 17**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Validation**

### Base de Datos
- **PostgreSQL** (producción)
- **H2 Database** (desarrollo)
- **Hibernate**

### Documentación y Testing
- **Swagger/OpenAPI 3**
- **JaCoCo** (cobertura de código)
- **JUnit 5**

### Utilidades
- **Lombok** (reducción de código boilerplate)
- **ModelMapper** (mapeo de objetos)
- **JWT** (autenticación)

## 📁 Estructura del Proyecto

```
src/main/java/com/gestoteam/
├── config/                 # Configuraciones de Spring
│   ├── CorsConfig.java
│   ├── DevDataInitializer.java
│   ├── JwtAuthFilter.java
│   ├── ModelMapperConfig.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/             # Controladores REST
│   ├── AuthController.java
│   ├── EnumController.java
│   ├── MatchController.java
│   ├── OpponentController.java
│   ├── PlayerController.java
│   ├── PlayerMatchStatsController.java
│   ├── TeamController.java
│   └── UserSettingsController.java
├── dto/                   # Objetos de transferencia de datos
│   ├── request/           # DTOs de entrada
│   └── response/          # DTOs de salida
├── enums/                 # Enumeraciones del sistema
│   ├── Category.java
│   ├── PlayerStatus.java
│   └── Position.java
├── exception/             # Manejo de excepciones
├── model/                 # Entidades JPA
│   ├── Attendance.java
│   ├── Injury.java
│   ├── Match.java
│   ├── Opponent.java
│   ├── Player.java
│   ├── PlayerMatchStats.java
│   ├── Season.java
│   ├── Team.java
│   ├── Training.java
│   └── User.java
├── repository/            # Repositorios de datos
├── service/               # Lógica de negocio
└── util/                  # Utilidades y helpers
```

## 🔧 Configuración

### Requisitos Previos

- **Java 17** o superior
- **Gradle 7.x** o superior
- **PostgreSQL** (para producción)
- **H2 Database** (para desarrollo, incluido)

### Variables de Entorno

#### Desarrollo (`application-dev.yml`)
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:h2:mem:gestoteamdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    defer-datasource-initialization: true
  h2:
    console:
      enabled: true

jwt:
  secret: "unaClaveSecretaLargaYUnicaParaGestoTeamQueSeaDificilDeAdivinar"
  expiration: 86400
```

#### Producción (`application-prod.yml`)
```yaml
server:
  address: 0.0.0.0
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://SERGIO-PC:5432/gestoteam
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: admin
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## 🚀 Instalación y Ejecución

### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd gestoteam-backend
```

### 2. Configurar Base de Datos

#### Para Desarrollo
No es necesario configurar nada, se usa H2 en memoria con datos de prueba.

#### Para Producción
1. Crear base de datos PostgreSQL
2. Configurar credenciales en `application-prod.yml`
3. Cambiar el perfil activo a `prod` en `application.yml`

### 3. Ejecutar la Aplicación

#### Con Gradle Wrapper
```bash
# Desarrollo (por defecto)
./gradlew bootRun

# Producción
./gradlew bootRun --args='--spring.profiles.active=prod'
```

#### Con Gradle
```bash
# Desarrollo
gradle bootRun

# Producción
gradle bootRun --args='--spring.profiles.active=prod'
```

### 4. Verificar la Instalación

- **API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui/
- **H2 Console** (desarrollo): http://localhost:8081/h2-console

## 🔐 Autenticación y Seguridad

### JWT Authentication
- **Secret**: Configurado en `application.yml`
- **Expiración**: 24 horas (86400 segundos)
- **Algoritmo**: HS256

### Endpoints Públicos
- `/api/auth/register` - Registro de usuarios
- `/api/auth/login` - Inicio de sesión
- `/swagger-ui/**` - Documentación de la API
- `/v3/api-docs/**` - Especificación OpenAPI

### Endpoints Protegidos
Todos los demás endpoints requieren autenticación JWT válida.

### Uso del Token
```bash
# Incluir en el header de las peticiones
Authorization: Bearer <jwt-token>
```

## 📚 API Endpoints

### Autenticación
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `GET /api/auth/profile` - Obtener perfil del usuario

### Equipos
- `GET /api/teams` - Listar equipos
- `POST /api/teams` - Crear equipo
- `GET /api/teams/{id}` - Obtener equipo
- `PUT /api/teams/{id}` - Actualizar equipo
- `DELETE /api/teams/{id}` - Eliminar equipo

### Jugadores
- `GET /api/players` - Listar jugadores
- `POST /api/players` - Crear jugador
- `GET /api/players/{id}` - Obtener jugador
- `PUT /api/players/{id}` - Actualizar jugador
- `DELETE /api/players/{id}` - Eliminar jugador

### Partidos
- `GET /api/matches` - Listar partidos
- `POST /api/matches` - Crear partido
- `GET /api/matches/{id}` - Obtener partido
- `PUT /api/matches/{id}` - Actualizar partido
- `DELETE /api/matches/{id}` - Eliminar partido

### Estadísticas
- `GET /api/player-match-stats` - Listar estadísticas
- `POST /api/player-match-stats` - Crear estadísticas
- `PUT /api/player-match-stats/{id}` - Actualizar estadísticas

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./gradlew test

# Con reporte de cobertura
./gradlew test jacocoTestReport
```

### Cobertura de Código
- **JaCoCo** genera reportes en `build/reports/jacoco/`
- **Excluye**: modelos, DTOs, configuraciones y utilidades
- **Formato**: HTML, XML

## 📊 Datos de Prueba (Desarrollo)

El perfil de desarrollo incluye datos de prueba automáticos:

- **Usuario**: `testuser` / `password`
- **Equipos**: Atlético Gesto (Senior), Gesto Academy (Juvenil)
- **Jugadores**: Plantillas completas con posiciones
- **Partidos**: Calendario de partidos de ejemplo
- **Oponentes**: Equipos rivales predefinidos

## 🔧 Configuración de Swagger

- **URL**: `/swagger-ui/`
- **Título**: GestoTeam API
- **Versión**: 1.0
- **Descripción**: API para gestionar equipos de fútbol amateur
- **Seguridad**: Bearer JWT en todos los endpoints

## 📝 Logs y Monitoreo

- **Logging**: SLF4J con Logback
- **Nivel**: INFO por defecto
- **Auditoría**: Fechas automáticas de creación/modificación

## 🚀 Despliegue

### Docker (Recomendado)
```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Variables de Entorno de Producción
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-secure-jwt-secret
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver el archivo `LICENSE` para más detalles.

## 📞 Soporte

Para soporte técnico o preguntas:
- Crear un issue en el repositorio
- Contactar al equipo de desarrollo

## 🔄 Versiones

- **Versión Actual**: 0.0.1-SNAPSHOT
- **Java**: 17
- **Spring Boot**: 3.3.0
- **Gradle**: 7.x+

---

**GestoTeam** - Gestión profesional de equipos de fútbol amateur ⚽
