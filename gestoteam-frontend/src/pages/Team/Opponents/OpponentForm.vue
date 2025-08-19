<template>
  <div class="form-page-container">
    <PageHeader 
      :title="isEditMode ? 'Editar Oponente' : 'Añadir Oponente'" 
      show-back-button 
      @back="goBack" 
    />
    <BaseCard>
      <form @submit.prevent="submitForm">
        <div class="form-grid">
          <BaseInput v-model="opponent.name" label="Nombre del Equipo" id="name" required />
          <BaseInput v-model="opponent.field" label="Campo (opcional)" id="field" />
          <BaseTextarea v-model="opponent.observations" label="Observaciones" id="observations" rows="3" placeholder="Notas sobre el equipo rival, estilo de juego, etc." />
        </div>
        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Guardar Oponente") }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script>
import { notificationService } from '@/services/notificationService';
import api from "@/services/api";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseTextarea from "@/components/base/BaseTextarea.vue";
import BaseButton from "@/components/base/BaseButton.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseInput,
    BaseTextarea,
    BaseButton,
  },
  data() {
    return {
      opponent: {
        name: "",
        field: "",
        observations: "",
        teamId: this.$route.params.teamId,
      },
      isEditMode: false,
      isSaving: false,
    };
  },

  methods: {

    async fetchOpponent() {
      try {
        const opponentId = this.$route.params.opponentId;
        const response = await api.get(`/opponents/${opponentId}`);
        this.opponent = response.data;
      } catch (error) {
        notificationService.showError("Error al cargar los datos del oponente.");
      }
    },
    async submitForm() {
      this.isSaving = true;
      try {
        const payload = {
          name: this.opponent.name,
          field: this.opponent.field,
          observations: this.opponent.observations,
          teamId: this.opponent.teamId,
        };

        if (this.isEditMode) {
          await api.put(`/opponents/${this.opponent.id}`, payload);
          notificationService.showSuccess('Oponente actualizado con éxito');
          this.$router.push({ name: "OpponentDetails", params: { opponentId: this.opponent.id } });
        } else {
          await api.post("/opponents", payload);
          notificationService.showSuccess('Oponente añadido con éxito');
          this.$router.push({ name: "Opponents", params: { teamId: this.opponent.teamId } });
        }
      } catch (error) {
        // El interceptor de api.js se encarga de mostrar el error
      } finally {
        this.isSaving = false;
      }
    },
    goBack() {
      if (this.isEditMode) {
        this.$router.push({ name: "OpponentDetails", params: { opponentId: this.opponent.id } });
      } else {
        this.$router.push({ name: "Opponents", params: { teamId: this.opponent.teamId } });
      }
    },
  },
  created() {
    this.isEditMode = !!this.$route.params.opponentId;
    if (this.isEditMode) {
      this.fetchOpponent();
    }
  },
};
</script>

<style scoped>
.form-page-container {
  max-width: 900px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-5);
}

.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
</style>
