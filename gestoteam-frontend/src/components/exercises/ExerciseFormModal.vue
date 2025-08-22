<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>{{ isEdit ? 'Editar Ejercicio' : 'Nuevo Ejercicio' }}</h2>
        <button @click="closeModal" class="close-button">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="saveExercise" class="exercise-form">
          <!-- Información básica -->
          <div class="form-section">
            <h3>Información del Ejercicio</h3>
            
            <div class="form-row">
              <div class="form-group">
                <label for="title">Título *</label>
                <BaseInput
                  id="title"
                  v-model="form.title"
                  placeholder="Nombre del ejercicio"
                  required
                />
              </div>
              
              <div class="form-group">
                <label for="category">Categoría *</label>
                <BaseSelect
                  id="category"
                  v-model="form.category"
                  :options="categoryOptions"
                  placeholder="Selecciona una categoría"
                  required
                />
              </div>
            </div>
            
            <div class="form-group">
              <label for="description">Descripción *</label>
              <BaseTextarea
                id="description"
                v-model="form.description"
                placeholder="Describe el ejercicio..."
                rows="3"
                required
              />
            </div>
          </div>

          <!-- Objetivos -->
          <div class="form-section">
            <h3>Objetivos del Ejercicio</h3>
            
            <div class="form-row">
              <div class="form-group">
                <label for="tacticalObjectives">Objetivos Tácticos</label>
                <BaseTextarea
                  id="tacticalObjectives"
                  v-model="form.tacticalObjectives"
                  placeholder="Objetivos tácticos..."
                  rows="2"
                />
              </div>
              
              <div class="form-group">
                <label for="technicalObjectives">Objetivos Técnicos</label>
                <BaseTextarea
                  id="technicalObjectives"
                  v-model="form.technicalObjectives"
                  placeholder="Objetivos técnicos..."
                  rows="2"
                />
              </div>
            </div>
            
            <div class="form-group">
              <label for="physicalObjectives">Objetivos Físicos</label>
              <BaseTextarea
                id="physicalObjectives"
                v-model="form.physicalObjectives"
                placeholder="Objetivos físicos..."
                rows="2"
              />
            </div>
          </div>

          <!-- Material -->
          <div class="form-section">
            <h3>Material Necesario</h3>
            
            <div class="form-group">
              <label for="material">Material</label>
              <BaseInput
                id="material"
                v-model="form.material"
                placeholder="Conos, balones, vallas..."
              />
            </div>
          </div>

          <!-- Pizarra Táctica -->
          <div class="form-section">
            <h3>Diagrama Táctico</h3>
            
            <div class="tactical-board-container">
              <TacticalBoard
                :initial-data="form.drawingData"
                :read-only="false"
                @drawing-exported="onDrawingExported"
              />
            </div>
          </div>

          <!-- Acciones -->
          <div class="form-actions">
            <BaseButton
              type="button"
              variant="secondary"
              @click="closeModal"
            >
              Cancelar
            </BaseButton>
            
            <BaseButton
              type="submit"
              variant="primary"
              :loading="saving"
            >
              {{ isEdit ? 'Actualizar' : 'Crear' }} Ejercicio
            </BaseButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
/* eslint-disable no-undef */
import { ref, reactive, onMounted, watch } from 'vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseSelect from '@/components/base/BaseSelect.vue';
import BaseTextarea from '@/components/base/BaseTextarea.vue';
import BaseButton from '@/components/base/BaseButton.vue';

import TacticalBoard from './TacticalBoard.vue';
import exerciseService from '@/services/exerciseService';
import { useNotification } from '@/composables/useNotification';

const props = defineProps({
  exercise: {
    type: Object,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'saved']);

const { showNotification } = useNotification();

// Estado del formulario
const saving = ref(false);
const form = reactive({
  title: '',
  description: '',
  category: '',
  tacticalObjectives: '',
  technicalObjectives: '',
  physicalObjectives: '',
  material: '',
  drawingData: null
});

// Opciones de categoría
const categoryOptions = [
  { value: 'CALENTAMIENTO', label: 'Calentamiento' },
  { value: 'TECNICO', label: 'Técnico' },
  { value: 'TACTICO', label: 'Táctico' },
  { value: 'FISICO', label: 'Físico' },
  { value: 'PARTIDO_MODIFICADO', label: 'Partido Modificado' },
  { value: 'OTRO', label: 'Otro' }
];

// Métodos
const closeModal = () => {
  emit('close');
};

const onDrawingExported = (drawingData) => {
  form.drawingData = drawingData;
};

const saveExercise = async () => {
  try {
    saving.value = true;
    
    // Validar campos requeridos
    if (!form.title || !form.description || !form.category) {
      showNotification('Por favor, completa todos los campos obligatorios', 'warning');
      return;
    }
    
    const exerciseData = {
      title: form.title,
      description: form.description,
      category: form.category,
      tacticalObjectives: form.tacticalObjectives || null,
      technicalObjectives: form.technicalObjectives || null,
      physicalObjectives: form.physicalObjectives || null,
      material: form.material || null,
      drawingData: form.drawingData ? JSON.stringify(form.drawingData) : null
    };
    
    if (props.isEdit) {
      await exerciseService.updateExercise(props.exercise.id, exerciseData);
    } else {
      await exerciseService.createExercise(exerciseData);
    }
    
    emit('saved');
    
  } catch (error) {
    console.error('Error saving exercise:', error);
    showNotification(
      `Error al ${props.isEdit ? 'actualizar' : 'crear'} el ejercicio`, 
      'error'
    );
  } finally {
    saving.value = false;
  }
};

const loadExerciseData = (exercise) => {
  if (!exercise) return;
  
  form.title = exercise.title || '';
  form.description = exercise.description || '';
  form.category = exercise.category || '';
  form.tacticalObjectives = exercise.tacticalObjectives || '';
  form.technicalObjectives = exercise.technicalObjectives || '';
  form.physicalObjectives = exercise.physicalObjectives || '';
  form.material = exercise.material || '';
  
  // Cargar datos del diagrama si existe
  if (exercise.drawingData) {
    try {
      form.drawingData = JSON.parse(exercise.drawingData);
    } catch (e) {
      console.warn('Error parsing drawing data:', e);
      form.drawingData = null;
    }
  } else {
    form.drawingData = null;
  }
};

const resetForm = () => {
  form.title = '';
  form.description = '';
  form.category = '';
  form.tacticalObjectives = '';
  form.technicalObjectives = '';
  form.physicalObjectives = '';
  form.material = '';
  form.drawingData = null;
};

// Lifecycle
onMounted(() => {
  if (props.exercise) {
    loadExerciseData(props.exercise);
  } else {
    resetForm();
  }
});

// Observar cambios en el ejercicio
watch(() => props.exercise, (newExercise) => {
  if (newExercise) {
    loadExerciseData(newExercise);
  } else {
    resetForm();
  }
}, { immediate: true });
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 1000px;
  max-height: 90vh;
  width: 100%;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
  background: #f8f9fa;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.close-button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background: #f0f0f0;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-button:hover {
  background: #e0e0e0;
  color: #333;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: calc(90vh - 80px);
}

.exercise-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-section {
  margin-bottom: var(--spacing-6);
  padding-bottom: var(--spacing-5);
  border-bottom: 1px solid var(--color-border);
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.form-section h3 {
  margin: 0 0 var(--spacing-4) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.tactical-board-container {
  height: 500px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .modal-content {
    max-width: 95vw;
    max-height: 95vh;
  }
  
  .modal-body {
    padding: 16px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .tactical-board-container {
    height: 400px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
