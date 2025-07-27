<template>
  <div :class="['toast-notification', notification.type]">
    <span class="toast-message">{{ notification.message }}</span>
    <button @click="close" class="close-button">&times;</button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  notification: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['close']);

const close = () => {
  emit('close', props.notification.id);
};
</script>

<style scoped>
.toast-notification {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-3) var(--spacing-4);
  border-radius: var(--border-radius-md);
  color: var(--color-white);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-bottom: var(--spacing-3);
  animation: slideIn 0.3s ease-out;
}

.toast-notification.success {
  background-color: var(--color-success);
}

.toast-notification.error {
  background-color: var(--color-danger);
}

.toast-message {
  font-weight: 500;
}

.close-button {
  background: none;
  border: none;
  color: var(--color-white);
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  opacity: 0.8;
  padding-left: var(--spacing-4);
}

.close-button:hover {
  opacity: 1;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>