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

    <!-- Componente MessageBox para mensajes -->
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
import MessageBox from "@/pages/utils/MessageBox.vue"; // Ajustada la ruta según tu estructura

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
        venue: "",
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
        console.log(
          "Fetching opponents for team ID:",
          this.$route.params.teamId
        );
        const response = await apiClient.get(
          `/opponents/team/${this.$route.params.teamId}`
        );
        this.opponents = response.data;
        console.log("Opponents loaded:", this.opponents);
      } catch (error) {
        console.error("Error fetching opponents:", error.response || error);
        this.message =
          "No se pudo cargar la lista de oponentes: " +
          (error.response?.data?.message ||
            error.message ||
            "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    async addMatch() {
      console.log("Attempting to add match with data:", this.newMatch);
      if (!this.newMatch.opponentId) {
        this.message = "Debes seleccionar un oponente.";
        this.messageType = "warning";
        this.showMessage = true;
        console.log(
          "ShowMessage after opponent validation:",
          this.showMessage,
          this.message,
          this.messageType
        );
        return;
      }

      try {
        const dateTime = new Date(
          `${this.newMatch.date}T${this.newMatch.time}`
        );
        const selectedOpponent = this.opponents.find(
          (opp) => opp.id === this.newMatch.opponentId
        );

        if (!selectedOpponent) {
          throw new Error("Oponente no encontrado en la lista.");
        }

        const matchPayload = {
          opponent: selectedOpponent.name,
          date: dateTime.toISOString(),
          location: this.newMatch.venue,
          result: "",
          finalized: dateTime < new Date(),
          teamId: this.$route.params.teamId,
        };

        console.log("Sending match payload to backend:", matchPayload);
        const response = await apiClient.post("/matches", matchPayload);

        console.log(
          "Match added successfully, response status:",
          response.status,
          "Response data:",
          response.data
        );
        if (response.status === 200) {
          this.message = "Partido agregado con éxito.";
          this.messageType = "success";
          this.showMessage = true;
          console.log(
            "ShowMessage after success:",
            this.showMessage,
            this.message,
            this.messageType
          );

          setTimeout(() => {
            this.$router.push({
              name: "TeamMatches",
              params: { id: this.$route.params.teamId },
            });
            console.log("Redirected to TeamMatches after showing message");
          }, 2000); 
        } else {
          throw new Error("Respuesta inesperada del servidor.");
        }
      } catch (error) {
        console.error("Error adding match:", error.response || error);
        this.message =
          "No se pudo agregar el partido: " +
          (error.response?.data?.message ||
            error.message ||
            "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
        console.log(
          "ShowMessage after error:",
          this.showMessage,
          this.message,
          this.messageType
        );
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
      console.log("MessageBox closed, showMessage:", this.showMessage);
    },
  },
  mounted() {
    this.fetchOpponents();
  },
  watch: {
    showMessage(newValue) {
      console.log(
        "showMessage changed to:",
        newValue,
        "Message:",
        this.message,
        "Type:",
        this.messageType
      );
    },
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
