
# GestoTeam

**GestoTeam** es una aplicación diseñada para facilitar la gestión integral de equipos de fútbol. Permite registrar jugadores, controlar asistencias a entrenamientos, gestionar convocatorias, llevar estadísticas detalladas y mucho más.

## Características Principales

- **Gestión de Jugadores**: Registro y actualización de información de los jugadores, incluyendo posiciones y estadísticas individuales.
- **Control de Asistencias**: Seguimiento de la asistencia de los jugadores a los entrenamientos y partidos.
- **Gestión de Convocatorias**: Creación y administración de convocatorias para partidos, asignando roles y posiciones.
- **Estadísticas de Partidos**: Registro de goles, tarjetas y otros datos relevantes de cada partido.
- **Análisis de Rivales**: Almacenamiento de información sobre equipos rivales, incluyendo formaciones y tácticas observadas.

## Tecnologías Utilizadas

- **Backend**: [Spring Boot](https://spring.io/projects/spring-boot) con Java 21.
- **Frontend**: [Vue.js](https://vuejs.org/).
- **Base de Datos**: [PostgreSQL](https://www.postgresql.org/).

## Requisitos del Sistema

- **Java**: Versión 21 o superior.
- **Node.js**: Versión 14 o superior.
- **PostgreSQL**: Versión 13 o superior.

## Estructura del Proyecto

```
GestoTeam/
├── backend/          # Proyecto de Spring Boot
│   ├── src/          # Código fuente del backend
│   ├── pom.xml       # Configuración de Maven
│   └── ...           # Archivos de configuración del backend
├── frontend/         # Proyecto de Vue.js
│   ├── src/          # Código fuente del frontend
│   ├── package.json  # Configuración de npm
│   └── ...           # Archivos de configuración del frontend
└── README.md         # Descripción general del proyecto
```

## Instalación y Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/GestoTeam.git
cd GestoTeam
```

### Paso 2: Configuración del Backend

1. **Navega al directorio del backend**:
   ```bash
   cd backend
   ```

2. **Configura la base de datos**:
   - Asegúrate de tener PostgreSQL instalado y ejecutándose.
   - Crea una base de datos llamada `gestoteam` (o cualquier nombre que prefieras).
   - Configura el archivo `src/main/resources/application.properties` con los detalles de la conexión a la base de datos:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/gestoteam
     spring.datasource.username=tu_usuario
     spring.datasource.password=tu_contraseña
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Construir y ejecutar el backend**:
   - Ejecuta el siguiente comando para iniciar el backend en modo desarrollo:
     ```bash
     ./mvnw spring-boot:run
     ```
   - El backend estará disponible en `http://localhost:8080`.

### Paso 3: Configuración del Frontend

1. **Navega al directorio del frontend**:
   ```bash
   cd ../frontend
   ```

2. **Instala las dependencias**:
   - Ejecuta el siguiente comando para instalar todas las dependencias de Vue:
     ```bash
     npm install
     ```

3. **Configura la conexión al backend**:
   - En el archivo `frontend/src/config.js` (o el archivo de configuración correspondiente), define la URL del backend:
     ```javascript
     export const API_BASE_URL = "http://localhost:8080/api";
     ```

4. **Ejecuta el frontend**:
   - Inicia el servidor de desarrollo de Vue con el siguiente comando:
     ```bash
     npm run serve
     ```
   - El frontend estará disponible en `http://localhost:3000`.

## Ejecución de la Aplicación Completa

1. Asegúrate de que tanto el backend como el frontend están ejecutándose.
2. Accede a `http://localhost:3000` en tu navegador para comenzar a utilizar GestoTeam.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o envía un pull request con tus sugerencias o mejoras.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
