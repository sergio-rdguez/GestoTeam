<template>
  <div class="rivals-details-page">
    <div v-if="loading" class="loading-message">
      <p>Cargando detalles del rival...</p>
    </div>
    <div v-else>
      <div class="rival-header">
        <h2 class="rival-name">{{ rival.name }}</h2>
        <p class="rival-info">Fundado: <strong>{{ rival.founded }}</strong></p>
        <p class="rival-info">Ciudad: <strong>{{ rival.city }}</strong></p>
      </div>

      <div class="rival-content">
        <slot />
      </div>

      <FabMenu :actions="fabActions" @action-clicked="handleFabAction" />
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import FabMenu from "@/components/common/FabMenu.vue";

export default {
  name: "RivalsDetails",
  components: {
    FabMenu,
  },
  data() {
    return {
      rival: null,
      loading: true,
      fabActions: [
        { label: "Volver", event: "go-back" },
        { label: "Editar Rival", event: "edit-rival" },
      ],
    };
  },
  methods: {
    async fetchRival() {
      try {
        const rivalId = this.$route.params.id;
        const { data } = await apiClient.get(`/rivals/${rivalId}`);
        this.rival = data;
      } catch (error) {
        console.error("Error al cargar rival:", error);
      } finally {
        this.loading = false;
      }
    },
    handleFabAction(event) {
      if (event === 'go-back') {
        this.goBack();
      } else if (event === 'edit-rival') {
        this.editRival();
      }
    },
    goBack() {
      this.$router.push({ name: "Teams" });
    },
    editRival() {
      this.$router.push({ name: "EditRival", params: { id: this.rival.id } });
    },
  },
  mounted() {
    this.fetchRival();
  },
};
</script>

<style scoped>
.rivals-details-page {
  padding: 20px;
  font-family: 'Arial', sans-serif;
  background-color: #f9f9f9;
  min-height: 100vh;
}
.loading-message {
  text-align: center;
  font-size: 1.2rem;
  color: #666;
}
.rival-header {
  text-align: center;
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
.rival-name {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
}
.rival-info {
  font-size: 1rem;
  color: #666;
  margin: 5px 0;
}
.nav-item {
  text-decoration: none;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border-radius: 8px;
  text-align: center;
  font-size: 1rem;
  transition: background-color 0.2s ease, transform 0.2s ease;
}
.nav-item:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}
</style>