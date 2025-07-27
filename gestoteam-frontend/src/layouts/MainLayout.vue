<template>
  <div class="main-layout">
    <header class="app-header">
      <h1>GestoTeam</h1>
      <div class="header-buttons">
        <router-link to="/teams" class="home-button" aria-label="Inicio">
          <i class="fas fa-home"></i>
        </router-link>
        <router-link
          to="/settings"
          class="settings-button"
          aria-label="Configuración"
        >
          <i class="fas fa-cog"></i>
        </router-link>
        <button
          class="logout-button"
          @click="logout"
          aria-label="Cerrar sesión"
        >
          <i class="fas fa-sign-out-alt"></i>
        </button>
      </div>
    </header>

    <ErrorMessage v-if="error" :message="errorMessage" @close="resetError" />

    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
import { EventBus } from "@/utils/EventBus";
import ErrorMessage from "@/pages/utils/ErrorMessage.vue";
import authService from "@/services/auth"; // Importamos el servicio

export default {
  name: "MainLayout",
  components: { ErrorMessage },
  data() {
    return {
      error: false,
      errorMessage: "",
    };
  },
  mounted() {
    EventBus.on("error", this.handleError);
  },
  beforeUnmount() {
    EventBus.off("error", this.handleError);
  },
  methods: {
    handleError({ message }) {
      this.error = true;
      this.errorMessage = message || "Ocurrió un error inesperado";
    },
    resetError() {
      this.error = false;
      this.errorMessage = "";
    },
    logout() {
      // Usamos nuestro servicio de autenticación para cerrar sesión
      authService.logout();
      this.$router.push({ name: "LoginPage" });
    },
  },
};
</script>

<style scoped>
/* Los estilos se mantienen igual */
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.app-header {
  background-color: #007bff;
  color: white;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1.2rem;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.2);
}

.app-header h1 {
  margin: 0;
  font-size: 1.5rem;
  flex: 1;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: bold;
}

.header-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.settings-button,
.logout-button,
.home-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: white;
  border-radius: 50%;
  color: #007bff;
  font-size: 1.2rem;
  border: none;
  cursor: pointer;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease, color 0.3s ease;
  text-decoration: none; /* Para los router-link */
}

.settings-button:hover,
.logout-button:hover,
.home-button:hover {
  background-color: #007bff;
  color: white;
}

.settings-button i,
.logout-button i,
.home-button i {
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .app-header {
    padding: 8px;
  }

  .app-header h1 {
    font-size: 1.2rem;
  }

  .header-buttons {
    gap: 8px;
  }

  .settings-button,
  .home-button,
  .logout-button {
    width: 36px;
    height: 36px;
    font-size: 1.1rem;
  }
}
</style>