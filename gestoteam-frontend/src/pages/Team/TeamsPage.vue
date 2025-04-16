<template>
  <div class="teams-page">
    <div v-if="teams.length === 0" class="no-teams-message">
      <p>Es hora de crear tu primer equipo.</p>
      <button class="btn btn-primary" @click="addTeam">Crear Equipo</button>
    </div>

    <div v-else>
      <h2 class="page-title">Mis Equipos</h2>
      <ul class="team-list">
        <li v-for="team in teams" :key="team.id" class="team-item clickable" @click="viewTeam(team.id)">
          <div class="team-info">
            <h3>{{ team.name }}</h3>
            <p>{{ team.category }} - {{ team.division }}</p>
          </div>
        </li>
      </ul>

      <!-- Botón flotante -->
      <div class="fab-container">
        <button class="fab" @click="toggleFabMenu">
          <i class="fa-solid fa-bars"></i>
        </button>
        <!-- Menú desplegable -->
        <div v-if="showFabMenu" class="fab-menu">
          <button class="fab-menu-item add-button" @click="addTeam">Añadir</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
  data() {
    return {
      teams: [],
      showFabMenu: false,
    };
  },
  methods: {
    async fetchTeams() {
      try {
        const response = await apiClient.get("/teams");
        this.teams = response.data;
      } catch (error) {
        console.error("Error al obtener equipos:", error);
      }
    },
    viewTeam(teamId) {
      this.$router.push({ name: "TeamDetails", params: { id: teamId } });
    },
    addTeam() {
      this.$router.push({ name: "AddTeam" });
      this.showFabMenu = false;
    },
    toggleFabMenu() {
      this.showFabMenu = !this.showFabMenu;
    },
  },
  mounted() {
    this.fetchTeams();
  },
};
</script>

<style scoped>
.teams-page {
  padding: 20px;
  font-family: "Arial", sans-serif;
  background: #f9f9f9;
  min-height: 100vh;
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

.no-teams-message {
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.no-teams-message p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 15px;
}

.team-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.team-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.team-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.team-info h3 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.team-info p {
  font-size: 1.2rem;
  color: #666;
  margin: 5px 0 0;
}

.fab-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column-reverse;
  align-items: flex-end;
}

.fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #007bff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.fab i {
  font-size: 1.5em;
  color: white;
}

.fab-menu {
  position: absolute;
  bottom: 80px;
  right: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  padding: 10px;
}

.fab-menu-item {
  display: block;
  padding: 10px 15px;
  background: none;
  border: none;
  text-align: left;
  width: 100%;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.fab-menu-item:hover {
  background: #f1f1f1;
}

.fab-menu-item.add-button {
  color: #fff;
  background-color: #28a745;
  border-radius: 8px;
  font-weight: bold;
  transition: background 0.2s, transform 0.2s;
}

.fab-menu-item.add-button:hover {
  background-color: #218838;
  transform: scale(1.05);
}

.btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  text-align: center;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  display: block;
  margin: 20px auto;
  width: 100%;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.6rem;
  }

  .team-info h3 {
    font-size: 1.3rem;
  }

  .team-info p {
    font-size: 1rem;
  }

  .btn {
    font-size: 0.9rem;
    padding: 10px 15px;
  }
}

@media (min-width: 1200px) {
  .teams-page {
    padding: 40px;
  }

  .page-title {
    font-size: 2.5rem;
  }

  .team-item {
    padding: 20px;
  }

  .team-info h3 {
    font-size: 1.8rem;
  }

  .team-info p {
    font-size: 1.4rem;
  }
}
</style>
