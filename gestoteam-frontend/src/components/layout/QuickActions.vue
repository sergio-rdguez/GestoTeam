<template>
  <div class="quick-actions" :class="{ 'expanded': isExpanded }">
    <button 
      @click="toggleExpanded" 
      class="quick-actions-toggle"
      :title="isExpanded ? 'Ocultar acciones rápidas' : 'Mostrar acciones rápidas'"
    >
      <i class="fas fa-bolt"></i>
      <span v-if="isExpanded" class="toggle-text">Acciones Rápidas</span>
    </button>
    
    <div class="quick-actions-content" v-if="isExpanded">
      <div class="quick-actions-section">
        <h4 class="section-title">Equipos</h4>
        <div class="action-grid">
          <router-link 
            to="/teams/new" 
            class="quick-action-item"
            title="Crear nuevo equipo"
          >
            <i class="fas fa-plus-circle"></i>
            <span>Nuevo Equipo</span>
          </router-link>
          
          <router-link 
            to="/teams" 
            class="quick-action-item"
            title="Ver todos los equipos"
          >
            <i class="fas fa-users"></i>
            <span>Ver Equipos</span>
          </router-link>
        </div>
      </div>
      
      <div class="quick-actions-section">
        <h4 class="section-title">Entrenamiento</h4>
        <div class="action-grid">
          <router-link 
            to="/my-resources/exercises/new" 
            class="quick-action-item"
            title="Crear nuevo ejercicio"
          >
            <i class="fas fa-dumbbell"></i>
            <span>Nuevo Ejercicio</span>
          </router-link>
          
          <button 
            @click="openTacticalBoard" 
            class="quick-action-item"
            title="Abrir pizarra táctica"
          >
            <i class="fas fa-chalkboard"></i>
            <span>Pizarra Táctica</span>
          </button>
        </div>
      </div>
      
      <div class="quick-actions-section">
        <h4 class="section-title">Accesos Directos</h4>
        <div class="action-grid">
          <router-link 
            to="/my-resources" 
            class="quick-action-item"
            title="Centro de recursos"
          >
            <i class="fas fa-folder-open"></i>
            <span>Recursos</span>
          </router-link>
          
          <router-link 
            to="/settings" 
            class="quick-action-item"
            title="Configuración"
          >
            <i class="fas fa-cog"></i>
            <span>Configuración</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const isExpanded = ref(false);

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value;
};

const openTacticalBoard = () => {
  window.open('/tactical-board.html?v=' + Date.now(), 'TacticalBoard', 
    'width=1200,height=800,scrollbars=yes,resizable=yes');
};
</script>

<style scoped>
.quick-actions {
  position: fixed;
  right: var(--spacing-4);
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.quick-actions-toggle {
  background-color: var(--color-primary);
  color: var(--color-white);
  border: none;
  border-radius: 50px;
  padding: var(--spacing-3);
  cursor: pointer;
  box-shadow: var(--shadow-lg);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  min-width: 56px;
  height: 56px;
  justify-content: center;
}

.quick-actions-toggle:hover {
  background-color: var(--color-primary-dark);
  transform: scale(1.05);
  box-shadow: var(--shadow-xl);
}

.quick-actions-toggle i {
  font-size: var(--font-size-lg);
}

.toggle-text {
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
  overflow: hidden;
  max-width: 0;
  transition: max-width 0.3s ease;
}

.quick-actions.expanded .toggle-text {
  max-width: 200px;
}

.quick-actions-content {
  background-color: var(--color-white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-xl);
  padding: var(--spacing-4);
  margin-top: var(--spacing-3);
  min-width: 280px;
  border: 1px solid var(--color-border);
}

.quick-actions-section {
  margin-bottom: var(--spacing-4);
}

.quick-actions-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-2) 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.action-grid {
  display: grid;
  gap: var(--spacing-2);
}

.quick-action-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  padding: var(--spacing-2) var(--spacing-3);
  border-radius: var(--border-radius-md);
  text-decoration: none;
  color: var(--color-text);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.quick-action-item:hover {
  background-color: var(--color-background-light);
  border-color: var(--color-primary-light);
  color: var(--color-primary);
  transform: translateX(4px);
}

.quick-action-item i {
  font-size: var(--font-size-base);
  width: 20px;
  text-align: center;
  color: var(--color-primary);
}

.quick-action-item span {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

/* Responsive */
@media (max-width: 1024px) {
  .quick-actions {
    right: var(--spacing-3);
  }
  
  .quick-actions-content {
    min-width: 260px;
  }
}

@media (max-width: 768px) {
  .quick-actions {
    right: var(--spacing-2);
    bottom: var(--spacing-4);
    top: auto;
    transform: none;
  }
  
  .quick-actions-toggle {
    min-width: 48px;
    height: 48px;
    padding: var(--spacing-2);
  }
  
  .quick-actions-content {
    position: absolute;
    bottom: 100%;
    right: 0;
    margin-bottom: var(--spacing-2);
    min-width: 240px;
    padding: var(--spacing-3);
  }
  
  .toggle-text {
    display: none;
  }
}

@media (max-width: 480px) {
  .quick-actions {
    right: var(--spacing-2);
    bottom: var(--spacing-3);
  }
  
  .quick-actions-content {
    min-width: 220px;
    padding: var(--spacing-2);
  }
  
  .quick-action-item {
    padding: var(--spacing-2);
  }
  
  .quick-action-item span {
    font-size: var(--font-size-xs);
  }
}
</style>
