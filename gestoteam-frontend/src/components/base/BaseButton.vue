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
    type: {
      type: String,
      default: 'button',
    },
    variant: {
      type: String,
      default: 'primary', // primary, secondary, danger
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    icon: {
      type: String,
      default: '',
    },
  },
  computed: {
    buttonClass() {
      return [
        'btn',
        `btn-${this.variant}`,
        { 'loading': this.loading }
      ];
    }
  }
};
</script>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
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

/* Variants */
.btn-primary {
  background-color: #007bff;
  color: white;
}
.btn-primary:hover:not(:disabled) {
  background-color: #0069d9;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}
.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}
.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

/* Icon and Content */
.icon {
  margin-right: 0.5rem;
}
.content {
  display: inherit;
  align-items: inherit;
  justify-content: inherit;
}

/* Loading State */
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
  to {
    transform: rotate(360deg);
  }
}
</style>