<template>
  <div class="teams-page">
    <div v-if="teams.length === 0 && !loading" class="no-teams-message">
      <h2>Bienvenido a GestoTeam</h2>
      <p>Aún no has creado ningún equipo. ¡Empieza ahora!</p>
      <button class="btn btn-primary" @click="addTeam">Crear mi primer equipo</button>
    </div>

    <div v-else>
      <h2 class="page-title">Mis Equipos</h2>
      <DataTable
        :items="teams"
        :columns="columns"
        :loading="loading"
        @row-click="viewTeam"
      />
    </div>

    <FabMenu v-if="teams.length > 0" :actions="fabActions" @action-clicked="addTeam" />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import FabMenu from "@/components/common/FabMenu.vue";

export default {
  components: {
    DataTable,
    FabMenu,
  },
  data() {
    return {
      teams: [],
      loading: true,
      columns: [
        { key: 'name', label: 'Nombre', sortable: true },
        { key: 'category', label: 'Categoría', sortable: true },
        { key: 'division', label: 'División', sortable: true },
      ],
      fabActions: [
        { label: "Añadir Equipo", event: "add-team" }
      ],
    };
  },
  methods: {
    async fetchTeams() {
      this.loading = true;
      try {
        const response = await apiClient.get("/teams");
        this.teams = response.data;
      } catch (error) {
        console.error("Error al obtener equipos:", error);
      } finally {
        this.loading = false;
      }
    },
    viewTeam(team) {
      this.$router.push({ name: "TeamDetails", params: { id: team.id } });
    },
    addTeam() {
      this.$router.push({ name: "AddTeam" });
    },
  },
  mounted() {
    this.fetchTeams();
  },
};
</script>

<style scoped>
.teams-page {
  padding: 2rem;
  font-family: "Arial", sans-serif;
  background: #f9fafb;
  min-height: 100vh;
}
.page-title {
  text-align: center;
  font-size: 2.2rem;
  margin-bottom: 2rem;
  color: #333;
}
.no-teams-message {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 150px);
  text-align: center;
}
.no-teams-message h2 {
    font-size: 2.5rem;
    color: #007bff;
}
.no-teams-message p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 1.5rem;
}
.btn-primary {
  background-color: #007bff;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}
.btn-primary:hover {
    background-color: #0056b3;
}
</style>