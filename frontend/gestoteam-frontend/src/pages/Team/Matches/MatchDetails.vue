<template>
  <div class="match-details-page">
    <h2 class="page-title">Detalles del Partido</h2>

    <div v-if="loading" class="loading-message">
      <p>Cargando datos del partido...</p>
    </div>

    <div v-else-if="match">
      <!-- Información del partido -->
      <div class="match-info card">
        <h3>{{ match.team.name }} vs {{ match.opponent }}</h3>
        <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
        <p><strong>Hora:</strong> {{ formatTime(match.date) }}</p>
        <p><strong>Estadio:</strong> {{ match.location }}</p>
        <p><strong>Estado:</strong> {{ match.isPast ? "Finalizado" : "Pendiente" }}</p>
      </div>

      <!-- Resultados -->
      <div class="match-result card" v-if="match.isPast">
        <h4>Resultado Final</h4>
        <p><strong>Marcador:</strong> {{ match.result }}</p>
      </div>

      <div class="edit-result card" v-else>
        <h4>Actualizar Resultado</h4>
        <form @submit.prevent="updateMatchResult">
          <label>Marcador Local</label>
          <input type="number" v-model.number="editableResult.homeGoals" required />
          <label>Marcador Visitante</label>
          <input type="number" v-model.number="editableResult.awayGoals" required />
          <button type="submit" class="btn primary">Guardar</button>
        </form>
      </div>

      <!-- Estadísticas del Partido -->
      <div class="player-match-stats card">
        <h4>Estadísticas del Partido</h4>
        <table>
          <thead>
            <tr>
              <th>Jugador</th>
              <th>Goles</th>
              <th>Minutos Jugados</th>
              <th>Amarillas</th>
              <th>Doble Amarillas</th>
              <th>Rojas</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="stat in match.playerStats" :key="stat.id">
              <td>{{ stat.playerFullName }}</td>
              <td>{{ stat.goals }}</td>
              <td>{{ stat.minutesPlayed }}</td>
              <td>{{ stat.yellowCard ? 1 : 0 }}</td>
              <td>{{ stat.doubleYellowCard ? 1 : 0 }}</td>
              <td>{{ stat.redCard ? 1 : 0 }}</td>
              <td>
                <button @click="editPlayerStats(stat.id)">Editar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

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
    async fetchMatchDetails() {
      try {
        const response = await apiClient.get(`/matches/details/${this.id}`);
        this.match = response.data;
        if (this.match.result) {
          const parts = this.match.result.split("-");
          this.editableResult.homeGoals = parseInt(parts[0], 10);
          this.editableResult.awayGoals = parseInt(parts[1], 10);
        }
      } catch (error) {
        console.error("Error al cargar los detalles del partido:", error);
        alert("No se pudieron cargar los detalles del partido.");
      } finally {
        this.loading = false;
      }
    },
    async updateMatchResult() {
      try {
        const newResult = `${this.editableResult.homeGoals}-${this.editableResult.awayGoals}`;
        const updatedMatch = { ...this.match, result: newResult };
        const response = await apiClient.put(`/matches/${this.match.id}`, updatedMatch);
        this.match = response.data;
        alert("Resultado actualizado correctamente.");
      } catch (error) {
        console.error("Error al actualizar el resultado del partido:", error);
        alert("No se pudo actualizar el resultado del partido.");
      }
    },
    editPlayerStats(statId) {
      this.$router.push({ name: "EditPlayerMatchStats", params: { id: statId } });
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

.match-info p,
.match-result p {
  font-size: 1rem;
  color: #555;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

table th,
table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

table th {
  background-color: #f2f2f2;
  color: #333;
}

button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
