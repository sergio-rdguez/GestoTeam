<template>
  <div class="team-players-page">
    <PageHeader :title="teamName || 'Jugadores'" show-back-button @back="goBack">
      <BaseButton v-if="players.length > 0" @click="addPlayer">
        <i class="fa-solid fa-plus"></i> Añadir Jugador
      </BaseButton>
    </PageHeader>

    <!-- Layout de dos columnas -->
    <div class="players-layout">
      <!-- Columna izquierda: Tabla -->
      <div class="left-column">
        <div v-if="loading">
          <LoadingSpinner message="Cargando jugadores..." />
        </div>

        <EmptyState
          v-else-if="players.length === 0"
          title="Este equipo aún no tiene jugadores"
          message="Añade tu primer jugador para empezar a construir tu plantilla."
          icon="fa-user-plus"
        >
          <template #actions>
            <BaseButton @click="addPlayer">
              <i class="fa-solid fa-plus"></i> Añadir primer jugador
            </BaseButton>
          </template>
        </EmptyState>

        <DataTable
          v-else
          :items="players"
          :columns="columns"
          table-name="players"
          default-sort-key="fullName"
          @row-click="viewPlayerDetails"
          class="players-table"
        >
          <template #cell-photoUrl="{ value }">
            <img v-if="value" :src="getImageUrl(value)" alt="Foto" class="avatar" />
            <span v-else class="avatar placeholder"><i class="fa-regular fa-user"></i></span>
          </template>
          <template #cell-status="{ item }">
            <span class="player-status" :class="item.status.toLowerCase()">
              {{ item.status }}
            </span>
          </template>
        </DataTable>
      </div>

      <!-- Columna derecha: Estadísticas -->
      <div class="right-column">
        <div v-if="players && players.length > 0" class="stats-section">
          <BaseCard title="Estadísticas" class="stats-card">
                         <div class="stats-grid">
               <div class="stat-item total-stat">
                 <span class="stat-number">{{ players.length }}</span>
                 <span class="stat-label">Total Jugadores</span>
               </div>
               <div class="stats-subgrid">
                 <div class="stat-item">
                   <span class="stat-number">{{ activePlayers.length }}</span>
                   <span class="stat-label">Activos</span>
                 </div>
                 <div class="stat-item">
                   <span class="stat-number">{{ injuredPlayers.length }}</span>
                   <span class="stat-label">Lesionados</span>
                 </div>
                 <div class="stat-item">
                   <span class="stat-number">{{ suspendedPlayers.length }}</span>
                   <span class="stat-label">Suspendidos</span>
                 </div>
               </div>
             </div>
          </BaseCard>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";
import { buildImageUrl } from "@/utils/imageUtils";
import DataTable from "@/components/common/DataTable.vue";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
  components: {
    DataTable,
    PageHeader,
    BaseButton,
    BaseCard,
    EmptyState,
    LoadingSpinner,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      teamName: '',
      players: [],
      loading: true,
      columns: [
        { key: 'photoUrl', label: 'Foto', sortable: false },
        { key: 'fullName', label: 'Nombre', sortable: true },
        { key: 'position', label: 'Posición', sortable: true, sortOn: 'positionOrder' },
        { key: 'foot', label: 'Pie', sortable: true },
        { key: 'number', label: 'Dorsal', sortable: true },
        { key: 'status', label: 'Estado', sortable: true },
      ],
    };
  },
  computed: {
    activePlayers() {
      return this.players.filter(player => player.status === 'ACTIVO');
    },
    
    injuredPlayers() {
      return this.players.filter(player => player.status === 'LESIONADO');
    },
    
    suspendedPlayers() {
      return this.players.filter(player => player.status === 'SUSPENDIDO');
    },
  },
  methods: {
    async fetchPlayers() {
      this.loading = true;
      try {
        // Obtenemos el nombre del equipo y los jugadores en paralelo
        const [teamResponse, playersResponse] = await Promise.all([
                  api.get(`/teams/${this.teamId}`),
        api.get(`/players/team/${this.teamId}`)
        ]);
        this.teamName = teamResponse.data.name;
        this.players = playersResponse.data.players;
      } catch (error) {
        console.error("Error al cargar los jugadores:", error);
      } finally {
        this.loading = false;
      }
    },
    viewPlayerDetails(player) {
      if (player && player.id) {
        this.$router.push({ name: 'PlayerDetails', params: { playerId: player.id } });
      } else {
        console.error("No se puede ver detalles: ID de jugador inválido");
      }
    },
    addPlayer() {
      this.$router.push({ name: 'NewPlayer', params: { teamId: this.teamId } });
    },
    getImageUrl(photoUrl) {
      if (!photoUrl) return null;
      return buildImageUrl(photoUrl);
    },
    goBack() {
      this.$router.push({ name: 'TeamDetails', params: { teamId: this.teamId } });
    }
  },
  mounted() {
    this.fetchPlayers();
  }
};
</script>

<style scoped>
.team-players-page {
  padding: 20px;
}

.players-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--spacing-6);
  align-items: start;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.right-column {
  position: sticky;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-6);
}

.players-table {
  margin-top: 0;
}

.stats-section {
  margin-bottom: var(--spacing-6);
}

.stats-card .card-content {
  padding-top: 0;
}

.stats-grid {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-5);
}

.stats-subgrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-3);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-4);
  background-color: var(--color-background-white);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  width: 100%;
  box-sizing: border-box;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-number {
  display: block;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
  margin-bottom: var(--spacing-2);
  line-height: 1;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  line-height: 1.2;
}

.total-stat {
  background-color: var(--color-primary);
  color: var(--color-background-white);
  border: none;
  padding: var(--spacing-6);
  margin-bottom: var(--spacing-4);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  border-radius: var(--border-radius-md);
  text-align: center;
}

.total-stat .stat-number {
  color: var(--color-background-white);
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-3);
  display: block;
  line-height: 1;
}

.total-stat .stat-label {
  color: var(--color-background-white);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-lg);
  opacity: 0.95;
}

.player-status {
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-semibold);
    padding: var(--spacing-1) var(--spacing-3);
    border-radius: 9999px;
    text-transform: capitalize;
    color: var(--color-background-white);
    min-width: 80px;
    text-align: center;
    display: inline-block;
}
.player-status.activo { background-color: var(--color-success); }
.player-status.lesionado { background-color: var(--color-danger); }
.player-status.suspendido { background-color: var(--color-warning); }
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.avatar.placeholder {
  display: inline-flex;
  width: 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #eef2f7;
  color: #6b7280;
}

/* Responsive */
@media (max-width: 768px) {
  .team-players-page {
    padding: 16px;
  }
  
  .players-layout {
    grid-template-columns: 1fr;
    gap: var(--spacing-4);
  }
  
  .right-column {
    position: static;
    order: -1;
  }
  
  .stats-subgrid {
    grid-template-columns: repeat(2, 1fr);
  }
}

</style>