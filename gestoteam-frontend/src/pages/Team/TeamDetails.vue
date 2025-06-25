<template>
  <div class="team-details-page">
    <div v-if="loading" class="loading-message">
      <p>Cargando detalles del equipo...</p>
    </div>
    <div v-else>
      <!-- Encabezado del equipo -->
      <div class="team-header">
        <h2 class="team-name">{{ team.name }}</h2>
        <p class="team-division">División: <strong>{{ team.division }}</strong></p>
      </div>

      <!-- Navegación a las secciones -->
      <div class="team-navigation">
        <router-link :to="{ name: 'TeamPlayers', params: { id: team.id } }" class="nav-item">
          Jugadores
        </router-link>
        <router-link :to="{ name: 'TeamMatches', params: { id: team.id } }" class="nav-item">
          Partidos
        </router-link>
        <!--
        <router-link :to="{ name: 'RivalsDetails', params: { id: team.id } }" class="nav-item">
          Rivales  
        </router-link>
        <router-link :to="{ name: 'TeamStatistics', params: { id: team.id } }" class="nav-item">
          Estadísticas
        </router-link>
        <router-link :to="{ name: 'TeamNotifications', params: { id: team.id } }" class="nav-item">
          Notificaciones
        </router-link>
        <router-link :to="{ name: 'TeamTrainings', params: { id: team.id } }" class="nav-item">
          Entrenamientos
        </router-link>
        -->
      </div>

      <!-- FAB Actions -->
      <div class="fab-container">
        <button class="fab" @click="toggleFabMenu">
          <i class="fa-solid fa-bars"></i>
        </button>
        <div v-if="showFabMenu" class="fab-actions">
          <button @click="goBack" class="fab-action">Volver</button>
          <button @click="editTeam" class="fab-action">Editar Equipo</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  data() {
    return {
      team: null,
      loading: true,
      showFabMenu: false, // Controla el estado del FAB
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
    toggleFabMenu() {
      this.showFabMenu = !this.showFabMenu;
    },
    goBack() {
      this.$router.push({ name: "Teams" }); // Navega a la lista de equipos
    },
    editTeam() {
      this.$router.push({ name: "EditTeam", params: { id: this.team.id } }); // Navega a la página de edición del equipo
    },
  },
  mounted() {
    this.fetchTeamDetails();
  },
};
</script>

<style scoped>
/* General */
.team-details-page {
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background-color: #f9f9f9;
  min-height: 100vh;
}

/* Loading Message */
.loading-message {
  text-align: center;
  font-size: 1.2rem;
  color: #666;
}

/* Team Header */
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

/* Navigation Links */
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

/* FAB Actions */
.fab-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column-reverse;
  align-items: flex-end;
}

.fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #007bff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.fab i {
  font-size: 1.5em;
  color: white;
}

.fab-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 10px;
}

.fab-action {
  background: white;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.fab-action:hover {
  background: #f1f1f1;
}

.fab-action.danger {
  background: #dc3545;
  color: white;
}

.fab-action.danger:hover {
  background: #c82333;
}

/* Responsive */
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
