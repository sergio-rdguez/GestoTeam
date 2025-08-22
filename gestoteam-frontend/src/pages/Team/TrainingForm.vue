<template>
  <div class="form-page-container">
    <PageHeader 
      :title="isEditMode ? 'Editar Entrenamiento' : 'Nuevo Entrenamiento'" 
      show-back-button 
      @back="goBack" 
    />
    
    <BaseCard>
      <form @submit.prevent="submitForm">
        <div class="form-grid">
          <BaseInput
            v-model="training.date"
            label="Fecha y Hora"
            id="date"
            type="datetime-local"
            required
          />
          
          <BaseInput
            v-model="training.location"
            label="Ubicación"
            id="location"
            placeholder="Ej: Cancha Principal"
            required
          />
          
          <BaseInput
            v-model="training.trainingType"
            label="Tipo de Entrenamiento"
            id="trainingType"
            placeholder="Ej: Táctico, Físico, Técnico"
            required
          />
          
          <BaseTextarea
            v-model="training.observations"
            label="Observaciones"
            id="observations"
            placeholder="Descripción del entrenamiento, objetivos, etc."
            :rows="4"
            class="form-grid-span-2"
          />
        </div>

        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Crear Entrenamiento") }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script>
import { trainingService } from "@/services/trainingService";
import { notificationService } from "@/services/notificationService";
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
      teamId: this.$route.params.teamId,
      trainingId: this.$route.params.trainingId,
      isEditMode: false,
      isSaving: false,
      training: {
        date: '',
        location: '',
        trainingType: '',
        observations: '',
        teamId: this.$route.params.teamId
      }
    };
  },
  methods: {
    async fetchTraining() {
      try {
        const response = await trainingService.getTrainingById(this.trainingId);
        this.training = {
          ...response,
          date: this.formatDateForInput(response.date)
        };
      } catch (error) {
        notificationService.showError("No se pudo cargar el entrenamiento.");
      }
    },
    
    formatDateForInput(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toISOString().slice(0, 16);
    },
    
    async submitForm() {
      this.isSaving = true;
      try {
        if (this.isEditMode) {
          await trainingService.updateTraining(this.trainingId, this.training);
          notificationService.showSuccess("Entrenamiento actualizado con éxito.");
        } else {
          await trainingService.createTraining(this.training);
          notificationService.showSuccess("Entrenamiento creado con éxito.");
        }
        
        this.$router.push({ 
          name: 'TeamTrainings', 
          params: { teamId: this.teamId } 
        });
      } catch (error) {
        console.error("Error al guardar el entrenamiento:", error);
        notificationService.showError("Error al guardar el entrenamiento.");
      } finally {
        this.isSaving = false;
      }
    },
    
    goBack() {
      this.$router.push({ 
        name: 'TeamTrainings', 
        params: { teamId: this.teamId } 
      });
    }
  },
  created() {
    this.isEditMode = !!this.trainingId;
    if (this.isEditMode) {
      this.fetchTraining();
    }
  }
};
</script>

<style scoped>
.form-page-container {
  max-width: 900px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-5);
}

.form-grid-span-2 {
  grid-column: span 2;
}

.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
</style>
