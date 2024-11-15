<template>
    <div class="add-team-page">
      <h2>Añadir Nuevo Equipo</h2>
      <form @submit.prevent="submitForm">
        <!-- Nombre del Equipo -->
        <div class="form-group">
          <label for="name">Nombre del Equipo</label>
          <input
            id="name"
            v-model="team.name"
            type="text"
            placeholder="Nombre del equipo"
            required
          />
          <p v-if="errors.name" class="error-message">{{ errors.name }}</p>
        </div>
  
        <!-- Descripción -->
        <div class="form-group">
          <label for="description">Descripción</label>
          <textarea
            id="description"
            v-model="team.description"
            placeholder="Descripción del equipo"
          ></textarea>
          <p v-if="errors.description" class="error-message">{{ errors.description }}</p>
        </div>
  
        <!-- Ubicación -->
        <div class="form-group">
          <label for="location">Ubicación</label>
          <input
            id="location"
            v-model="team.location"
            type="text"
            placeholder="Ubicación del equipo"
          />
          <p v-if="errors.location" class="error-message">{{ errors.location }}</p>
        </div>
  
        <!-- División -->
        <div class="form-group">
          <label for="division">División</label>
          <input
            id="division"
            v-model="team.division"
            type="text"
            placeholder="División del equipo"
            required
          />
          <p v-if="errors.division" class="error-message">{{ errors.division }}</p>
        </div>
  
        <!-- Categoría -->
        <div class="form-group">
          <label for="categoryId">Categoría</label>
          <select
            id="categoryId"
            v-model="team.categoryId"
            required
          >
            <option value="" disabled selected>Seleccione una categoría</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <p v-if="errors.categoryId" class="error-message">{{ errors.categoryId }}</p>
        </div>
  
        <!-- Botones de acción -->
        <div class="form-actions">
          <button type="submit" class="btn btn-primary">Guardar</button>
          <router-link to="/teams" class="btn btn-secondary">Cancelar</router-link>
        </div>
      </form>
    </div>
  </template>
  
  <script>
  import apiClient from "@/services/api";
  
  export default {
    data() {
      return {
        team: {
          name: "",
          description: "",
          location: "",
          division: "",
          categoryId: null,
        },
        categories: [], // Lista de categorías cargadas del backend
        errors: {}, // Almacena errores del backend
      };
    },
    methods: {
      async fetchCategories() {
        try {
          const response = await apiClient.get("/categories");
          this.categories = response.data;
        } catch (error) {
          console.error("Error al obtener categorías:", error);
        }
      },
      async submitForm() {
        try {
          // Intenta guardar el equipo en el backend
          await apiClient.post("/teams", this.team);
          // Redirige de vuelta a la lista de equipos si se guarda correctamente
          this.$router.push("/teams");
        } catch (error) {
          if (error.response && error.response.data && error.response.data.errors) {
            // Captura errores de validación desde el backend
            this.errors = error.response.data.errors;
          } else {
            console.error("Error inesperado:", error);
          }
        }
      },
    },
    mounted() {
      this.fetchCategories(); // Cargar categorías al montar el componente
    },
  };
  </script>
  
  <style scoped>
  .add-team-page {
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
  
  textarea {
    height: 80px;
    resize: none;
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
  