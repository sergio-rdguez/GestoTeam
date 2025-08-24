<template>
  <div class="exercise-list-page">
    <PageHeader title="Mis Ejercicios" :show-back-button="true" @back="goBack">
      <BaseButton v-if="exercises.length > 0" @click="createExercise">
        <i class="fa-solid fa-plus"></i> Añadir Ejercicio
      </BaseButton>
    </PageHeader>
    
    <div v-if="loading">
      <LoadingSpinner message="Cargando ejercicios..." />
    </div>

    <EmptyState
      v-else-if="exercises.length === 0"
      title="Aún no tienes ejercicios"
      message="Crea tu primer ejercicio táctico para empezar a construir tu biblioteca personal."
      icon="fa-dumbbell"
    >
      <template #actions>
        <BaseButton @click="createExercise">
          <i class="fa-solid fa-plus"></i> Crear mi primer ejercicio
        </BaseButton>
      </template>
    </EmptyState>

    <DataTable
      v-else
      :items="exercises"
      :columns="columns"
      table-name="exercises"
      @row-click="viewExercise"
    >
      <template #cell-category="{ value }">
        <span class="category-badge">{{ getCategoryLabel(value) }}</span>
      </template>
      
      <template #cell-createdAt="{ value }">
        <span class="date-cell">{{ formatDate(value) }}</span>
      </template>
    </DataTable>


  </div>
</template>

<script>
import api from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";


export default {
  components: {
    DataTable,
    PageHeader,
    BaseButton,
    EmptyState,
    LoadingSpinner
  },
  data() {
    return {
      exercises: [],
      loading: true,

      columns: [
        { key: 'title', label: 'Título', sortable: true },
        { key: 'category', label: 'Categoría', sortable: true },
        { key: 'createdAt', label: 'Fecha de Creación', sortable: true },
      ],
    };
  },
  methods: {
    async fetchExercises() {
      this.loading = true;
      try {
        const response = await api.get("/exercises");
        this.exercises = response.data;
      } catch (error) {
        console.error("Error al obtener ejercicios:", error);
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push('/my-resources');
    },
    viewExercise(exercise) {
      this.$router.push(`/my-resources/exercises/${exercise.id}`);
    },
    createExercise() {
      this.$router.push('/my-resources/exercises/new');
    },
    editExercise(exercise) {
      this.$router.push(`/my-resources/exercises/${exercise.id}/edit`);
    },
    async deleteExercise(exercise) {
      if (!confirm(`¿Estás seguro de que quieres eliminar el ejercicio "${exercise.title}"?`)) {
        return;
      }

      try {
        await api.delete(`/exercises/${exercise.id}`);
        await this.fetchExercises();
        // Aquí podrías usar el sistema de notificaciones si lo tienes
      } catch (error) {
        console.error("Error al eliminar el ejercicio:", error);
      }
    },
    getCategoryLabel(category) {
      const categoryMap = {
        'CALENTAMIENTO': 'Calentamiento',
        'TECNICO': 'Técnico',
        'TACTICO': 'Táctico',
        'FISICO': 'Físico',
        'PARTIDO_MODIFICADO': 'Partido Modificado',
        'OTRO': 'Otro'
      };
      return categoryMap[category] || category;
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
  },
  mounted() {
    this.fetchExercises();
  },
};
</script>

<style scoped>
.category-badge {
  background: var(--color-primary-light);
  color: var(--color-primary);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.date-cell {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}
</style>
