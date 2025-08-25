<template>
  <div class="exercise-form-page">
    <PageHeader 
      :title="isEdit ? 'Editar Ejercicio' : 'Nuevo Ejercicio'" 
      show-back-button 
      @back="goBack" 
    />

    <div class="form-container">
      <BaseCard class="form-card">
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

          <!-- Imagen del Ejercicio -->
          <div class="form-section">
            <h3>Imagen del Ejercicio</h3>
            
            <div v-if="currentImageUrl" class="current-image">
              <img :src="currentImageUrl" alt="Imagen actual" class="preview-image" />
              <small>Imagen actual del ejercicio</small>
            </div>
            
            <div class="form-group">
              <label for="imageFile">Seleccionar Imagen</label>
              <input 
                id="imageFile" 
                type="file" 
                accept="image/*" 
                @change="onImageSelected" 
              />
              <small class="form-help">Formatos soportados: JPG, PNG, GIF, WEBP</small>
            </div>
          </div>

          <!-- Acciones -->
          <div class="form-actions">
            <BaseButton
              type="button"
              variant="secondary"
              @click="goBack"
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
      </BaseCard>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseSelect from '@/components/base/BaseSelect.vue';
import BaseTextarea from '@/components/base/BaseTextarea.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import BaseCard from '@/components/base/BaseCard.vue';
import PageHeader from '@/components/layout/PageHeader.vue';

import { exerciseService } from '@/services/exerciseService';
import { useNotification } from '@/composables/useNotification';
import api from '@/services/api';
import { buildImageUrl } from '@/utils/imageUtils';

const route = useRoute();
const router = useRouter();
const { showNotification } = useNotification();

const isEdit = computed(() => route.params.id !== undefined);

// Estado del formulario
const saving = ref(false);
const selectedImageFile = ref(null);
const currentImageUrl = ref(null);
const form = reactive({
  title: '',
  description: '',
  category: '',
  tacticalObjectives: '',
  technicalObjectives: '',
  physicalObjectives: '',
  material: ''
});

// Opciones de categoría - Corregido para usar 'text' en lugar de 'label'
const categoryOptions = [
  { value: 'CALENTAMIENTO', text: 'Calentamiento' },
  { value: 'TECNICO', text: 'Técnico' },
  { value: 'TACTICO', text: 'Táctico' },
  { value: 'FISICO', text: 'Físico' },
  { value: 'PARTIDO_MODIFICADO', text: 'Partido Modificado' },
  { value: 'OTRO', text: 'Otro' }
];

// Métodos
const goBack = () => {
  // Si es edición, volver al detalle del ejercicio; si es creación, ir a la lista
  if (isEdit.value) {
    router.push(`/my-resources/exercises/${route.params.id}`);
  } else {
    router.push('/my-resources/exercises');
  }
};

const onImageSelected = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedImageFile.value = file;
    // Crear preview de la imagen
    const reader = new FileReader();
    reader.onload = (e) => {
      currentImageUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
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
      materials: form.material || null
    };
    
    let response;
    if (isEdit.value) {
      response = await exerciseService.updateExercise(route.params.id, exerciseData);
    } else {
      response = await exerciseService.createExercise(exerciseData);
    }

    // Subir imagen si se seleccionó una nueva
    if (selectedImageFile.value) {
      try {
        const formData = new FormData();
        formData.append('file', selectedImageFile.value);
        
        const imageResponse = await api.post(`/files/exercises/${response.id}/image`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        showNotification('Imagen subida correctamente', 'success');
        
        // Actualizar la URL de la imagen actual
        if (imageResponse.data) {
          currentImageUrl.value = buildImageUrl(imageResponse.data);
        }
      } catch (imageError) {
        console.error('Error al subir imagen:', imageError);
        showNotification(`Error al subir la imagen: ${imageError.message || 'Error desconocido'}`, 'error');
      }
    }
    
    showNotification(
      `Ejercicio ${isEdit.value ? 'actualizado' : 'creado'} correctamente`, 
      'success'
    );
    
    // Verificar si se está creando desde un contexto específico
    const creationContext = sessionStorage.getItem('exerciseCreationContext');
    if (creationContext && !isEdit.value) {
      try {
        const context = JSON.parse(creationContext);
        
        // Limpiar el contexto
        sessionStorage.removeItem('exerciseCreationContext');
        
        // Si se creó desde un entrenamiento, regresar ahí
        if (context.from === 'training') {
          router.push({
            name: context.returnRoute,
            params: { 
              teamId: context.teamId, 
              trainingId: context.trainingId 
            }
          });
          return;
        }
      } catch (error) {
        console.error('Error parsing creation context:', error);
      }
    }
    
    // Si es edición, volver al detalle del ejercicio; si es creación, ir a la lista
    if (isEdit.value) {
      router.push(`/my-resources/exercises/${route.params.id}`);
    } else {
      router.push('/my-resources/exercises');
    }
    
  } catch (error) {
    console.error('Error saving exercise:', error);
    showNotification(
      `Error al ${isEdit.value ? 'actualizar' : 'crear'} el ejercicio`, 
      'error'
    );
  } finally {
    saving.value = false;
  }
};

const loadExerciseData = async () => {
  if (!isEdit.value) return;
  
  try {
    const exercise = await exerciseService.getExerciseById(route.params.id);
    
    form.title = exercise.title || '';
    form.description = exercise.description || '';
    form.category = exercise.category || '';
    form.tacticalObjectives = exercise.tacticalObjectives || '';
    form.technicalObjectives = exercise.technicalObjectives || '';
    form.physicalObjectives = exercise.physicalObjectives || '';
    form.material = exercise.materials || '';
    
    // Cargar imagen existente si existe
    if (exercise.imageUrl) {
      currentImageUrl.value = buildImageUrl(exercise.imageUrl);
    } else {
      currentImageUrl.value = null;
    }
    
    // Limpiar imagen seleccionada al cargar ejercicio existente
    selectedImageFile.value = null;
  } catch (error) {
    console.error('Error loading exercise:', error);
    showNotification('Error al cargar el ejercicio', 'error');
    router.push('/my-resources/exercises');
  }
};

// Lifecycle
onMounted(() => {
  loadExerciseData();
});
</script>

<style scoped>
.exercise-form-page {
  min-height: 100vh;
  background: var(--color-background);
  padding: var(--spacing-6);
}

.form-container {
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  background: var(--color-background-white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.exercise-form {
  padding: var(--spacing-6);
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

.current-image {
  margin-bottom: 16px;
  text-align: center;
}

.preview-image {
  max-width: 200px;
  max-height: 150px;
  border-radius: 8px;
  border: 2px solid #e0e0e0;
  object-fit: cover;
}

.current-image small {
  display: block;
  margin-top: 8px;
  color: #666;
  font-size: 0.875rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

@media (max-width: 768px) {
  .exercise-form-page {
    padding: var(--spacing-4);
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>
