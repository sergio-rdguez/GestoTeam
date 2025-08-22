<template>
  <div class="team-trainings-page">
    <PageHeader :title="`Entrenamientos de ${teamName}`" show-back-button @back="goBack">
      <BaseButton v-if="trainings && trainings.length > 0" @click="createTraining">
        <i class="fa-solid fa-plus"></i> Nuevo Entrenamiento
      </BaseButton>
    </PageHeader>

    <!-- Filtros y Estadísticas -->
    <div v-if="trainings && trainings.length > 0" class="filters-section">
      <BaseCard title="Filtros y Estadísticas" class="filters-card">
        <div class="filters-grid">
          <div class="filter-group">
            <BaseInput
              v-model="searchTerm"
              placeholder="Buscar entrenamientos..."
              icon="fa-search"
            />
          </div>
          <div class="filter-group">
            <BaseSelect
              v-model="selectedType"
              :options="trainingTypeOptions"
              placeholder="Todos los tipos"
            />
          </div>
          <div class="filter-group">
            <BaseInput
              v-model="dateFilter"
              type="date"
              placeholder="Filtrar por fecha"
            />
          </div>
        </div>
        
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-number">{{ filteredTrainings.length }}</span>
            <span class="stat-label">Total</span>
          </div>
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
      </BaseCard>
    </div>

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
      :items="filteredTrainings"
      :columns="columns"
      table-name="trainings"
      default-sort-key="date"
      :default-sort-asc="false"
      @row-click="viewTrainingDetails"
    >
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
</template>

<script>
import { trainingService } from "@/services/trainingService";
import api from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import { notificationService } from "@/services/notificationService";

export default {
  components: {
    DataTable,
    PageHeader,
    BaseCard,
    BaseButton,
    BaseInput,
    BaseSelect,
    EmptyState,
    LoadingSpinner,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      teamName: '',
      trainings: [],
      loading: true,
      searchTerm: '',
      selectedType: '',
      dateFilter: '',
      columns: [
        { key: 'date', label: 'Fecha y Hora', sortable: true },
        { key: 'location', label: 'Ubicación', sortable: true },
        { key: 'trainingType', label: 'Tipo', sortable: true },
        { key: 'exercises', label: 'Ejercicios', sortable: false },
        { key: 'actions', label: 'Acciones', sortable: false },
      ],
      trainingTypeOptions: [
        { value: '', label: 'Todos los tipos' },
        { value: 'Táctico', label: 'Táctico' },
        { value: 'Físico', label: 'Físico' },
        { value: 'Técnico', label: 'Técnico' },
        { value: 'Calentamiento', label: 'Calentamiento' },
        { value: 'Partido Modificado', label: 'Partido Modificado' },
        { value: 'Transición', label: 'Transición' },
        { value: 'Finalización', label: 'Finalización' },
        { value: 'Posesión', label: 'Posesión' },
        { value: 'Pressing', label: 'Pressing' },
        { value: 'Otro', label: 'Otro' },
      ],
    };
  },
  computed: {
    filteredTrainings() {
      let filtered = this.trainings;
      
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase();
        filtered = filtered.filter(training =>
          training.location?.toLowerCase().includes(term) ||
          training.trainingType?.toLowerCase().includes(term) ||
          training.observations?.toLowerCase().includes(term)
        );
      }
      
      if (this.selectedType) {
        filtered = filtered.filter(training => training.trainingType === this.selectedType);
      }
      
      if (this.dateFilter) {
        const filterDate = new Date(this.dateFilter);
        filtered = filtered.filter(training => {
          const trainingDate = new Date(training.date);
          return trainingDate.toDateString() === filterDate.toDateString();
        });
      }
      
      return filtered;
    },
    
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
        // Obtener nombre del equipo y entrenamientos en paralelo
        const [teamResponse, trainingsResponse] = await Promise.all([
          api.get(`/teams/${this.teamId}`),
          trainingService.getTrainings()
        ]);
        
        this.teamName = teamResponse.data.name;
        this.trainings = trainingsResponse.filter(t => t.teamId === this.teamId);
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

.filters-section {
  margin-bottom: var(--spacing-6);
}

.filters-card .card-content {
  padding-top: 0;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-5);
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--spacing-4);
  padding-top: var(--spacing-4);
  border-top: 1px solid var(--color-border);
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