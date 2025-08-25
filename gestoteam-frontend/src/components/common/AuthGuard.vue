<template>
  <div v-if="isLoading" class="auth-loading">
    <LoadingSpinner />
    <p>Verificando autenticación...</p>
  </div>
  <div v-else-if="!isAuthenticated" class="auth-redirect">
    <p>Redirigiendo al login...</p>
  </div>
  <slot v-else />
</template>

<script>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '@/services/auth';
import LoadingSpinner from './LoadingSpinner.vue';

export default {
  name: 'AuthGuard',
  components: {
    LoadingSpinner
  },
  setup() {
    const router = useRouter();
    const isLoading = ref(true);
    
    const isAuthenticated = computed(() => authService.state.isAuthenticated);

    onMounted(async () => {
      try {
        // Verificar si hay un token y validarlo
        if (authService.state.token && !authService.state.isAuthenticated) {
          await authService.checkAuth();
        }
        
        // Si después de la validación no está autenticado, redirigir
        if (!authService.state.isAuthenticated) {
          router.push({ name: 'Login' });
          return;
        }
      } catch (error) {
        console.error('Error en AuthGuard:', error);
        router.push({ name: 'Login' });
        return;
      } finally {
        isLoading.value = false;
      }
    });

    return {
      isLoading,
      isAuthenticated
    };
  }
};
</script>

<style scoped>
.auth-loading,
.auth-redirect {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  gap: 1rem;
}

.auth-loading p,
.auth-redirect p {
  color: #666;
  font-size: 1.1rem;
}
</style>
