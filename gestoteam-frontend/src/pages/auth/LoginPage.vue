<template>
  <div class="login-page">
    <BaseCard class="login-container" title="Iniciar Sesión">
      <div v-if="loading" class="loading-overlay">
        <LoadingSpinner />
      </div>
      <form @submit.prevent="handleLogin" class="login-form">
        <BaseInput
          v-model="form.username"
          label="Usuario"
          id="username"
          placeholder="Introduce tu usuario"
          required
        />
        <BaseInput
          v-model="form.password"
          label="Contraseña"
          id="password"
          type="password"
          placeholder="Introduce tu contraseña"
          required
        />
        <div class="form-actions">
            <BaseButton type="submit" variant="primary" :loading="isLoggingIn">
                Iniciar Sesión
            </BaseButton>
        </div>
      </form>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </BaseCard>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
    components: {
        BaseCard,
        BaseInput,
        BaseButton,
        LoadingSpinner,
    },
    data() {
        return {
            form: {
                username: "",
                password: "",
            },
            isLoggingIn: false,
            loading: false, // Para un estado de carga inicial si fuera necesario
            errorMessage: "",
        };
    },
    methods: {
        async handleLogin() {
            this.isLoggingIn = true;
            this.errorMessage = "";
            try {
                const response = await apiClient.post("/auth/login", this.form);
                const token = response.data.token;
                localStorage.setItem('jwt_token', token);
                this.$router.push({ name: "Teams" });
            } catch (error) {
                this.errorMessage = error.response?.data?.message || "Error inesperado. Inténtalo de nuevo.";
            } finally {
                this.isLoggingIn = false;
            }
        },
    },
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--color-background-light);
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  border-radius: 12px; /* Heredar el borde de la tarjeta */
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-actions {
  margin-top: 1rem;
}

.error-message {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  text-align: center;
  margin-top: 1.5rem;
}
</style>