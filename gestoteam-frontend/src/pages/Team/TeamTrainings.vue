<template>
  <div class="team-trainings-page">
    <PageHeader :title="`Entrenamientos de ${teamName}`" show-back-button @back="goBack">
      <BaseButton v-if="trainings && trainings.length > 0" @click="createTraining">
        <i class="fa-solid fa-plus"></i> Nuevo Entrenamiento
      </BaseButton>
    </PageHeader>



    <!-- Lista de Entrenamientos -->
    <div class="trainings-section">
      <div v-if="loading">
        <LoadingSpinner message="Cargando entrenamientos..." />
      </div>

      <EmptyState
        v-else-if="!trainings || trainings.length === 0"
        title="No hay entrenamientos programados"
        message="Crea tu primer entrenamiento para organizar las sesiones del equipo."
        icon="fa-dumbbell"
      >
        <template #actions>
          <BaseButton @click="createTraining">
            <i class="fa-solid fa-plus"></i> Crear primer entrenamiento
          </BaseButton>
        </template>
      </EmptyState>

      <DataTable
        v-else
        :items="trainings"
        :columns="columns"
        table-name="trainings"
        default-sort-key="sessionNumber"
        :default-sort-asc="true"
        @row-click="viewTrainingDetails"
        class="trainings-table"
      >
        <template #cell-sessionNumber="{ item }">
          <span class="session-number">Sesión {{ item.sessionNumber }}</span>
        </template>
        
        <template #cell-title="{ item }">
          <span class="training-title">{{ item.title }}</span>
        </template>
        
        <template #cell-date="{ item }">
          {{ formatDate(item.date) }}
        </template>
        <template #cell-trainingType="{ item }">
          <span class="training-type">{{ item.trainingType }}</span>
        </template>
        <template #cell-exercises="{ item }">
          <span class="exercises-count">{{ item.exercises?.length || 0 }} ejercicios</span>
        </template>
        <template #cell-actions="{ item }">
          <div class="actions">
            <BaseButton size="sm" variant="secondary" @click.stop="editTraining(item)">
              <i class="fa-solid fa-pencil"></i>
            </BaseButton>
            <BaseButton size="sm" variant="danger" @click.stop="deleteTraining(item)">
              <i class="fa-solid fa-trash"></i>
            </BaseButton>
          </div>
        </template>
      </DataTable>
    </div>
  </div>
</template>

<script>
import { trainingService } from "@/services/trainingService";
import api from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseButton from "@/components/base/BaseButton.vue";

import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import { notificationService } from "@/services/notificationService";

export default {
  components: {
    DataTable,
    PageHeader,
    BaseButton,
    EmptyState,
    LoadingSpinner,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      teamName: '',
      trainings: [],
      loading: true,
      columns: [
        { key: 'sessionNumber', label: 'Sesión', sortable: true },
        { key: 'title', label: 'Título', sortable: true },
        { key: 'date', label: 'Fecha y Hora', sortable: true },
        { key: 'trainingType', label: 'Tipo', sortable: true },
        { key: 'location', label: 'Ubicación', sortable: true },
        { key: 'exercises', label: 'Ejercicios', sortable: false },
        { key: 'actions', label: 'Acciones', sortable: false },
      ],
    };
  },

  methods: {
    async fetchTrainings() {
      this.loading = true;
      try {
        console.log('Fetching trainings for team:', this.teamId);
        
        // Obtener nombre del equipo y entrenamientos en paralelo
        const [teamResponse, trainingsResponse] = await Promise.all([
          api.get(`/teams/${this.teamId}`),
          trainingService.getTeamTrainings(this.teamId)
        ]);
        
        console.log('Team response:', teamResponse.data);
        console.log('Trainings response:', trainingsResponse);
        
        this.teamName = teamResponse.data.name;
        this.trainings = trainingsResponse;
        
        console.log('Trainings set to:', this.trainings);
      } catch (error) {
        console.error("Error al cargar los entrenamientos:", error);
        notificationService.showError("Error al cargar los entrenamientos");
      } finally {
        this.loading = false;
      }
    },
    
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleDateString('es-ES', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    viewTrainingDetails(training) {
      this.$router.push({ 
        name: 'TrainingDetails', 
        params: { teamId: this.teamId, trainingId: training.id } 
      });
    },
    
    createTraining() {
      this.$router.push({ 
        name: 'NewTraining', 
        params: { teamId: this.teamId } 
      });
    },
    
    editTraining(training) {
      this.$router.push({ 
        name: 'EditTraining', 
        params: { teamId: this.teamId, trainingId: training.id } 
      });
    },
    
    async deleteTraining(training) {
      if (confirm(`¿Estás seguro de que quieres eliminar el entrenamiento del ${this.formatDate(training.date)}?`)) {
        try {
          await trainingService.deleteTraining(training.id);
          notificationService.showSuccess("Entrenamiento eliminado con éxito");
          this.fetchTrainings();
        } catch (error) {
          console.error("Error al eliminar el entrenamiento:", error);
          notificationService.showError("Error al eliminar el entrenamiento");
        }
      }
    },
    
    goBack() {
      this.$router.push({ name: 'TeamDetails', params: { teamId: this.teamId } });
    }
  },
  mounted() {
    this.fetchTrainings();
  },
};
</script>

<style scoped>
.team-trainings-page {
  padding: 20px;
}



.trainings-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.trainings-table {
  margin-top: 0;
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

.training-title {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.training-type {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.exercises-count {
  color: var(--color-text-secondary);
  font-size: 0.875rem;
}

.actions {
  display: flex;
  gap: 8px;
}

.actions .btn {
  padding: 4px 8px;
  font-size: 0.875rem;
}

/* Responsive */
@media (max-width: 768px) {
  .team-trainings-page {
    padding: 16px;
  }
  

}
</style>