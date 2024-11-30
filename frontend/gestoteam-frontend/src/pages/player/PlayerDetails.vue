<template>
  <div class="player-details-page">
    <div v-if="loading" class="loading-overlay">
      <p>Procesando...</p>
    </div>
    <div v-else>
      <h1 class="page-title">Ficha del Jugador</h1>

      <div class="player-details-container">
        <!-- Información básica -->
        <div class="player-info">
          <div class="player-photo">
            <img :src="player.photoUrl || defaultPhoto" alt="Foto del jugador" />
          </div>
          <div class="basic-info">
            <p><strong>Nombre:</strong> {{ player.fullName }}</p>
            <p><strong>Edad:</strong> {{ player.age }} años</p>
            <p><strong>Equipo:</strong> {{ player.team.name }}</p>
            <p><strong>Categoría:</strong> {{ player.team.category }}</p>
            <p><span class="player-status" :class="player.status.toLowerCase()">{{ player.status }}</span></p>
          </div>
        </div>

        <!-- Estadísticas -->
        <div class="player-stats">
          <div class="stats-section">
            <h2 class="section-title">Partidos</h2>
            <p>Convocados: <strong>{{ player.stats.matches.convoked }}</strong></p>
            <p>Titular: <strong>{{ player.stats.matches.starter }}</strong></p>
            <p>Suplente: <strong>{{ player.stats.matches.substitute }}</strong></p>
            <p>Jugados: <strong>{{ player.stats.matches.played }}</strong></p>
          </div>
          <div class="stats-section">
            <h2 class="section-title">Goles</h2>
            <p>Total: <strong>{{ player.stats.goals.total }}</strong></p>
            <p>Promedio: <strong>{{ player.stats.goals.average }}</strong></p>
          </div>
          <div class="stats-section">
            <h2 class="section-title">Tarjetas</h2>
            <div class="cards">
              <div class="card">
                <i class="fas fa-square yellow-card"></i>
                <p><strong>Amarillas: {{ player.stats.cards.yellow }}</strong></p>
              </div>
              <div class="card">
                <i class="fas fa-square red-card"></i>
                <p><strong>Rojas: {{ player.stats.cards.red }}</strong></p>
              </div>
              <div class="card">
                <i class="fas fa-square double-yellow-card"></i>
                <p><strong>Doble Amarillas: {{ player.stats.cards.doubleYellow }}</strong></p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Botón flotante -->
      <div class="fab-container">
        <button class="fab" @click="toggleFabMenu">
          <i class="fa-solid fa-bars"></i>
        </button>
        <div v-if="showFabMenu" class="fab-actions">
          <button @click="goBack" class="fab-action">Volver</button>
          <button @click="editPlayer" class="fab-action">Editar</button>
          <button @click="deletePlayer" class="fab-action danger">Eliminar</button>
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
      player: null,
      loading: true,
      showFabMenu: false,
      defaultPhoto: require('@/assets/default-player-photo.png'),
    };
  },
  methods: {
    async fetchPlayerDetails() {
      try {
        const playerId = this.$route.params.id;
        const response = await apiClient.get(`/players/${playerId}`);
        this.player = response.data;
      } catch (error) {
        console.error("Error al cargar los detalles del jugador:", error);
        alert("No se pudieron cargar los detalles del jugador.");
      } finally {
        this.loading = false;
      }
    },
    toggleFabMenu() {
      this.showFabMenu = !this.showFabMenu;
    },
    goBack() {
      this.$router.push({ name: "TeamPlayers", params: { id: this.player.team.id } });
    },
    editPlayer() {
      this.$router.push({ name: "EditPlayer", params: { id: this.player.id } });
    },
    async deletePlayer() {
      if (confirm("¿Estás seguro de que deseas eliminar este jugador?")) {
        this.loading = true;
        try {
          await apiClient.delete(`/players/${this.player.id}`);
          alert("Jugador eliminado correctamente.");
          this.goBack();
        } catch (error) {
          console.error("Error al eliminar el jugador:", error);
          alert("No se pudo eliminar el jugador. Inténtalo de nuevo.");
        } finally {
          this.loading = false;
        }
      }
    }
  },
  mounted() {
    this.fetchPlayerDetails();
  },
};
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  color: #007bff;
  z-index: 1000;
}

.player-details-page {
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.player-details-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.player-info,
.player-stats {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* Player Status */
.player-status {
  font-size: 0.9rem;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 12px;
  text-transform: capitalize;
  color: white;
  display: inline-block;
  /* Para que sea un bloque alineado en línea */
}

.player-status.activo {
  background-color: #28a745;
  /* Verde */
}

.player-status.lesionado {
  background-color: #dc3545;
  /* Rojo */
}

.player-status.suspendido {
  background-color: #ffc107;
  /* Amarillo */
}

.player-photo img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 10px;
  border: 2px solid #007bff;
}

.basic-info p {
  font-size: 1rem;
  margin-bottom: 8px;
  color: #333;
}

.basic-info strong {
  color: #007bff;
}

.section-title {
  font-size: 1.5rem;
  color: #007bff;
  margin-bottom: 10px;
}

.stats-section p {
  font-size: 1rem;
  margin-bottom: 5px;
}

.stats-section strong {
  color: #333;
}

.cards {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.card {
  display: flex;
  align-items: center;
  flex-direction: column;
  gap: 5px;
  background: none;
  /* Elimina cualquier fondo */
  border: none;
  /* Elimina cualquier borde */
  box-shadow: none;
  /* Elimina cualquier sombra */
}

.card i {
  font-size: 2.5rem;
  /* Ajusta el tamaño del icono */
  color: #007bff;
  /* Azul predeterminado para los iconos */
  margin: 0;
  padding: 0;
  background: none;
  /* Asegura que no haya fondo */
  border: none;
  /* Asegura que no haya borde */
  box-shadow: none;
  /* Elimina sombras adicionales */
  display: inline-block;
  /* Asegura un comportamiento consistente */
}

.card i.yellow-card {
  color: #ffc107;
  /* Amarillo */
}

.card i.red-card {
  color: #dc3545;
  /* Rojo */
}

.card i.double-yellow-card {
  color: #ff8c00;
  /* Naranja oscuro */
}

.card p {
  font-size: 0.9rem;
  text-align: center;
  color: #333;
  margin: 0;
}

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

@media (max-width: 768px) {
  .player-details-container {
    flex-direction: column;
  }

  .player-photo img {
    width: 80px;
    height: 80px;
  }
}
</style>
