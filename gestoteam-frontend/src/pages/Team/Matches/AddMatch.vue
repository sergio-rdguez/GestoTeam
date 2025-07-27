<template>
  <div class="form-page">
    <PageHeader 
      title="Agregar Partido" 
      show-back-button 
      @back="goBack"
    />

    <BaseCard>
      <form @submit.prevent="addMatch">
        <div class="form-grid">
          <BaseSelect
            v-model="newMatch.opponentId"
            label="Oponente"
            id="opponent"
            :options="opponentOptions"
            placeholder="Seleccione un oponente"
            required
          />
          <BaseInput
            v-model="newMatch.location"
            label="Estadio"
            id="location"
            type="text"
            required
          />
          <BaseInput
            v-model="newMatch.date"
            label="Fecha"
            id="date"
            type="date"
            required
          />
          <BaseInput
            v-model="newMatch.time"
            label="Hora"
            id="time"
            type="time"
            required
          />
        </div>
        <div class="form-actions">
            <BaseButton type="submit" variant="primary" :loading="isSaving">
                {{ isSaving ? "Guardando..." : "Guardar Partido" }}
            </BaseButton>
        </div>
      </form>
    </BaseCard>

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
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseButton from "@/components/base/BaseButton.vue";


export default {
  components: {
    MessageBox,
    PageHeader,
    BaseCard,
    BaseInput,
    BaseSelect,
    BaseButton,
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
      isSaving: false,
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  computed: {
    opponentOptions() {
      return this.opponents.map(opp => ({ value: opp.id, text: opp.name }));
    }
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

      this.isSaving = true;
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

        setTimeout(() => this.goBack(), 1500);

      } catch (error) {
        this.message = "No se pudo agregar el partido: " + (error.response?.data?.message || "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      } finally {
          this.isSaving = false;
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
.form-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}
.form-actions {
  margin-top: 1.5rem;
}
@media (max-width: 768px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>