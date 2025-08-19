<template>
  <div class="form-page-container">
    <PageHeader 
      :title="isEditMode ? 'Editar Partido' : 'Añadir Partido'" 
      show-back-button 
      @back="goBack" 
    />

    <div v-if="loading" class="loading-container">
      <LoadingSpinner message="Cargando datos del partido..." />
    </div>

    <BaseCard v-else>
      <form @submit.prevent="submitForm">
        <div class="form-grid">
          <BaseSelect
            v-model="match.opponentId"
            label="Rival"
            id="opponent"
            :options="opponentOptions"
            placeholder="Selecciona un rival"
            required
            class="form-grid-span-2"
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
            label="Lugar del Partido"
            id="location"
            placeholder="Ej: Ciudad Deportiva GestoTeam"
            required
          />

          <template v-if="isEditMode">
            <div class="form-grid-span-2 result-section">
              <BaseCheckbox
                v-model="match.finalized"
                label="Partido Finalizado"
                id="finalized"
              />
              <div v-if="match.finalized" class="result-inputs">
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
               <p v-else class="result-info-text">
                Marca la casilla para poder registrar el resultado.
              </p>
            </div>
          </template>
        </div>

        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Crear Partido") }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from "@/services/api";
import { notificationService } from '@/services/notificationService';

import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import BaseCheckbox from "@/components/base/BaseCheckbox.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

const route = useRoute();
const router = useRouter();

const teamId = route.params.teamId;
const matchId = route.params.id;
const isEditMode = computed(() => !!matchId);

const loading = ref(true);
const isSaving = ref(false);

const match = ref({
  opponentId: '',
  date: '',
  location: '',
  finalized: false,
  goalsFor: null,
  goalsAgainst: null,
});
const opponents = ref([]);

const opponentOptions = computed(() => 
  opponents.value.map(op => ({ value: op.id, text: op.name }))
);

const formatDateForInput = (isoDate) => {
  if (!isoDate) return '';
  const date = new Date(isoDate);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
};

const fetchOpponents = async () => {
  try {
            const response = await api.get(`/opponents/team/${teamId}`);
    opponents.value = response.data;
  } catch (error) {
    notificationService.showError('No se pudieron cargar los rivales.');
  }
};

const fetchMatchForEdit = async () => {
  try {
            const response = await api.get(`/matches/details/${matchId}`);
    const data = response.data;
    match.value = {
      opponentId: data.opponentId,
      date: formatDateForInput(data.date),
      location: data.location,
      finalized: data.finalized,
      goalsFor: data.goalsFor ?? null,
      goalsAgainst: data.goalsAgainst ?? null,
    };
  } catch (error) {
    notificationService.showError('Error al cargar los datos del partido.');
    goBack();
  }
};

const submitForm = async () => {
  isSaving.value = true;
  try {
    const payload = {
      ...match.value,
      date: new Date(match.value.date).toISOString(),
    };
    
    if (!payload.finalized) {
        payload.goalsFor = null;
        payload.goalsAgainst = null;
    }

    if (isEditMode.value) {
              await api.put(`/matches/${matchId}`, payload);
      notificationService.showSuccess('Partido actualizado con éxito.');
    } else {
      payload.teamId = teamId;
              await api.post('/matches', payload);
      notificationService.showSuccess('Partido creado con éxito.');
    }
    goBack();
  } catch (error) {
    // El interceptor se encarga de mostrar el error
  } finally {
    isSaving.value = false;
  }
};

const goBack = () => {
  router.push({ name: 'TeamMatches', params: { teamId: teamId } });
};

onMounted(async () => {
  loading.value = true;
  await fetchOpponents();
  if (isEditMode.value) {
    await fetchMatchForEdit();
  }
  loading.value = false;
});
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
.result-section {
  margin-top: var(--spacing-4);
  padding-top: var(--spacing-4);
  border-top: 1px solid var(--color-border);
}
.result-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-5);
  margin-top: var(--spacing-4);
}
.result-info-text {
    font-size: var(--font-size-sm);
    color: var(--color-text-secondary);
    margin-top: var(--spacing-2);
}
.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
@media (max-width: 768px) {
  .form-grid, .result-inputs {
    grid-template-columns: 1fr;
  }
  .form-grid-span-2 {
    grid-column: span 1;
  }
}
</style>