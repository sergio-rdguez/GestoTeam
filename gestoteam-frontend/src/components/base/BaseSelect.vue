<template>
  <div class="form-group">
    <label v-if="label" :for="id">{{ label }}</label>
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
        {{ option.label || option.text }}
      </option>
    </select>
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
.base-select {
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  background-color: var(--color-background-white);
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3E%3Cpath stroke='%236B7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='m6 8 4 4 4-4'/%3E%3C/svg%3E");
  background-position: right 0.5rem center;
  background-repeat: no-repeat;
  background-size: 1.5em 1.5em;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.base-select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}
.base-select.has-error {
  border-color: var(--color-danger);
}
.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-1);
}
</style>