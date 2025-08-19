<template>
  <span class="status-badge" :class="[`status-badge--${variant}`, `status-badge--${size}`]">
    {{ status }}
  </span>
</template>

<script>
export default {
  name: 'StatusBadge',
  props: {
    status: {
      type: String,
      required: true
    },
    variant: {
      type: String,
      default: 'default',
      validator: (value) => {
        return ['default', 'success', 'warning', 'danger', 'info', 'secondary'].includes(value)
      }
    },
    size: {
      type: String,
      default: 'md',
      validator: (value) => {
        return ['sm', 'md', 'lg'].includes(value)
      }
    }
  }
}
</script>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
  transition: all 0.2s ease;
}

/* Variantes de color */
.status-badge--default {
  background-color: var(--color-gray-100, #f3f4f6);
  color: var(--color-gray-800, #1f2937);
  border: 1px solid var(--color-gray-200, #e5e7eb);
}

.status-badge--success {
  background-color: var(--color-success-light, #d1fae5);
  color: var(--color-success-dark, #065f46);
  border: 1px solid var(--color-success, #10b981);
}

.status-badge--warning {
  background-color: var(--color-warning-light, #fef3c7);
  color: var(--color-warning-dark, #92400e);
  border: 1px solid var(--color-warning, #f59e0b);
}

.status-badge--danger {
  background-color: var(--color-danger-light, #fee2e2);
  color: var(--color-danger-dark, #991b1b);
  border: 1px solid var(--color-danger, #ef4444);
}

.status-badge--info {
  background-color: var(--color-info-light, #dbeafe);
  color: var(--color-info-dark, #1e40af);
  border: 1px solid var(--color-info, #3b82f6);
}

.status-badge--secondary {
  background-color: var(--color-secondary-light, #f3f4f6);
  color: var(--color-secondary-dark, #374151);
  border: 1px solid var(--color-secondary, #6b7280);
}

/* Tamaños */
.status-badge--sm {
  padding: 0.125rem 0.5rem;
  font-size: 0.625rem;
}

.status-badge--md {
  padding: 0.25rem 0.75rem;
  font-size: 0.75rem;
}

.status-badge--lg {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

/* Hover effects */
.status-badge:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Focus states */
.status-badge:focus {
  outline: 2px solid var(--color-primary, #3b82f6);
  outline-offset: 2px;
}
</style>
