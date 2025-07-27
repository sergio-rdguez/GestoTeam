<template>
  <div class="team-matches-page">
    <div class="header">
      <button class="back-button" @click="goBack">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h2 class="page-title">Partidos de {{ teamName }}</h2>
    </div>

    <div class="matches-list">
      <div
        v-for="match in sortedMatches"
        :key="match.id"
        class="match-item"
        @click="goToMatchDetails(match.id)"
      >
        <div class="match-content">
          <h4>{{ match.opponent }}</h4>
          <div class="match-details">
            <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
            <p><strong>Hora:</strong> {{ formatTime(match.date) }}</p>
            <p><strong>Estadio:</strong> {{ match.location }}</p>
          </div>
          <div
            class="match-status"
            :class="{
              victory: match.finalized && match.won,
              defeat: match.finalized && !match.won,
              pending: !match.finalized,
            }"
          >
            {{ match.finalized ? match.result : "Pendiente" }}
          </div>
        </div>
      </div>
      <div v-if="!sortedMatches.length && !loading" class="no-matches">
        <p>Aún no hay partidos para este equipo.</p>
      </div>
    </div>

    <button class="fab" @click="goToAddMatch">
      <i class="fa-solid fa-plus"></i>
    </button>

    <MessageBox
      v-if="showMessage"
      :message="message"
      :type="messageType"
      @close="closeMessage"
    />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import MessageBox from "@/pages/utils/MessageBox.vue";

export default {
  components: {
    MessageBox,
  },
  data() {
    return {
      teamName: "Cargando...",
      matches: [],
      loading: true,
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  computed: {
    sortedMatches() {
      // Ordena los partidos por fecha, de más reciente a más antiguo
      return [...this.matches].sort(
        (a, b) => new Date(b.date) - new Date(a.date)
      );
    },
  },
  methods: {
    async fetchMatches() {
      this.loading = true;
      try {
        const teamId = this.$route.params.id;
        // Primero obtenemos los datos del equipo para tener el nombre
        const teamResponse = await apiClient.get(`/teams/${teamId}`);
        this.teamName = teamResponse.data.name;

        // Luego obtenemos los partidos
        const matchesResponse = await apiClient.get(`/matches/team/${teamId}`);
        this.matches = matchesResponse.data;
      } catch (error) {
        this.message = "No se pudieron cargar los partidos del equipo.";
        this.messageType = "error";
        this.showMessage = true;
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push({
        name: "TeamDetails",
        params: { id: this.$route.params.id },
      });
    },
    goToAddMatch() {
      this.$router.push({
        name: "AddMatch",
        params: { teamId: this.$route.params.id },
      });
    },
    goToMatchDetails(matchId) {
      this.$router.push({ name: "MatchDetails", params: { id: matchId } });
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("es-ES", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
    formatTime(date) {
      return new Date(date).toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },
    closeMessage() {
      this.showMessage = false;
    },
  },
  mounted() {
    this.fetchMatches();
  },
};
</script>

<style scoped>
/* Estilos sin cambios */
@import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&family=Roboto:wght@400;500&display=swap");

.team-matches-page {
  padding: 40px;
  background: linear-gradient(135deg, #f0f4f8, #d9e2ec);
  min-height: 100vh;
  font-family: "Roboto", sans-serif;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
}
.back-button {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.3s ease, transform 0.2s ease;
}
.back-button i {
  margin-right: 5px;
}
.back-button:hover {
  background: #2980b9;
  transform: scale(1.05);
}
.page-title {
  text-align: center;
  font-size: 2.5rem;
  margin: 0;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  flex-grow: 1;
}
.matches-list {
  display: grid;
  gap: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.match-item {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.match-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}
.match-content h4 {
  margin: 0 0 10px;
  font-size: 1.5rem;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
}
.match-details p {
  margin: 5px 0;
  font-size: 1rem;
  color: #555;
}
.match-status {
  padding: 10px;
  font-size: 1rem;
  font-weight: 500;
  color: #fff;
  border-radius: 6px;
  text-align: center;
}
.match-status.pending { background-color: #3498db; }
.match-status.victory { background-color: #2ecc71; }
.match-status.defeat { background-color: #e74c3c; }
.no-matches {
  text-align: center;
  color: #666;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
}
.fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #007bff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
  z-index: 10;
}
.fab i {
  font-size: 1.5em;
  color: white;
}
.fab:hover {
  background-color: #0056b3;
  transform: scale(1.1);
}
</style>