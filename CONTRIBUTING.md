# 🤝 Guía de Contribución - GestoTeam

¡Gracias por tu interés en contribuir a **GestoTeam**! Este documento te guiará a través del proceso de contribución.

## 🎯 ¿Cómo Contribuir?

### Tipos de Contribuciones
- 🐛 **Reportar bugs**
- 💡 **Sugerir nuevas funcionalidades**
- 📝 **Mejorar documentación**
- 🔧 **Corregir código**
- 🧪 **Añadir tests**
- 🎨 **Mejorar la interfaz**

## 🚀 Configuración del Entorno

### Prerrequisitos
- **Java 17** o superior
- **Node.js 18** o superior
- **Git** instalado
- **IDE** (IntelliJ IDEA, VS Code, etc.)

### Configuración Inicial
```bash
# 1. Fork del repositorio
# Ve a https://github.com/tu-usuario/gestoteam y haz fork

# 2. Clonar tu fork
git clone https://github.com/tu-usuario/gestoteam.git
cd gestoteam

# 3. Añadir upstream
git remote add upstream https://github.com/original/gestoteam.git

# 4. Instalar dependencias
cd gestoteam-backend && ./gradlew build
cd ../gestoteam-frontend && npm install
cd ../gestoteam-desktop && npm install
```

## 🔄 Flujo de Trabajo

### 1. Sincronizar con Upstream
```bash
# Actualizar tu fork
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

### 2. Crear Rama de Feature
```bash
# Crear nueva rama
git checkout -b feature/nombre-de-la-funcionalidad

# O para bugs
git checkout -b fix/nombre-del-bug
```

### 3. Desarrollar Cambios
- **Backend**: Desarrollar en `gestoteam-backend/`
- **Frontend**: Desarrollar en `gestoteam-frontend/`
- **Desktop**: Desarrollar en `gestoteam-desktop/`

### 4. Commit y Push
```bash
# Añadir cambios
git add .

# Commit con mensaje descriptivo
git commit -m "feat: añadir nueva funcionalidad de estadísticas"

# Push a tu fork
git push origin feature/nombre-de-la-funcionalidad
```

### 5. Crear Pull Request
1. Ve a tu fork en GitHub
2. Haz clic en "New Pull Request"
3. Selecciona la rama de tu feature
4. Completa la plantilla del PR
5. Envía el PR

## 📝 Convenciones de Código

### Java (Backend)
```java
// Nombres de clases: PascalCase
public class PlayerService {
    
    // Nombres de métodos: camelCase
    public Player createPlayer(PlayerRequest request) {
        // Implementación
    }
    
    // Constantes: UPPER_SNAKE_CASE
    private static final String DEFAULT_POSITION = "Delantero";
}

// Anotaciones en líneas separadas
@RestController
@RequestMapping("/api/players")
public class PlayerController {
    // ...
}
```

### JavaScript/Vue.js (Frontend)
```javascript
// Nombres de archivos: kebab-case
// player-form.vue

// Nombres de componentes: PascalCase
export default {
  name: 'PlayerForm',
  
  // Props en camelCase
  props: {
    playerData: {
      type: Object,
      required: true
    }
  },
  
  // Métodos en camelCase
  methods: {
    handleSubmit() {
      // Implementación
    }
  }
}
```

### Electron (Desktop)
```javascript
// Nombres de archivos: kebab-case
// main.js

// Constantes en UPPER_SNAKE_CASE
const BACKEND_PORT = 8081;
const APP_NAME = 'GestoTeam Desktop';

// Funciones en camelCase
function createWindow() {
  // Implementación
}
```

## 🧪 Testing

### Backend Tests
```bash
# Ejecutar todos los tests
cd gestoteam-backend
./gradlew test

# Ejecutar tests específicos
./gradlew test --tests PlayerServiceTest

# Ejecutar tests con coverage
./gradlew test jacocoTestReport
```

### Frontend Tests (Futuro)
```bash
# Ejecutar tests
cd gestoteam-frontend
npm run test

# Ejecutar tests con coverage
npm run test:coverage
```

### Desktop Tests (Futuro)
```bash
# Ejecutar tests
cd gestoteam-desktop
npm run test
```

## 📋 Plantilla de Pull Request

### Título
```
feat: añadir nueva funcionalidad de estadísticas
fix: corregir bug en gestión de jugadores
docs: actualizar guía de usuario
```

### Descripción
```markdown
## 🎯 Descripción
Breve descripción de los cambios realizados.

## 🔧 Cambios Realizados
- [ ] Nueva funcionalidad X
- [ ] Corrección de bug Y
- [ ] Mejora en componente Z

## 🧪 Testing
- [ ] Tests unitarios pasan
- [ ] Tests de integración pasan
- [ ] Aplicación funciona correctamente

## 📸 Screenshots (si aplica)
Añadir capturas de pantalla de los cambios.

## 🔍 Checklist
- [ ] Código sigue las convenciones del proyecto
- [ ] Documentación actualizada
- [ ] Tests añadidos/actualizados
- [ ] No hay warnings o errores de compilación
```

## 🐛 Reportar Bugs

### Plantilla de Bug Report
```markdown
## 🐛 Descripción del Bug
Descripción clara y concisa del bug.

## 🔄 Pasos para Reproducir
1. Ir a '...'
2. Hacer clic en '...'
3. Desplazarse hacia abajo hasta '...'
4. Ver error

## ✅ Comportamiento Esperado
Descripción de lo que debería pasar.

## 📱 Información del Sistema
- **OS**: Windows 10/11
- **Versión**: 1.0.0
- **Navegador**: Chrome 120.0
- **Backend**: Puerto 8081

## 📸 Screenshots
Añadir capturas de pantalla si es posible.

## 📝 Logs
Añadir logs relevantes del error.
```

## 💡 Sugerir Funcionalidades

### Plantilla de Feature Request
```markdown
## 💡 Descripción de la Funcionalidad
Descripción clara de la funcionalidad deseada.

## 🎯 Problema que Resuelve
Explicar qué problema resuelve esta funcionalidad.

## 🔧 Solución Propuesta
Descripción de la solución propuesta.

## 🔄 Alternativas Consideradas
Otras soluciones que se consideraron.

## 📱 Información Adicional
Cualquier información adicional relevante.
```

## 📚 Documentación

### Actualizar Documentación
- **README.md**: Cambios generales del proyecto
- **docs/**: Documentación específica
- **CHANGELOG.md**: Nuevas versiones y cambios
- **Comentarios en código**: Explicar lógica compleja

### Convenciones de Documentación
```markdown
# Títulos principales
## Subtítulos
### Sub-subtítulos

**Texto importante** en negrita
*Texto en cursiva* para énfasis

- Lista con viñetas
- Otro elemento

`código inline` para comandos o código

```bash
# Bloques de código para comandos
npm install
```

```java
// Bloques de código para Java
public class Example {
    // código
}
```
```

## 🔒 Seguridad

### Reportar Vulnerabilidades
Si encuentras una vulnerabilidad de seguridad:

1. **NO** crear un issue público
2. **NO** crear un PR público
3. **Email** a: security@gestoteam.com
4. **Descripción detallada** del problema
5. **Pasos para reproducir**
6. **Impacto potencial**

### Proceso de Respuesta
- **Acknowledgment**: 24-48 horas
- **Initial Assessment**: 1 semana
- **Fix Development**: 2-4 semanas
- **Security Release**: Coordinado

## 🏆 Reconocimiento

### Contribuidores Destacados
- **Gold Contributors**: 10+ PRs aprobados
- **Silver Contributors**: 5+ PRs aprobados
- **Bronze Contributors**: 1+ PR aprobado

### Criterios de Reconocimiento
- **Calidad del código**
- **Documentación**
- **Tests**
- **Revisión de PRs**
- **Ayuda a otros contribuidores**

## 📞 Contacto y Soporte

### Canales de Comunicación
- **Issues**: Para bugs y feature requests
- **Discussions**: Para preguntas generales
- **Email**: contribuciones@gestoteam.com

### Horarios de Respuesta
- **Issues**: 24-48 horas
- **PRs**: 1-3 días
- **Email**: 24-72 horas

---

## 🎯 **Resumen para Contribuidores**

**GestoTeam** es un proyecto **profesional y acogedor** que valora:

✅ **Código limpio** y bien documentado  
✅ **Tests completos** y funcionales  
✅ **Comunicación clara** y respetuosa  
✅ **Mejoras incrementales** y bien pensadas  
✅ **Colaboración** y ayuda mutua  

**¡Tu contribución hace que GestoTeam sea mejor cada día!** 🚀

---

**GestoTeam AI** - *Construyendo el futuro de la gestión deportiva*
