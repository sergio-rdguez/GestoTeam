<template>
  <button :type="type" :class="buttonClass" :disabled="disabled || loading">
    <span v-if="loading" class="spinner"></span>
    <i v-if="icon && !loading" :class="['icon', icon]"></i>
    <span class="content">
      <slot></slot>
    </span>
  </button>
</template>

<script>
export default {
  name: "BaseButton",
  props: {
    type: { type: String, default: 'button' },
    variant: { type: String, default: 'primary' },
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    icon: { type: String, default: '' },
  },
  computed: {
    buttonClass() {
      return ['btn', `btn-${this.variant}`, { 'loading': this.loading }];
    }
  }
};
</script>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3) var(--spacing-6);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  white-space: nowrap;
  user-select: none;
}
.btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: none;
}
.btn:disabled {
  background-color: #c0c0c0;
  color: #888;
  cursor: not-allowed;
}
.btn-primary {
  background-color: var(--color-primary);
  color: var(--color-background-white);
}
.btn-primary:hover:not(:disabled) {
  background-color: var(--color-primary-dark);
}
.btn-secondary {
  background-color: var(--color-text-secondary);
  color: var(--color-background-white);
}
.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268; /* Considerar añadir un --color-secondary-dark */
}
.btn-danger {
  background-color: var(--color-danger);
  color: var(--color-background-white);
}
.btn-danger:hover:not(:disabled) {
  background-color: #c82333; /* Considerar añadir un --color-danger-dark */
}
.icon {
  margin-right: var(--spacing-2);
}
.content {
  display: inherit;
  align-items: inherit;
  justify-content: inherit;
}
.btn.loading .content, .btn.loading .icon {
  visibility: hidden;
}
.spinner {
  position: absolute;
  width: 1.2em;
  height: 1.2em;
  border: 2px solid currentColor;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>