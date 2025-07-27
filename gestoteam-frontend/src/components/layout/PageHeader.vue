<template>
  <div class="page-header">
    <button v-if="showBackButton" class="back-button" @click="$emit('back')">
      <i class="fa-solid fa-arrow-left"></i> Volver
    </button>
    <h2 class="page-title">{{ title }}</h2>
    <div class="header-actions">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "PageHeader",
  props: {
    title: {
      type: String,
      required: true,
    },
    showBackButton: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['back'],
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&family=Roboto:wght@400;500&display=swap");

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e0e0e0;
}

.page-title {
  text-align: center;
  font-size: 2.2rem;
  margin: 0;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  flex-grow: 1;
}

.back-button {
  background: transparent;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 1rem;
  transition: background-color 0.2s ease, color 0.2s ease;
  margin-right: 1rem; /* Espacio para que el título se centre bien */
}

.back-button:hover {
  background-color: #f0f0f0;
}

.back-button i {
  margin-right: 8px;
}

/* Contenedor para acciones, asegura alineación correcta */
.header-actions {
  min-width: 100px; /* Ajusta según el tamaño de tu botón de volver */
  display: flex;
  justify-content: flex-end;
}
</style>