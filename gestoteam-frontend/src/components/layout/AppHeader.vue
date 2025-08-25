<template>
  <header class="app-header">
    <div class="header-content">
      <router-link to="/dashboard" class="logo-container">
        <img src="@/assets/logo.png" alt="GestoTeam Logo" class="logo">
        <h1 class="app-title">GestoTeam</h1>
      </router-link>
      
      <!-- Menú hamburguesa para móviles -->
      <button 
        @click="toggleMobileMenu" 
        class="mobile-menu-toggle"
        :class="{ active: isMobileMenuOpen }"
        aria-label="Abrir menú de navegación"
      >
        <span></span>
        <span></span>
        <span></span>
      </button>
      
      <nav class="main-navigation" :class="{ 'mobile-open': isMobileMenuOpen }">
        <router-link 
          to="/dashboard" 
          class="nav-item" 
          :class="{ active: $route.name === 'Dashboard' }"
          title="Dashboard - Vista general de tu actividad"
          @click="closeMobileMenu"
        >
          <i class="fas fa-tachometer-alt"></i>
          <span class="nav-text">Dashboard</span>
        </router-link>
        
        <router-link 
          to="/teams" 
          class="nav-item" 
          :class="{ active: $route.path.startsWith('/teams') }"
          title="Equipos - Gestiona tus equipos y jugadores"
          @click="closeMobileMenu"
        >
          <i class="fas fa-users"></i>
          <span class="nav-text">Equipos</span>
        </router-link>
        
        <router-link 
          to="/my-resources" 
          class="nav-item" 
          :class="{ active: $route.path.startsWith('/my-resources') }"
          title="Recursos - Ejercicios y materiales de entrenamiento"
          @click="closeMobileMenu"
        >
          <i class="fas fa-folder-open"></i>
          <span class="nav-text">Recursos</span>
        </router-link>
        
        <button 
          @click="openTacticalBoard" 
          class="nav-item action-button" 
          title="Pizarra Táctica - Herramienta para planificar tácticas"
        >
          <i class="fas fa-chalkboard"></i>
          <span class="nav-text">Pizarra</span>
        </button>
      </nav>

      <div class="header-actions">
        <router-link 
          to="/settings" 
          class="action-button settings-btn" 
          :class="{ active: $route.name === 'UserSettings' }"
          title="Configuración de usuario"
        >
          <i class="fas fa-cog"></i>
          <span class="action-text">Config</span>
        </router-link>

        <button @click="handleLogout" class="logout-button" title="Cerrar sesión">
          <i class="fa-solid fa-right-from-bracket"></i>
          <span class="logout-text">Cerrar Sesión</span>
        </button>
      </div>
    </div>
    
    <!-- Overlay para cerrar menú móvil -->
    <div 
      v-if="isMobileMenuOpen" 
      class="mobile-overlay" 
      @click="closeMobileMenu"
    ></div>
  </header>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import authService from '@/services/auth';

const route = useRoute();
const isMobileMenuOpen = ref(false);

const handleLogout = () => {
  authService.logout();
};

const openTacticalBoard = () => {
  // Abrir la pizarra táctica en una nueva ventana con parámetro de versión para forzar recarga
  window.open('/tactical-board.html?v=' + Date.now(), 'TacticalBoard', 
    'width=1200,height=800,scrollbars=yes,resizable=yes');
};

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false;
};

// Cerrar menú móvil al cambiar de ruta
watch(() => route.path, () => {
  closeMobileMenu();
});
</script>

<style scoped>
.app-header {
  background-color: var(--color-primary);
  color: var(--color-white);
  padding: 0 var(--spacing-5);
  box-shadow: var(--shadow-md);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 70px;
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.logo-container {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
  flex-shrink: 0;
}

.logo {
  height: 36px;
  width: auto;
  margin-right: var(--spacing-3);
  transition: transform 0.3s ease;
}

.logo-container:hover .logo {
  transform: rotate(-10deg) scale(1.1);
}

.app-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  font-family: var(--font-family-headings);
  margin: 0;
}

/* Menú hamburguesa */
.mobile-menu-toggle {
  display: none;
  flex-direction: column;
  justify-content: space-around;
  width: 30px;
  height: 30px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 1001;
}

.mobile-menu-toggle span {
  width: 100%;
  height: 3px;
  background-color: var(--color-white);
  border-radius: 2px;
  transition: all 0.3s ease;
}

.mobile-menu-toggle.active span:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.mobile-menu-toggle.active span:nth-child(2) {
  opacity: 0;
}

.mobile-menu-toggle.active span:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}

.main-navigation {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  flex: 1;
  justify-content: center;
  margin: 0 var(--spacing-6);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  color: var(--color-white);
  text-decoration: none;
  padding: var(--spacing-2) var(--spacing-3);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  font-weight: var(--font-weight-medium);
  position: relative;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.nav-item.active {
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background-color: var(--color-white);
  border-radius: 2px;
}

.nav-item i {
  font-size: var(--font-size-lg);
  width: 20px;
  text-align: center;
}

.nav-text {
  font-size: var(--font-size-sm);
  white-space: nowrap;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  flex-shrink: 0;
}

.action-button, .logout-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-white);
  background-color: transparent;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-decoration: none;
}

.action-button {
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.action-button:hover, .logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.action-button.active {
  background-color: rgba(255, 255, 255, 0.2);
}

.logout-button {
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.action-text {
  font-size: var(--font-size-xs);
  white-space: nowrap;
}

/* Overlay móvil */
.mobile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

/* Responsive design */
@media (max-width: 1024px) {
  .main-navigation {
    margin: 0 var(--spacing-3);
  }
  
  .nav-text {
    font-size: var(--font-size-xs);
  }
}

@media (max-width: 768px) {
  .app-header {
    height: 60px;
    padding: 0 var(--spacing-3);
  }
  
  .mobile-menu-toggle {
    display: flex;
  }
  
  .main-navigation {
    position: fixed;
    top: 60px;
    left: 0;
    width: 100%;
    height: calc(100vh - 60px);
    background-color: var(--color-primary);
    flex-direction: column;
    justify-content: flex-start;
    align-items: stretch;
    margin: 0;
    padding: var(--spacing-4);
    gap: var(--spacing-2);
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    z-index: 1000;
  }
  
  .main-navigation.mobile-open {
    transform: translateX(0);
  }
  
  .nav-item {
    padding: var(--spacing-3);
    border-radius: var(--border-radius-lg);
    justify-content: flex-start;
    font-size: var(--font-size-base);
  }
  
  .nav-text {
    display: block;
    font-size: var(--font-size-base);
  }
  
  .app-title, .logout-text, .action-text {
    display: none;
  }
  
  .logout-button {
    width: 44px;
    height: 44px;
    padding: 0;
    justify-content: center;
  }
  
  .action-button {
    width: 44px;
    height: 44px;
    padding: 0;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .header-content {
    justify-content: space-between;
  }
  
  .logo-container {
    flex: 1;
  }
}
</style>