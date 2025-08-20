<template>
  <div class="opponents-page">
    <PageHeader :title="`Oponentes - ${teamName || 'Equipo'}`" show-back-button @back="goBack">
      <BaseButton @click="addOpponent">
        <i class="fa-solid fa-plus"></i> Añadir Oponente
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando oponentes..." />
    </div>

    <EmptyState
      v-else-if="opponents.length === 0"
      title="Este equipo aún no tiene oponentes"
      message="Añade los equipos rivales para poder programar partidos."
      icon="fa-solid fa-users"
    >
      <template #actions>
        <BaseButton @click="addOpponent">
          <i class="fa-solid fa-plus"></i> Añadir primer oponente
        </BaseButton>
      </template>
    </EmptyState>

    <DataTable
      v-else
      :items="opponents"
      :columns="columns"
      table-name="opponents"
      default-sort-key="name"
      @row-click="viewOpponentDetails"
    >

      <template #cell-actions="{ item }">
        <div class="actions">
          <BaseButton @click.stop="editOpponent(item)" variant="secondary" size="sm">
            <i class="fa-solid fa-pencil"></i>
          </BaseButton>
          <BaseButton @click.stop="deleteOpponent(item)" variant="danger" size="sm">
            <i class="fa-solid fa-trash"></i>
          </BaseButton>
        </div>
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
    LoadingSpinner,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      teamName: '',
      opponents: [],
      loading: true,
      columns: [
        { key: 'name', label: 'Nombre', sortable: true },
        { key: 'field', label: 'Campo', sortable: true },
        { key: 'observations', label: 'Observaciones', sortable: false },
        { key: 'actions', label: 'Acciones', sortable: false },
      ],
    };
  },
  methods: {
    async fetchData() {
      this.loading = true;
      try {
        const [teamResponse, opponentsResponse] = await Promise.all([
          api.get(`/teams/${this.teamId}`),
          api.get(`/opponents/team/${this.teamId}`)
        ]);
        this.teamName = teamResponse.data.name;
        this.opponents = opponentsResponse.data;
      } catch (error) {
        console.error("Error al cargar los oponentes:", error);
      } finally {
        this.loading = false;
      }
    },
    viewOpponentDetails(opponent) {
      if (opponent && opponent.id) {
        this.$router.push({ name: 'OpponentDetails', params: { opponentId: opponent.id } });
      } else {
        console.error("No se puede ver detalles: ID de oponente inválido");
      }
    },
    addOpponent() {
      this.$router.push({ name: 'NewOpponent', params: { teamId: this.teamId } });
    },
    editOpponent(opponent) {
      if (opponent && opponent.id) {
        this.$router.push({ name: 'EditOpponent', params: { opponentId: opponent.id, teamId: this.teamId } });
      } else {
        console.error("No se puede editar: ID de oponente inválido");
      }
    },
    async deleteOpponent(opponent) {
      if (confirm(`¿Estás seguro de que deseas eliminar al oponente "${opponent.name}"?`)) {
        try {
          await api.delete(`/opponents/${opponent.id}`);
          this.fetchData(); // Recargar la lista
        } catch (error) {
          console.error("Error al eliminar el oponente:", error);
        }
      }
    },

    goBack() {
      this.$router.push({ name: 'TeamDetails', params: { teamId: this.teamId } });
    }
  },
  mounted() {
    this.fetchData();
  }
};
</script>

<style scoped>
.opponents-page {
  padding: var(--spacing-6);
}

.actions {
  display: flex;
  gap: var(--spacing-2);
}

.actions .base-button {
  padding: var(--spacing-1) var(--spacing-2);
  font-size: 0.875rem;
}
</style>
