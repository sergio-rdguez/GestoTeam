<template>
  <div class="teams-page">
    <div v-if="teams.length === 0" class="no-teams-message">
      <p>Es hora de crear tu primer equipo.</p>
      <router-link to="/teams/add" class="btn btn-primary">
        Crear Equipo
      </router-link>
    </div>

    <div v-else>
      <h2 class="page-title">Mis Equipos</h2>
      <ul class="team-list">
        <li v-for="team in teams" :key="team.id" class="team-item">
          <div class="team-info">
            <h3>{{ team.name }}</h3>
            <p>{{ team.categoryName }} - {{ team.division }}</p>
          </div>
          <button @click="viewTeam(team.id)" class="btn btn-secondary">
            Detalles
          </button>
        </li>
      </ul>

      <router-link to="/teams/add" class="btn btn-primary btn-block">
        Añadir Equipo
      </router-link>
    </div>
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
      this.$router.push({ name: "TeamDetails", params: { id: teamId } });
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
  font-family: 'Arial', sans-serif;
  background: #f9f9f9;
  min-height: 100vh;
}

.page-title {
  text-align: center;
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #333;
}

.no-teams-message {
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.no-teams-message p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 15px;
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
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.1);
}

.team-info h3 {
  font-size: 1.3rem;
  color: #333;
  margin: 0;
}

.team-info p {
  font-size: 1rem;
  color: #666;
  margin: 5px 0 0;
}

.btn {
  padding: 10px 15px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  display: block;
  margin: 20px auto;
  text-align: center;
  width: 100%;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-block {
  text-align: center;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.5rem;
  }

  .team-info h3 {
    font-size: 1.2rem;
  }

  .team-info p {
    font-size: 0.9rem;
  }

  .btn {
    font-size: 0.9rem;
    padding: 8px 12px;
  }
}
</style>
