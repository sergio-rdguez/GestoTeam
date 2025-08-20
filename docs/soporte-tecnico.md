# 🛠️ Guía de Soporte Técnico - GestoTeam Desktop

## 📋 **Información del Cliente**

**Aplicación:** GestoTeam Desktop v0.0.1  
**Fecha de Instalación:** [Fecha de instalación]  
**Cliente:** [Nombre del cliente]  
**Soporte:** [Tu información de contacto]

---

## 🚨 **Problemas Comunes y Soluciones**

### **1. La aplicación no inicia**

**Síntomas:**
- La aplicación no se abre al hacer doble clic
- Aparece un mensaje de error al iniciar

**Soluciones:**
1. **Verificar Java instalado:**
   - Abrir CMD y escribir: `java -version`
   - Si no está instalado, descargar Java 17 desde: https://adoptium.net/

2. **Reiniciar la aplicación:**
   - Cerrar completamente la aplicación
   - Esperar 10 segundos
   - Volver a abrir

3. **Verificar permisos:**
   - Hacer clic derecho en el icono
   - Seleccionar "Ejecutar como administrador"

### **2. Error: "El backend se ha cerrado de forma inesperada"**

**Síntomas:**
- Mensaje de error del backend
- La aplicación se cierra automáticamente

**Soluciones:**
1. **Generar reporte automático:**
   - La aplicación genera un reporte automáticamente
   - Enviar el archivo de reporte al soporte técnico

2. **Reiniciar manualmente:**
   - Abrir la aplicación nuevamente
   - Usar el botón "Reiniciar Backend" en el panel de estado

3. **Verificar puerto 8081:**
   - Abrir CMD y escribir: `netstat -an | findstr 8081`
   - Si hay otro proceso usando el puerto, cerrarlo

### **3. La aplicación funciona lento**

**Síntomas:**
- Respuesta lenta al hacer clic
- Carga lenta de datos
- Interfaz congelada

**Soluciones:**
1. **Verificar recursos del sistema:**
   - Abrir Administrador de tareas
   - Verificar uso de CPU y memoria
   - Cerrar aplicaciones innecesarias

2. **Reiniciar la aplicación:**
   - Cerrar completamente
   - Esperar 30 segundos
   - Volver a abrir

3. **Verificar espacio en disco:**
   - Asegurar al menos 1GB libre

---

## 📊 **Generación de Reportes**

### **Reporte Automático**
La aplicación genera reportes automáticamente cuando:
- Ocurre un error del backend
- La aplicación se cierra inesperadamente
- Hay problemas de conectividad

### **Reporte Manual**
Para generar un reporte manual:
1. Abrir la aplicación
2. Ir al panel "Estado del Sistema"
3. Hacer clic en "Generar Reporte"
4. El archivo se guarda en: `%APPDATA%\GestoTeam Desktop\reports\`

---

## 📁 **Ubicación de Archivos Importantes**

### **Logs de la Aplicación**
```
%APPDATA%\GestoTeam Desktop\logs\
├── app.log          # Log general de la aplicación
└── error.log        # Log de errores críticos
```

### **Reportes del Sistema**
```
%APPDATA%\GestoTeam Desktop\reports\
└── report-[timestamp].json  # Reportes automáticos y manuales
```

### **Base de Datos**
```
%APPDATA%\GestoTeam Desktop\data\
└── gestoteamdb.mv.db  # Base de datos local
```

---

## 🔧 **Comandos de Diagnóstico**

### **Verificar Estado del Backend**
```cmd
netstat -an | findstr 8081
```

### **Verificar Procesos Java**
```cmd
tasklist | findstr java
```

### **Verificar Logs en Tiempo Real**
```cmd
type "%APPDATA%\GestoTeam Desktop\logs\app.log"
```

---

## 📞 **Contacto de Soporte**

**Cuando contactar al soporte técnico:**

1. **Problemas críticos:**
   - La aplicación no inicia
   - Pérdida de datos
   - Errores persistentes del backend

2. **Información necesaria:**
   - Descripción del problema
   - Pasos para reproducir
   - Archivo de reporte del sistema
   - Capturas de pantalla del error

3. **Canales de contacto:**
   - Email: [tu-email@dominio.com]
   - Teléfono: [tu-número]
   - Horario: [horario de atención]

---

## 🚀 **Mantenimiento Preventivo**

### **Recomendaciones Diarias**
- Cerrar la aplicación correctamente al terminar
- No forzar el cierre con el Administrador de tareas
- Mantener al menos 1GB de espacio libre en disco

### **Recomendaciones Semanales**
- Reiniciar la aplicación una vez por semana
- Verificar que no hay actualizaciones pendientes
- Revisar el panel de estado del sistema

### **Recomendaciones Mensuales**
- Verificar espacio en disco
- Revisar logs de errores
- Generar reporte manual del sistema

---

## 📝 **Registro de Incidencias**

**Fecha:** ___________  
**Problema:** ___________  
**Solución Aplicada:** ___________  
**Tiempo de Resolución:** ___________  
**Observaciones:** ___________  

---

## ✅ **Checklist de Verificación**

- [ ] Java 17 instalado y funcionando
- [ ] Puerto 8081 libre
- [ ] Espacio suficiente en disco
- [ ] Permisos de administrador verificados
- [ ] Logs accesibles
- [ ] Reportes generándose correctamente
- [ ] Panel de estado funcionando
- [ ] Notificaciones del sistema visibles

---

**Última actualización:** [Fecha]  
**Versión del documento:** 1.0  
**Responsable:** [Tu nombre]
