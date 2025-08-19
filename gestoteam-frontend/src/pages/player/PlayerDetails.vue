<template>
  <div class="player-details-page">
    <PageHeader title="Ficha del Jugador" show-back-button @back="goBack" />
    
    <div v-if="loading" class="loading-overlay">
      <p>Cargando...</p>
    </div>

    <div v-else class="player-details-container">
      <BaseCard>
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
      </BaseCard>

      <div class="player-stats">
        <BaseCard title="Partidos">
            <p>Convocados: <strong>{{ player.stats.matches.convoked }}</strong></p>
            <p>Titular: <strong>{{ player.stats.matches.starter }}</strong></p>
            <p>Suplente: <strong>{{ player.stats.matches.substitute }}</strong></p>
            <p>Jugados: <strong>{{ player.stats.matches.played }}</strong></p>
        </BaseCard>
        <BaseCard title="Goles">
            <p>Total: <strong>{{ player.stats.goals.total }}</strong></p>
            <p>Promedio: <strong>{{ player.stats.goals.average.toFixed(2) }}</strong></p>
        </BaseCard>
        <BaseCard title="Tarjetas">
            <div class="cards">
              <div class="card-item">
                <i class="fas fa-square yellow-card"></i>
                <p><strong>Amarillas: {{ player.stats.cards.yellow }}</strong></p>
              </div>
              <div class="card-item">
                <i class="fas fa-square red-card"></i>
                <p><strong>Rojas: {{ player.stats.cards.red }}</strong></p>
              </div>
              <div class="card-item">
                <i class="fas fa-square double-yellow-card"></i>
                <p><strong>Doble Amarillas: {{ player.stats.cards.doubleYellow }}</strong></p>
              </div>
            </div>
        </BaseCard>
      </div>
    </div>
    
    <FabMenu :actions="fabActions" @action-clicked="handleFabAction" />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import FabMenu from "@/components/common/FabMenu.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";

export default {
  components: {
    FabMenu,
    PageHeader,
    BaseCard,
  },
  data() {
    return {
      player: null,
      loading: true,
      defaultPhoto: require('@/assets/default-player-photo.png'),
      fabActions: [
        { label: "Editar", event: "edit-player" },
        { label: "Eliminar", event: "delete-player", class: "danger" },
      ]
    };
  },
  methods: {
    async fetchPlayerDetails() {
      this.loading = true;
      try {
        const playerId = this.$route.params.playerId;
        const response = await apiClient.get(`/players/${playerId}`);
        this.player = response.data;
      } catch (error) {
        console.error("Error al cargar los detalles del jugador:", error);
      } finally {
        this.loading = false;
      }
    },
    handleFabAction(event) {
        if (event === 'edit-player') this.editPlayer();
        else if (event === 'delete-player') this.deletePlayer();
    },
    goBack() {
      if (this.player && this.player.team) {
        this.$router.push({ name: "TeamPlayers", params: { teamId: this.player.team.id } });
      } else {
        this.$router.push({ name: 'Teams' });
      }
    },
    editPlayer() {
      this.$router.push({ name: "EditPlayer", params: { playerId: this.player.id,  teamId: this.player.team.id  } });
    },
    async deletePlayer() {
      if (confirm("¿Estás seguro de que deseas eliminar este jugador?")) {
        this.loading = true;
        try {
          await apiClient.delete(`/players/${this.player.id}`);
          this.goBack();
        } catch (error) {
          console.error("Error al eliminar el jugador:", error);
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
.player-details-page {
  padding: 2rem;
  background-color: #f9fafb;
}
.loading-overlay {
  /* ... estilos para el loading ... */
}
.player-details-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}
.player-info {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}
.player-photo img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid #007bff;
  object-fit: cover;
}
.basic-info p {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}
.player-status {
  font-size: 0.9rem;
  font-weight: bold;
  padding: 5px 12px;
  border-radius: 15px;
  color: white;
  display: inline-block;
}
.player-status.activo { background-color: #28a745; }
.player-status.lesionado { background-color: #dc3545; }
.player-status.suspendido { background-color: #ffc107; }
.player-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}
.cards {
  display: flex;
  justify-content: space-around;
  text-align: center;
}
.card-item i {
  font-size: 2.5rem;
}
.yellow-card { color: #ffc107; }
.red-card { color: #dc3545; }
.double-yellow-card { color: #ff8c00; }

@media (min-width: 768px) {
    .player-details-container {
        grid-template-columns: 1fr 2fr;
    }
}
</style>