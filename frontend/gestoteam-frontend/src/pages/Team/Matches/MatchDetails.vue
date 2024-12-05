<template>
  <div class="match-details-page">
    <h2 class="page-title">Detalles del Partido</h2>

    <!-- Mensaje de carga -->
    <div v-if="loading" class="loading-message">
      <p>Cargando datos del partido...</p>
    </div>

    <!-- Contenido principal -->
    <div v-else-if="match">
      <!-- Información del partido -->
      <div class="match-info card">
        <h3>{{ match.teamHome }} vs {{ match.teamAway }}</h3>
        <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
        <p><strong>Hora:</strong> {{ match.time }}</p>
        <p><strong>Estadio:</strong> {{ match.venue }}</p>
        <p><strong>Estado:</strong> {{ match.isPast ? "Finalizado" : "Pendiente" }}</p>
      </div>

      <!-- Resultados -->
      <div class="match-result card" v-if="match.isPast">
        <h4>Resultado Final</h4>
        <p><strong>Marcador:</strong> {{ match.stats.goals }}</p>
      </div>

      <div class="edit-result card" v-else>
        <h4>Actualizar Resultado</h4>
        <form @submit.prevent="updateMatchResult">
          <label>Marcador Local</label>
          <input type="number" v-model="editableResult.homeGoals" required />
          <label>Marcador Visitante</label>
          <input type="number" v-model="editableResult.awayGoals" required />
          <button type="submit" class="btn primary">Guardar</button>
        </form>
      </div>

      <!-- Titulares y Suplentes -->
      <div class="lineups-container">
        <div class="lineup card">
          <h4>Titulares</h4>
          <ul>
            <li v-for="player in match.lineup.starters" :key="player">{{ player }}</li>
          </ul>
        </div>
        <div class="lineup card">
          <h4>Suplentes</h4>
          <ul>
            <li v-for="player in match.lineup.substitutes" :key="player">{{ player }}</li>
          </ul>
        </div>
      </div>

      <!-- Sustituciones -->
      <div class="substitutions card" v-if="match.isPast">
        <h4>Sustituciones</h4>
        <ul>
          <li v-for="sub in match.substitutions" :key="sub.minute">
            <strong>{{ sub.minute }}'</strong>: {{ sub.in }} entra por {{ sub.out }}
          </li>
        </ul>
      </div>

      <!-- Goles -->
      <div class="match-stats card">
        <h4>Goles</h4>
        <ul>
          <li v-for="goal in match.stats.goalsDetails" :key="goal.minute">
            <strong>{{ goal.minute }}'</strong>: {{ goal.player }} ({{ goal.team }})
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    id: Number,
  },
  data() {
    return {
      match: null,
      loading: true,
      editableResult: {
        homeGoals: 0,
        awayGoals: 0,
      },
    };
  },
  mounted() {
    this.fetchMatchDetails();
  },
  methods: {
    fetchMatchDetails() {
      // Simulación de datos de partido
      setTimeout(() => {
        this.match = {
          id: this.id,
          teamHome: "Equipo Local",
          teamAway: "Equipo Visitante",
          date: "2024-12-01",
          time: "18:00",
          venue: "Estadio Local",
          isPast: this.id % 2 === 0,
          stats: {
            goals: "2-1",
            goalsDetails: [
              { minute: 10, player: "Jugador A", team: "Equipo Local" },
              { minute: 55, player: "Jugador B", team: "Equipo Visitante" },
            ],
          },
          lineup: {
            starters: ["Jugador 1", "Jugador 2", "Jugador 3"],
            substitutes: ["Suplente 1", "Suplente 2"],
          },
          substitutions: [
            { minute: 60, in: "Jugador 4", out: "Jugador 3" },
          ],
        };
        this.editableResult.homeGoals = 2;
        this.editableResult.awayGoals = 1;
        this.loading = false;
      }, 1000);
    },
    updateMatchResult() {
      // Actualizar datos del partido con el resultado editado
      this.match.stats.goals = `${this.editableResult.homeGoals}-${this.editableResult.awayGoals}`;
      alert("Resultado actualizado correctamente.");
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("es-ES", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
  },
};
</script>

<style scoped>
.match-details-page {
  padding: 20px;
  background-color: #f9f9f9;
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.card {
  background: white;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.card h4 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #333;
}

.lineups-container {
  display: flex;
  gap: 20px;
}

.lineup ul {
  padding: 0;
  margin: 0;
  list-style: none;
}

.lineup li {
  margin-bottom: 5px;
  font-size: 1rem;
  color: #555;
}

.substitutions ul,
.match-stats ul {
  padding: 0;
  margin: 0;
  list-style: none;
}

.substitutions li,
.match-stats li {
  margin-bottom: 5px;
  font-size: 1rem;
  color: #555;
}
</style>