<template>
  <div class="empty-state-container">
    <div class="icon-wrapper">
      <i :class="['fa-solid', icon]"></i>
    </div>
    <h3 class="title">{{ title }}</h3>
    <p class="message">{{ message }}</p>
    <div class="actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "EmptyState",
  props: {
    title: {
      type: String,
      required: true,
    },
    message: {
      type: String,
      required: true,
    },
    icon: {
      type: String,
      default: "fa-box-open",
    },
  },
};
</script>

<style scoped>
.empty-state-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 3rem;
  text-align: center;
  background-color: #f9fafb;
  border-radius: 12px;
  border: 1px dashed #e0e0e0;
}

.icon-wrapper {
  font-size: 3rem;
  color: #007bff;
  margin-bottom: 1rem;
  line-height: 1;
}

.title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.message {
  font-size: 1rem;
  color: #666;
  max-width: 400px;
  margin: 0 0 1.5rem 0;
}

.actions {
  display: flex;
  gap: 1rem;
}
</style>