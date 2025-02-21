<template>
  <div class="add-match-page">
    <h2 class="page-title">Agregar Partido</h2>

    <div class="form-card">
      <form @submit.prevent="addMatch">
        <div class="form-group">
          <label for="opponent">Oponente</label>
          <select v-model="newMatch.opponentId" id="opponent" required>
            <option disabled value="">Seleccione un oponente</option>
            <option v-for="opp in opponents" :key="opp.id" :value="opp.id">
              {{ opp.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label for="date">Fecha</label>
          <input v-model="newMatch.date" type="date" id="date" required />
        </div>
        <div class="form-group">
          <label for="time">Hora</label>
          <input v-model="newMatch.time" type="time" id="time" required />
        </div>
        <div class="form-group">
          <label for="venue">Estadio</label>
          <input v-model="newMatch.venue" type="text" id="venue" required />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn primary">Guardar Partido</button>
          <button type="button" class="btn secondary" @click="goBack">
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  data() {
    return {
      newMatch: {
        opponentId: "",
        date: "",
        time: "",
        venue: "",
      },
      opponents: [],
    };
  },
  methods: {
    async fetchOpponents() {
      try {
        const response = await apiClient.get(
          `/opponents/team/${this.$route.params.teamId}`
        );
        this.opponents = response.data;
      } catch (error) {
        console.error("Error al cargar la lista de oponentes:", error);
        alert("No se pudo cargar la lista de oponentes.");
      }
    },
    async addMatch() {
      try {
        if (!this.newMatch.opponentId) {
          alert("Debes seleccionar un oponente.");
          return;
        }

        const dateTime = new Date(
          `${this.newMatch.date}T${this.newMatch.time}`
        );
        const selectedOpponent = this.opponents.find(
          (opp) => opp.id === this.newMatch.opponentId
        );

        const matchPayload = {
          opponent: selectedOpponent.name,
          date: dateTime.toISOString(),
          location: this.newMatch.venue,
          result: "",
          finalized: dateTime < new Date(),
          teamId: this.$route.params.teamId,
        };

        await apiClient.post("/matches", matchPayload);

        alert("Partido agregado con éxito.");
        this.$router.push({
          name: "TeamMatches",
          params: { id: this.$route.params.teamId },
        });
      } catch (error) {
        console.error("Error al agregar el partido:", error);
        alert("No se pudo agregar el partido.");
      }
    },
    goBack() {
      this.$router.push({
        name: "TeamMatches",
        params: { id: this.$route.params.teamId },
      });
    },
  },
  mounted() {
    this.fetchOpponents();
  },
};
</script>
<style scoped>
.add-match-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
  background-color: #f2f2f2;
  min-height: 100vh;
}

.page-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 20px;
}

.form-card {
  background-color: #fff;
  width: 100%;
  max-width: 500px;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.form-group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #555;
}

.form-group input,
.form-group select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn.primary {
  background-color: #007bff;
  color: #fff;
}

.btn.primary:hover {
  background-color: #0056b3;
}

.btn.secondary {
  background-color: #6c757d;
  color: #fff;
}

.btn.secondary:hover {
  background-color: #565e64;
}

/* Responsividad */
@media (max-width: 480px) {
  .form-card {
    padding: 15px;
  }
  .btn {
    padding: 8px 16px;
    font-size: 0.9rem;
  }
}
</style>
