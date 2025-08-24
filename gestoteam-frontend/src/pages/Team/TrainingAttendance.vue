<template>
  <div class="training-attendance-page">
         <PageHeader :title="`Control de Asistencia - ${training?.title || 'Entrenamiento'}`" show-back-button @back="goBack" />

         <!-- Layout de dos columnas -->
     <div class="training-layout">
               <!-- Columna izquierda: Solo Lista -->
        <div class="left-column">
          <div v-if="loading">
            <LoadingSpinner message="Cargando asistencia..." />
          </div>

          <EmptyState
            v-else-if="!attendance || attendance.length === 0"
            title="No hay jugadores registrados"
            message="No se encontraron jugadores para este entrenamiento."
            icon="fa-users"
          />

          <!-- Tabla de Asistencia -->
          <DataTable
            v-else
            :items="attendance"
            :columns="columns"
            table-name="training-attendance"
            default-sort-key="position"
            :default-sort-asc="true"
            :default-page-size="25"
            class="attendance-table"
          >
            <template #cell-playerFullName="{ item }">
              <span class="player-name">{{ item.playerFullName }}</span>
            </template>
            
            <template #cell-position="{ item }">
              <span class="player-position">{{ item.position || 'Sin asignar' }}</span>
            </template>
            
            <template #cell-status="{ item }">
              <BaseSelect
                v-model="item.status"
                :options="attendanceStatusOptions"
                @update:modelValue="(value) => updateAttendanceStatus(item, value)"
                size="sm"
                class="status-select"
              />
            </template>
            
            <template #cell-notes="{ item }">
              <BaseInput
                v-model="item.notes"
                placeholder="Notas..."
                @update:modelValue="(value) => updateAttendanceNotes(item, value)"
                size="sm"
                class="notes-input"
              />
            </template>
          </DataTable>
        </div>

               <!-- Columna derecha: Información y Estadísticas -->
        <div class="right-column">
          <!-- Información del Entrenamiento -->
          <div v-if="training" class="training-info-section">
            <BaseCard title="Información del Entrenamiento" class="training-info-card">
              <div class="training-info-grid">
                <div class="info-item">
                  <span class="info-label">Sesión</span>
                  <span class="info-value">Sesión {{ training.sessionNumber }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Fecha y Hora</span>
                  <span class="info-value">{{ formatDate(training.date) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Ubicación</span>
                  <span class="info-value">{{ training.location }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Tipo</span>
                  <span class="info-value">{{ training.trainingType }}</span>
                </div>
              </div>
            </BaseCard>
          </div>

          <!-- Estadísticas de Asistencia -->
          <div v-if="attendance && attendance.length > 0" class="stats-section">
            <BaseCard title="Resumen de Asistencia" class="stats-card">
              <div class="stats-grid">
                <div class="stat-item">
                  <span class="stat-number">{{ attendanceStats.present }}</span>
                  <span class="stat-label">Presentes</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ attendanceStats.absent }}</span>
                  <span class="stat-label">Ausentes</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ attendanceStats.late }}</span>
                  <span class="stat-label">Retrasos</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ attendanceStats.injured }}</span>
                  <span class="stat-label">Lesionados</span>
                </div>
              </div>
            </BaseCard>
          </div>
        </div>
     </div>
  </div>
</template>

<script>
import { trainingService } from "@/services/trainingService";
import { notificationService } from "@/services/notificationService";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";

import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import EmptyState from "@/components/common/EmptyState.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";
import DataTable from "@/components/common/DataTable.vue";

export default {
  name: "TrainingAttendance",
     components: {
     PageHeader,
     BaseCard,
     BaseSelect,
     BaseInput,
     EmptyState,
     LoadingSpinner,
     DataTable,
   },
  data() {
    return {
      teamId: this.$route.params.teamId,
      trainingId: this.$route.params.trainingId,
      training: null,
      attendance: [],
      loading: true,
      originalAttendance: [], // Para restaurar cambios
      attendanceStatusOptions: [
        { value: 'PRESENT', label: 'Presente' },
        { value: 'ABSENT', label: 'Ausente' },
        { value: 'LATE', label: 'Retraso' },
        { value: 'INJURED', label: 'Lesionado' },
        { value: 'JUSTIFIED_ABSENCE', label: 'Falta Justificada' },
        { value: 'UNJUSTIFIED_ABSENCE', label: 'Falta No Justificada' },
      ],
      columns: [
        { key: 'playerFullName', label: 'Jugador', sortable: true },
        { key: 'position', label: 'Posición', sortable: true, sortOn: 'positionOrder' },
        { key: 'status', label: 'Estado', sortable: true },
        { key: 'notes', label: 'Notas', sortable: false },
      ],
    };
  },
  computed: {
    attendanceStats() {
      const stats = {
        present: 0,
        absent: 0,
        late: 0,
        injured: 0,
      };
      
      this.attendance.forEach(item => {
        switch (item.status) {
          case 'PRESENT':
            stats.present++;
            break;
          case 'ABSENT':
          case 'JUSTIFIED_ABSENCE':
          case 'UNJUSTIFIED_ABSENCE':
            stats.absent++;
            break;
          case 'LATE':
            stats.late++;
            break;
          case 'INJURED':
            stats.injured++;
            break;
        }
      });
      
      return stats;
    },
    
               
  },
  methods: {
    async fetchTrainingDetails() {
      this.loading = true;
      try {
        const [trainingResponse, attendanceResponse] = await Promise.all([
          trainingService.getTrainingById(this.trainingId),
          trainingService.getTrainingAttendance(this.trainingId)
        ]);
        
        this.training = trainingResponse;
        this.attendance = this.processAttendanceData(attendanceResponse);
        this.originalAttendance = JSON.parse(JSON.stringify(this.attendance)); // Deep copy
      } catch (error) {
        console.error("Error al cargar los detalles del entrenamiento:", error);
        notificationService.showError("Error al cargar el entrenamiento");
      } finally {
        this.loading = false;
      }
    },

               processAttendanceData(attendanceData) {
             if (!attendanceData || attendanceData.length === 0) {
               return [];
             }
             
             return attendanceData.map(item => {
               // Asegurar que el status sea válido
               if (!item.status || item.status === '' || item.status === null || item.status === undefined) {
                 item.status = 'PRESENT';
               }
               
               // Asegurar que las notas sean válidas
               if (!item.notes) {
                 item.notes = '';
               }
               
               return item;
             });
           },
    
                   async updateAttendanceStatus(attendanceItem, newStatus) {
            const statusToSend = typeof newStatus === 'string' ? newStatus : String(newStatus);
            attendanceItem.status = statusToSend;
            
            try {
              await trainingService.updatePlayerAttendance(
                this.trainingId,
                attendanceItem.playerId,
                {
                  playerId: attendanceItem.playerId,
                  status: statusToSend,
                  notes: attendanceItem.notes
                }
              );
              
              // Actualizar el original después de guardar exitosamente
              const original = this.originalAttendance.find(orig => orig.playerId === attendanceItem.playerId);
              if (original) {
                original.status = statusToSend;
              }
              
              notificationService.showSuccess(`Estado de ${attendanceItem.playerFullName} actualizado`);
            } catch (error) {
              console.error(`Error al actualizar estado de ${attendanceItem.playerFullName}:`, error);
              notificationService.showError(`Error al actualizar estado de ${attendanceItem.playerFullName}`);
              // Revertir el cambio en caso de error
              const original = this.originalAttendance.find(orig => orig.playerId === attendanceItem.playerId);
              attendanceItem.status = original?.status || 'PRESENT';
            }
          },
    
                   async updateAttendanceNotes(attendanceItem, newNotes) {
            const notesToSend = typeof newNotes === 'string' ? newNotes : String(newNotes);
            attendanceItem.notes = notesToSend;
            
            try {
              await trainingService.updatePlayerAttendance(
                this.trainingId,
                attendanceItem.playerId,
                {
                  playerId: attendanceItem.playerId,
                  status: attendanceItem.status,
                  notes: notesToSend
                }
              );
              
              // Actualizar el original después de guardar exitosamente
              const original = this.originalAttendance.find(orig => orig.playerId === attendanceItem.playerId);
              if (original) {
                original.notes = notesToSend;
              }
              
              notificationService.showSuccess(`Notas de ${attendanceItem.playerFullName} actualizadas`);
            } catch (error) {
              console.error(`Error al actualizar notas de ${attendanceItem.playerFullName}:`, error);
              notificationService.showError(`Error al actualizar notas de ${attendanceItem.playerFullName}`);
              // Revertir el cambio en caso de error
              const original = this.originalAttendance.find(orig => orig.playerId === attendanceItem.playerId);
              attendanceItem.notes = original?.notes || '';
            }
          },
    
    
    
    
    
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleDateString('es-ES', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    goBack() {
      this.$router.push({ 
        name: 'TrainingDetails', 
        params: { teamId: this.teamId, trainingId: this.trainingId } 
      });
    }
  },
  mounted() {
    this.fetchTrainingDetails();
  },
};
</script>

<style scoped>
 .training-attendance-page {
   padding: 20px;
 }

 .training-layout {
   display: grid;
   grid-template-columns: 1fr 300px;
   gap: var(--spacing-6);
   align-items: start;
 }

 .left-column {
   display: flex;
   flex-direction: column;
   gap: var(--spacing-6);
 }

 .right-column {
   position: sticky;
   top: 20px;
   display: flex;
   flex-direction: column;
   gap: var(--spacing-6);
 }

 .training-info-section {
   margin-bottom: 0;
 }

.training-info-card .card-content {
  padding-top: 0;
}

.training-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-4);
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

.stats-section {
  margin-bottom: var(--spacing-6);
}

.stats-card .card-content {
  padding-top: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--spacing-4);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-3);
  background-color: var(--color-background-soft);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--color-border);
}

.stat-number {
  display: block;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

 .attendance-table {
   margin-top: 0;
 }

.player-name {
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.player-position {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  text-transform: uppercase;
  font-size: var(--font-size-sm);
}

.status-select {
  min-width: 140px;
}

.notes-input {
  min-width: 200px;
}



 /* Responsive */
 @media (max-width: 768px) {
   .training-attendance-page {
     padding: 16px;
   }
   
   .training-layout {
     grid-template-columns: 1fr;
     gap: var(--spacing-4);
   }
   
   .right-column {
     position: static;
     order: -1;
   }
   
   .training-info-grid {
     grid-template-columns: 1fr;
   }
   
   .stats-grid {
     grid-template-columns: repeat(2, 1fr);
   }
 }
</style>
