<template>
  <div class="exercise-details-page">
    <!-- Header estándar de la aplicación -->
    <PageHeader 
      :title="exercise ? exercise.title : 'Detalles del Ejercicio'" 
      show-back-button 
      @back="goBack"
    >
      <BaseButton 
        v-if="exercise" 
        @click="editExercise"
        variant="primary"
        size="sm"
      >
        <i class="fas fa-edit"></i>
        Editar
      </BaseButton>
    </PageHeader>

    <!-- Loading state -->
    <div v-if="loading" class="loading-container">
      <LoadingSpinner />
      <p>Cargando ejercicio...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="!exercise" class="error-container">
      <div class="error-content">
        <div class="error-icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h2>Ejercicio no encontrado</h2>
        <p>El ejercicio que buscas no existe o no tienes permisos para verlo.</p>
        <BaseButton @click="goBack" variant="primary">
          <i class="fas fa-arrow-left"></i>
          Volver a la lista
        </BaseButton>
      </div>
    </div>

    <!-- Contenido principal del ejercicio -->
    <div v-else class="exercise-content">
      <!-- Columna izquierda: Información del ejercicio -->
      <div class="content-left">
        <!-- Tarjeta principal de información -->
        <BaseCard class="main-info-card">
          <div class="card-header">
            <div class="category-badge">
              <StatusBadge 
                :status="getCategoryLabel(exercise.category)"
                :variant="getCategoryVariant(exercise.category)"
                size="lg"
              />
            </div>
            
            <div class="timestamps">
              <div class="timestamp-item">
                <i class="fas fa-calendar-plus"></i>
                <div class="timestamp-content">
                  <span class="timestamp-label">Creado</span>
                  <span class="timestamp-value">{{ formatDate(exercise.createdAt) }}</span>
                </div>
              </div>
              
              <div v-if="exercise.updatedAt !== exercise.createdAt" class="timestamp-item">
                <i class="fas fa-calendar-check"></i>
                <div class="timestamp-content">
                  <span class="timestamp-label">Actualizado</span>
                  <span class="timestamp-value">{{ formatDate(exercise.updatedAt) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Descripción -->
          <div class="description-section">
            <h3>
              <i class="fas fa-info-circle"></i>
              Descripción
            </h3>
            <div class="description-content">
              <p>{{ exercise.description || 'Sin descripción disponible' }}</p>
            </div>
          </div>

          <!-- Objetivos -->
          <div v-if="hasObjectives" class="objectives-section">
            <h3>
              <i class="fas fa-bullseye"></i>
              Objetivos del Ejercicio
            </h3>
            
            <div class="objectives-grid">
              <div v-if="exercise.tacticalObjectives" class="objective-card tactical">
                <div class="objective-header">
                  <div class="objective-icon">
                    <i class="fas fa-chess"></i>
                  </div>
                  <h4>Objetivos Tácticos</h4>
                </div>
                <div class="objective-content">
                  <p>{{ exercise.tacticalObjectives }}</p>
                </div>
              </div>
              
              <div v-if="exercise.technicalObjectives" class="objective-card technical">
                <div class="objective-header">
                  <div class="objective-icon">
                    <i class="fas fa-cog"></i>
                  </div>
                  <h4>Objetivos Técnicos</h4>
                </div>
                <div class="objective-content">
                  <p>{{ exercise.technicalObjectives }}</p>
                </div>
              </div>
              
              <div v-if="exercise.physicalObjectives" class="objective-card physical">
                <div class="objective-header">
                  <div class="objective-icon">
                    <i class="fas fa-running"></i>
                  </div>
                  <h4>Objetivos Físicos</h4>
                </div>
                <div class="objective-content">
                  <p>{{ exercise.physicalObjectives }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Material -->
          <div v-if="exercise.materials" class="material-section">
            <h3>
              <i class="fas fa-tools"></i>
              Material Necesario
            </h3>
            <div class="material-content">
              <div class="material-icon">
                <i class="fas fa-tools"></i>
              </div>
              <div class="material-text">
                <p>{{ exercise.materials }}</p>
              </div>
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- Columna derecha: Imagen del ejercicio -->
      <div class="content-right">
        <BaseCard class="image-card">
          <div class="image-header">
            <h3>
              <i class="fas fa-image"></i>
              Imagen del Ejercicio
            </h3>
          </div>
          
          <div class="image-container">
            <div v-if="exercise.imageUrl" class="image-wrapper">
              <img 
                :src="getExerciseImageUrl()" 
                :alt="`Imagen del ejercicio: ${exercise.title}`" 
                class="exercise-image"
                @error="handleImageError"
                @load="handleImageLoad"
              />
              <div class="image-overlay">
                <button @click="openImageFullscreen" class="fullscreen-button">
                  <i class="fas fa-expand"></i>
                </button>
              </div>
            </div>
            
            <div v-else class="no-image-placeholder">
              <div class="placeholder-icon">
                <i class="fas fa-image"></i>
              </div>
              <p>Este ejercicio no tiene imagen</p>
              <small>Puedes añadir una imagen editando el ejercicio</small>
            </div>
          </div>
        </BaseCard>

        <!-- Información adicional -->
        <BaseCard class="additional-info-card">
          <div class="card-header">
            <h3>
              <i class="fas fa-chart-bar"></i>
              Información Adicional
            </h3>
          </div>
          
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">ID del Ejercicio</div>
              <div class="info-value">{{ exercise.id }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">Categoría</div>
              <div class="info-value">
                <StatusBadge 
                  :status="getCategoryLabel(exercise.category)"
                  :variant="getCategoryVariant(exercise.category)"
                  size="sm"
                />
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">Estado</div>
              <div class="info-value">
                <StatusBadge 
                  status="Activo"
                  variant="success"
                  size="sm"
                />
              </div>
            </div>
          </div>
        </BaseCard>
      </div>
    </div>



    <!-- Modal de imagen en pantalla completa -->
    <div v-if="showFullscreenImage" class="fullscreen-modal" @click="closeFullscreenImage">
      <div class="fullscreen-content" @click.stop>
        <button @click="closeFullscreenImage" class="close-fullscreen">
          <i class="fas fa-times"></i>
        </button>
        <img 
          :src="getExerciseImageUrl()" 
          :alt="`Imagen del ejercicio: ${exercise.title}`" 
          class="fullscreen-image"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import BaseButton from '@/components/base/BaseButton.vue';
import StatusBadge from '@/components/common/StatusBadge.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import BaseCard from '@/components/base/BaseCard.vue';
import PageHeader from '@/components/layout/PageHeader.vue';

import { exerciseService } from '@/services/exerciseService';
import { useNotification } from '@/composables/useNotification';
import { buildImageUrl } from '@/utils/imageUtils';

const route = useRoute();
const router = useRouter();
const { showNotification } = useNotification();

// Estado
const exercise = ref(null);
const loading = ref(false);

const showFullscreenImage = ref(false);
const imageLoaded = ref(false);

// Computed properties
const hasObjectives = computed(() => {
  return exercise.value?.tacticalObjectives || 
         exercise.value?.technicalObjectives || 
         exercise.value?.physicalObjectives;
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
  router.push(`/my-resources/exercises/${route.params.id}/edit`);
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

const getExerciseImageUrl = () => {
  if (!exercise.value?.imageUrl) {
    return null;
  }
  
  return buildImageUrl(exercise.value.imageUrl);
};

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  
  return new Date(dateString).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const handleImageError = () => {
  imageLoaded.value = false;
};

const handleImageLoad = () => {
  imageLoaded.value = true;
};

const openImageFullscreen = () => {
  showFullscreenImage.value = true;
};

const closeFullscreenImage = () => {
  showFullscreenImage.value = false;
};

// Lifecycle
onMounted(() => {
  loadExercise();
});
</script>

<style scoped>
.exercise-details-page {
  min-height: 100vh;
  background: var(--color-background);
}

/* Contenido principal */
.exercise-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-6);
  margin-bottom: var(--spacing-8);
}

/* Columna izquierda */
.content-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.main-info-card {
  background: var(--color-background-white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.card-header {
  background: var(--color-background-subtle);
  padding: var(--spacing-6);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-6);
}

.category-badge {
  flex-shrink: 0;
}

.timestamps {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
  text-align: right;
}

.timestamp-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  color: var(--color-text-secondary);
}

.timestamp-item i {
  color: var(--color-text-tertiary);
  width: 16px;
}

.timestamp-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.timestamp-label {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  text-transform: uppercase;
  color: var(--color-text-secondary);
}

.timestamp-value {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
}

/* Secciones de contenido */
.description-section,
.objectives-section,
.material-section {
  padding: var(--spacing-6);
  border-bottom: 1px solid var(--color-border-subtle);
}

.description-section:last-child,
.objectives-section:last-child,
.material-section:last-child {
  border-bottom: none;
}

.description-section h3,
.objectives-section h3,
.material-section h3 {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin: 0 0 var(--spacing-4) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.description-section h3 i,
.objectives-section h3 i,
.material-section h3 i {
  color: var(--color-primary);
}

.description-content p {
  color: var(--color-text-secondary);
  line-height: 1.7;
  margin: 0;
  font-size: var(--font-size-base);
}

/* Objetivos */
.objectives-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.objective-card {
  background: var(--color-background-subtle);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-6);
  border: 1px solid var(--color-border);
  transition: all 0.2s ease;
}

.objective-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.objective-card.tactical {
  border-left: 4px solid var(--color-warning);
}

.objective-card.technical {
  border-left: 4px solid var(--color-info);
}

.objective-card.physical {
  border-left: 4px solid var(--color-success);
}

.objective-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-4);
}

.objective-icon {
  width: 40px;
  height: 40px;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-lg);
  flex-shrink: 0;
}

.objective-content h4 {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.objective-content p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* Material */
.material-content {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-4);
  padding: var(--spacing-6);
  background: var(--color-background-subtle);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.material-icon {
  width: 40px;
  height: 40px;
  background: var(--color-danger);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-lg);
  flex-shrink: 0;
}

.material-text p {
  margin: 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
  font-weight: var(--font-weight-medium);
}

/* Columna derecha */
.content-right {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.image-card,
.additional-info-card {
  background: var(--color-background-white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.image-header {
  padding: var(--spacing-6);
  background: var(--color-background-subtle);
  border-bottom: 1px solid var(--color-border);
}

.image-header h3 {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.image-header h3 i {
  color: var(--color-primary);
}

.image-container {
  padding: var(--spacing-6);
}

.image-wrapper {
  position: relative;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  background: var(--color-background-subtle);
  border: 2px solid var(--color-border);
}

.exercise-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  display: block;
  transition: transform 0.2s ease;
}

.image-wrapper:hover .exercise-image {
  transform: scale(1.02);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.image-wrapper:hover .image-overlay {
  opacity: 1;
}

.fullscreen-button {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: var(--color-text-primary);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-lg);
  transition: all 0.2s ease;
}

.fullscreen-button:hover {
  background: white;
  transform: scale(1.05);
}

.no-image-placeholder {
  text-align: center;
  padding: var(--spacing-8) var(--spacing-4);
  color: var(--color-text-secondary);
}

.placeholder-icon {
  font-size: 3rem;
  color: var(--color-border);
  margin-bottom: var(--spacing-4);
}

.no-image-placeholder p {
  margin: 0 0 var(--spacing-2) 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.no-image-placeholder small {
  color: var(--color-text-tertiary);
}

/* Información adicional */
.additional-info-card .card-header {
  padding: var(--spacing-6);
  background: var(--color-background-subtle);
  border-bottom: 1px solid var(--color-border);
}

.additional-info-card .card-header h3 {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.additional-info-card .card-header h3 i {
  color: var(--color-primary);
}

.info-grid {
  padding: var(--spacing-6);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4);
  background: var(--color-background-subtle);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.info-label {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
}

.info-value {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

/* Modal de imagen en pantalla completa */
.fullscreen-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: var(--spacing-8);
}

.fullscreen-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
}

.close-fullscreen {
  position: absolute;
  top: -50px;
  right: 0;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-lg);
  transition: all 0.2s ease;
}

.close-fullscreen:hover {
  background: rgba(255, 255, 255, 0.3);
}

.fullscreen-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: var(--border-radius-md);
}

/* Estados de carga y error */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
  text-align: center;
}

.loading-container p {
  margin-top: var(--spacing-4);
  color: var(--color-text-secondary);
  font-size: var(--font-size-base);
}

.error-content {
  max-width: 500px;
  padding: var(--spacing-8);
}

.error-icon {
  font-size: 3rem;
  color: var(--color-warning);
  margin-bottom: var(--spacing-6);
}

.error-content h2 {
  margin: 0 0 var(--spacing-4) 0;
  color: var(--color-text-primary);
  font-size: var(--font-size-2xl);
}

.error-content p {
  margin: 0 0 var(--spacing-6) 0;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* Responsive */
@media (max-width: 1024px) {
  .exercise-content {
    grid-template-columns: 1fr;
    gap: var(--spacing-6);
  }
  
  .content-right {
    order: -1;
  }
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: var(--spacing-4);
    text-align: center;
  }
  
  .timestamps {
    text-align: center;
  }
  
  .timestamp-content {
    align-items: center;
  }
  
  .exercise-content {
    gap: var(--spacing-4);
  }
  
  .description-section,
  .objectives-section,
  .material-section {
    padding: var(--spacing-4);
  }
  
  .image-container {
    padding: var(--spacing-4);
  }
  
  .exercise-image {
    height: 200px;
  }
}

@media (max-width: 480px) {
  .exercise-content {
    gap: var(--spacing-4);
  }
  
  .description-section,
  .objectives-section,
  .material-section {
    padding: var(--spacing-4);
  }
  
  .objective-card {
    padding: var(--spacing-4);
  }
  
  .material-content {
    padding: var(--spacing-4);
  }
}
</style>
