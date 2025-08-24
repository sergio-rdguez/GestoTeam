<template>
  <div class="my-resources-page">
    <PageHeader title="Mis Recursos" :show-back-button="true" @back="goBack" />

    <div class="resources-grid">
      <!-- Mis Ejercicios -->
      <BaseCard class="resource-card" @click="goToExercises">
        <div class="card-content">
          <div class="card-icon">
            <i class="fas fa-dumbbell"></i>
          </div>
          <div class="card-info">
            <h3>Mis Ejercicios</h3>
            <p>Gestiona tu biblioteca personal de ejercicios tácticos</p>
          </div>
          <div class="card-arrow">
            <i class="fas fa-arrow-right"></i>
          </div>
        </div>
      </BaseCard>

      <!-- Pizarra Táctica -->
      <BaseCard class="resource-card" @click="openTacticalBoard">
        <div class="card-content">
          <div class="card-icon">
            <i class="fas fa-chalkboard"></i>
          </div>
          <div class="card-info">
            <h3>Pizarra Táctica</h3>
            <p>Herramienta avanzada para crear diagramas tácticos</p>
          </div>
          <div class="card-arrow">
            <i class="fas fa-arrow-right"></i>
          </div>
        </div>
      </BaseCard>

    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import PageHeader from '@/components/layout/PageHeader.vue';
import BaseCard from '@/components/base/BaseCard.vue';

const router = useRouter();

const goBack = () => {
  router.push('/dashboard');
};

const goToExercises = () => {
  router.push('/my-resources/exercises');
};

const openTacticalBoard = () => {
  // Abrir la pizarra táctica en una nueva ventana con parámetro de versión para forzar recarga
  window.open('/tactical-board.html?v=' + Date.now(), 'TacticalBoard', 
    'width=1200,height=800,scrollbars=yes,resizable=yes');
};


</script>

<style scoped>
.my-resources-page {
  max-width: 1200px;
  margin: 0 auto;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: var(--spacing-6);
  margin-top: var(--spacing-6);
}

.resource-card {
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid var(--color-border);
}

.resource-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary);
}

.card-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  padding: var(--spacing-6);
}

.card-icon {
  width: 64px;
  height: 64px;
  background: var(--color-primary);
  color: var(--color-white);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-2xl);
  flex-shrink: 0;
}

.card-info {
  flex: 1;
}

.card-info h3 {
  margin: 0 0 var(--spacing-2) 0;
  color: var(--color-text-primary);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
}

.card-info p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
  line-height: var(--line-height-normal);
}

.card-arrow {
  color: var(--color-text-secondary);
  font-size: var(--font-size-lg);
  transition: all 0.2s ease;
}

.resource-card:hover .card-arrow {
  color: var(--color-primary);
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .resources-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
  
  .card-content {
    padding: var(--spacing-4);
  }
  
  .card-icon {
    width: 56px;
    height: 56px;
    font-size: var(--font-size-xl);
  }
}
</style>
