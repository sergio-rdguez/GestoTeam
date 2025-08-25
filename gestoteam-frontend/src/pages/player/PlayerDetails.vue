<template>
  <div class="player-details-page">
    <PageHeader title="Ficha del Jugador" show-back-button @back="goBack" />
    
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>Cargando información del jugador...</p>
    </div>

    <div v-else class="player-details-container">
      <!-- Información principal del jugador -->
      <BaseCard class="player-main-card">
        <div class="player-header">
          <div class="player-photo-container">
            <img :src="getPlayerPhotoUrl()" alt="Foto del jugador" class="player-photo" />
            <div class="player-status-badge" :class="player.status.toLowerCase()">
              {{ player.status }}
            </div>
          </div>
          <div class="player-basic-info">
            <h1 class="player-name">{{ player.fullName }}</h1>
            <div class="player-details-grid">
              <div class="info-item">
                <span class="info-label">Edad</span>
                <span class="info-value">{{ player.age }} años</span>
              </div>
              <div class="info-item">
                <span class="info-label">Equipo</span>
                <span class="info-value">{{ player.team.name }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Categoría</span>
                <span class="info-value">{{ player.team.category }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Posición</span>
                <span class="info-value">{{ getPositionDescription(player.position) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Pie</span>
                <span class="info-value">{{ getFootDescription(player.foot) }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Botones de acción -->
        <div class="player-actions">
          <button class="btn btn-primary" @click="editPlayer">
            <i class="fas fa-edit"></i>
            Editar Jugador
          </button>
          <button class="btn btn-danger" @click="deletePlayer">
            <i class="fas fa-trash"></i>
            Eliminar
          </button>
        </div>
      </BaseCard>

      <!-- Estadísticas del jugador -->
      <div class="player-stats-grid">
        <BaseCard class="stats-card" title="Partidos">
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-icon">
                <i class="fas fa-calendar-check"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.matches.convoked }}</span>
                <span class="stat-label">Convocados</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <i class="fas fa-star"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.matches.starter }}</span>
                <span class="stat-label">Titular</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <i class="fas fa-exchange-alt"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.matches.substitute }}</span>
                <span class="stat-label">Suplente</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <i class="fas fa-play-circle"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.matches.played }}</span>
                <span class="stat-label">Jugados</span>
              </div>
            </div>
          </div>
        </BaseCard>

        <BaseCard class="stats-card" title="Goles">
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-icon goals-icon">
                <i class="fas fa-futbol"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.goals.total }}</span>
                <span class="stat-label">Total</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <i class="fas fa-chart-line"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.goals.average.toFixed(2) }}</span>
                <span class="stat-label">Promedio</span>
              </div>
            </div>
          </div>
        </BaseCard>

        <BaseCard class="stats-card" title="Tarjetas">
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-icon yellow-card-icon">
                <i class="fas fa-square"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.cards.yellow }}</span>
                <span class="stat-label">Amarillas</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon red-card-icon">
                <i class="fas fa-square"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.cards.red }}</span>
                <span class="stat-label">Rojas</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon double-yellow-icon">
                <i class="fas fa-square"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.cards.doubleYellow }}</span>
                <span class="stat-label">Doble Amarillas</span>
              </div>
            </div>
          </div>
        </BaseCard>

        <BaseCard class="stats-card" title="Entrenamientos">
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-icon training-icon">
                <i class="fas fa-dumbbell"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.training.total }}</span>
                <span class="stat-label">Total</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon present-icon">
                <i class="fas fa-check-circle"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.training.present }}</span>
                <span class="stat-label">Presente</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon absent-icon">
                <i class="fas fa-times-circle"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.training.absent }}</span>
                <span class="stat-label">Ausente</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon attendance-rate-icon">
                <i class="fas fa-percentage"></i>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ player.stats.training.attendanceRate.toFixed(1) }}%</span>
                <span class="stat-label">Asistencia</span>
              </div>
            </div>
            <div class="stat-item" v-if="player.stats.training.total > 0">
              <button class="btn btn-outline btn-sm" @click="showAbsentTrainings">
                <i class="fas fa-info-circle"></i>
                Ver detalles
              </button>
            </div>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- Modal para mostrar entrenamientos ausentes -->
    <div v-if="showAbsentModal" class="modal-overlay" @click="closeAbsentModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Entrenamientos Ausentes</h3>
          <button class="modal-close" @click="closeAbsentModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="loadingAbsent" class="loading-spinner-container">
            <div class="loading-spinner"></div>
            <p>Cargando entrenamientos ausentes...</p>
          </div>
          <div v-else-if="absentTrainings.length === 0" class="empty-state">
            <i class="fas fa-check-circle"></i>
            <p>¡Excelente! El jugador no ha faltado a ningún entrenamiento.</p>
          </div>
          <div v-else class="absent-trainings-list">
            <div v-for="training in absentTrainings" :key="training.id" class="training-item">
              <div class="training-header">
                <h4>{{ training.title }}</h4>
                <span class="training-date">{{ formatDate(training.date) }}</span>
              </div>
              <div class="training-details">
                <div class="detail-row">
                  <span class="detail-label">Sesión:</span>
                  <span class="detail-value">{{ training.sessionNumber }}</span>
                </div>
                <div class="detail-row" v-if="training.location">
                  <span class="detail-label">Ubicación:</span>
                  <span class="detail-value">{{ training.location }}</span>
                </div>
                <div class="detail-row" v-if="training.trainingType">
                  <span class="detail-label">Tipo:</span>
                  <span class="detail-value">{{ training.trainingType }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">Estado:</span>
                  <span class="detail-value status-badge" :class="getStatusClass(training.absenceStatus)">
                    {{ getStatusText(training.absenceStatus) }}
                  </span>
                </div>
                <div class="detail-row" v-if="training.absenceNotes">
                  <span class="detail-label">Notas:</span>
                  <span class="detail-value">{{ training.absenceNotes }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from "@/services/api";
import { buildImageUrl } from "@/utils/imageUtils";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import { notificationService } from "@/services/notificationService";

export default {
  components: {
    PageHeader,
    BaseCard,
  },
  data() {
    return {
      player: null,
      loading: true,
      defaultPhoto: require('@/assets/default-player-photo.png'),
      positions: [],
      foots: [],
      showAbsentModal: false,
      loadingAbsent: true,
      absentTrainings: [],
    };
  },
  methods: {
    async fetchPlayerDetails() {
      this.loading = true;
      try {
        const [playerResponse, positionsResponse, footsResponse] = await Promise.all([
          api.get(`/players/${this.$route.params.playerId}`),
          api.get('/enums/positions'),
          api.get('/enums/foots')
        ]);
        
        this.player = playerResponse.data;
        this.positions = positionsResponse.data;
        this.foots = footsResponse.data;
      } catch (error) {
        console.error("Error al cargar los detalles del jugador:", error);
      } finally {
        this.loading = false;
      }
    },
    getPositionDescription(positionCode) {
      const position = this.positions.find(p => p.code === positionCode);
      return position ? position.description : positionCode;
    },
    getFootDescription(footCode) {
      const foot = this.foots.find(f => f.code === footCode);
      return foot ? foot.description : footCode;
    },
    getPlayerPhotoUrl() {
      if (this.player && this.player.photoUrl) {
        return buildImageUrl(this.player.photoUrl);
      }
      return this.defaultPhoto;
    },
    goBack() {
      // Intentar obtener el teamId de múltiples fuentes
      let teamId = null;
      
      if (this.player && this.player.team && this.player.team.id) {
        teamId = this.player.team.id;
      } else if (this.player && this.player.teamId) {
        teamId = this.player.teamId;
      }
      
      if (teamId) {
        this.$router.push({ 
          name: "TeamPlayers", 
          params: { teamId: teamId } 
        });
      } else {
        console.error("No se puede volver: falta teamId");
        // Fallback: ir a la lista de equipos
        this.$router.push({ name: 'Teams' });
      }
    },
    editPlayer() {
      // Intentar obtener el teamId de múltiples fuentes
      let teamId = null;
      
      if (this.player && this.player.team && this.player.team.id) {
        teamId = this.player.team.id;
      } else if (this.player && this.player.teamId) {
        teamId = this.player.teamId;
      }
      
      if (teamId) {
        this.$router.push({ 
          name: "EditPlayer", 
          params: { 
            playerId: this.player.id, 
            teamId: teamId 
          } 
        });
      } else {
        console.error("No se puede editar: falta información del equipo");
        notificationService.showError("Error: No se puede editar el jugador. Falta información del equipo.");
      }
    },
    async deletePlayer() {
      if (confirm("¿Estás seguro de que deseas eliminar este jugador?")) {
        this.loading = true;
        try {
          await api.delete(`/players/${this.player.id}`);
          this.goBack();
        } catch (error) {
          console.error("Error al eliminar el jugador:", error);
        } finally {
          this.loading = false;
        }
      }
    },
         async showAbsentTrainings() {
       this.loadingAbsent = true;
       try {
         const response = await api.get(`/trainings/player/${this.player.id}/absences`);
         this.absentTrainings = response.data;
         this.showAbsentModal = true;
       } catch (error) {
         console.error("Error al cargar entrenamientos ausentes:", error);
         notificationService.showError("Error al cargar entrenamientos ausentes.");
       } finally {
         this.loadingAbsent = false;
       }
     },
    closeAbsentModal() {
      this.showAbsentModal = false;
      this.absentTrainings = [];
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    },
         getStatusClass(status) {
       switch (status) {
         case 'JUSTIFIED_ABSENCE':
           return 'justified';
         case 'UNJUSTIFIED_ABSENCE':
           return 'unjustified';
         case 'INJURED':
           return 'injured';
         case 'ABSENT':
           return 'absent';
         case 'LATE':
           return 'late';
         default:
           return 'default';
       }
     },
     getStatusText(status) {
       switch (status) {
         case 'JUSTIFIED_ABSENCE':
           return 'Falta Justificada';
         case 'UNJUSTIFIED_ABSENCE':
           return 'Falta No Justificada';
         case 'INJURED':
           return 'Lesionado';
         case 'ABSENT':
           return 'Ausente';
         case 'LATE':
           return 'Retraso';
         default:
           return status;
       }
     }
  },
  mounted() {
    this.fetchPlayerDetails();
  },
};
</script>

<style scoped>
.player-details-page {
  padding: 2rem;
  background-color: #f8fafc;
  min-height: 100vh;
}

.loading-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  gap: 1rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.player-details-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.player-main-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.player-header {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.player-photo-container {
  position: relative;
  flex-shrink: 0;
}

.player-photo {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 4px solid #3b82f6;
  object-fit: cover;
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.3);
}

.player-status-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #3b82f6;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.player-status-badge.activo { background: #10b981; }
.player-status-badge.lesionado { background: #ef4444; }
.player-status-badge.suspendido { background: #f59e0b; }

.player-basic-info {
  flex: 1;
}

.player-name {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 1.5rem 0;
  line-height: 1.2;
}

.player-details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: 1.125rem;
  font-weight: 500;
  color: #1e293b;
}

.player-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.btn-danger {
  background: #ef4444;
  color: white;
}

 .btn-danger:hover {
   background: #dc2626;
   transform: translateY(-1px);
   box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
 }

 .btn-outline {
   background: transparent;
   border: 2px solid #3b82f6;
   color: #3b82f6;
 }

 .btn-outline:hover {
   background: #3b82f6;
   color: white;
   transform: translateY(-1px);
   box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
 }

 .btn-sm {
   padding: 0.5rem 1rem;
   font-size: 0.75rem;
 }

.player-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.stats-card {
  background: white;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.stat-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #e0f2fe;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0284c7;
  font-size: 1.25rem;
}

.goals-icon {
  background: #fef3c7;
  color: #d97706;
}

.yellow-card-icon {
  background: #fef3c7;
  color: #d97706;
}

.red-card-icon {
  background: #fee2e2;
  color: #dc2626;
}

.double-yellow-icon {
  background: #fed7aa;
  color: #ea580c;
}

.training-icon {
  background: #e0f2fe;
  color: #0284c7;
}

.present-icon {
  background: #d1fae5;
  color: #065f46;
}

.absent-icon {
  background: #fee2e2;
  color: #dc2626;
}

.attendance-rate-icon {
  background: #fef3c7;
  color: #d97706;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.stat-label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e2e8f0;
  background-color: #f8fafc;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.8rem;
  color: #1e293b;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #64748b;
  cursor: pointer;
  transition: color 0.2s ease;
}

.modal-close:hover {
  color: #3b82f6;
}

.modal-body {
  padding: 2rem;
  overflow-y: auto;
  flex-grow: 1;
}

.loading-spinner-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  gap: 1rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #64748b;
  font-size: 1.25rem;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.absent-trainings-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.training-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 1.5rem;
  transition: all 0.2s ease;
}

.training-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.training-header h4 {
  margin: 0;
  font-size: 1.25rem;
  color: #1e293b;
}

.training-date {
  font-size: 0.875rem;
  color: #64748b;
}

.training-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 1.125rem;
  font-weight: 500;
  color: #1e293b;
}

.status-badge {
  padding: 0.4rem 0.8rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.justified {
  background-color: #d1fae5;
  color: #065f46;
}

.status-badge.unjustified {
  background-color: #fee2e2;
  color: #dc2626;
}

 .status-badge.excused {
   background-color: #fef3c7;
   color: #d97706;
 }

 .status-badge.injured {
   background-color: #fef3c7;
   color: #d97706;
 }

 .status-badge.absent {
   background-color: #fee2e2;
   color: #dc2626;
 }

 .status-badge.late {
   background-color: #fef3c7;
   color: #d97706;
 }

 .status-badge.default {
   background-color: #e2e8f0;
   color: #475569;
 }

@media (max-width: 768px) {
  .player-details-page {
    padding: 1rem;
  }
  
  .player-header {
    flex-direction: column;
    text-align: center;
    gap: 1.5rem;
  }
  
  .player-name {
    font-size: 2rem;
  }
  
  .player-details-grid {
    grid-template-columns: 1fr;
  }
  
  .player-actions {
    flex-direction: column;
  }
  
  .player-stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>