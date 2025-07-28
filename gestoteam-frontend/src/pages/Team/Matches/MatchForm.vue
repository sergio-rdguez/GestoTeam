<template>
  <MainLayout>
    <PageHeader :title="pageTitle" :breadcrumbs="breadcrumbs" />
    <BaseCard>
      <form @submit.prevent="submitForm" class="space-y-8">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label for="opponent" class="block text-sm font-medium text-gray-700">Rival</label>
            <BaseSelect
              id="opponent"
              v-model="match.opponentId"
              :options="opponentOptions"
              required
              placeholder="Selecciona un rival"
            />
          </div>
          <div>
            <label for="date" class="block text-sm font-medium text-gray-700">Fecha y Hora</label>
            <BaseInput
              id="date"
              v-model="match.date"
              type="datetime-local"
              required
            />
          </div>
          <div class="md:col-span-2">
            <label for="location" class="block text-sm font-medium text-gray-700">Lugar</label>
            <BaseInput
              id="location"
              v-model="match.location"
              type="text"
              placeholder="Ej: Ciudad Deportiva GestoTeam"
              required
            />
          </div>
        </div>

        <div v-if="editMode" class="pt-8 border-t border-gray-200">
          <h3 class="text-lg leading-6 font-medium text-gray-900">Resultado del Partido</h3>
          <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6 items-center">
            <BaseCheckbox
                id="finalized"
                v-model="match.finalized"
                label="Partido Finalizado"
            />
            <div v-if="match.finalized">
              <label for="goalsFor" class="block text-sm font-medium text-gray-700">Goles a Favor</label>
              <BaseInput
                id="goalsFor"
                v-model.number="match.goalsFor"
                type="number"
                min="0"
                placeholder="0"
              />
            </div>
            <div v-if="match.finalized">
              <label for="goalsAgainst" class="block text-sm font-medium text-gray-700">Goles en Contra</label>
              <BaseInput
                id="goalsAgainst"
                v-model.number="match.goalsAgainst"
                type="number"
                min="0"
                placeholder="0"
              />
            </div>
          </div>
           <p v-if="!match.finalized" class="mt-4 text-sm text-gray-500">
              Marca la casilla "Partido Finalizado" para poder introducir el resultado.
            </p>
        </div>

        <div class="flex justify-end space-x-4 pt-4">
          <BaseButton type="button" @click="cancel" variant="secondary">Cancelar</BaseButton>
          <BaseButton type="submit" variant="primary">{{ submitButtonText }}</BaseButton>
        </div>
      </form>
    </BaseCard>
  </MainLayout>
</template>

<script>
import MainLayout from '@/components/layout/MainLayout.vue';
import PageHeader from '@/components/layout/PageHeader.vue';
import BaseCard from '@/components/base/BaseCard.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseSelect from '@/components/base/BaseSelect.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import BaseCheckbox from '@/components/base/BaseCheckbox.vue';
import api from '@/services/api';
import { notificationService } from '@/services/notificationService';

export default {
  name: 'MatchForm',
  components: {
    MainLayout,
    PageHeader,
    BaseCard,
    BaseInput,
    BaseSelect,
    BaseButton,
    BaseCheckbox,
  },
  data() {
    return {
      match: {
        opponentId: null,
        date: '',
        location: '',
        finalized: false,
        goalsFor: 0,
        goalsAgainst: 0,
      },
      opponents: [],
      editMode: false,
      teamId: null,
      matchId: null,
    };
  },
  computed: {
    pageTitle() {
      return this.editMode ? 'Editar Partido' : 'Añadir Partido';
    },
    submitButtonText() {
      return this.editMode ? 'Guardar Cambios' : 'Crear Partido';
    },
    breadcrumbs() {
      return [
        { name: 'Equipos', to: '/teams' },
        { name: 'Equipo', to: `/team/${this.teamId}` },
        { name: 'Partidos', to: `/team/${this.teamId}/matches` },
        { name: this.pageTitle, to: this.$route.path },
      ];
    },
    opponentOptions() {
      return this.opponents.map(op => ({ value: op.id, text: op.name }));
    },
  },
  async created() {
    this.teamId = this.$route.params.teamId;
    this.matchId = this.$route.params.matchId;
    this.editMode = !!this.matchId;

    await this.fetchOpponents();

    if (this.editMode) {
      await this.fetchMatchForEdit();
    }
  },
  methods: {
    async fetchOpponents() {
      try {
        const response = await api.get(`/opponents/team/${this.teamId}`);
        this.opponents = response.data;
      } catch (error) {
        notificationService.showError('No se pudieron cargar los rivales.');
      }
    },
    async fetchMatchForEdit() {
      try {
        const response = await api.get(`/matches/details/${this.matchId}`);
        const data = response.data;
        this.match = {
          opponentId: data.opponentId,
          date: this.formatDateForInput(data.date),
          location: data.location,
          finalized: data.finalized,
          goalsFor: data.goalsFor ?? 0,
          goalsAgainst: data.goalsAgainst ?? 0,
        };
      } catch (error) {
        notificationService.showError('Error al cargar los datos del partido.');
        this.$router.push(`/team/${this.teamId}/matches`);
      }
    },
    async submitForm() {
      if (this.editMode) {
        await this.updateMatch();
      } else {
        await this.createMatch();
      }
    },
    async createMatch() {
      try {
        const payload = {
          teamId: this.teamId,
          opponentId: this.match.opponentId,
          date: new Date(this.match.date).toISOString(),
          location: this.match.location,
        };
        await api.post('/matches', payload);
        notificationService.showSuccess('Partido creado con éxito.');
        this.$router.push(`/team/${this.teamId}/matches`);
      } catch (error) {
        notificationService.showError('Error al crear el partido.');
      }
    },
    async updateMatch() {
       try {
        const payload = {
            opponentId: this.match.opponentId,
            date: new Date(this.match.date).toISOString(),
            location: this.match.location,
            finalized: this.match.finalized,
            goalsFor: this.match.finalized ? this.match.goalsFor : null,
            goalsAgainst: this.match.finalized ? this.match.goalsAgainst : null
        };
        
        await api.put(`/matches/${this.matchId}`, payload);
        notificationService.showSuccess('Partido actualizado con éxito.');
        this.$router.push(`/team/${this.teamId}/matches`);
      } catch (error) {
        notificationService.showError('Error al actualizar el partido.');
      }
    },
    cancel() {
      this.$router.push(`/team/${this.teamId}/matches`);
    },
    formatDateForInput(isoDate) {
      if (!isoDate) return '';
      const date = new Date(isoDate);
      // Ajuste para la zona horaria local
      date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
      return date.toISOString().slice(0, 16);
    },
  },
};
</script>