<template>
  <header class="app-header">
    <div class="header-content">
      <router-link to="/teams" class="logo-container">
        <img src="@/assets/logo.png" alt="GestoTeam Logo" class="logo">
        <h1 class="app-title">GestoTeam</h1>
      </router-link>
      <div class="header-actions">
        <router-link to="/teams" class="action-button" aria-label="Inicio">
          <i class="fas fa-home"></i>
        </router-link>
        <router-link to="/settings" class="action-button" aria-label="Configuración">
          <i class="fas fa-cog"></i>
        </router-link>
        <button @click="logout" class="logout-button">
          <i class="fa-solid fa-right-from-bracket"></i>
          <span class="logout-text">Cerrar Sesión</span>
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { defineEmits } from 'vue';

const emit = defineEmits(['logout']);

const logout = () => {
  emit('logout');
};
</script>

<style scoped>
.app-header {
  background-color: var(--color-background-white);
  border-bottom: 1px solid var(--color-border);
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
  max-width: 1280px;
  margin: 0 auto;
}

.logo-container {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
}

.logo {
  height: 40px;
  width: auto;
  margin-right: var(--spacing-3);
  transition: transform 0.3s ease;
}

.logo-container:hover .logo {
  transform: rotate(-10deg) scale(1.1);
}

.app-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  font-family: 'Montserrat', sans-serif;
  color: var(--color-text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.action-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  color: var(--color-text-secondary);
  background-color: transparent;
  border: none;
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-lg);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.action-button:hover {
  color: var(--color-primary);
  background-color: var(--color-primary-light);
}

.logout-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  background-color: transparent;
  color: var(--color-text-secondary);
  border: 1px solid transparent;
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-2) var(--spacing-3);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.logout-button:hover {
  color: var(--color-primary);
  background-color: var(--color-primary-light);
  border-color: var(--color-primary-light);
}

@media (max-width: 768px) {
  .app-title {
    display: none;
  }

  .logout-text {
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