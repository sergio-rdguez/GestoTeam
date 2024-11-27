<template>
  <div class="team-details-page">
    <div v-if="loading" class="loading-message">
      <p>Cargando...</p>
    </div>

    <div v-else>
      <!-- Encabezado del equipo -->
      <div class="team-header">
        <h2>{{ team.name }}</h2>
        <p class="division">División: <strong>{{ team.division }}</strong></p>
        <div class="actions">
          <button @click="goBack" class="btn btn-secondary">Volver a Mis Equipos</button>
          <button @click="addPlayer" class="btn btn-primary">Añadir Jugador</button>
        </div>
      </div>

      <!-- Lista de jugadores -->
      <div class="players-section">
        <h3 class="players-title">Jugadores</h3>
        <div class="player-list" v-if="players.length > 0">
          <div v-for="player in players" :key="player.id" class="player-card">
            <div class="player-info">
              <h4 class="player-name">{{ player.fullName }}</h4>
              <p class="player-details">{{ player.position }} | #{{ player.number }}</p>
            </div>
            <div class="player-status">
              <span :class="['status-label', player.status.toLowerCase()]">{{ player.status }}</span>
            </div>
          </div>
        </div>
        <p v-else class="no-players-message">No hay jugadores en este equipo.</p>
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
      players: [],
      loading: true,
    };
  },
  methods: {
    async fetchTeamDetails() {
      try {
        const teamId = this.$route.params.id;
        const response = await apiClient.get(`/teams/${teamId}`);
        this.team = response.data;

        const playersResponse = await apiClient.get(`/players/team/${teamId}`);
        this.players = playersResponse.data;
      } catch (error) {
        console.error("Error al cargar los detalles del equipo:", error);
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push({ name: "Teams" }); // Cambiar a la lista de equipos
    },
    addPlayer() {
      this.$router.push({ name: "AddPlayer", params: { teamId: this.$route.params.id } }); // Redirigir a añadir jugador
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
  background: #f9f9f9;
}

.team-header {
  text-align: center;
  margin-bottom: 20px;
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.team-header h2 {
  font-size: 1.8rem;
  color: #333;
}

.division {
  font-size: 1rem;
  color: #666;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 15px;
}

.actions .btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
}

.actions .btn-primary {
  background-color: #007bff;
  color: white;
}

.actions .btn-secondary {
  background-color: #6c757d;
  color: white;
}

.players-section {
  margin-top: 20px;
  padding: 10px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.players-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 15px;
}

.player-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.player-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f4f4f4;
  border-radius: 8px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
}

.player-info {
  display: flex;
  flex-direction: column;
}

.player-name {
  font-size: 1.2rem;
  font-weight: bold;
  color: #222;
}

.player-details {
  font-size: 0.9rem;
  color: #666;
}

.player-status {
  display: flex;
  align-items: center;
}

.status-label {
  padding: 5px 10px;
  font-size: 0.9rem;
  font-weight: bold;
  border-radius: 12px;
  color: white;
  text-transform: capitalize;
}

.status-label.activo {
  background-color: #28a745;
}

.status-label.inactivo {
  background-color: #dc3545;
}

.no-players-message {
  text-align: center;
  color: #666;
  font-size: 1rem;
  margin-top: 15px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .team-header h2 {
    font-size: 1.5rem;
  }

  .actions .btn {
    font-size: 0.9rem;
    padding: 8px 15px;
  }

  .player-card {
    padding: 8px;
  }

  .player-name {
    font-size: 1rem;
  }

  .player-details {
    font-size: 0.8rem;
  }

  .status-label {
    font-size: 0.8rem;
  }
}
</style>
