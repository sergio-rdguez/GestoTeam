<template>
  <div class="team-players-page">
    <h2 class="page-title">{{ teamName || 'Jugadores' }}</h2>
    
    <DataTable
      :items="players"
      :columns="columns"
      :loading="loading"
      default-sort-key="fullName"
      @row-click="viewPlayerDetails"
    >
      <template #cell-status="{ item }">
        <span class="player-status" :class="item.status.toLowerCase()">
          {{ item.status }}
        </span>
      </template>
    </DataTable>

    <FabMenu :actions="fabActions" @action-clicked="addPlayer" />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import DataTable from "@/components/common/DataTable.vue";
import FabMenu from "@/components/common/FabMenu.vue";

export default {
  components: {
    DataTable,
    FabMenu,
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
      fabActions: [
        { label: 'Añadir Jugador', event: 'add-player' }
      ],
    };
  },
  methods: {
    async fetchPlayers() {
      this.loading = true;
      try {
        const teamResponse = await apiClient.get(`/teams/${this.teamId}`);
        this.teamName = teamResponse.data.name;

        const playersResponse = await apiClient.get(`/players/team/${this.teamId}`);
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
  },
  mounted() {
    this.fetchPlayers();
  }
};
</script>

<style scoped>
.team-players-page {
  padding: 2rem;
}
.page-title {
  text-align: center;
  font-size: 2rem;
  color: #333;
  margin-bottom: 2rem;
}
.player-status {
    font-size: 0.9rem;
    font-weight: bold;
    padding: 5px 10px;
    border-radius: 12px;
    text-transform: capitalize;
    color: white;
}
.player-status.activo { background-color: #28a745; }
.player-status.lesionado { background-color: #dc3545; }
.player-status.suspendido { background-color: #ffc107; }
</style>