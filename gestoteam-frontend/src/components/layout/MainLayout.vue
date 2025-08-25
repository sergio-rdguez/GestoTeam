<template>
  <AuthGuard>
    <div class="main-layout">
      <AppHeader />
      <NavigationBreadcrumbs />
      
      <main class="main-content">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
      
      <QuickActions />
    </div>
  </AuthGuard>
</template>

<script setup>

import AppHeader from './AppHeader.vue';
import NavigationBreadcrumbs from './Breadcrumbs.vue';
import QuickActions from './QuickActions.vue';
import AuthGuard from '@/components/common/AuthGuard.vue';
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--color-background-light);
}
.main-content {
  flex-grow: 1;
  padding: var(--spacing-6) var(--spacing-4);
}
.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}



/* Responsive */
@media (max-width: 768px) {
  .main-content {
    padding: var(--spacing-4) var(--spacing-3);
  }
}
</style>