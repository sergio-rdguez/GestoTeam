<template>
  <div class="match-details-page">
    <h2 class="page-title">Detalles del Partido</h2>

    <div v-if="loading" class="loading-message">
      <p>Cargando datos del partido...</p>
    </div>

    <div v-else-if="match">
      <!-- Información del partido -->
      <div class="match-info card">
        <h3>
          {{ match.team ? match.team.name : "Equipo no definido" }}
          vs
          {{ match.opponent }}
        </h3>
        <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
        <p><strong>Hora:</strong> {{ formatTime(match.date) }}</p>
        <p><strong>Estadio:</strong> {{ match.location }}</p>
        <p>
          <strong>Estado:</strong>
          {{ match.finalized ? "Finalizado" : "Pendiente" }}
        </p>
      </div>

      <!-- Resultado del Partido -->
      <div class="match-result card">
        <h4>Resultado del Partido</h4>
        <div class="result-fields">
          <div class="score-inputs">
            <div class="score-field">
              <label>Marcador Local</label>
              <input
                type="number"
                v-model.number="editableResult.homeGoals"
                required
                :disabled="match.finalized"
              />
            </div>
            <div class="score-field">
              <label>Marcador Visitante</label>
              <input
                type="number"
                v-model.number="editableResult.awayGoals"
                required
                :disabled="match.finalized"
              />
            </div>
          </div>
          <div v-if="match.result" class="final-result">
            <p><strong>Marcador Actual:</strong> {{ match.result }}</p>
          </div>
        </div>
      </div>

      <!-- Estadísticas del Partido -->
      <div class="player-match-stats card">
        <h4>Estadísticas del Partido</h4>
        <table class="stats-table">
          <thead>
            <tr>
              <th>Jugador</th>
              <th>Goles</th>
              <th>Convocado</th>
              <th>Titular</th>
              <th>Amarilla</th>
              <th>Doble Amarilla</th>
              <th>Roja</th>
              <th>Goles Recibidos</th>
              <th>Minutos Jugados</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="stat in match.playerStatsFull"
              :key="stat.playerId"
              :class="rowClass(stat)"
            >
              <td>{{ stat.playerFullName }}</td>
              <td>
                <input
                  type="number"
                  min="0"
                  class="stat-input"
                  v-model.number="stat.goals"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.calledUp"
                  class="stat-checkbox"
                  :disabled="match.finalized"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.starter"
                  class="stat-checkbox"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.yellowCard"
                  class="stat-checkbox"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.doubleYellowCard"
                  class="stat-checkbox"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.redCard"
                  class="stat-checkbox"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="number"
                  min="0"
                  class="stat-input"
                  v-model.number="stat.goalsConceded"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
              <td>
                <input
                  type="number"
                  min="0"
                  max="90"
                  class="stat-input"
                  v-model.number="stat.minutesPlayed"
                  :disabled="!stat.calledUp || match.finalized"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Botones de acción -->
      <div class="actions">
        <button
          class="btn btn-save"
          @click="saveEverything"
          :disabled="match.finalized || isSaving"
        >
          Guardar Todo
        </button>
        <button
          class="btn btn-finalize"
          v-if="!match.finalized"
          @click="finalizeMatch"
        >
          Finalizar Partido
        </button>
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
      isSaving: false,
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
        
        if (Array.isArray(this.match.playerStats)) {
          this.match.playerStatsFull = [...this.match.playerStats];
        } else {
          this.match.playerStatsFull = [];
          if (this.match.team) {
            await this.fetchTeamPlayers(this.match.team.id);
          }
        }
      } catch (error) {
        console.error("Error al cargar los detalles del partido:", error);
        alert("No se pudieron cargar los detalles del partido.");
      } finally {
        this.loading = false;
      }
    },

    async fetchTeamPlayers(teamId) {
      try {
        const response = await apiClient.get(`/players/team/${teamId}`);
        const players = response.data.players || [];
        players.forEach((p) => {
          const existingStat = this.match.playerStatsFull.find(
            (s) => s.playerId === p.id
          );
          if (!existingStat) {
            this.match.playerStatsFull.push({
              id: null,
              playerId: p.id,
              playerFullName: p.name,
              goals: 0,
              minutesPlayed: 0,
              yellowCard: false,
              doubleYellowCard: false,
              redCard: false,
              goalsConceded: 0,
              ownGoals: 0,
              calledUp: false,
              starter: false,
            });
          }
        });
      } catch (error) {
        console.error("Error al cargar jugadores del equipo:", error);
        alert("No se pudo cargar la lista de jugadores.");
      }
    },

    async updateMatchResult() {
      try {
        const newResult = `${this.editableResult.homeGoals}-${this.editableResult.awayGoals}`;
        const currentStats = this.match.playerStatsFull || [];
        const updatedMatch = {
          ...this.match,
          result: newResult,
          finalized: this.match.finalized,
        };
        const response = await apiClient.put(
          `/matches/${this.match.id}`,
          updatedMatch
        );
        this.match = response.data;
        this.match.playerStatsFull = currentStats;
      } catch (error) {
        console.error("Error al actualizar el resultado del partido:", error);
        throw error;
      }
    },

    async saveAllStats() {
      try {
        for (const stat of this.match.playerStatsFull) {
          if (!stat.id) {
            await apiClient.post("/player-match-stats", {
              matchId: this.match.id,
              playerId: stat.playerId,
              goals: stat.goals,
              minutesPlayed: stat.minutesPlayed,
              yellowCard: stat.yellowCard,
              doubleYellowCard: stat.doubleYellowCard,
              redCard: stat.redCard,
              goalsConceded: stat.goalsConceded,
              ownGoals: stat.ownGoals,
              calledUp: stat.calledUp,
              starter: stat.starter,
            });
          } else {
            await apiClient.put(`/player-match-stats/${stat.id}`, {
              ...stat,
            });
          }
        }
      } catch (error) {
        console.error("Error al guardar las estadísticas:", error);
        throw error;
      }
    },

    async saveEverything() {
      if (this.isSaving) return;
      this.isSaving = true;
      try {
        await this.updateMatchResult();
        await this.saveAllStats();
        alert("Todo guardado correctamente.");
      } catch (error) {
        alert("No se pudo guardar todo.");
      } finally {
        this.isSaving = false;
      }
    },

    async finalizeMatch() {
      try {
        this.match.finalized = true;
        await this.updateMatchResult();
        alert("El partido ha sido finalizado y no es editable.");
      } catch (error) {
        alert("No se pudo finalizar el partido.");
      }
    },

    rowClass(stat) {
      if (!stat.calledUp) return "not-called-row";
      if (stat.starter) return "starter-row";
      return "";
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
  min-height: 100vh;
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.loading-message {
  text-align: center;
  font-size: 1.1rem;
  color: #666;
}

.card {
  background: white;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.card h4 {
  font-size: 1.4rem;
  margin-bottom: 10px;
  color: #333;
}

.score-inputs {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.score-field {
  display: flex;
  flex-direction: column;
}

.score-field label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.score-field input {
  width: 100px;
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.final-result {
  margin-top: 10px;
  font-size: 0.95rem;
  color: #555;
}

.player-match-stats {
  margin-top: 20px;
}

.player-match-stats table.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.player-match-stats th,
.player-match-stats td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #ddd;
  vertical-align: middle;
}

.player-match-stats thead th {
  background-color: #f5f5f5;
  color: #333;
  border-bottom: 2px solid #ddd;
  font-weight: bold;
}

.stat-input {
  width: 60px;
  border: 1px solid #ccc;
  padding: 4px;
  border-radius: 4px;
}

.stat-checkbox {
  cursor: pointer;
}

.not-called-row {
  background-color: #f8d7da;
  opacity: 0.9;
}

.starter-row {
  background-color: #d4edda;
  opacity: 0.9;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-save {
  background-color: #28a745;
  color: white;
}

.btn-save:hover {
  background-color: #218838;
}

.btn-finalize {
  background-color: #dc3545;
  color: white;
}

.btn-finalize:hover {
  background-color: #c82333;
}
</style>
