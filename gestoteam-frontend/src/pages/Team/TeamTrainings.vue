<template>
  <div class="team-trainings-page">
    <PageHeader :title="`Entrenamientos de ${teamName}`" show-back-button @back="goBack">
      <BaseButton v-if="trainings && trainings.length > 0" @click="createTraining">
        <i class="fa-solid fa-plus"></i> Nuevo Entrenamiento
      </BaseButton>
    </PageHeader>

    <!-- Layout de dos columnas -->
    <div class="trainings-layout">
      <!-- Columna izquierda: Tabla -->
      <div class="left-column">
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

      <!-- Columna derecha: Estadísticas -->
      <div class="right-column">
        <div v-if="trainings && trainings.length > 0" class="stats-section">
          <BaseCard title="Estadísticas" class="stats-card">
            <div class="stats-grid">
              <div class="stat-item total-stat">
                <span class="stat-number">{{ trainings.length }}</span>
                <span class="stat-label">Total Entrenamientos</span>
              </div>
              <div class="stats-subgrid">
                <div class="stat-item">
                  <span class="stat-number">{{ upcomingTrainings.length }}</span>
                  <span class="stat-label">Próximos</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ thisWeekTrainings.length }}</span>
                  <span class="stat-label">Esta Semana</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ thisMonthTrainings.length }}</span>
                  <span class="stat-label">Este Mes</span>
                </div>
              </div>
            </div>
          </BaseCard>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { trainingService } from "@/services/trainingService";
import api from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseButton from "@/components/base/BaseButton.vue";

import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import { notificationService } from "@/services/notificationService";

export default {
  components: {
    DataTable,
    PageHeader,
    BaseCard,
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
  computed: {
    upcomingTrainings() {
      const now = new Date();
      return this.trainings.filter(training => new Date(training.date) > now);
    },
    
    thisWeekTrainings() {
      const now = new Date();
      const endOfWeek = new Date(now);
      endOfWeek.setDate(now.getDate() + 7);
      
      return this.trainings.filter(training => {
        const trainingDate = new Date(training.date);
        return trainingDate >= now && trainingDate <= endOfWeek;
      });
    },
    
    thisMonthTrainings() {
      const now = new Date();
      const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      
      return this.trainings.filter(training => {
        const trainingDate = new Date(training.date);
        return trainingDate >= now && trainingDate <= endOfMonth;
      });
    },
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

.trainings-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: var(--spacing-6);
  align-items: start;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.right-column {
  position: sticky;
  top: 20px;
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

.stats-section {
  margin-bottom: var(--spacing-6);
}

.stats-card .card-content {
  padding-top: 0;
}

.stats-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-5);
}

.stats-subgrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-4);
  background-color: var(--color-background-white);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  width: 100%;
  box-sizing: border-box;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.total-stat {
  background-color: var(--color-primary);
  color: var(--color-background-white);
  border: none;
  padding: var(--spacing-6);
  margin-bottom: var(--spacing-4);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  border-radius: var(--border-radius-md);
  text-align: center;
}

.stat-number {
  display: block;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: var(--spacing-2);
  line-height: 1;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  line-height: 1.2;
}

.total-stat .stat-number {
  color: var(--color-background-white);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-3);
  display: block;
  line-height: 1;
}

.total-stat .stat-label {
  color: var(--color-background-white);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-lg);
  opacity: 0.95;
}

/* Responsive */
@media (max-width: 768px) {
  .team-trainings-page {
    padding: 16px;
  }
  
  .trainings-layout {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
  
  .right-column {
    position: static;
    order: -1;
  }
  
  .stats-subgrid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>