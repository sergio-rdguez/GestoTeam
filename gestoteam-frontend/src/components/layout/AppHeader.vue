<template>
  <header class="app-header">
    <div class="header-content">
      <router-link to="/dashboard" class="logo-container">
        <img src="@/assets/logo.png" alt="GestoTeam Logo" class="logo">
        <h1 class="app-title">GestoTeam</h1>
      </router-link>
      <div class="header-actions">
        <router-link to="/my-resources" class="action-button" aria-label="Mis Recursos">
          <i class="fas fa-folder-open"></i>
        </router-link>
        <button @click="openTacticalBoard" class="action-button" aria-label="Pizarra Táctica">
          <i class="fas fa-chalkboard"></i>
        </button>
        <router-link to="/teams" class="action-button" aria-label="Mis Equipos">
          <i class="fas fa-users"></i>
        </router-link>
        <router-link to="/settings" class="action-button" aria-label="Configuración">
          <i class="fas fa-cog"></i>
        </router-link>

        <button @click="handleLogout" class="logout-button">
          <i class="fa-solid fa-right-from-bracket"></i>
          <span class="logout-text">Cerrar Sesión</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>

import authService from '@/services/auth';

const handleLogout = () => {
  authService.logout();
};

const openTacticalBoard = () => {
  // Abrir la pizarra táctica en una nueva ventana con parámetro de versión para forzar recarga
  window.open('/tactical-board.html?v=' + Date.now(), 'TacticalBoard', 
    'width=1200,height=800,scrollbars=yes,resizable=yes');
};
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
  height: 65px;
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
.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
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
}
.action-button {
  width: 40px;
  height: 40px;
  font-size: var(--font-size-lg);
}
.logout-button {
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}
.action-button:hover, .logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
@media (max-width: 768px) {
  .app-title, .logout-text {
    display: none;
  }
  .logout-button {
    width: 40px;
    height: 40px;
    padding: 0;
    justify-content: center;
    font-size: var(--font-size-lg);
  }
}
</style>