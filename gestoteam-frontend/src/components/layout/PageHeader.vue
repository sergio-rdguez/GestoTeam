<template>
  <div class="page-header">
    <div class="header-main">
      <button v-if="showBackButton" class="back-button" @click="$emit('back')">
        <i class="fa-solid fa-arrow-left"></i>
        <span class="back-button-text">Volver</span>
      </button>
      <h2 class="page-title">{{ title }}</h2>
    </div>
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
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-8);
  padding-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--color-border);
  gap: var(--spacing-4);
}
.header-main {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}
.page-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  line-height: 1.2;
}
.back-button {
  background: transparent;
  color: var(--color-text-secondary);
  border: 1px solid transparent;
  border-radius: var(--border-radius-md);
  padding: var(--spacing-2) var(--spacing-3);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  font-size: var(--font-size-base);
  transition: all 0.2s ease;
}
.back-button:hover {
  background-color: var(--color-background-white);
  border-color: var(--color-border);
  color: var(--color-text-primary);
}
.back-button i {
  font-size: 1.1em;
}
.header-actions {
  display: flex;
  gap: var(--spacing-3);
}
@media (max-width: 640px) {
  .page-title {
    font-size: var(--font-size-2xl);
  }
  .back-button-text {
    display: none;
  }
  .back-button {
    padding: var(--spacing-2);
  }
}
</style>