<template>
  <div class="form-group">
    <label v-if="label" :for="id">{{ label }}</label>
    <input
      :id="id"
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="placeholder"
      :required="required"
      :disabled="disabled"
      class="base-input"
      :class="{ 'has-error': error }"
    />
    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<script>
export default {
  name: "BaseInput",
  props: {
    modelValue: { type: [String, Number], default: '' },
    label: { type: String, default: '' },
    id: { type: String, default: () => `input-${Math.random().toString(36).substr(2, 9)}` },
    type: { type: String, default: 'text' },
    placeholder: { type: String, default: '' },
    required: { type: Boolean, default: false },
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
.base-input {
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  background-color: var(--color-background-white);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.base-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25); /* Sombra de color primario con opacidad */
}
.base-input.has-error {
  border-color: var(--color-danger);
}
.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-1);
}
</style>