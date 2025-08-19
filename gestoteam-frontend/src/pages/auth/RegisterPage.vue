<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '@/services/auth';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import BaseCard from '@/components/base/BaseCard.vue';

const router = useRouter();
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
});
const isLoading = ref(false);
const errorMessage = ref('');

const handleRegister = async () => {
  errorMessage.value = '';
  if (form.value.password !== form.value.confirmPassword) {
    errorMessage.value = 'Las contraseñas no coinciden';
    return;
  }
  isLoading.value = true;
  try {
    await authService.register({ username: form.value.username, password: form.value.password });
    await authService.login({ username: form.value.username, password: form.value.password });
    await router.replace({ name: 'Teams' });
  } catch (error) {
    errorMessage.value = error?.response?.data?.message || 'No se pudo completar el registro';
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="register-page">
    <BaseCard class="register-card">
      <template #header>
        <h2 class="text-center">Crear cuenta</h2>
      </template>
      <template #default>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">Usuario</label>
            <BaseInput id="username" type="text" v-model="form.username" placeholder="Tu nombre de usuario" required />
          </div>
          <div class="form-group">
            <label for="password">Contraseña</label>
            <BaseInput id="password" type="password" v-model="form.password" placeholder="••••••••" required />
          </div>
          <div class="form-group">
            <label for="confirmPassword">Repite la contraseña</label>
            <BaseInput id="confirmPassword" type="password" v-model="form.confirmPassword" placeholder="••••••••" required />
          </div>

          <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

          <BaseButton type="submit" class="w-100" :is-loading="isLoading" primary>
            Registrarme
          </BaseButton>
        </form>
      </template>
    </BaseCard>
  </div>
  
</template>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--color-background-soft);
}

.register-card {
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

.error-text {
  color: #b00020;
  margin-bottom: var(--spacing-3);
}
</style>

