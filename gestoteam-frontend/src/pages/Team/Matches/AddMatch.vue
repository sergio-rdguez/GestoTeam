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
          <label for="location">Estadio</label>
          <input v-model="newMatch.location" type="text" id="location" required />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn primary">Guardar Partido</button>
          <button type="button" class="btn secondary" @click="goBack">
            Cancelar
          </button>
        </div>
      </form>
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
  components: {
    MessageBox,
  },
  data() {
    return {
      newMatch: {
        opponentId: "",
        date: "",
        time: "",
        location: "",
      },
      opponents: [],
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  methods: {
    async fetchOpponents() {
      try {
        const teamId = this.$route.params.teamId;
        const response = await apiClient.get(`/opponents/team/${teamId}`);
        this.opponents = response.data;
      } catch (error) {
        this.message = "No se pudo cargar la lista de oponentes.";
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    async addMatch() {
      if (!this.newMatch.opponentId) {
        this.message = "Debes seleccionar un oponente.";
        this.messageType = "warning";
        this.showMessage = true;
        return;
      }

      try {
        const dateTime = `${this.newMatch.date}T${this.newMatch.time}`;

        const matchPayload = {
          opponentId: this.newMatch.opponentId,
          date: dateTime,
          location: this.newMatch.location,
          teamId: this.$route.params.teamId,
        };

        await apiClient.post("/matches", matchPayload);

        this.message = "Partido agregado con éxito.";
        this.messageType = "success";
        this.showMessage = true;

        setTimeout(() => {
          this.goBack();
        }, 1500);

      } catch (error) {
        this.message = "No se pudo agregar el partido: " + (error.response?.data?.message || "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    goBack() {
      this.$router.push({
        name: "TeamMatches",
        params: { id: this.$route.params.teamId },
      });
    },
    closeMessage() {
      this.showMessage = false;
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
</style>