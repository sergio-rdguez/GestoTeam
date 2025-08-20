<template>
  <div class="team-details-page">
    <div v-if="loading">
      <LoadingSpinner message="Cargando equipo..." />
    </div>
    <div v-else-if="team">
      <PageHeader :title="team.name" show-back-button @back="goBack">
        <BaseButton @click="editTeam">
          <i class="fa-solid fa-pencil"></i> Editar Equipo
        </BaseButton>
      </PageHeader>

      <div class="details-grid">
        <BaseCard title="Información General" class="info-card">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">Categoría</span>
              <span class="info-value">{{ team.category }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">División</span>
              <span class="info-value">{{ team.division }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Campo</span>
              <span class="info-value">{{ team.field }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Ubicación</span>
              <span class="info-value">{{ team.location }}</span>
            </div>
          </div>
          <div v-if="team.description" class="description">
            <p>{{ team.description }}</p>
          </div>
        </BaseCard>

        <div class="navigation-cards">
          <router-link :to="{ name: 'TeamPlayers', params: { teamId : team.id } }" class="nav-card">
            <i class="fa-solid fa-users nav-icon"></i>
            <span class="nav-title">Jugadores</span>
            <p class="nav-description">Gestiona tu plantilla</p>
          </router-link>
          <router-link :to="{ name: 'TeamMatches', params: { teamId : team.id } }" class="nav-card">
            <i class="fa-solid fa-trophy nav-icon"></i>
            <span class="nav-title">Partidos</span>
            <p class="nav-description">Consulta el calendario y resultados</p>
          </router-link>
          <router-link :to="{ name: 'Opponents', params: { teamId : team.id } }" class="nav-card">
            <i class="fa-solid fa-users-gear nav-icon"></i>
            <span class="nav-title">Oponentes</span>
            <p class="nav-description">Gestiona los equipos rivales</p>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
  components: {
    PageHeader,
    BaseCard,
    BaseButton,
    LoadingSpinner,
  },
  data() {
    return {
      team: null,
      loading: true,
    };
  },
  methods: {
    async fetchTeamDetails() {
      this.loading = true;
      try {
        const teamId = this.$route.params.teamId;
                    const response = await api.get(`/teams/${teamId}`);
        this.team = response.data;
        
      } catch (error) {
        console.error("Error al cargar los detalles del equipo:", error);
      } finally {
        this.loading = false;
      }
    },
    goBack() {
      this.$router.push({ name: "Teams" });
    },
    editTeam() {
      if (this.team && this.team.id) {
        this.$router.push({ name: "EditTeam", params: { teamId: this.team.id } });
      } else {
        console.error("No se puede editar: falta información del equipo");
      }
    },
  },
  mounted() {
    this.fetchTeamDetails();
  },
};
</script>

<style scoped>
.details-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-6);
}
.info-card .card-content {
  padding-top: 0;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-5);
  padding-top: var(--spacing-4);
}
.info-item {
  display: flex;
  flex-direction: column;
}
.info-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-1);
}
.info-value {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}
.description {
  margin-top: var(--spacing-5);
  padding-top: var(--spacing-5);
  border-top: 1px solid var(--color-border);
  font-style: italic;
  color: var(--color-text-secondary);
}
.navigation-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-6);
}
.nav-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--spacing-8);
  border-radius: var(--border-radius-lg);
  background-color: var(--color-background-white);
  border: 1px solid var(--color-border);
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}
.nav-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary);
  color: var(--color-primary);
}
.nav-icon {
  font-size: 2.5rem;
  margin-bottom: var(--spacing-4);
  color: var(--color-primary);
}
.nav-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}
.nav-description {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-top: var(--spacing-1);
}
@media (max-width: 768px) {
  .details-grid, .navigation-cards {
    grid-template-columns: 1fr;
  }
}
</style>