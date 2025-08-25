<template>
  <nav class="breadcrumbs" v-if="breadcrumbs.length > 1">
    <ol class="breadcrumb-list">
      <li 
        v-for="(breadcrumb, index) in breadcrumbs" 
        :key="index"
        class="breadcrumb-item"
      >
        <router-link 
          v-if="breadcrumb.path && index < breadcrumbs.length - 1"
          :to="breadcrumb.path"
          class="breadcrumb-link"
        >
          {{ breadcrumb.name }}
        </router-link>
        <span 
          v-else 
          class="breadcrumb-current"
          :title="breadcrumb.description"
        >
          {{ breadcrumb.name }}
        </span>
        
        <span 
          v-if="index < breadcrumbs.length - 1" 
          class="breadcrumb-separator"
        >
          <i class="fas fa-chevron-right"></i>
        </span>
      </li>
    </ol>
  </nav>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const breadcrumbs = computed(() => {
  const paths = route.path.split('/').filter(Boolean);
  const result = [{ name: 'Inicio', path: '/dashboard', description: 'Volver al dashboard' }];
  
  let currentPath = '';
  
  paths.forEach((path, index) => {
    currentPath += `/${path}`;
    
    // Mapear rutas a nombres legibles
    let name = path;
    let description = '';
    
    switch (path) {
      case 'teams':
        name = 'Equipos';
        description = 'Gestionar equipos';
        break;
      case 'players':
        name = 'Jugadores';
        description = 'Gestionar jugadores del equipo';
        break;
      case 'matches':
        name = 'Partidos';
        description = 'Gestionar partidos del equipo';
        break;
      case 'opponents':
        name = 'Rivales';
        description = 'Gestionar equipos rivales';
        break;
      case 'trainings':
        name = 'Entrenamientos';
        description = 'Gestionar sesiones de entrenamiento';
        break;
      case 'my-resources':
        name = 'Recursos';
        description = 'Centro de recursos y ejercicios';
        break;
      case 'exercises':
        name = 'Ejercicios';
        description = 'Gestionar ejercicios de entrenamiento';
        break;
      case 'settings':
        name = 'Configuración';
        description = 'Configuración de usuario';
        break;
      case 'new':
        name = 'Nuevo';
        description = 'Crear nuevo elemento';
        break;
      case 'edit':
        name = 'Editar';
        description = 'Modificar elemento existente';
        break;
      case 'stats':
        name = 'Estadísticas';
        description = 'Ver estadísticas del partido';
        break;
      case 'attendance':
        name = 'Asistencia';
        description = 'Gestionar asistencia al entrenamiento';
        break;
      default:
        // Si es un ID, intentar obtener el nombre del contexto
        if (route.params.teamId && path === route.params.teamId) {
          name = 'Detalles del Equipo';
          description = 'Información del equipo';
        } else if (route.params.playerId && path === route.params.playerId) {
          name = 'Detalles del Jugador';
          description = 'Información del jugador';
        } else if (route.params.matchId && path === route.params.matchId) {
          name = 'Detalles del Partido';
          description = 'Información del partido';
        } else if (route.params.opponentId && path === route.params.opponentId) {
          name = 'Detalles del Rival';
          description = 'Información del equipo rival';
        } else if (route.params.trainingId && path === route.params.trainingId) {
          name = 'Detalles del Entrenamiento';
          description = 'Información del entrenamiento';
        } else if (route.params.id && path === route.params.id) {
          name = 'Detalles';
          description = 'Información del elemento';
        }
        break;
    }
    
    // Solo agregar breadcrumb si no es el último y tiene un nombre válido
    if (index < paths.length - 1 || name !== path) {
      result.push({
        name,
        path: index < paths.length - 1 ? currentPath : null,
        description
      });
    }
  });
  
  return result;
});
</script>

<style scoped>
.breadcrumbs {
  background-color: var(--color-background);
  border-bottom: 1px solid var(--color-border);
  padding: var(--spacing-3) var(--spacing-5);
  margin-bottom: var(--spacing-4);
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: var(--spacing-2);
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.breadcrumb-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  transition: color 0.2s ease;
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--border-radius-sm);
}

.breadcrumb-link:hover {
  color: var(--color-primary-dark);
  background-color: var(--color-primary-light);
}

.breadcrumb-current {
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-1) var(--spacing-2);
}

.breadcrumb-separator {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-xs);
  display: flex;
  align-items: center;
}

/* Responsive */
@media (max-width: 768px) {
  .breadcrumbs {
    padding: var(--spacing-2) var(--spacing-3);
    margin-bottom: var(--spacing-3);
  }
  
  .breadcrumb-list {
    flex-wrap: wrap;
    gap: var(--spacing-1);
  }
  
  .breadcrumb-item {
    gap: var(--spacing-1);
  }
  
  .breadcrumb-link,
  .breadcrumb-current {
    padding: var(--spacing-1);
    font-size: var(--font-size-sm);
  }
}

@media (max-width: 480px) {
  .breadcrumbs {
    padding: var(--spacing-2);
  }
  
  .breadcrumb-list {
    justify-content: flex-start;
  }
}
</style>
