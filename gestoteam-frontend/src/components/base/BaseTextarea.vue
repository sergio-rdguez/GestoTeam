<template>
  <div class="form-group">
    <label v-if="label" :for="id">{{ label }}</label>
    <textarea
      :id="id"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :placeholder="placeholder"
      :required="required"
      :disabled="disabled"
      :rows="rows"
      class="base-textarea"
      :class="{ 'has-error': error }"
    ></textarea>
    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<script>
export default {
  name: "BaseTextarea",
  props: {
    modelValue: { type: String, default: '' },
    label: { type: String, default: '' },
    id: { type: String, default: () => `textarea-${Math.random().toString(36).substr(2, 9)}`},
    rows: { type: Number, default: 4 },
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
.base-textarea {
  width: 100%;
  padding: var(--spacing-3);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: var(--font-size-base);
  font-family: var(--font-family-sans);
  color: var(--color-text-primary);
  background-color: var(--color-background-white);
  resize: vertical;
  min-height: 80px;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.base-textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}
.base-textarea.has-error {
  border-color: var(--color-danger);
}
.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-1);
}
</style>