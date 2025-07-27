<template>
  <div class="notification-container">
    <transition-group name="list" tag="div">
      <ToastNotification
        v-for="notification in notifications"
        :key="notification.id"
        :notification="notification"
        @close="removeNotification"
      />
    </transition-group>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import ToastNotification from '@/components/common/ToastNotification.vue';
import { notificationService } from '@/services/notificationService';

const notifications = computed(() => notificationService.notifications);

const removeNotification = (id) => {
  notificationService.remove(id);
};
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 80px; /* Debajo del PageHeader */
  right: var(--spacing-4);
  z-index: 9999;
  width: 350px;
}

/* Estilos para la animación de la lista */
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>