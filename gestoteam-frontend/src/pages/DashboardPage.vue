<template>
  <div class="dashboard-page">
    <PageHeader title="Panel Principal" />

    <div class="dashboard-grid">
      <div class="dashboard-section" @click="goToResources">
        <div class="section-icon">
          <i class="fas fa-folder-open"></i>
        </div>
        <h3 class="section-title">Mis Recursos</h3>
        <p class="section-description">
          Gestiona tu biblioteca de ejercicios tácticos
        </p>
      </div>

      <div class="dashboard-section" @click="goToTeams">
        <div class="section-icon">
          <i class="fas fa-users"></i>
        </div>
        <h3 class="section-title">Mis Equipos</h3>
        <p class="section-description">
          Administra tus equipos, jugadores y partidos
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import PageHeader from '@/components/layout/PageHeader.vue';

const router = useRouter();

const goToResources = () => {
  router.push('/my-resources');
};

const goToTeams = () => {
  router.push('/teams');
};
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-6);
}

.dashboard-section {
  background: var(--color-background-white);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-8);
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.dashboard-section:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.section-icon {
  width: 64px;
  height: 64px;
  background: var(--color-primary);
  color: var(--color-white);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto var(--spacing-4);
  font-size: var(--font-size-2xl);
}

.section-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-3);
}

.section-description {
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
  line-height: var(--line-height-normal);
  margin: 0;
}

@media (max-width: 640px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
  
  .dashboard-section {
    padding: var(--spacing-6);
  }
  
  .section-icon {
    width: 56px;
    height: 56px;
    font-size: var(--font-size-xl);
  }
}
</style>
