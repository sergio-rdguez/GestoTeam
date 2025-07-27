<template>
  <div class="team-details-page">
    <div v-if="loading" class="loading-message">
      <p>Cargando detalles del equipo...</p>
    </div>
    <div v-else>
      <div class="team-header">
        <h2 class="team-name">{{ team.name }}</h2>
        <p class="team-division">División: <strong>{{ team.division }}</strong></p>
      </div>

      <div class="team-navigation">
        <router-link :to="{ name: 'TeamPlayers', params: { id: team.id } }" class="nav-item">
          Jugadores
        </router-link>
        <router-link :to="{ name: 'TeamMatches', params: { id: team.id } }" class="nav-item">
          Partidos
        </router-link>
      </div>

      <FabMenu :actions="fabActions" @action-clicked="handleFabAction" />
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import FabMenu from "@/components/common/FabMenu.vue";

export default {
  components: {
    FabMenu,
  },
  data() {
    return {
      team: null,
      loading: true,
      fabActions: [
          { label: "Volver", event: "go-back" },
          { label: "Editar Equipo", event: "edit-team" }
      ]
    };
  },
  methods: {
    async fetchTeamDetails() {
      try {
        const teamId = this.$route.params.id;
        const response = await apiClient.get(`/teams/${teamId}`);
        this.team = response.data;
      } catch (error) {
        console.error("Error al cargar los detalles del equipo:", error);
      } finally {
        this.loading = false;
      }
    },
    handleFabAction(event) {
        if (event === 'go-back') {
            this.goBack();
        } else if (event === 'edit-team') {
            this.editTeam();
        }
    },
    goBack() {
      this.$router.push({ name: "Teams" });
    },
    editTeam() {
      this.$router.push({ name: "EditTeam", params: { id: this.team.id } });
    },
  },
  mounted() {
    this.fetchTeamDetails();
  },
};
</script>

<style scoped>
.team-details-page {
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.loading-message {
  text-align: center;
  font-size: 1.2rem;
  color: #666;
}

.team-header {
  text-align: center;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.team-name {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}

.team-division {
  font-size: 1rem;
  color: #666;
}

.team-navigation {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.nav-item {
  text-decoration: none;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border-radius: 8px;
  text-align: center;
  font-size: 1rem;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.nav-item:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

@media (min-width: 768px) {
  .team-navigation {
    flex-direction: row;
    justify-content: center;
    gap: 20px;
  }

  .nav-item {
    flex: 1 1 auto;
    max-width: 200px;
  }
}
</style>