<template>
  <div class="form-group-checkbox">
    <input
      :id="id"
      type="checkbox"
      :checked="modelValue"
      @change="$emit('update:modelValue', $event.target.checked)"
      :disabled="disabled"
      class="base-checkbox"
    />
    <label v-if="label" :for="id">{{ label }}</label>
  </div>
</template>

<script>
export default {
  name: "BaseCheckbox",
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    label: {
      type: String,
      default: '',
    },
    id: {
      type: String,
      default: () => `checkbox-${Math.random().toString(36).substr(2, 9)}`,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue'],
};
</script>

<style scoped>
.form-group-checkbox {
  display: flex;
  align-items: center;
  width: 100%;
}

.base-checkbox {
  width: 1.25em;
  height: 1.25em;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s;
  appearance: none;
  -webkit-appearance: none;
  display: grid;
  place-content: center;
}

.base-checkbox::before {
    content: "";
    width: 0.75em;
    height: 0.75em;
    transform: scale(0);
    transition: 120ms transform ease-in-out;
    box-shadow: inset 1em 1em var(--color-primary);
    transform-origin: bottom left;
    clip-path: polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%);
}

.base-checkbox:checked {
    background-color: var(--color-primary-light);
    border-color: var(--color-primary);
}

.base-checkbox:checked::before {
    transform: scale(1);
}

label {
  margin-left: var(--spacing-2);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  cursor: pointer;
}
</style>