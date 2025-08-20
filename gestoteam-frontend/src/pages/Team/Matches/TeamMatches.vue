<template>
  <div class="team-matches-page">
    <PageHeader :title="teamName || 'Partidos'" show-back-button @back="goBack">
      <BaseButton v-if="matches && matches.length > 0" @click="goToAddMatch">
        <i class="fa-solid fa-plus"></i> Añadir Partido
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando partidos..." />
    </div>

    <EmptyState
      v-else-if="!matches || matches.length === 0"
      title="No hay partidos registrados"
      message="Registra tu primer partido para llevar un seguimiento de la temporada."
      icon="fa-solid fa-calendar-plus"
    >
      <template #actions>
        <BaseButton @click="goToAddMatch">
          <i class="fa-solid fa-plus"></i> Añadir primer partido
        </BaseButton>
      </template>
    </EmptyState>
    
    <DataTable
      v-else
      :items="matches"
      :columns="columns"
      table-name="matches"
      default-sort-key="date"
      :default-sort-asc="false"
      @row-click="viewMatchDetails"
    >
      <template #cell-opponent="{ item }">
        {{ item.opponent }}
      </template>
      <template #cell-result="{ item }">
        <span class="match-result" :class="getResultClass(item)">
          {{ formatResult(item) }}
        </span>
      </template>
      <template #cell-date="{ item }">
        {{ formatDate(item.date) }}
      </template>
    </DataTable>

  </div>
</template>

<script>
import api from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
  components: {
    DataTable,
    PageHeader,
    BaseButton,
    EmptyState,
    LoadingSpinner,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      teamName: '',
      matches: [],
      loading: true,
      columns: [
        { key: 'date', label: 'Fecha', sortable: true },
        { key: 'opponent', label: 'Rival', sortable: true },
        { key: 'result', label: 'Resultado', sortable: false },
        { key: 'location', label: 'Ubicación', sortable: true },
      ],
    };
  },
  methods: {
    async fetchMatches() {
      this.loading = true;
      try {
        const [teamResponse, matchesResponse] = await Promise.all([
                  api.get(`/teams/${this.teamId}`),
        api.get(`/matches/team/${this.teamId}`)
        ]);
        this.teamName = teamResponse.data.name;
        this.matches = Array.isArray(matchesResponse.data) ? matchesResponse.data : [];
      } catch (error) {
        console.error("Error al cargar los partidos:", error);
        this.matches = [];
      } finally {
        this.loading = false;
      }
    },
    formatResult(match) {
        return match.result || 'Pendiente';
    },
    getResultClass(match) {
        if (!match.finalized) return 'pending';
        if (match.won) return 'win';

        // Check for a draw (e.g., "1-1", "2-2")
        if (match.result) {
            const scores = match.result.split('-');
            if (scores.length === 2 && scores[0] === scores[1]) {
                return 'draw';
            }
        }
        
        return 'loss';
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
      return new Date(dateString).toLocaleDateString('es-ES', options);
    },
    viewMatchDetails(match) {
      this.$router.push({ name: 'MatchDetails', params: { matchId: match.id } });
    },
    goToAddMatch() {
      this.$router.push({ name: 'NewMatch', params: { teamId: this.teamId } });
    },
    goBack() {
      this.$router.push({ name: 'TeamDetails', params: { teamId: this.teamId } });
    }
  },
  mounted() {
    this.fetchMatches();
  },
};
</script>

<style scoped>
.match-result {
    font-weight: var(--font-weight-bold);
    padding: var(--spacing-1) var(--spacing-3);
    border-radius: 9999px;
    min-width: 80px;
    text-align: center;
    display: inline-block;
    color: var(--color-background-white);
}
.match-result.win { background-color: var(--color-success); }
.match-result.loss { background-color: var(--color-danger); }
.match-result.draw { background-color: var(--color-warning); }
.match-result.pending { background-color: var(--color-gray-400); }
</style>