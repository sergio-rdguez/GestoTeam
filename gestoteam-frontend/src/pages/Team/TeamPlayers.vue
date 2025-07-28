<template>
  <div class="team-players-page">
    <PageHeader :title="teamName || 'Jugadores'" show-back-button @back="goBack">
      <BaseButton v-if="players.length > 0" @click="addPlayer">
        <i class="fa-solid fa-plus"></i> Añadir Jugador
      </BaseButton>
    </PageHeader>

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
      default-sort-key="fullName"
      @row-click="viewPlayerDetails"
    >
      <template #cell-status="{ item }">
        <span class="player-status" :class="item.status.toLowerCase()">
          {{ item.status }}
        </span>
      </template>
    </DataTable>
  </div>
</template>

<script>
import apiClient from "@/services/api";
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
      teamId: this.$route.params.id,
      teamName: '',
      players: [],
      loading: true,
      columns: [
        { key: 'fullName', label: 'Nombre', sortable: true },
        { key: 'position', label: 'Posición', sortable: true, sortOn: 'positionOrder' },
        { key: 'number', label: 'Dorsal', sortable: true },
        { key: 'status', label: 'Estado', sortable: true },
      ],
    };
  },
  methods: {
    async fetchPlayers() {
      this.loading = true;
      try {
        // Obtenemos el nombre del equipo y los jugadores en paralelo
        const [teamResponse, playersResponse] = await Promise.all([
          apiClient.get(`/teams/${this.teamId}`),
          apiClient.get(`/players/team/${this.teamId}`)
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
      this.$router.push({ name: 'PlayerDetails', params: { id: player.id } });
    },
    addPlayer() {
      this.$router.push({ name: 'AddPlayer', params: { teamId: this.teamId } });
    },
    goBack() {
      this.$router.push({ name: 'TeamDetails', params: { id: this.teamId } });
    }
  },
  mounted() {
    this.fetchPlayers();
  }
};
</script>

<style scoped>
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
</style>