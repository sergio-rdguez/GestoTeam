<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '@/services/auth';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import BaseCard from '@/components/base/BaseCard.vue';

const router = useRouter();
const credentials = ref({
  username: '',
  password: '',
});
const isLoading = ref(false);

const handleLogin = async () => {
  isLoading.value = true;
  try {
    await authService.login(credentials.value);
    await router.replace({ name: 'Teams' });

  } catch (error) {
    console.error("Error en el login desde la página:", error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="login-page">
    <BaseCard class="login-card">
      <template #header>
        <h2 class="text-center">Iniciar Sesión en GestoTeam</h2>
      </template>
      <template #default>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">Usuario</label>
            <BaseInput id="username" type="text" v-model="credentials.username" placeholder="Tu nombre de usuario"
              required />
          </div>
          <div class="form-group">
            <label for="password">Contraseña</label>
            <BaseInput id="password" type="password" v-model="credentials.password" placeholder="••••••••" required />
          </div>
          <BaseButton type="submit" class="w-100" :is-loading="isLoading" primary>
            Entrar
          </BaseButton>
        </form>
      </template>
    </BaseCard>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--color-background-soft);
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: var(--spacing-4);
}

.w-100 {
  width: 100%;
}

.text-center {
  text-align: center;
}
</style>