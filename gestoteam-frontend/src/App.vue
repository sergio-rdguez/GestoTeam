<template>
  <div id="app">
    <!-- Pantalla de carga -->
    <div v-if="isLoading" class="loading-screen">
      <div class="loading-content">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="GestoTeam Logo" class="logo" />
        </div>
        <div class="spinner"></div>
        <h2>Iniciando GestoTeam...</h2>
        <p>Cargando aplicación...</p>
      </div>
    </div>
    
    <!-- Aplicación principal cuando el backend esté listo -->
    <div v-else>
      <router-view />
      <NotificationContainer />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import api from '@/services/api';
import NotificationContainer from '@/components/common/NotificationContainer.vue';

export default {
  name: 'App',
  components: {
    NotificationContainer
  },
  setup() {
    const isLoading = ref(true);

    const checkBackendHealth = async () => {
      try {
        const response = await api.get('/health');
        if (response.status === 200) {
          // Pequeña pausa para que el usuario vea el logo
          setTimeout(() => {
            isLoading.value = false;
          }, 800);
        }
      } catch (error) {
        // Reintentar sin mostrar errores técnicos al usuario
        setTimeout(checkBackendHealth, 2000);
      }
    };

    onMounted(() => {
      checkBackendHealth();
    });

    return {
      isLoading
    };
  }
};
</script>

<style>
body {
  font-family: 'Roboto', Arial, sans-serif;
  margin: 0;
  background-color: #f4f4f9;
  color: #333;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.loading-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.loading-content {
  text-align: center;
  max-width: 400px;
  padding: 2rem;
}

.logo-container {
  margin-bottom: 2rem;
}

.logo {
  width: 120px;
  height: auto;
  filter: brightness(0) invert(1);
  opacity: 0.9;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 2rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-content h2 {
  margin-bottom: 1rem;
  font-size: 28px;
  font-weight: 300;
  letter-spacing: 0.5px;
}

.loading-content p {
  margin-bottom: 2rem;
  opacity: 0.9;
  font-size: 16px;
  line-height: 1.5;
}

.loading-status {
  font-size: 14px;
  opacity: 0.8;
  font-style: italic;
  padding: 0.75rem 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
</style>
