<template>
  <div class="match-details-page">
    <div class="header">
      <button class="back-button" @click="goBackToTeamMatches">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h2 class="page-title">Detalles del Partido</h2>
    </div>

    <div v-if="loading" class="loading-message">
      <p>Cargando datos del partido...</p>
    </div>

    <div v-else-if="match">
      <div class="match-info card">
        <div class="card-header">
          <h3>Información del Partido</h3>
          <button
            v-if="!match.finalized"
            @click="editMode = !editMode"
            class="edit-button"
          >
            {{ editMode ? "Cancelar" : "Editar Partido" }}
          </button>
        </div>

        <div class="info-grid">
            <p><strong>Equipo Local:</strong> {{ match.team.name }}</p>
            <p><strong>Visitante:</strong> {{ match.opponent }}</p>
            <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
            <p><strong>Hora:</strong> {{ formatTime(match.date) }}</p>
            <p><strong>Estadio:</strong> {{ match.location }}</p>
            <p><strong>Estado:</strong> {{ match.finalized ? 'Finalizado' : 'Pendiente' }}</p>
        </div>
      </div>

      <div v-if="editMode && !match.finalized">
        <div class="match-result card">
            <h4>Resultado del Partido</h4>
            <div class="result-fields">
                <div class="score-inputs">
                    <div class="score-field">
                        <label>Local</label>
                        <input type="number" v-model.number="editableResult.home" />
                    </div>
                    <span>-</span>
                    <div class="score-field">
                        <label>Visitante</label>
                        <input type="number" v-model.number="editableResult.away" />
                    </div>
                </div>
            </div>
        </div>

        <div class="player-match-stats card">
          <h4>Estadísticas de Jugadores</h4>
          <table class="stats-table">
            <thead>
              <tr>
                <th>Jugador</th>
                <th>G</th>
                <th>TA</th>
                <th>TR</th>
                <th>MIN</th>
                <th>CONV</th>
                <th>TIT</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="stat in match.playerStats" :key="stat.id">
                <td>{{ stat.playerFullName }}</td>
                <td><input type="number" min="0" v-model.number="stat.goals" class="stat-input" /></td>
                <td><input type="checkbox" v-model="stat.yellowCard" class="stat-checkbox" /></td>
                <td><input type="checkbox" v-model="stat.redCard" class="stat-checkbox" /></td>
                <td><input type="number" min="0" max="90" v-model.number="stat.minutesPlayed" class="stat-input" /></td>
                <td><input type="checkbox" v-model="stat.calledUp" class="stat-checkbox" /></td>
                <td><input type="checkbox" v-model="stat.starter" class="stat-checkbox" /></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
       <div v-if="match.finalized" class="final-score card">
        <h3>Marcador Final</h3>
        <p class="score">{{ match.result }}</p>
      </div>


      <div class="actions">
        <button
          class="btn btn-save"
          @click="saveEverything"
          v-if="editMode && !match.finalized"
          :disabled="isSaving"
        >
          {{ isSaving ? "Guardando..." : "Guardar Cambios" }}
        </button>
        <button
          class="btn btn-finalize"
          v-if="!match.finalized"
          @click="finalizeMatch"
          :disabled="isSaving"
        >
          Finalizar Partido
        </button>
      </div>
    </div>
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
  components: { MessageBox },
  data() {
    return {
      match: null,
      loading: true,
      isSaving: false,
      editMode: false,
      editableResult: { home: 0, away: 0 },
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  methods: {
    async fetchMatchDetails() {
      this.loading = true;
      try {
        const matchId = this.$route.params.id;
        const { data } = await apiClient.get(`/matches/details/${matchId}`);
        this.match = data;
        if (data.result) {
            const [home, away] = data.result.split('-').map(Number);
            this.editableResult = { home, away };
        }
      } catch (error) {
        this.message = "No se pudieron cargar los detalles del partido.";
        this.messageType = "error";
        this.showMessage = true;
      } finally {
        this.loading = false;
      }
    },
    async saveEverything() {
      this.isSaving = true;
      try {
        const matchUpdatePayload = {
          date: this.match.date,
          location: this.match.location,
          opponentId: this.match.opponentId,
          result: `${this.editableResult.home}-${this.editableResult.away}`,
          won: this.editableResult.home > this.editableResult.away,
          finalized: this.match.finalized
        };
        await apiClient.put(`/matches/${this.match.id}`, matchUpdatePayload);

        const statsPromises = this.match.playerStats.map(stat =>
          apiClient.put(`/player-match-stats/${stat.id}`, stat)
        );
        await Promise.all(statsPromises);
        
        this.message = "Cambios guardados correctamente.";
        this.messageType = "success";
        this.showMessage = true;
        this.editMode = false;
        this.fetchMatchDetails(); // Recargar datos

      } catch (error) {
        this.message = "Error al guardar los cambios.";
        this.messageType = "error";
        this.showMessage = true;
      } finally {
        this.isSaving = false;
      }
    },
    async finalizeMatch() {
      if (confirm("¿Seguro que quieres finalizar el partido? Ya no podrás editar las estadísticas.")) {
        this.isSaving = true;
        try {
            const matchUpdatePayload = {
                ...this.match,
                result: `${this.editableResult.home}-${this.editableResult.away}`,
                won: this.editableResult.home > this.editableResult.away,
                finalized: true
            };
            await apiClient.put(`/matches/${this.match.id}`, matchUpdatePayload);

            this.message = "Partido finalizado.";
            this.messageType = "success";
            this.showMessage = true;
            this.editMode = false;
            await this.fetchMatchDetails(); // Recargar datos
        } catch (error) {
            this.message = "Error al finalizar el partido.";
            this.messageType = "error";
            this.showMessage = true;
        } finally {
            this.isSaving = false;
        }
      }
    },
    formatDate(isoString) {
      if (!isoString) return "";
      return new Date(isoString).toLocaleDateString("es-ES", {
        year: "numeric", month: "long", day: "numeric",
      });
    },
    formatTime(isoString) {
        if (!isoString) return "";
        return new Date(isoString).toLocaleTimeString("es-ES", {
            hour: '2-digit', minute: '2-digit'
        });
    },
    goBackToTeamMatches() {
      this.$router.push({
        name: "TeamMatches",
        params: { id: this.match.team.id },
      });
    },
    closeMessage() {
      this.showMessage = false;
    },
  },
  mounted() {
    this.fetchMatchDetails();
  },
};
</script>


<style scoped>
.match-details-page {
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
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}
.back-button:hover {
  background: #2980b9;
  transform: scale(1.05);
}
.page-title {
  text-align: center;
  font-size: 2.5rem;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
  flex-grow: 1;
}
.card {
  background: #fff;
  padding: 20px;
  margin-bottom: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
.card h3, .card h4 {
  font-family: "Montserrat", sans-serif;
  color: #2c3e50;
}
.info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 10px;
}
.info-grid p {
    margin: 5px 0;
}
.edit-button {
  background: #3498db;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.result-fields {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 15px;
}
.score-inputs {
    display: flex;
    align-items: center;
    gap: 10px;
}
.score-field input {
    width: 70px;
    text-align: center;
    font-size: 1.2rem;
    padding: 5px;
}
.stats-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}
.stats-table th, .stats-table td {
  padding: 8px;
  border-bottom: 1px solid #ddd;
}
.stats-table th {
  background: #f5f5f5;
}
.stat-input {
  width: 50px;
  text-align: center;
}
.stat-checkbox {
  transform: scale(1.2);
}
.final-score {
    text-align: center;
}
.final-score .score {
    font-size: 3rem;
    font-weight: bold;
    color: #2c3e50;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
}
.btn-save { background: #2ecc71; }
.btn-finalize { background: #e74c3c; }
</style>