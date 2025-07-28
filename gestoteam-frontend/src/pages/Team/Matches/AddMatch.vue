<template>
  <div class="form-page-container">
    <PageHeader :title="isEditMode ? 'Editar Partido' : 'Añadir Partido'" show-back-button @back="goBack" />

    <BaseCard>
      <form @submit.prevent="submitForm">
        <div class="form-grid">
          <BaseSelect
            v-model="match.opponentId"
            label="Rival"
            id="opponent"
            :options="opponentOptions"
            required
            :disabled="loadingOpponents"
          />
          <BaseInput
            v-model="match.date"
            label="Fecha y Hora"
            id="date"
            type="datetime-local"
            required
          />
           <BaseInput
            v-model="match.location"
            label="Ubicación"
            id="location"
            placeholder="Ej: Estadio del rival"
            class="form-grid-span-2"
          />
        </div>

        <div v-if="isEditMode" class="result-grid">
            <h3 class="grid-span-2">Resultado</h3>
            <BaseInput
                v-model.number="match.goalsFor"
                label="Goles a Favor"
                id="goalsFor"
                type="number"
                min="0"
            />
            <BaseInput
                v-model.number="match.goalsAgainst"
                label="Goles en Contra"
                id="goalsAgainst"
                type="number"
                min="0"
            />
        </div>

        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Añadir Partido") }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import { notificationService } from '@/services/notificationService';
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseButton from "@/components/base/BaseButton.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseInput,
    BaseSelect,
    BaseButton,
  },
  data() {
    return {
      match: {
        teamId: this.$route.params.teamId,
        opponentId: null,
        date: '',
        location: '',
        goalsFor: null,
        goalsAgainst: null,
      },
      opponents: [],
      isEditMode: false,
      isSaving: false,
      loadingOpponents: true,
    };
  },
  computed: {
    opponentOptions() {
      return this.opponents.map(o => ({ value: o.id, text: o.name }));
    }
  },
  methods: {
    async fetchOpponents() {
        this.loadingOpponents = true;
        const teamId = this.$route.params.teamId;
        try {
            const response = await apiClient.get(`/opponents/team/${teamId}`);
            this.opponents = response.data.opponents || [];
        } catch (error) {
            notificationService.showError("No se pudieron cargar los rivales.");
        } finally {
            this.loadingOpponents = false;
        }
    },
    async fetchMatchForEdit() {
        try {
            const matchId = this.$route.params.id;
            const response = await apiClient.get(`/matches/details/${matchId}`);
            const data = response.data;
            
            this.match.opponentId = data.opponentId;
            this.match.location = data.location;
            this.match.date = this.formatDateForInput(data.date);

            if (data.result) {
                const scores = data.result.split('-').map(Number);
                if (data.won) {
                    this.match.goalsFor = scores[1];
                    this.match.goalsAgainst = scores[0];
                } else {
                    this.match.goalsFor = scores[0];
                    this.match.goalsAgainst = scores[1];
                }
            }

        } catch (error) {
            notificationService.showError("Error al cargar los datos del partido.");
        }
    },
    async submitForm() {
      this.isSaving = true;
      try {
        if (this.isEditMode) {
          const payload = { ...this.match, teamId: undefined }; // No reenviamos el teamId en el payload del PUT
          await apiClient.put(`/matches/${this.$route.params.id}`, payload);
          notificationService.showSuccess("Partido actualizado con éxito.");
          this.$router.push({ name: 'MatchDetails', params: { id: this.$route.params.id } });
        } else {
          await apiClient.post("/matches", this.match);
          notificationService.showSuccess("Partido añadido con éxito.");
          this.$router.push({ name: "TeamMatches", params: { id: this.match.teamId } });
        }
      } catch (error) {
        // El interceptor se encarga de mostrar el error.
      } finally {
        this.isSaving = false;
      }
    },
    formatDateForInput(date) {
      if (!date) return '';
      const d = new Date(date);
      d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
      return d.toISOString().slice(0, 16);
    },
    goBack() {
        const teamId = this.$route.params.teamId;
        this.$router.push({ name: "TeamMatches", params: { id: teamId } });
    }
  },
  async created() {
    this.isEditMode = !!this.$route.params.id;
    this.fetchOpponents();
    if (this.isEditMode) {
        await this.fetchMatchForEdit();
    }
  }
};
</script>

<style scoped>
.form-page-container {
  max-width: 900px;
  margin: 0 auto;
}
.form-grid, .result-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-5);
}
.result-grid {
    margin-top: var(--spacing-6);
    padding-top: var(--spacing-6);
    border-top: 1px solid var(--color-border);
}
.form-grid-span-2 {
  grid-column: span 2;
}
.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
@media (max-width: 768px) {
    .form-grid, .result-grid {
        grid-template-columns: 1fr;
    }
    .form-grid-span-2 {
        grid-column: span 1;
    }
}
</style>