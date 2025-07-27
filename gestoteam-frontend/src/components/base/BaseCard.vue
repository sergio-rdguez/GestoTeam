<template>
  <div class="base-card">
    <div v-if="title || $slots.header" class="card-header">
      <h3 v-if="title" class="card-title">{{ title }}</h3>
      <div class="card-actions">
        <slot name="header"></slot>
      </div>
    </div>
    <div class="card-content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "BaseCard",
  props: {
    title: {
      type: String,
      default: "",
    },
  },
};
</script>

<style scoped>
.base-card {
  background: var(--color-background-white);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: var(--spacing-6);
  margin-bottom: var(--spacing-6);
  overflow: hidden;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: var(--spacing-4);
  margin-bottom: var(--spacing-4);
  border-bottom: 1px solid var(--color-border);
}
.card-title {
  font-family: "Montserrat", sans-serif;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}
.card-content {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}
</style>