<template>
  <div class="teams-page">
    <div v-if="teams.length === 0" class="no-teams-message">
      <p>Es hora de crear tu primer equipo.</p>
      <router-link to="/teams/add" class="btn btn-primary">
        Crear Equipo
      </router-link>
    </div>

    <ul v-else class="team-list">
      <li v-for="team in teams" :key="team.id" class="team-item">
        <div>
          <h3>{{ team.name }}</h3>
          <p>{{ team.categoryName }} - {{ team.division }}</p>
        </div>
        <button @click="viewTeam(team.id)" class="btn btn-outline-secondary">
          Ver Detalles
        </button>
      </li>
    </ul>

    <router-link v-if="teams.length > 0" to="/teams/add" class="btn btn-primary btn-block">
      Añadir Equipo
    </router-link>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  data() {
    return {
      teams: [],
    };
  },
  methods: {
    async fetchTeams() {
      try {
        const response = await apiClient.get("/teams");
        this.teams = response.data;
      } catch (error) {
        console.error("Error al obtener equipos:", error);
      }
    },
    viewTeam(teamId) {
      console.log(`Ver equipo con ID: ${teamId}`);
    },
  },
  mounted() {
    this.fetchTeams();
  },
};
</script>

<style scoped>
.teams-page {
  padding: 20px;
}

.team-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.team-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  margin-bottom: 10px;
  padding: 15px;
}

.btn-block {
  display: block;
  width: 100%;
  text-align: center;
  margin-top: 20px;
}
</style>
