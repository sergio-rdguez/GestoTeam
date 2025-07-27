<template>
  <div class="team-matches-page">
    <div class="header">
      <button class="back-button" @click="goBack">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h2 class="page-title">Partidos de {{ teamName }}</h2>
    </div>

    <DataTable
      :items="matches"
      :columns="columns"
      :loading="loading"
      default-sort-key="date"
      @row-click="goToMatchDetails"
    >
      <template #cell-date="{ value }">
        {{ formatDate(value) }}
      </template>
      <template #cell-status="{ item }">
        <div
          class="match-status"
          :class="{
            victory: item.finalized && item.won,
            defeat: item.finalized && !item.won,
            pending: !item.finalized,
          }"
        >
          {{ item.finalized ? item.result : "Pendiente" }}
        </div>
      </template>
    </DataTable>

    <FabMenu :actions="fabActions" @action-clicked="goToAddMatch" />
    
    <MessageBox
      v-if="showMessage"
      :message="message"
      :type="messageType"
      @close="closeMessage"
    />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import MessageBox from "@/pages/utils/MessageBox.vue";
import DataTable from "@/components/common/DataTable.vue";
import FabMenu from "@/components/common/FabMenu.vue";

export default {
  components: {
    MessageBox,
    DataTable,
    FabMenu,
  },
  data() {
    return {
      teamName: "Cargando...",
      matches: [],
      loading: true,
      showMessage: false,
      message: "",
      messageType: "info",
      columns: [
        { key: 'opponent', label: 'Rival', sortable: true },
        { key: 'date', label: 'Fecha', sortable: true },
        { key: 'location', label: 'Estadio', sortable: true },
        { key: 'status', label: 'Resultado', sortable: false }, // Columna custom para el estado
      ],
      fabActions: [
          { label: "Añadir Partido", event: "add-match" }
      ]
    };
  },
  methods: {
    async fetchMatches() {
      this.loading = true;
      try {
        const teamId = this.$route.params.id;
        const teamResponse = await apiClient.get(`/teams/${teamId}`);
        this.teamName = teamResponse.data.name;

        const matchesResponse = await apiClient.get(`/matches/team/${teamId}`);
        this.matches = matchesResponse.data;
      } catch (error) {
        this.message = "No se pudieron cargar los partidos del equipo.";
        this.messageType = "error";
        this.showMessage = true;
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push({
        name: "TeamDetails",
        params: { id: this.$route.params.id },
      });
    },
    goToAddMatch() {
      this.$router.push({
        name: "AddMatch",
        params: { teamId: this.$route.params.id },
      });
    },
    goToMatchDetails(match) {
      this.$router.push({ name: "MatchDetails", params: { id: match.id } });
    },
    formatDate(date) {
        const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
        return new Date(date).toLocaleString("es-ES", options);
    },
    closeMessage() {
      this.showMessage = false;
    },
  },
  mounted() {
    this.fetchMatches();
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600&family=Roboto:wght@400;500&display=swap");

.team-matches-page {
  padding: 2rem;
  background: #f9fafb;
  min-height: 100vh;
  font-family: "Roboto", sans-serif;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}
.back-button {
  background: white;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.3s ease, transform 0.2s ease;
}
.back-button i {
  margin-right: 5px;
}
.back-button:hover {
  background: #f0f0f0;
  transform: scale(1.05);
}
.page-title {
  text-align: center;
  font-size: 2.2rem;
  margin: 0;
  color: #2c3e50;
  font-family: "Montserrat", sans-serif;
  font-weight: 600;
  flex-grow: 1;
}
.match-status {
  padding: 5px 12px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #fff;
  border-radius: 15px;
  text-align: center;
  display: inline-block;
}
.match-status.pending { background-color: #3498db; }
.match-status.victory { background-color: #2ecc71; }
.match-status.defeat { background-color: #e74c3c; }
</style>