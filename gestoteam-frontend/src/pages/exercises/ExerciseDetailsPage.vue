<template>
  <div class="exercise-details-page">
    <PageHeader 
      :title="exercise?.title || 'Cargando...'"
      :show-back-button="true"
      @back="goBack"
    >
      <BaseButton v-if="exercise" @click="editExercise">
        <i class="fa-solid fa-edit"></i>
        Editar
      </BaseButton>
    </PageHeader>

    <div v-if="loading" class="loading-container">
      <LoadingSpinner />
    </div>

    <div v-else-if="!exercise" class="error-container">
      <div class="error-message">
        <i class="fas fa-exclamation-triangle"></i>
        <h3>Ejercicio no encontrado</h3>
        <p>El ejercicio que buscas no existe o no tienes permisos para verlo.</p>
        <BaseButton @click="goBack">
          Volver a la lista
        </BaseButton>
      </div>
    </div>

    <div v-else class="exercise-content">
      <!-- Información del ejercicio -->
      <BaseCard class="exercise-info">
        <div class="info-header">
          <div class="exercise-category">
            <StatusBadge 
              :text="getCategoryLabel(exercise.category)"
              :variant="getCategoryVariant(exercise.category)"
            />
          </div>
          
          <div class="exercise-meta">
            <div class="meta-item">
              <i class="fas fa-calendar"></i>
              <span>Creado: {{ formatDate(exercise.createdAt) }}</span>
            </div>
            <div v-if="exercise.updatedAt !== exercise.createdAt" class="meta-item">
              <i class="fas fa-edit"></i>
              <span>Actualizado: {{ formatDate(exercise.updatedAt) }}</span>
            </div>
          </div>
        </div>

        <div class="exercise-description">
          <h3>Descripción</h3>
          <p>{{ exercise.description }}</p>
        </div>

        <!-- Objetivos -->
        <div v-if="exercise.tacticalObjectives || exercise.technicalObjectives || exercise.physicalObjectives" class="exercise-objectives">
          <h3>Objetivos del Ejercicio</h3>
          
          <div class="objectives-grid">
            <div v-if="exercise.tacticalObjectives" class="objective-card">
              <div class="objective-icon">
                <i class="fas fa-chess"></i>
              </div>
              <div class="objective-content">
                <h4>Objetivos Tácticos</h4>
                <p>{{ exercise.tacticalObjectives }}</p>
              </div>
            </div>
            
            <div v-if="exercise.technicalObjectives" class="objective-card">
              <div class="objective-icon">
                <i class="fas fa-cog"></i>
              </div>
              <div class="objective-content">
                <h4>Objetivos Técnicos</h4>
                <p>{{ exercise.technicalObjectives }}</p>
              </div>
            </div>
            
            <div v-if="exercise.physicalObjectives" class="objective-card">
              <div class="objective-icon">
                <i class="fas fa-running"></i>
              </div>
              <div class="objective-content">
                <h4>Objetivos Físicos</h4>
                <p>{{ exercise.physicalObjectives }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Material -->
        <div v-if="exercise.material" class="exercise-material">
          <h3>Material Necesario</h3>
          <div class="material-content">
            <i class="fas fa-tools"></i>
            <span>{{ exercise.material }}</span>
          </div>
        </div>
      </BaseCard>

      <!-- Diagrama Táctico -->
      <BaseCard class="tactical-diagram">
        <h3>Diagrama Táctico</h3>
        
        <div v-if="exercise.drawingData" class="diagram-container">
          <TacticalBoard
            :initial-data="parsedDrawingData"
            :read-only="true"
          />
        </div>
        
        <div v-else class="no-diagram">
          <i class="fas fa-image"></i>
          <h4>Sin diagrama táctico</h4>
          <p>Este ejercicio no tiene un diagrama táctico asociado.</p>
          <BaseButton @click="editExercise" variant="primary">
            Añadir Diagrama
          </BaseButton>
        </div>
      </BaseCard>
    </div>

    <!-- Modal de edición -->
    <ExerciseFormModal
      v-if="showEditModal"
      :exercise="exercise"
      :is-edit="true"
      @close="closeEditModal"
      @saved="onExerciseUpdated"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PageHeader from '@/components/layout/PageHeader.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import StatusBadge from '@/components/common/StatusBadge.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import TacticalBoard from '@/components/exercises/TacticalBoard.vue';
import ExerciseFormModal from '@/components/exercises/ExerciseFormModal.vue';
import exerciseService from '@/services/exerciseService';
import { useNotification } from '@/composables/useNotification';
import BaseCard from '@/components/base/BaseCard.vue';

const route = useRoute();
const router = useRouter();
const { showNotification } = useNotification();

// Estado
const exercise = ref(null);
const loading = ref(false);
const showEditModal = ref(false);

// Computed
const parsedDrawingData = computed(() => {
  if (!exercise.value?.drawingData) return null;
  
  try {
    return JSON.parse(exercise.value.drawingData);
  } catch (e) {
    console.warn('Error parsing drawing data:', e);
    return null;
  }
});

// Métodos
const loadExercise = async () => {
  try {
    loading.value = true;
    const data = await exerciseService.getExerciseById(route.params.id);
    exercise.value = data;
  } catch (error) {
    console.error('Error loading exercise:', error);
    showNotification('Error al cargar el ejercicio', 'error');
    exercise.value = null;
  } finally {
    loading.value = false;
  }
};

const editExercise = () => {
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
};

const onExerciseUpdated = async () => {
  await loadExercise();
  closeEditModal();
  showNotification('Ejercicio actualizado correctamente', 'success');
};

const goBack = () => {
  router.push('/my-resources/exercises');
};

const getCategoryLabel = (category) => {
  const labels = {
    'CALENTAMIENTO': 'Calentamiento',
    'TECNICO': 'Técnico',
    'TACTICO': 'Táctico',
    'FISICO': 'Físico',
    'PARTIDO_MODIFICADO': 'Partido Modificado',
    'OTRO': 'Otro'
  };
  return labels[category] || category;
};

const getCategoryVariant = (category) => {
  const variants = {
    'CALENTAMIENTO': 'info',
    'TECNICO': 'primary',
    'TACTICO': 'warning',
    'FISICO': 'success',
    'PARTIDO_MODIFICADO': 'secondary',
    'OTRO': 'light'
  };
  return variants[category] || 'light';
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Lifecycle
onMounted(() => {
  loadExercise();
});
</script>

<style scoped>
.exercise-details-page {
  max-width: 1200px;
  margin: 0 auto;
}

.exercise-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 20px;
}

.exercise-info {
  margin-bottom: 0;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.exercise-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: right;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.meta-item i {
  color: #999;
  width: 16px;
}

.exercise-description h3,
.exercise-objectives h3,
.exercise-material h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.exercise-description p {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

.exercise-objectives {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.objectives-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.objective-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.objective-icon {
  width: 48px;
  height: 48px;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.objective-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.objective-content p {
  margin: 0;
  color: #555;
  line-height: 1.5;
}

.exercise-material {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.material-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.material-content i {
  color: var(--color-primary);
  font-size: 18px;
}

.material-content span {
  color: #555;
  font-weight: 500;
}

.tactical-diagram {
  margin-bottom: 0;
}

.tactical-diagram h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.diagram-container {
  height: 500px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.no-diagram {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.no-diagram i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ccc;
}

.no-diagram h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #666;
}

.no-diagram p {
  margin: 0 0 20px 0;
  color: #999;
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.error-message {
  text-align: center;
  color: #666;
}

.error-message i {
  font-size: 48px;
  color: #ff9800;
  margin-bottom: 16px;
}

.error-message h3 {
  margin: 0 0 8px 0;
  color: #333;
}

.error-message p {
  margin: 0 0 20px 0;
  color: #666;
}

@media (max-width: 768px) {
  .exercise-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .info-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .exercise-meta {
    text-align: left;
  }
  
  .diagram-container {
    height: 400px;
  }
}
</style>
