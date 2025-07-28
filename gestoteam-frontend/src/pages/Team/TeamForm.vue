<template>
  <div class="form-page-container">
    <PageHeader 
      :title="isEditMode ? 'Editar Equipo' : 'Añadir Nuevo Equipo'" 
      show-back-button 
      @back="goBack" 
    />

    <BaseCard>
      <form @submit.prevent="submitForm">
        <div class="form-grid">
          <BaseInput
            v-model="team.name"
            label="Nombre del Equipo"
            id="name"
            placeholder="Ej: GestoTeam Senior"
            required
          />
          <BaseSelect
            v-model="team.category"
            label="Categoría"
            id="category"
            :options="categoryOptions"
            required
          />
          <BaseInput
            v-model="team.division"
            label="División"
            id="division"
            placeholder="Ej: Primera Aficionados"
            required
          />
          <BaseInput
            v-model="team.field"
            label="Campo"
            id="field"
            placeholder="Ej: Ciudad Deportiva Gesto"
          />
          <BaseInput
            v-model="team.location"
            label="Ubicación"
            id="location"
            placeholder="Ej: Madrid"
          />
          <BaseTextarea
            v-model="team.description"
            label="Descripción"
            id="description"
            placeholder="Anotaciones sobre el equipo (opcional)"
            :rows="5"
            class="form-grid-span-2"
          />
        </div>

        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Crear Equipo") }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>
  </div>
</template>

<script>
import apiClient from "@/services/api";
import { notificationService } from '@/services/notificationService';
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseTextarea from "@/components/base/BaseTextarea.vue";
import BaseButton from "@/components/base/BaseButton.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseInput,
    BaseSelect,
    BaseTextarea,
    BaseButton,
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
      isEditMode: false,
      isSaving: false,
    };
  },
  computed: {
    categoryOptions() {
      return this.categories.map(c => ({ value: c.code, text: c.description }));
    }
  },
  methods: {
    async fetchTeam() {
      try {
        const response = await apiClient.get(`/teams/${this.$route.params.id}`);
        this.team = response.data;
      } catch (error) {
        notificationService.showError("No se pudo cargar el equipo.");
      }
    },
    async fetchCategories() {
      try {
        const response = await apiClient.get("/enums/categories");
        this.categories = response.data;
        if (!this.isEditMode && this.categories.length > 0) {
          this.team.category = this.categories[0].code;
        }
      } catch (error) {
        notificationService.showError("No se pudieron cargar las categorías.");
      }
    },
    async submitForm() {
      this.isSaving = true;
      try {
        let savedTeam;
        if (this.isEditMode) {
          const response = await apiClient.put(`/teams/${this.$route.params.id}`, this.team);
          savedTeam = response.data;
          notificationService.showSuccess("Equipo actualizado con éxito.");
        } else {
          const response = await apiClient.post("/teams", this.team);
          savedTeam = response.data;
          notificationService.showSuccess("Equipo creado con éxito.");
        }
        this.$router.push({ name: 'TeamDetails', params: { id: savedTeam.id } });
      } catch (error) {
        // El interceptor de api.js ya se encarga de mostrar el error
      } finally {
        this.isSaving = false;
      }
    },
    goBack() {
      this.$router.push({ name: 'Teams' });
    },
  },
  created() {
    this.isEditMode = !!this.$route.params.id;
    this.fetchCategories();
    if (this.isEditMode) {
      this.fetchTeam();
    }
  },
};
</script>

<style scoped>
.form-page-container {
  max-width: 900px;
  margin: 0 auto;
}
.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-5);
}
.form-grid-span-2 {
  grid-column: span 2;
}
.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
@media (max-width: 768px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
    .form-grid-span-2 {
        grid-column: span 1;
    }
}
</style>