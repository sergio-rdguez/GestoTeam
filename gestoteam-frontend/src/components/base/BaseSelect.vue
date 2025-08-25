<template>
  <div class="form-group">
    <label v-if="label" :for="id">{{ label }}</label>
    <div class="select-wrapper">
      <select
        :id="id"
        :value="modelValue"
        @change="$emit('update:modelValue', $event.target.value)"
        :disabled="disabled"
        class="base-select"
        :class="{ 'has-error': error }"
      >
        <option v-if="placeholder" disabled value="">{{ placeholder }}</option>
        <option v-for="option in options" :key="option.value" :value="option.value">
          {{ option.label || option.text || option.value }}
        </option>
      </select>
      <div class="select-arrow">
        <i class="fa-solid fa-chevron-down"></i>
      </div>
    </div>
    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<script>
export default {
  name: "BaseSelect",
  props: {
    modelValue: { type: [String, Number], default: '' },
    label: { type: String, default: '' },
    id: { type: String, default: () => `select-${Math.random().toString(36).substr(2, 9)}` },
    options: { type: Array, required: true },
    placeholder: { type: String, default: 'Selecciona una opción' },
    disabled: { type: Boolean, default: false },
    error: { type: String, default: '' },
  },
  emits: ['update:modelValue'],
};
</script>

<style scoped>
.form-group {
  display: flex;
  flex-direction: column;
  width: 100%;
}

label {
  margin-bottom: var(--spacing-2);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.select-wrapper {
  position: relative;
  width: 100%;
}

.base-select {
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  background-color: var(--color-background-white);
  appearance: none;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.base-select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}

.base-select:hover:not(:disabled) {
  border-color: var(--color-primary);
}

.base-select.has-error {
  border-color: var(--color-danger);
}

.base-select:disabled {
  background-color: var(--color-background-light);
  color: var(--color-text-secondary);
  cursor: not-allowed;
}

.select-arrow {
  position: absolute;
  right: var(--spacing-3);
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-1);
}

/* Estilos para las opciones */
.base-select option {
  padding: var(--spacing-2);
  background-color: var(--color-background-white);
  color: var(--color-text-primary);
}

.base-select option:hover {
  background-color: var(--color-primary-light);
}
</style>