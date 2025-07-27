<template>
  <div class="main-layout">
    <PageHeader @logout="handleLogout" />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import PageHeader from './PageHeader.vue';
import authService from '@/services/auth';

const handleLogout = () => {
  authService.logout();
};
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--color-background-soft);
}

.main-content {
  flex-grow: 1;
  padding: var(--spacing-5);
}

/* Transición suave entre páginas */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>