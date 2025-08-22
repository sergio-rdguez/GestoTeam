<template>
  <div class="training-details-page">
    <PageHeader :title="`Entrenamiento - ${formatDate(training?.date)}`" show-back-button @back="goBack">
      <BaseButton @click="editTraining" variant="secondary">
        <i class="fa-solid fa-pencil"></i> Editar
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando entrenamiento..." />
    </div>

    <div v-else-if="training" class="training-content">
      <!-- Información General del Entrenamiento -->
      <BaseCard title="Información General" class="info-card">
        <div class="info-grid">
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
        </div>
        <div v-if="training.observations" class="description">
          <h4>Observaciones</h4>
          <p>{{ training.observations }}</p>
        </div>
      </BaseCard>

      <!-- Ejercicios del Entrenamiento -->
      <BaseCard title="Ejercicios" class="exercises-card">
        <div class="exercises-header">
          <span class="exercises-count">{{ training.exercises?.length || 0 }} ejercicios</span>
          <BaseButton @click="showExerciseSelector" variant="primary" size="sm">
            <i class="fa-solid fa-plus"></i> Añadir Ejercicios
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

      <!-- Control de Asistencia -->
      <BaseCard title="Control de Asistencia" class="attendance-card">
        <div class="attendance-header">
          <div class="attendance-stats">
            <div class="stat-item">
              <span class="stat-number">{{ attendanceStats.present }}</span>
              <span class="stat-label">Presentes</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ attendanceStats.absent }}</span>
              <span class="stat-label">Ausentes</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ attendanceStats.late }}</span>
              <span class="stat-label">Retrasos</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ attendanceStats.injured }}</span>
              <span class="stat-label">Lesionados</span>
            </div>
          </div>
        </div>

        <DataTable
          :items="attendance"
          :columns="attendanceColumns"
          table-name="attendance"
          default-sort-key="playerName"
        >
          <template #cell-status="{ item }">
            <BaseSelect
              :value="item.status"
              :options="attendanceStatusOptions"
              @change="updateAttendanceStatus(item, $event)"
              size="sm"
            />
          </template>
          <template #cell-notes="{ item }">
            <BaseInput
              :value="item.notes"
              placeholder="Notas..."
              @input="updateAttendanceNotes(item, $event)"
              size="sm"
            />
          </template>
        </DataTable>
      </BaseCard>
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
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import DataTable from "@/components/common/DataTable.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import ExerciseSelectorModal from "@/components/training/ExerciseSelectorModal.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseButton,
    BaseSelect,
    BaseInput,
    DataTable,
    EmptyState,
    LoadingSpinner,
    ExerciseSelectorModal,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      trainingId: this.$route.params.trainingId,
      training: null,
      teamName: '',
      attendance: [],
      loading: true,
      showExerciseModal: false,
      attendanceColumns: [
        { key: 'playerName', label: 'Jugador', sortable: true },
        { key: 'position', label: 'Posición', sortable: true },
        { key: 'status', label: 'Estado', sortable: true },
        { key: 'notes', label: 'Notas', sortable: false },
      ],
      attendanceStatusOptions: [
        { value: 'PRESENT', label: 'Presente' },
        { value: 'ABSENT', label: 'Ausente' },
        { value: 'LATE', label: 'Retraso' },
        { value: 'INJURED', label: 'Lesionado' },
        { value: 'JUSTIFIED_ABSENCE', label: 'Falta Justificada' },
        { value: 'UNJUSTIFIED_ABSENCE', label: 'Falta No Justificada' },
      ],
    };
  },
  computed: {
    attendanceStats() {
      const stats = {
        present: 0,
        absent: 0,
        late: 0,
        injured: 0,
      };
      
      this.attendance.forEach(item => {
        switch (item.status) {
          case 'PRESENT':
            stats.present++;
            break;
          case 'ABSENT':
          case 'JUSTIFIED_ABSENCE':
          case 'UNJUSTIFIED_ABSENCE':
            stats.absent++;
            break;
          case 'LATE':
            stats.late++;
            break;
          case 'INJURED':
            stats.injured++;
            break;
        }
      });
      
      return stats;
    },
  },
  methods: {
    async fetchTrainingDetails() {
      this.loading = true;
      try {
        const [trainingResponse, teamResponse, attendanceResponse] = await Promise.all([
          trainingService.getTrainingById(this.trainingId),
          api.get(`/teams/${this.teamId}`),
          trainingService.getTrainingAttendance(this.trainingId)
        ]);
        
        this.training = trainingResponse;
        this.teamName = teamResponse.data.name;
        this.attendance = attendanceResponse;
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
    
    async updateAttendanceStatus(attendanceItem, newStatus) {
      try {
        await trainingService.updatePlayerAttendance(
          this.trainingId, 
          attendanceItem.playerId, 
          { status: newStatus, notes: attendanceItem.notes }
        );
        attendanceItem.status = newStatus;
        notificationService.showSuccess("Estado de asistencia actualizado");
      } catch (error) {
        console.error("Error al actualizar asistencia:", error);
        notificationService.showError("Error al actualizar asistencia");
      }
    },
    
    async updateAttendanceNotes(attendanceItem, newNotes) {
      try {
        await trainingService.updatePlayerAttendance(
          this.trainingId, 
          attendanceItem.playerId, 
          { status: attendanceItem.status, notes: newNotes }
        );
        attendanceItem.notes = newNotes;
        notificationService.showSuccess("Notas actualizadas");
      } catch (error) {
        console.error("Error al actualizar notas:", error);
        notificationService.showError("Error al actualizar notas");
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
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.info-card .card-content {
  padding-top: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-5);
  padding-top: var(--spacing-4);
}

.info-item {
  display: flex;
  flex-direction: column;
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

.attendance-header {
  margin-bottom: var(--spacing-4);
}

.attendance-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--spacing-4);
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
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}
</style>
