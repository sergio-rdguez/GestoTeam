<template>
  <div class="match-details-page">
    <PageHeader :title="pageTitle" show-back-button @back="goBack">
      <BaseButton @click="manageStats" variant="secondary" v-if="match">
        <i class="fa-solid fa-chart-simple"></i> Gestionar Estadísticas
      </BaseButton>
      <BaseButton @click="editMatch" v-if="match">
        <i class="fa-solid fa-pencil"></i> Editar Partido
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando detalles del partido..." />
    </div>

    <div v-if="!loading && match">
      <BaseCard class="score-card">
        <div class="team-info">
          <span class="team-name">{{ match.team.name }}</span>
        </div>
        <div class="score">
          <span class="goals">{{ goalsFor }}</span>
          <span>-</span>
          <span class="goals">{{ goalsAgainst }}</span>
        </div>
        <div class="team-info opponent">
          <span class="team-name">{{ match.opponent }}</span>
        </div>
      </BaseCard>

      <BaseCard class="details-card">
        <h2 class="section-title">Detalles del Partido</h2>
        <div class="details-grid">
          <div>
            <span class="detail-label">Fecha</span>
            <span class="detail-value">{{ formatDate(match.date) }}</span>
          </div>
          <div>
            <span class="detail-label">Ubicación</span>
            <span class="detail-value">{{ match.location || 'No especificada' }}</span>
          </div>
        </div>
      </BaseCard>

      <BaseCard class="stats-card">
        <h2 class="section-title">Estadísticas de Jugadores</h2>
        <DataTable v-if="calledUpPlayers.length > 0" :items="calledUpPlayers" :columns="playerStatsColumns"
          default-sort-key="starter" :default-sort-asc="false">
          <template #cell-playerFullName="{ item }">
            <span :class="{ 'starter': item.starter }">{{ item.playerFullName }}</span>
          </template>
          <template #cell-cards="{ item }">
            <div class="cards-container">
              <i v-if="item.yellowCard" class="fa-solid fa-square card yellow"></i>
              <i v-if="item.doubleYellowCard" class="fa-solid fa-clone card yellow"></i>
              <i v-if="item.redCard" class="fa-solid fa-square card red"></i>
            </div>
          </template>
        </DataTable>
        <EmptyState v-else title="Sin datos de jugadores" message="Añade estadísticas para este partido."
          icon="fa-solid fa-chart-simple" :show-border="false" />
      </BaseCard>

    </div>
  </div>
</template>

<script>
import api from "@/services/api";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import DataTable from "@/components/common/DataTable.vue";
import EmptyState from "@/components/common/EmptyState.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseButton,
    LoadingSpinner,
    DataTable,
    EmptyState,
  },
  data() {
    return {
      match: null,
      loading: true,
      playerStatsColumns: [
        { key: 'playerFullName', label: 'Jugador', sortable: true },
        { key: 'minutesPlayed', label: 'Minutos', sortable: true },
        { key: 'goals', label: 'Goles', sortable: true },
        { key: 'assists', label: 'Asist.', sortable: true },
        { key: 'cards', label: 'Tarjetas', sortable: false },
      ],
    };
  },
  computed: {
    pageTitle() {
      if (!this.match) return "Detalles del Partido";
      return `${this.match.team.name} vs ${this.match.opponent}`;
    },
    goalsFor() {
      if (!this.match || !this.match.result) return '-';
      return this.match.won ? this.match.result.split('-')[1] : this.match.result.split('-')[0];
    },
    goalsAgainst() {
      if (!this.match || !this.match.result) return '-';
      return this.match.won ? this.match.result.split('-')[0] : this.match.result.split('-')[1];
    },
    calledUpPlayers() {
      if (!this.match || !this.match.playerStats) return [];
      return this.match.playerStats.filter(p => p.calledUp);
    }
  },
  methods: {
    async fetchMatchDetails() {
      this.loading = true;
      try {
        const matchId = this.$route.params.matchId;
                    const response = await api.get(`/matches/details/${matchId}`);
        this.match = response.data;
      } catch (error) {
        console.error("Error al cargar los detalles del partido:", error);
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'Fecha no disponible';
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleString('es-ES', options);
    },
    editMatch() {
      this.$router.push({ name: 'EditMatch', params: { matchId: this.match.id, teamId: this.match.team.id } });
    },
    manageStats() {
      this.$router.push({ name: 'MatchStatsManager', params: { matchId: this.match.id, teamId: this.match.team.id } });
    },
    goBack() {
      if (this.match) {
        this.$router.push({ name: 'TeamMatches', params: { teamId: this.match.team.id } });
      } else {
        this.$router.push({ name: 'Teams' });
      }
    }
  },
  mounted() {
    this.fetchMatchDetails();
  },
};
</script>

<style scoped>
.score-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-6) var(--spacing-8);
  text-align: center;
  margin-bottom: var(--spacing-6);
}

.team-info {
  flex: 1;
}

.team-name {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.score {
  display: flex;
  align-items: center;
  gap: var(--spacing-6);
  font-size: 3rem;
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.details-card,
.stats-card {
  margin-bottom: var(--spacing-6);
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-5);
  padding-bottom: var(--spacing-2);
  border-bottom: 1px solid var(--color-border);
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-5);
}

.detail-label {
  display: block;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-1);
  font-weight: var(--font-weight-medium);
}

.detail-value {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
}

.starter {
  font-weight: var(--font-weight-bold);
}

.cards-container {
  display: flex;
  gap: var(--spacing-2);
}

.card {
  font-size: 1.1rem;
}

.card.yellow {
  color: #ffc107;
}

.card.red {
  color: #dc3545;
}
</style>