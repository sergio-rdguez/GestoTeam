<template>
  <div class="rivals-details-page">
    <div v-if="loading" class="loading-message">
      <p>Cargando detalles del rival...</p>
    </div>
    <div v-else>
      <!-- Encabezado del rival -->
      <div class="rival-header">
        <h2 class="rival-name">{{ rival.name }}</h2>
        <p class="rival-info">Fundado: <strong>{{ rival.founded }}</strong></p>
        <p class="rival-info">Ciudad: <strong>{{ rival.city }}</strong></p>
      </div>

      <!-- Contenido de detalle -->
      <div class="rival-content">
        <slot />
      </div>

      <!-- FAB Actions -->
      <div class="fab-container">
        <button class="fab" @click="toggleFabMenu">
          <i class="fa-solid fa-bars"></i>
        </button>
        <div v-if="showFabMenu" class="fab-actions">
          <button @click="goBack" class="fab-action">Volver</button>
          <button @click="editRival" class="fab-action">Editar Rival</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  name: "RivalsDetails",
  data() {
    return {
      rival: null,
      loading: true,
      showFabMenu: false,
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
    toggleFabMenu() {
      this.showFabMenu = !this.showFabMenu;
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

.fab-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column-reverse;
  align-items: flex-end;
}
.fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #007bff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}
.fab i {
  font-size: 1.5em;
  color: white;
}
.fab-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 10px;
}
.fab-action {
  background: white;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}
.fab-action:hover {
  background: #f1f1f1;
}
</style>
