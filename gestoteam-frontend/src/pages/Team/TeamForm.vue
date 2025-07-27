<template>
  <div class="form-page">
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
          <BaseTextarea
            v-model="team.description"
            label="Descripción"
            id="description"
            placeholder="Anotaciones sobre el equipo (opcional)"
            :rows="5"
          />
        </div>

        <div class="form-actions">
          <BaseButton type="submit" :loading="isSaving" variant="primary">
            {{ isEditMode ? "Guardar Cambios" : "Crear Equipo" }}
          </BaseButton>
        </div>
      </form>
    </BaseCard>

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
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import MessageBox from "@/pages/utils/MessageBox.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseTextarea from "@/components/base/BaseTextarea.vue";
import BaseButton from "@/components/base/BaseButton.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    MessageBox,
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
      showMessage: false,
      message: "",
      messageType: "info",
    };
  },
  computed: {
      categoryOptions() {
          return this.categories.map(c => ({ value: c.code, text: c.description }));
      }
  },
  created() {
    this.isEditMode = !!this.$route.params.id;
    this.fetchCategories();
    if (this.isEditMode) {
      this.fetchTeam();
    }
  },
  methods: {
    async fetchTeam() {
      try {
        const response = await apiClient.get(`/teams/${this.$route.params.id}`);
        this.team = { ...response.data, category: response.data.category.toUpperCase() };
      } catch (error) {
        this.message = "No se pudo cargar el equipo. " + (error.response?.data?.message || "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
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
        this.message = "No se pudieron cargar las categorías. " + (error.response?.data?.message || "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      }
    },
    async submitForm() {
      this.isSaving = true;
      try {
        if (this.isEditMode) {
          await apiClient.put(`/teams/${this.$route.params.id}`, this.team);
          this.message = "Equipo actualizado con éxito.";
        } else {
          await apiClient.post("/teams", this.team);
          this.message = "Equipo creado con éxito.";
        }
        this.messageType = "success";
        this.showMessage = true;
        setTimeout(() => this.goBack(), 1500);
      } catch (error) {
        this.message = "No se pudo guardar el equipo: " + (error.response?.data?.message || "Inténtelo de nuevo.");
        this.messageType = "error";
        this.showMessage = true;
      } finally {
        this.isSaving = false;
      }
    },
    goBack() {
      const teamId = this.$route.params.id;
      if (this.isEditMode && teamId) {
          this.$router.push({ name: 'TeamDetails', params: { id: teamId }});
      } else {
          this.$router.push({ name: 'Teams' });
      }
    },
    closeMessage() {
      this.showMessage = false;
    },
  },
  watch: {
    categories() {
        if (this.isEditMode && this.team.id) {
             const categoryEnum = this.categories.find(c => c.description === this.team.category);
             if (categoryEnum) {
                 this.team.category = categoryEnum.code;
             }
        }
    }
  }
};
</script>

<style scoped>
.form-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
}
.form-actions {
  margin-top: 1.5rem;
}
@media (max-width: 768px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>