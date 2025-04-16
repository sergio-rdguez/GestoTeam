<template>
  <div class="team-form-page">
    <h2>{{ isEditMode ? "Editar Equipo" : "Añadir Nuevo Equipo" }}</h2>
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

      <!-- Campo -->
      <div class="form-group">
        <label for="field">Campo</label>
        <input
          id="field"
          v-model="team.field"
          type="text"
          placeholder="Campo de juego (opcional)"
        />
      </div>

      <!-- Ubicación -->
      <div class="form-group">
        <label for="location">Ubicación</label>
        <input
          id="location"
          v-model="team.location"
          type="text"
          placeholder="Ubicación (opcional)"
        />
      </div>

      <!-- Categoría -->
      <div class="form-group">
        <label for="category">Categoría</label>
        <select
          id="category"
          v-model="team.category"
          class="styled-select"
          required
        >
          <option
            v-for="category in categories"
            :key="category.code"
            :value="category.code"
          >
            {{ category.description }}
          </option>
        </select>
        <p v-if="errors.category" class="error-message">
          {{ errors.category }}
        </p>
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
        <p v-if="errors.division" class="error-message">
          {{ errors.division }}
        </p>
      </div>

      <!-- Descripción -->
      <div class="form-group">
        <label for="description">Descripción</label>
        <textarea
          id="description"
          v-model="team.description"
          placeholder="Descripción del equipo (opcional)"
        ></textarea>
      </div>

      <!-- Botones de acción -->
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">
          {{ isEditMode ? "Guardar Cambios" : "Guardar" }}
        </button>
        <router-link to="/teams" class="btn btn-secondary"
          >Cancelar</router-link
        >
      </div>
    </form>

    <!-- Componente MessageBox para mensajes -->
    <MessageBox
      v-if="showMessage"
      :message="message"
      :type="messageType"
      @close="closeMessage"
    />
  </div>
</template>

<script>
import apiClient from "@/services/api";
import MessageBox from "@/pages/utils/MessageBox.vue"; // Ajustada la ruta según tu estructura

export default {
  components: {
    MessageBox,
  },
  data() {
    return {
      team: {
        name: "",
        field: "",
        location: "",
        division: "",
        category: "",
        description: "",
      },
      categories: [],
      errors: {},
      isEditMode: false, // Determina si estamos editando o añadiendo
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  created() {
    this.isEditMode = !!this.$route.params.id; // Verifica si hay un ID en la ruta
    this.fetchCategories();
    if (this.isEditMode) {
      this.fetchTeam();
    }
  },
  methods: {
    async fetchTeam() {
      try {
        console.log("Fetching team with ID:", this.$route.params.id);
        const response = await apiClient.get(`/teams/${this.$route.params.id}`);
        this.team = {
          ...response.data,
          category: response.data.category.toUpperCase(), // Normaliza el valor de la categoría
        };
      } catch (error) {
        console.error("Error fetching team:", error.response || error);
        this.message =
          "No se pudo cargar el equipo: " +
          (error.response?.data?.message ||
            error.message ||
            "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    async fetchCategories() {
      try {
        console.log("Fetching categories...");
        const response = await apiClient.get("/enums/categories");
        this.categories = response.data.map((category) => ({
          code: category.code,
          description: category.description,
        }));
      } catch (error) {
        console.error("Error fetching categories:", error.response || error);
        this.message =
          "No se pudieron cargar las categorías: " +
          (error.response?.data?.message ||
            error.message ||
            "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    async submitForm() {
      try {
        console.log(
          "Submitting team data:",
          this.team,
          "Edit mode:",
          this.isEditMode
        );
        if (this.isEditMode) {
          const teamId = this.$route.params.id;
          const response = await apiClient.put(`/teams/${teamId}`, this.team);
          if (response.status === 200) {
            this.message = "Equipo actualizado con éxito.";
            this.messageType = "success";
            this.showMessage = true;
            setTimeout(() => {
              this.$router.push("/teams");
            }, 2000); // Espera 2 segundos para que el usuario vea el mensaje
          } else {
            throw new Error("Respuesta inesperada del servidor al actualizar.");
          }
        } else {
          const response = await apiClient.post("/teams", this.team);
          if (response.status === 200 || response.status === 201) {
            // Aceptamos 201 (Created) también para POST
            this.message = "Equipo creado con éxito.";
            this.messageType = "success";
            this.showMessage = true;
            setTimeout(() => {
              this.$router.push("/teams");
            }, 2000); // Espera 2 segundos para que el usuario vea el mensaje
          } else {
            throw new Error("Respuesta inesperada del servidor al crear.");
          }
        }
      } catch (error) {
        console.error("Error submitting form:", error.response || error);
        if (
          error.response &&
          error.response.data &&
          error.response.data.errors
        ) {
          this.errors = error.response.data.errors;
          this.message = "Errores en los datos ingresados: revisa los campos.";
          this.messageType = "error";
        } else {
          this.message =
            "No se pudo guardar el equipo: " +
            (error.response?.data?.message ||
              error.message ||
              "Inténtelo de nuevo.");
          this.messageType = "error";
        }
        this.showMessage = true;
      }
    },
    closeMessage() {
      this.showMessage = false;
      console.log("MessageBox closed, showMessage:", this.showMessage);
    },
  },
};
</script>

<style scoped>
.team-form-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 1.8rem;
}

.form-group {
  margin-bottom: 20px;
}

textarea,
input,
select {
  width: 100%;
  padding: 12px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
  transition: border-color 0.3s, box-shadow 0.3s;
}

textarea {
  height: 60px;
  resize: none;
}

select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 30px;
  background-color: #fff;
  background-image: url("data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 fill=%22none%22 viewBox=%220 0 8 8%22 width=%228%22 height=%228%22%3E%3Cpath stroke=%22%23333%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22 stroke-width=%22.5%22 d=%22M1 2l3 3 3-3%22/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  cursor: pointer;
}

select:focus {
  border-color: #007bff;
  box-shadow: 0 0 8px rgba(0, 123, 255, 0.25);
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
  gap: 10px;
}

.btn {
  padding: 10px 15px;
  font-size: 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  flex: 1;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  flex: 1;
}

@media (max-width: 768px) {
  .team-form-page {
    padding: 15px;
  }

  h2 {
    font-size: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    margin-bottom: 10px;
  }
}

@media (min-width: 1200px) {
  .team-form-page {
    padding: 40px;
  }

  h2 {
    font-size: 2rem;
  }

  textarea,
  input,
  select {
    font-size: 1.1rem;
  }

  .btn {
    padding: 12px 20px;
    font-size: 1.1rem;
  }
}
</style>
