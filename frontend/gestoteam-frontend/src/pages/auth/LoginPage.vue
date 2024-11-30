<template>
  <div class="login-page">
    <div v-if="loading" class="loading-overlay">
      <p>Procesando...</p>
    </div>
    <div v-else class="login-container">
      <h1 class="page-title">Iniciar Sesión</h1>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Usuario</label>
          <input type="text" id="username" v-model="form.username" required placeholder="Introduce tu usuario" />
        </div>
        <div class="form-group">
          <label for="password">Contraseña</label>
          <input type="password" id="password" v-model="form.password" required placeholder="Introduce tu contraseña" />
        </div>
        <button type="submit" class="btn-login">Iniciar Sesión</button>
      </form>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  data() {
    return {
      form: {
        username: "",
        password: "",
      },
      loading: false,
      errorMessage: "",
    };
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      this.errorMessage = "";

      try {
        await apiClient.post("/auth/login", this.form);

        // Guardar el usuario en localStorage
        localStorage.setItem("audit_user", this.form.username);

        // Redirigir a la página principal
        this.$router.push({ name: "Teams" });
      } catch (error) {
        console.error("Error al iniciar sesión:", error);
        this.errorMessage =
          error.response?.data?.message || "Error inesperado. Inténtalo de nuevo.";
      } finally {
        this.loading = false;
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
  background-color: #f9f9f9;
  font-family: Arial, sans-serif;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  color: #007bff;
  z-index: 1000;
}

.login-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 100%;
  max-width: 400px;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
}

input:focus {
  border-color: #007bff;
  outline: none;
}

.btn-login {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  font-size: 1.2rem;
  cursor: pointer;
  width: 100%;
}

.btn-login:hover {
  background-color: #0056b3;
}

.error-message {
  color: #dc3545;
  font-size: 0.9rem;
  margin-top: 10px;
}
</style>