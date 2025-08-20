<template>
  <div class="opponent-details-page">
    <PageHeader :title="opponent?.name || 'Detalles del Oponente'" show-back-button @back="goBack">
      <BaseButton @click="editOpponent" v-if="opponent">
        <i class="fa-solid fa-pencil"></i> Editar Oponente
      </BaseButton>
    </PageHeader>

    <div v-if="loading">
      <LoadingSpinner message="Cargando detalles del oponente..." />
    </div>

    <div v-else-if="opponent" class="opponent-details-container">
              <BaseCard title="Información General" class="info-card">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">Campo</span>
              <span class="info-value">{{ opponent.field || 'No especificado' }}</span>
            </div>
          </div>
          <div v-if="opponent.observations" class="description">
            <p><strong>Observaciones:</strong> {{ opponent.observations }}</p>
          </div>
        </BaseCard>

      <BaseCard title="Partidos contra este oponente" class="matches-card">
        <div v-if="matches && matches.length > 0" class="matches-list">
          <div v-for="match in matches" :key="match.id" class="match-item" @click="viewMatch(match)">
            <div class="match-info">
              <span class="match-date">{{ formatDate(match.date) }}</span>
              <span class="match-location">{{ match.location || 'Ubicación no especificada' }}</span>
            </div>
            <div class="match-score">
              <span class="team-name">Mi Equipo</span>
              <span class="score">{{ match.result || 'Pendiente' }}</span>
              <span class="team-name">{{ opponent.name }}</span>
            </div>
            <div v-if="match.finalized" class="match-status">
              <span :class="['status-badge', match.won ? 'won' : match.draw ? 'draw' : 'lost']">
                {{ match.won ? 'Victoria' : match.draw ? 'Empate' : 'Derrota' }}
              </span>
            </div>
          </div>
        </div>
        <EmptyState 
          v-else-if="!loading" 
          title="Sin partidos programados" 
          message="Aún no hay partidos programados contra este oponente."
          icon="fa-solid fa-calendar"
          :show-border="false"
        />
        <div v-else class="loading-matches">
          <LoadingSpinner message="Cargando partidos..." />
        </div>
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

import EmptyState from "@/components/common/EmptyState.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseButton,
    LoadingSpinner,
    EmptyState,
  },
  data() {
    return {
      opponent: null,
      matches: [],
      loading: true,
    };
  },
  methods: {
    async fetchOpponentDetails() {
      this.loading = true;
      try {
        const opponentId = this.$route.params.opponentId;
        const response = await api.get(`/opponents/${opponentId}`);
        this.opponent = response.data;
      } catch (error) {
        console.error("Error al cargar los detalles del oponente:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchMatches() {
      try {
        const opponentId = this.$route.params.opponentId;
        const response = await api.get(`/matches/opponent/${opponentId}`);
        this.matches = response.data || [];
        
      } catch (error) {
        console.error("Error al cargar los partidos:", error);
        this.matches = [];
      }
    },
    editOpponent() {
      if (this.opponent && this.opponent.id) {
        this.$router.push({ name: "EditOpponent", params: { opponentId: this.opponent.id, teamId: this.opponent.teamId } });
      } else {
        console.error("No se puede editar: falta información del oponente");
      }
    },
    viewMatch(match) {
      if (match && match.id) {
        this.$router.push({ name: "MatchDetails", params: { matchId: match.id } });
      } else {
        console.error("No se puede ver el partido: ID inválido");
      }
    },
    formatDate(dateString) {
      if (!dateString) return 'Fecha no disponible';
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('es-ES', options);
    },

    goBack() {
      if (this.opponent && this.opponent.teamId) {
        this.$router.push({ name: "Opponents", params: { teamId: this.opponent.teamId } });
      } else {
        this.$router.push({ name: "Teams" });
      }
    }
  },
  async mounted() {
    await this.fetchOpponentDetails();
    if (this.opponent) {
      await this.fetchMatches();
    }
  },
  watch: {
    async opponent(newOpponent) {
      if (newOpponent) {
        await this.fetchMatches();
      }
    }
  },
};
</script>

<style scoped>
.opponent-details-page {
  padding: var(--spacing-6);
}

.opponent-details-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-6);
}

.info-card .card-content {
  padding-top: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-5);
  padding-top: var(--spacing-4);
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-1);
}

.info-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.description {
  margin-top: var(--spacing-5);
  padding-top: var(--spacing-5);
  border-top: 1px solid var(--color-border);
  font-style: italic;
  color: var(--color-text-secondary);
}

.matches-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-3);
}

.match-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-4);
  border: 1px solid var(--color-border);
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all 0.2s ease;
}

.match-item:hover {
  background-color: var(--color-background-hover);
  border-color: var(--color-primary);
}

.match-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1);
}

.match-date {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.match-location {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.match-score {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.team-name {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.score {
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  font-size: var(--font-size-lg);
}

.match-status {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: var(--spacing-1) var(--spacing-2);
  border-radius: var(--border-radius);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
}

.status-badge.won {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.status-badge.draw {
  background-color: var(--color-warning-light);
  color: var(--color-warning);
}

.status-badge.lost {
  background-color: var(--color-error-light);
  color: var(--color-error);
}

.loading-matches {
  display: flex;
  justify-content: center;
  padding: var(--spacing-8);
}

@media (max-width: 768px) {
  .opponent-details-container {
    grid-template-columns: 1fr;
  }
  
  .match-item {
    flex-direction: column;
    gap: var(--spacing-3);
    text-align: center;
  }
}
</style>
