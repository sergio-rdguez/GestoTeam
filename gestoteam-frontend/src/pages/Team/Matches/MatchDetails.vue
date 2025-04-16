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
      <!-- Información del Partido -->
      <div class="match-info card">
        <div class="card-header">
          <h3>Información del Partido</h3>
          <button
            v-if="!match.finalized"
            @click="editMode = !editMode"
            class="edit-button"
          >
            {{ editMode ? "Cancelar Edición" : "Editar" }}
          </button>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Equipo Local:</label>
          <input v-model="match.team.name" disabled />
        </div>
        <div v-else>
          <p><strong>Equipo Local:</strong> {{ match.team.name }}</p>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Equipo Visitante:</label>
          <input v-model="match.opponent" />
        </div>
        <div v-else>
          <p><strong>Equipo Visitante:</strong> {{ match.opponent }}</p>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Fecha:</label>
          <input type="date" v-model="match.date" />
        </div>
        <div v-else>
          <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Hora:</label>
          <input type="time" v-model="match.time" />
        </div>
        <div v-else>
          <p><strong>Hora:</strong> {{ match.time }}</p>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Estadio:</label>
          <input v-model="match.location" />
        </div>
        <div v-else>
          <p><strong>Estadio:</strong> {{ match.location }}</p>
        </div>

        <div v-if="editMode && !match.finalized" class="form-group">
          <label>Estado:</label>
          <select v-model="match.status">
            <option value="Pendiente">Pendiente</option>
            <option value="En Curso">En Curso</option>
            <option value="Finalizado">Finalizado</option>
          </select>
        </div>
        <div v-else>
          <p><strong>Estado:</strong> {{ match.status }}</p>
        </div>

        <div v-if="match.finalized" class="finalized-message">
          <p>Este partido ya está finalizado y no es editable.</p>
        </div>
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
                :disabled="match.finalized || !editMode"
              />
            </div>
            <div class="score-field">
              <label>Marcador Visitante</label>
              <input
                type="number"
                v-model.number="editableResult.awayGoals"
                :disabled="match.finalized || !editMode"
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
                  v-model.number="stat.goals"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-input"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.calledUp"
                  :disabled="match.finalized || !editMode"
                  class="stat-checkbox"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.starter"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-checkbox"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.yellowCard"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-checkbox"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.doubleYellowCard"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-checkbox"
                />
              </td>
              <td>
                <input
                  type="checkbox"
                  v-model="stat.redCard"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-checkbox"
                />
              </td>
              <td>
                <input
                  type="number"
                  min="0"
                  v-model.number="stat.goalsConceded"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-input"
                />
              </td>
              <td>
                <input
                  type="number"
                  min="0"
                  max="90"
                  v-model.number="stat.minutesPlayed"
                  :disabled="match.finalized || !editMode || !stat.calledUp"
                  class="stat-input"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Botones de Acción -->
      <div class="actions">
        <button
          class="btn btn-save"
          @click="saveEverything"
          :disabled="match.finalized || isSaving || !editMode"
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
export default {
  data() {
    return {
      match: null,
      loading: true,
      isSaving: false,
      editMode: false,
      editableResult: { homeGoals: 0, awayGoals: 0 },
    };
  },
  mounted() {
    this.fetchMatchDetails();
  },
  methods: {
    async fetchMatchDetails() {
      // Simulación de datos para este ejemplo
      this.match = {
        team: { name: "SAD Marianistas Amorós B", id: 2 }, // Added id for navigation
        opponent: "C.F. Águilas del Lucero 'A'",
        date: "2025-06-07",
        time: "18:30",
        location: "Polideportivo Amorós",
        status: "Pendiente",
        finalized: false,
        result: "0-0",
        playerStatsFull: [
          {
            playerId: 1,
            playerFullName: "Ramiro Martín Del Moral",
            goals: 0,
            calledUp: false,
            starter: false,
            yellowCard: false,
            doubleYellowCard: false,
            redCard: false,
            goalsConceded: 0,
            minutesPlayed: 0,
          },
          {
            playerId: 2,
            playerFullName: "Hugo Hernández González",
            goals: 0,
            calledUp: false,
            starter: false,
            yellowCard: false,
            doubleYellowCard: false,
            redCard: false,
            goalsConceded: 0,
            minutesPlayed: 0,
          },
          {
            playerId: 3,
            playerFullName: "David Correas Oliver",
            goals: 0,
            calledUp: false,
            starter: false,
            yellowCard: false,
            doubleYellowCard: false,
            redCard: false,
            goalsConceded: 0,
            minutesPlayed: 0,
          },
        ],
      };
      this.editableResult = { homeGoals: 0, awayGoals: 0 };
      this.loading = false;
    },
    async saveEverything() {
      this.isSaving = true;
      try {
        this.match.result = `${this.editableResult.homeGoals}-${this.editableResult.awayGoals}`;
        console.log("Guardando:", this.match);
        alert("Todo guardado correctamente.");
        this.editMode = false;
      } catch (error) {
        alert("Error al guardar.");
      } finally {
        this.isSaving = false;
      }
    },
    async finalizeMatch() {
      if (
        confirm(
          "¿Seguro que quieres finalizar el partido? Esto desactivará la edición."
        )
      ) {
        this.match.finalized = true;
        this.editMode = false;
        alert("Partido finalizado.");
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("es-ES", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
    rowClass(stat) {
      if (!stat.calledUp) return "not-called-row";
      if (stat.starter) return "starter-row";
      return "";
    },
    goBackToTeamMatches() {
      this.$router.push({
        name: "TeamMatches",
        params: { id: this.match.team.id }, 
      });
    },
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
  margin-bottom: 30px;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
  flex-grow: 1; /* Allows the title to take up remaining space */
}

.loading-message {
  text-align: center;
  font-size: 1.1rem;
  color: #666;
}

.card {
  background: #fff;
  padding: 20px;
  margin-bottom: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card h3 {
  font-size: 1.8rem;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
}

.card h4 {
  font-size: 1.5rem;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.finalized-message {
  color: #e74c3c;
  font-style: italic;
  margin-top: 10px;
}

.score-inputs {
  display: flex;
  gap: 20px;
}

.score-field label {
  font-weight: bold;
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
  color: #555;
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.stats-table th,
.stats-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}

.stats-table th {
  background: #2c3e50;
  color: #fff;
  font-weight: bold;
}

.stat-input {
  width: 60px;
  padding: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.stat-checkbox {
  cursor: pointer;
}

.not-called-row {
  background: #fce4e4;
}

.starter-row {
  background: #e6f4ea;
}

.actions {
  display: flex;
  justify-content: space-between;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.btn-save {
  background: #2ecc71;
}

.btn-save:hover {
  background: #27ae60;
  transform: scale(1.05);
}

.btn-finalize {
  background: #e74c3c;
}

.btn-finalize:hover {
  background: #c0392b;
  transform: scale(1.05);
}

.edit-button {
  background: #3498db;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit-button:hover {
  background: #2980b9;
}

/* Responsividad */
@media (max-width: 768px) {
  .match-details-page {
    padding: 20px;
  }
  .header {
    flex-direction: column;
    align-items: flex-start;
  }
  .back-button {
    margin-bottom: 15px;
    margin-right: 0;
  }
  .page-title {
    font-size: 2rem;
    text-align: left;
  }
  .card {
    padding: 15px;
  }
  .card h3 {
    font-size: 1.5rem;
  }
  .card h4 {
    font-size: 1.3rem;
  }
  .form-group input,
  .form-group select {
    padding: 6px;
  }
  .score-field input {
    width: 80px;
  }
  .stats-table th,
  .stats-table td {
    padding: 8px;
    font-size: 0.9rem;
  }
  .btn {
    padding: 10px 20px;
    font-size: 0.9rem;
  }
}
</style>
