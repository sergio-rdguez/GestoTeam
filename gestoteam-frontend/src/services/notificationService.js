import { reactive } from 'vue';

// Estado reactivo que contendrá la lista de notificaciones.
const state = reactive({
  notifications: [],
});

let nextId = 0;

// Servicio para gestionar las notificaciones
export const notificationService = {
  // Exponemos el estado para que los componentes puedan leerlo
  get notifications() {
    return state.notifications;
  },

  /**
   * Muestra una notificación en pantalla.
   * @param {string} message - El mensaje a mostrar.
   * @param {string} type - 'success' o 'error'.
   * @param {number} duration - Duración en ms antes de desaparecer.
   */
  show(message, type = 'success', duration = 5000) {
    const id = nextId++;
    state.notifications.push({ id, message, type });

    // Auto-cierre de la notificación
    setTimeout(() => {
      this.remove(id);
    }, duration);
  },
  
  showSuccess(message, duration = 4000) {
    this.show(message, 'success', duration);
  },

  showError(message, duration = 6000) {
    this.show(message, 'error', duration);
  },

  // Elimina una notificación por su ID
  remove(id) {
    const index = state.notifications.findIndex(n => n.id === id);
    if (index !== -1) {
      state.notifications.splice(index, 1);
    }
  },
};