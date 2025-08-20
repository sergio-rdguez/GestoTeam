<template>
  <div class="main-layout">
    <AppHeader />
    
    <!-- Estado del Sistema (overlay) -->
    <div v-if="showSystemStatus" class="system-status-overlay">
      <div class="system-status-container">
        <div class="system-status-header">
          <h3>Estado del Sistema</h3>
          <button @click="closeSystemStatus" class="close-button">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <SystemStatus />
      </div>
    </div>
    
    <main class="main-content">
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import AppHeader from './AppHeader.vue';
import SystemStatus from '@/components/common/SystemStatus.vue';

const showSystemStatus = ref(false);

const closeSystemStatus = () => {
  showSystemStatus.value = false;
};

const handleToggleSystemStatus = (event) => {
  showSystemStatus.value = event.detail.show;
};

onMounted(() => {
  window.addEventListener('toggle-system-status', handleToggleSystemStatus);
});

onUnmounted(() => {
  window.removeEventListener('toggle-system-status', handleToggleSystemStatus);
});
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

/* Overlay del estado del sistema */
.system-status-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.system-status-container {
  background: white;
  border-radius: 12px;
  max-width: 900px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

.system-status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.system-status-header h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #495057;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6c757d;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-button:hover {
  background: #e9ecef;
  color: #495057;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .system-status-overlay {
    padding: 10px;
  }
  
  .system-status-container {
    max-width: 100%;
    max-height: 95vh;
  }
  
  .system-status-header {
    padding: 15px 20px;
  }
  
  .system-status-header h3 {
    font-size: 1.3rem;
  }
}
</style>