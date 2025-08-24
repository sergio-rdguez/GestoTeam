<template>
  <div class="training-details-page">
    <PageHeader :title="training?.title || `Entrenamiento - ${formatDate(training?.date)}`" show-back-button @back="goBack">
      <BaseButton @click="editTraining" variant="secondary">
        <i class="fa-solid fa-pencil"></i> Editar
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando entrenamiento..." />
    </div>

    <div v-else-if="training" class="training-content">
      <div class="training-layout">
        <!-- Sección Izquierda: Ejercicios del Entrenamiento -->
        <div class="exercises-section">
          <BaseCard title="Ejercicios del Entrenamiento" class="exercises-card">
            <div class="exercises-header">
              <span class="exercises-count">{{ training.exercises?.length || 0 }} ejercicios</span>
              <BaseButton @click="showExerciseSelector" variant="primary" size="sm">
                <i class="fa-solid fa-plus"></i> Añadir
              </BaseButton>
            </div>
            
            <div v-if="training.exercises && training.exercises.length > 0" class="exercises-list">
              <div v-for="exercise in training.exercises" :key="exercise.id" class="exercise-item">
                <div class="exercise-info">
                  <h5>{{ exercise.title }}</h5>
                  <p class="exercise-category">{{ exercise.category?.name || 'Sin categoría' }}</p>
                  <p v-if="exercise.description" class="exercise-description">{{ exercise.description }}</p>
                </div>
                <BaseButton @click="removeExercise(exercise.id)" variant="danger" size="sm">
                  <i class="fa-solid fa-trash"></i>
                </BaseButton>
              </div>
            </div>
            
            <EmptyState
              v-else
              title="No hay ejercicios asignados"
              message="Añade ejercicios para estructurar tu entrenamiento."
              icon="fa-dumbbell"
            />
          </BaseCard>
        </div>

                 <!-- Sección Derecha: Información General -->
         <div class="info-section">
           <!-- Control de Asistencia - Botón Accesible -->
           <BaseCard title="Control de Asistencia" class="attendance-card">
             <div class="attendance-overview">
                             <BaseButton @click="goToAttendance" variant="primary" class="view-attendance-btn">
                <i class="fa-solid fa-users"></i> Gestionar Asistencia
              </BaseButton>
             </div>
           </BaseCard>

           <BaseCard title="Información General" class="info-card">
             <div class="info-content">
               <div class="info-item">
                 <span class="info-label">Sesión</span>
                 <span class="info-value">
                   <span class="session-number">Sesión {{ training.sessionNumber }}</span>
                 </span>
               </div>
               <div class="info-item">
                 <span class="info-label">Fecha y Hora</span>
                 <span class="info-value">{{ formatDate(training.date) }}</span>
               </div>
               <div class="info-item">
                 <span class="info-label">Ubicación</span>
                 <span class="info-value">{{ training.location }}</span>
               </div>
               <div class="info-item">
                 <span class="info-label">Tipo</span>
                 <span class="info-value">
                   <span class="training-type">{{ training.trainingType }}</span>
                 </span>
               </div>
               <div class="info-item">
                 <span class="info-label">Equipo</span>
                 <span class="info-value">{{ teamName }}</span>
               </div>
               
               <div v-if="training.observations" class="description">
                 <h4>Observaciones</h4>
                 <p>{{ training.observations }}</p>
               </div>
             </div>
           </BaseCard>
         </div>
      </div>
    </div>

    <!-- Modal para seleccionar ejercicios -->
    <ExerciseSelectorModal
      v-if="showExerciseModal"
      :team-id="teamId"
      :selected-exercises="training?.exercises || []"
      @close="showExerciseModal = false"
      @select="addExercisesToTraining"
    />


  </div>
</template>

<script>
import { trainingService } from "@/services/trainingService";
import api from "@/services/api";
import { notificationService } from "@/services/notificationService";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseButton from "@/components/base/BaseButton.vue";

import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import ExerciseSelectorModal from "@/components/training/ExerciseSelectorModal.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseButton,
    LoadingSpinner,
    ExerciseSelectorModal,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      trainingId: this.$route.params.trainingId,
      training: null,
      teamName: '',
      loading: true,
      showExerciseModal: false,
    };
  },

  methods: {
    async fetchTrainingDetails() {
      this.loading = true;
      try {
        const [trainingResponse, teamResponse] = await Promise.all([
          trainingService.getTrainingById(this.trainingId),
          api.get(`/teams/${this.teamId}`)
        ]);
        
        this.training = trainingResponse;
        this.teamName = teamResponse.data.name;
      } catch (error) {
        console.error("Error al cargar los detalles del entrenamiento:", error);
        notificationService.showError("Error al cargar el entrenamiento");
      } finally {
        this.loading = false;
      }
    },


    
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    editTraining() {
      this.$router.push({ 
        name: 'EditTraining', 
        params: { teamId: this.teamId, trainingId: this.trainingId } 
      });
    },
    
    showExerciseSelector() {
      this.showExerciseModal = true;
    },

    goToAttendance() {
      this.$router.push({ 
        name: 'TrainingAttendance', 
        params: { teamId: this.teamId, trainingId: this.trainingId } 
      });
    },
    
    async addExercisesToTraining(exerciseIds) {
      try {
        await trainingService.addExercisesToTraining(this.trainingId, exerciseIds);
        notificationService.showSuccess("Ejercicios añadidos con éxito");
        this.fetchTrainingDetails();
        this.showExerciseModal = false;
      } catch (error) {
        console.error("Error al añadir ejercicios:", error);
        notificationService.showError("Error al añadir ejercicios");
      }
    },
    
    async removeExercise(exerciseId) {
      if (confirm("¿Estás seguro de que quieres eliminar este ejercicio del entrenamiento?")) {
        try {
          await trainingService.removeExercisesFromTraining(this.trainingId, [exerciseId]);
          notificationService.showSuccess("Ejercicio eliminado con éxito");
          this.fetchTrainingDetails();
        } catch (error) {
          console.error("Error al eliminar ejercicio:", error);
          notificationService.showError("Error al eliminar ejercicio");
        }
      }
    },
    

    
    goBack() {
      this.$router.push({ 
        name: 'TeamTrainings', 
        params: { teamId: this.teamId } 
      });
    }
  },
  mounted() {
    this.fetchTrainingDetails();
  },
};
</script>

<style scoped>
.training-details-page {
  padding: 20px;
}

.training-content {
  margin-top: var(--spacing-6);
}

.training-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-6);
  align-items: start;
}

/* Sección de Ejercicios (Izquierda) */
.exercises-section {
  min-height: 600px;
}

.exercises-card {
  height: 100%;
}

.exercises-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
}

.exercises-count {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.exercises-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
  max-height: 500px;
  overflow-y: auto;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--spacing-4);
  background-color: var(--color-background-soft);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.exercise-info h5 {
  margin: 0 0 var(--spacing-1) 0;
  color: var(--color-text-primary);
}

.exercise-category {
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  margin: 0 0 var(--spacing-2) 0;
}

.exercise-description {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.4;
}

/* Sección de Información (Derecha) */
.info-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.info-card .card-content {
  padding-top: 0;
}

.info-content {
  padding-top: var(--spacing-4);
}

.info-item {
  display: flex;
  flex-direction: column;
  margin-bottom: var(--spacing-4);
}

.info-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-1);
}

.info-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.session-number {
  background-color: var(--color-success-light);
  color: var(--color-success);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
  text-align: center;
  display: inline-block;
  min-width: 80px;
}

.training-type {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 0.875rem;
  font-weight: 500;
}

.description {
  margin-top: var(--spacing-5);
  padding-top: var(--spacing-5);
  border-top: 1px solid var(--color-border);
}

.description h4 {
  margin-bottom: var(--spacing-2);
  color: var(--color-text-primary);
}

.description p {
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* Control de Asistencia - Vista Compacta */
.attendance-overview {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4);
}

.attendance-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-3);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-3);
  background-color: var(--color-background-soft);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.stat-number {
  display: block;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.view-attendance-btn {
  width: 100%;
  justify-content: center;
}



/* Responsive */
@media (max-width: 1024px) {
  .training-layout {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
  
  .info-section {
    order: -1;
  }
}

@media (max-width: 768px) {
  .training-details-page {
    padding: 16px;
  }
  

}
</style>
