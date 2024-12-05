<template>
  <div class="team-matches-page">
    <h2 class="page-title">Partidos de {{ team.name }}</h2>

    <!-- Filtros de partidos -->
    <div class="filter-options">
      <label>
        <input type="radio" value="upcoming" v-model="filter" /> Próximos
      </label>
      <label>
        <input type="radio" value="past" v-model="filter" /> Disputados
      </label>
    </div>

    <!-- Lista de partidos -->
    <div class="matches-list">
      <div v-for="match in filteredMatches" :key="match.id" class="match-item" @click="goToMatchDetails(match.id)">
        <div class="match-content">
          <h4>{{ match.opponent }}</h4>
          <p><strong>Fecha:</strong> {{ formatDate(match.date) }}</p>
          <p><strong>Hora:</strong> {{ match.time }}</p>
          <p><strong>Estadio:</strong> {{ match.venue }}</p>
          <div class="match-status" v-if="!match.isPast">Pendiente</div>
          <div class="match-status victory" v-if="match.isPast && match.result === 'Victoria'">{{ match.result }}</div>
          <div class="match-status defeat" v-if="match.isPast && match.result === 'Derrota'">{{ match.result }}</div>
        </div>
      </div>
    </div>

    <!-- FAB flotante con menú -->
    <div class="fab-container">
      <button class="fab" @click="toggleFabMenu">
        <i class="fa-solid fa-bars"></i>
      </button>
      <div v-if="showFabMenu" class="fab-menu">
        <button class="fab-action" @click="goBack">Volver</button>
        <button class="fab-action" @click="toggleMatchForm">Agregar Partido</button>
      </div>
    </div>

    <!-- Formulario para agregar partido -->
    <div v-if="showMatchForm" class="match-form">
      <h3>Nuevo Partido</h3>
      <form @submit.prevent="addMatch">
        <div class="form-group">
          <label>Oponente</label>
          <input v-model="newMatch.opponent" type="text" required />
        </div>
        <div class="form-group">
          <label>Fecha</label>
          <input v-model="newMatch.date" type="date" required />
        </div>
        <div class="form-group">
          <label>Hora</label>
          <input v-model="newMatch.time" type="time" required />
        </div>
        <div class="form-group">
          <label>Estadio</label>
          <input v-model="newMatch.venue" type="text" required />
        </div>
        <button type="submit" class="btn primary">Guardar</button>
        <button type="button" class="btn secondary" @click="toggleMatchForm">Cancelar</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      team: { name: "Equipo Dummy" },
      matches: [
        { id: 1, opponent: "Equipo A", date: "2024-12-01", time: "18:00", venue: "Estadio Local", isPast: false },
        { id: 2, opponent: "Equipo B", date: "2024-11-15", time: "20:00", venue: "Estadio Visitante", isPast: true, result: "Victoria" },
        { id: 3, opponent: "Equipo C", date: "2024-11-10", time: "19:00", venue: "Estadio Neutral", isPast: true, result: "Derrota" },
      ],
      filter: "upcoming",
      showMatchForm: false,
      showFabMenu: false,
      newMatch: {
        opponent: "",
        date: "",
        time: "",
        venue: "",
      },
    };
  },
  computed: {
    filteredMatches() {
      return this.matches.filter((match) =>
        this.filter === "upcoming" ? !match.isPast : match.isPast
      );
    },
  },
  methods: {
    toggleMatchForm() {
      this.showMatchForm = !this.showMatchForm;
      this.showFabMenu = false; // Cierra el menú FAB si está abierto
    },
    toggleFabMenu() {
      this.showFabMenu = !this.showFabMenu;
    },
    goBack() {
      this.$router.go(-1); // Navega a la pantalla anterior
    },
    addMatch() {
      const id = this.matches.length + 1;
      this.matches.push({
        id,
        ...this.newMatch,
        isPast: new Date(this.newMatch.date) < new Date(),
      });
      this.newMatch = { opponent: "", date: "", time: "", venue: "" };
      this.showMatchForm = false;
    },
    goToMatchDetails(id) {
      this.$router.push({ name: "MatchDetails", params: { id } });
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString("es-ES", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
  },
};
</script>

<style scoped>
/* General */
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background-color: #f9f9f9;
}

.team-matches-page {
  padding: 20px;
  background-color: #f9f9f9;
  min-height: 100vh;
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

/* Filtros */
.filter-options {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.filter-options label {
  margin: 0 10px;
  font-size: 1rem;
  color: #555;
}

/* Lista de partidos */
.matches-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.match-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.match-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.match-content {
  flex: 2;
  padding-right: 15px;
}

.match-content h4 {
  margin: 0 0 5px;
  font-size: 1.3rem;
  color: #333;
}

.match-content p {
  margin: 5px 0;
  font-size: 1rem;
  color: #555;
}

.match-status {
  flex: 0 0 120px;
  text-align: center;
  padding: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #f9f9f9;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.match-status.pending {
  border-color: #ffc107;
  color: #856404;
}

.match-status.victory {
  border-color: #28a745;
  color: #155724;
}

.match-status.defeat {
  border-color: #dc3545;
  color: #721c24;
}

.match-arrow {
  font-size: 1.5rem;
  color: #007bff;
}

/* FAB */
.fab-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 10;
}

.fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #007bff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
  transition: background-color 0.2s ease;
}

.fab i {
  font-size: 1.5em;
  color: white;
}

.fab:hover {
  background-color: #0056b3;
}

/* FAB Menu */
.fab-menu {
  position: absolute;
  bottom: 80px;
  right: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.fab-action {
  background: white;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.fab-action:hover {
  background: #f1f1f1;
}

/* Formulario */
.match-form {
  background: white;
  padding: 20px;
  margin-top: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

/* Botones */
.btn {
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  font-size: 1rem;
}

.btn.primary {
  background-color: #007bff;
  color: white;
}

.btn.secondary {
  background-color: #6c757d;
  color: white;
}

/* Responsividad */
@media (max-width: 768px) {
  .match-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .match-content {
    padding-right: 0;
  }

  .match-status {
    margin-top: 10px;
    width: 100%;
    text-align: left;
  }

  .fab {
    width: 50px;
    height: 50px;
  }

  .fab i {
    font-size: 1.2em;
  }
}
</style>
