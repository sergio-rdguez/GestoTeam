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
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px solid var(--color-border);
}
.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}
.card-content {
  padding: var(--spacing-5);
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}
</style>