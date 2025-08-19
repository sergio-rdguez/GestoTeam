<template>
  <div class="stats-manager-container">
    <PageHeader 
      :title="'Gestionar Estadísticas del Partido'" 
      show-back-button 
      @back="goBack" 
    />

    <div v-if="loading" class="loading-container">
      <LoadingSpinner message="Cargando jugadores y estadísticas..." />
    </div>

    <BaseCard v-else-if="stats.length > 0">
      <div class="stats-table-wrapper">
        <table class="stats-table">
          <thead>
            <tr>
              <th class="player-name-col">Jugador</th>
              <th class="status-col">Conv.</th>
              <th class="status-col">Titular</th>
              <th class="input-col">Minutos</th>
              <th class="input-col">Goles</th>
              <th class="input-col">Asist.</th>
              <th class="input-col">G. Encaj.</th>
              <th class="status-col"><span title="Tarjeta Amarilla">TA</span></th>
              <th class="status-col"><span title="Tarjeta Roja">TR</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="playerStat in stats" :key="playerStat.playerId">
              <td class="player-name-col">
                <span class="dorsal">{{ playerStat.playerDorsal }}</span>
                {{ playerStat.playerFullName }}
              </td>
              <td>
                <BaseCheckbox v-model="playerStat.calledUp" @change="handleCalledUpChange(playerStat)" />
              </td>
              <td>
                <BaseCheckbox v-model="playerStat.starter" :disabled="!playerStat.calledUp" />
              </td>
              <td>
                <BaseInput 
                  v-model.number="playerStat.minutesPlayed" 
                  type="number" 
                  min="0" 
                  :disabled="!playerStat.calledUp"
                />
              </td>
              <td>
                <BaseInput 
                  v-model.number="playerStat.goals" 
                  type="number" 
                  min="0" 
                  :disabled="!playerStat.calledUp"
                />
              </td>
              <td>
                <BaseInput 
                  v-model.number="playerStat.assists" 
                  type="number" 
                  min="0" 
                  :disabled="!playerStat.calledUp"
                />
              </td>
              <td>
                <BaseInput 
                  v-model.number="playerStat.goalsConceded" 
                  type="number" 
                  min="0" 
                  :disabled="!playerStat.calledUp"
                />
              </td>
              <td>
                <BaseCheckbox v-model="playerStat.yellowCard" :disabled="!playerStat.calledUp" />
              </td>
              <td>
                <BaseCheckbox v-model="playerStat.redCard" :disabled="!playerStat.calledUp" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="form-actions">
        <BaseButton @click="saveStats" :loading="isSaving" variant="primary">
          {{ isSaving ? "Guardando..." : "Guardar Cambios" }}
        </BaseButton>
      </div>
    </BaseCard>
    
    <BaseCard v-else>
      <p>No se encontraron jugadores en el equipo para este partido.</p>
    </BaseCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from "@/services/api";
import { notificationService } from '@/services/notificationService';

import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import BaseCheckbox from "@/components/base/BaseCheckbox.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

const route = useRoute();
const router = useRouter();

const matchId = route.params.matchId;
const teamId = route.params.teamId; 

const stats = ref([]);
const loading = ref(true);
const isSaving = ref(false);

const fetchStats = async () => {
  try {
    const response = await apiClient.get(`/player-match-stats/${matchId}`);
    stats.value = response.data.map(stat => ({
        ...stat,
        playerDorsal: stat.playerDorsal || '-',
        calledUp: stat.calledUp ?? false,
        starter: stat.starter ?? false,
        minutesPlayed: stat.minutesPlayed ?? 0,
        goals: stat.goals ?? 0,
        assists: stat.assists ?? 0,
        goalsConceded: stat.goalsConceded ?? 0,
        yellowCard: stat.yellowCard ?? false,
        redCard: stat.redCard ?? false,
    }));
  } catch (error) {
    notificationService.showError('No se pudieron cargar las estadísticas.');
  } finally {
    loading.value = false;
  }
};

const handleCalledUpChange = (playerStat) => {
  if (!playerStat.calledUp) {
    playerStat.starter = false;
    playerStat.minutesPlayed = 0;
    playerStat.goals = 0;
    playerStat.assists = 0;
    playerStat.goalsConceded = 0;
    playerStat.yellowCard = false;
    playerStat.redCard = false;
  }
};

const saveStats = async () => {
  isSaving.value = true;
  try {
    await apiClient.put(`/matches/${matchId}/stats`, stats.value);
    notificationService.showSuccess('Estadísticas guardadas con éxito.');
    goBack();
  } catch (error) {
    // El interceptor de errores de Axios ya muestra una notificación
  } finally {
    isSaving.value = false;
  }
};

const goBack = () => {
  router.push({ name: 'TeamMatches', params: { teamId: teamId } });
};

onMounted(fetchStats);
</script>

<style scoped>
.stats-manager-container {
  max-width: 1200px;
  margin: 0 auto;
}
.stats-table-wrapper {
  overflow-x: auto;
}
.stats-table {
  width: 100%;
  border-collapse: collapse;
  white-space: nowrap;
}
.stats-table th,
.stats-table td {
  padding: var(--spacing-3) var(--spacing-4);
  text-align: left;
  border-bottom: 1px solid var(--color-border);
  vertical-align: middle;
}
.stats-table th {
  font-weight: 600;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}
.player-name-col {
  width: 40%;
  min-width: 200px;
}
.input-col {
  width: 10%;
  min-width: 90px;
}
.status-col {
  width: 5%;
  min-width: 60px;
  text-align: center;
}
.stats-table td .dorsal {
  display: inline-block;
  font-weight: bold;
  margin-right: var(--spacing-3);
  width: 25px;
  text-align: right;
  color: var(--color-primary);
}
.stats-table td .base-input, .stats-table td .base-checkbox {
  margin: 0;
  width: 100%;
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
  min-height: 400px;
}
</style>