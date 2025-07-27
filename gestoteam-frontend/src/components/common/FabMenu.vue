<template>
  <div class="fab-container">
    <button class="fab" @click="toggleMenu" aria-label="Toggle Actions Menu">
      <i :class="menuIcon"></i>
    </button>
    <transition name="fab-fade">
      <div v-if="isOpen" class="fab-actions">
        <button
          v-for="action in actions"
          :key="action.event"
          @click="onActionClick(action.event)"
          :class="['fab-action', action.class]"
          :aria-label="action.label"
        >
          {{ action.label }}
        </button>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "FabMenu",
  props: {
    actions: {
      type: Array,
      required: true,
      validator: (actions) => {
        return actions.every(
          (action) =>
            typeof action.label === "string" &&
            typeof action.event === "string"
        );
      },
    },
    menuIcon: {
      type: String,
      default: "fa-solid fa-bars",
    },
  },
  data() {
    return {
      isOpen: false,
    };
  },
  methods: {
    toggleMenu() {
      this.isOpen = !this.isOpen;
    },
    onActionClick(event) {
      this.$emit("action-clicked", event);
      this.isOpen = false; // Cierra el menú después de la acción
    },
  },
};
</script>

<style scoped>
.fab-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column-reverse;
  align-items: flex-end;
  z-index: 999;
}

.fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #007bff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s ease-in-out, background-color 0.2s;
}

.fab:hover {
  background-color: #0056b3;
  transform: scale(1.1);
}

.fab i {
  font-size: 1.5em;
  transition: transform 0.3s ease;
}

.fab-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}

.fab-action {
  padding: 10px 20px;
  border-radius: 8px;
  background: white;
  color: #333;
  font-weight: 600;
  border: none;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
  transition: transform 0.2s, box-shadow 0.2s;
  white-space: nowrap;
}

.fab-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.fab-action.danger {
  background: #dc3545;
  color: white;
}

.fab-action.danger:hover {
  background: #c82333;
}

/* Transiciones */
.fab-fade-enter-active,
.fab-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fab-fade-enter-from,
.fab-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>