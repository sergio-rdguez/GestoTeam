<template>
  <div class="teams-page">
    <PageHeader title="Mis Equipos" :show-back-button="true" @back="goBack">
      <BaseButton v-if="teams.length > 0" @click="addTeam">
        <i class="fa-solid fa-plus"></i> Añadir Equipo
      </BaseButton>
    </PageHeader>
    
    <div v-if="loading">
      <LoadingSpinner message="Cargando equipos..." />
    </div>

    <EmptyState
      v-else-if="teams.length === 0"
      title="Aún no tienes equipos"
      message="Crea tu primer equipo para empezar a gestionar jugadores, partidos y estadísticas."
      icon="fa-users"
    >
      <template #actions>
        <BaseButton @click="addTeam">
          <i class="fa-solid fa-plus"></i> Crear mi primer equipo
        </BaseButton>
      </template>
    </EmptyState>

    <DataTable
      v-else
      :items="teams"
      :columns="columns"
      table-name="teams"
      @row-click="viewTeam"
    />
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
      teams: [],
      loading: true,
      columns: [
        { key: 'name', label: 'Nombre', sortable: true },
        { key: 'category', label: 'Categoría', sortable: true },
        { key: 'division', label: 'División', sortable: true },
        { key: 'location', label: 'Ubicación', sortable: true },
      ],
    };
  },
  methods: {
    async fetchTeams() {
      this.loading = true;
      try {
        const response = await api.get("/teams");
        this.teams = response.data;
      } catch (error) {
        console.error("Error al obtener equipos:", error);
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push('/dashboard');
    },
    viewTeam(team) {
      this.$router.push({ name: "TeamDetails", params: { teamId: team.id } });
    },
    addTeam() {
      this.$router.push({ name: "NewTeam" });
    },

  },
  mounted() {
    this.fetchTeams();
  },
};
</script>