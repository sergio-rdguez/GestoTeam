import { ref } from 'vue';

const notifications = ref([]);

export function useNotification() {
  const showNotification = (message, type = 'info', duration = 5000) => {
    const id = Date.now();
    const notification = {
      id,
      message,
      type,
      timestamp: new Date()
    };

    notifications.value.push(notification);

    // Auto-remove notification after duration
    setTimeout(() => {
      removeNotification(id);
    }, duration);

    return id;
  };

  const removeNotification = (id) => {
    const index = notifications.value.findIndex(n => n.id === id);
    if (index > -1) {
      notifications.value.splice(index, 1);
    }
  };

  const clearNotifications = () => {
    notifications.value = [];
  };

  return {
    notifications,
    showNotification,
    removeNotification,
    clearNotifications
  };
}
