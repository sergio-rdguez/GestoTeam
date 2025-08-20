# NSIS Script para GestoTeam Desktop
# Configuración personalizada del instalador

!macro customInstall
  # Crear directorio de datos de la aplicación
  CreateDirectory "$LOCALAPPDATA\GestoTeam"
  CreateDirectory "$LOCALAPPDATA\GestoTeam\logs"
  CreateDirectory "$LOCALAPPDATA\GestoTeam\data"
  
  # Crear directorio de configuración
  CreateDirectory "$APPDATA\GestoTeam"
  
  # Configurar permisos de escritura
  AccessControl::GrantOnFile "$LOCALAPPDATA\GestoTeam" "(BU)" "FullAccess"
  AccessControl::GrantOnFile "$APPDATA\GestoTeam" "(BU)" "FullAccess"
!macroend

!macro customUnInstall
  # Limpiar directorios de datos (opcional)
  RMDir /r "$LOCALAPPDATA\GestoTeam\logs"
  RMDir /r "$LOCALAPPDATA\GestoTeam\data"
  RMDir "$LOCALAPPDATA\GestoTeam"
  RMDir /r "$APPDATA\GestoTeam"
!macroend

# Configuración de la interfaz del instalador
!define MUI_WELCOMEPAGE_TITLE "Bienvenido a GestoTeam Desktop"
!define MUI_WELCOMEPAGE_TEXT "Este asistente te guiará a través de la instalación de GestoTeam Desktop v0.0.1.$\r$\n$\r$\nGestoTeam es una aplicación completa para la gestión de equipos deportivos."

!define MUI_FINISHPAGE_TITLE "Instalación Completada"
!define MUI_FINISHPAGE_TEXT "GestoTeam Desktop ha sido instalado exitosamente en tu sistema.$\r$\n$\r$\nLa aplicación se iniciará automáticamente."

# Configuración de la página de licencia
!define MUI_LICENSEPAGE_TEXT_TOP "Por favor, revisa los términos de la licencia antes de continuar con la instalación."
!define MUI_LICENSEPAGE_TEXT_BOTTOM "Si aceptas los términos de la licencia, haz clic en 'Acepto' para continuar."

# Configuración de la página de directorio
!define MUI_DIRECTORYPAGE_TEXT_TOP "Selecciona el directorio donde deseas instalar GestoTeam Desktop."
!define MUI_DIRECTORYPAGE_TEXT_BOTTOM "Se recomienda usar el directorio por defecto."

# Configuración de la página de componentes
!define MUI_COMPONENTSPAGE_TEXT_TOP "Selecciona los componentes que deseas instalar."
!define MUI_COMPONENTSPAGE_TEXT_BOTTOM "Todos los componentes son necesarios para el funcionamiento correcto de la aplicación."
