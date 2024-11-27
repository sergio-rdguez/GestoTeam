<template>
  <div class="add-player-page">
    <h2>Añadir Nuevo Jugador</h2>
    <form @submit.prevent="submitForm">
      <!-- Nombre -->
      <div class="form-group">
        <label for="name">Nombre</label>
        <input id="name" v-model="player.name" type="text" placeholder="Nombre del jugador" required />
        <p v-if="errors.name" class="error-message">{{ errors.name }}</p>
      </div>

      <!-- Primer Apellido -->
      <div class="form-group">
        <label for="surnameFirst">Primer Apellido</label>
        <input id="surnameFirst" v-model="player.surnameFirst" type="text" placeholder="Primer apellido" required />
        <p v-if="errors.surnameFirst" class="error-message">{{ errors.surnameFirst }}</p>
      </div>

      <!-- Segundo Apellido -->
      <div class="form-group">
        <label for="surnameSecond">Segundo Apellido</label>
        <input id="surnameSecond" v-model="player.surnameSecond" type="text" placeholder="Segundo apellido" required />
        <p v-if="errors.surnameSecond" class="error-message">{{ errors.surnameSecond }}</p>
      </div>

      <!-- Edad -->
      <div class="form-group">
        <label for="age">Edad</label>
        <input id="age" v-model="player.age" type="number" min="1" placeholder="Edad del jugador" required />
        <p v-if="errors.age" class="error-message">{{ errors.age }}</p>
      </div>

      <!-- Número -->
      <div class="form-group">
        <label for="number">Número</label>
        <input id="number" v-model="player.number" type="number" min="1" max="99" placeholder="Número del jugador"
          required />
        <p v-if="errors.number" class="error-message">{{ errors.number }}</p>
      </div>

      <!-- Posición -->
      <div class="form-group">
        <label for="position">Posición</label>
        <select id="position" v-model="player.position" required>
          <option value="" disabled>Selecciona una posición</option>
          <option value="POR">Portero</option>
          <option value="DFC">Defensa Central</option>
          <option value="LTD">Lateral Derecho</option>
          <option value="LTI">Lateral Izquierdo</option>
          <option value="MCD">Medio Centro Defensivo</option>
        </select>
        <p v-if="errors.position" class="error-message">{{ errors.position }}</p>
      </div>

      <!-- Estado -->
      <div class="form-group">
        <label for="status">Estado</label>
        <select id="status" v-model="player.status" required>
          <option value="ACTIVO">Activo</option>
          <option value="INACTIVO">Inactivo</option>
        </select>
        <p v-if="errors.status" class="error-message">{{ errors.status }}</p>
      </div>

      <!-- Botones de acción -->
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">Guardar</button>
        <router-link :to="{ name: 'TeamDetails', params: { id: player.teamId } }"
          class="btn btn-secondary">Cancelar</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import { getAudit } from "@/services/audit";

export default {
  data() {
    return {
      player: {
        name: "",
        surnameFirst: "",
        surnameSecond: "",
        age: null,
        number: null,
        position: "",
        status: "ACTIVO",
        teamId: this.$route.params.teamId,
      },
      errors: {}, // Para almacenar errores del backend
    };
  },
  methods: {
    async submitForm() {
      try {
        // Generar el audit dinámico
        const audit = getAudit();

        // Enviar datos al backend
        await apiClient.post("/api/players", this.player, {
          headers: {
            audit,
          },
        });

        // Redirigir al equipo
        this.$router.push({ name: "TeamDetails", params: { id: this.player.teamId } });
      } catch (error) {
        if (error.response && error.response.data && error.response.data.errors) {
          // Manejar errores de validación
          this.errors = error.response.data.errors;
        } else {
          console.error("Error inesperado:", error);
        }
      }
    },
  },
};
</script>

<style scoped>
.add-player-page {
  padding: 20px;
  max-width: 500px;
  margin: 0 auto;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

textarea,
input,
select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.error-message {
  color: red;
  font-size: 0.875rem;
  margin-top: 5px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}
</style>
