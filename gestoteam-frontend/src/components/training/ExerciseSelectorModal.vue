<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>Seleccionar Ejercicios</h3>
        <button class="close-button" @click="closeModal">
          <i class="fa-solid fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <!-- Filtros y botón de crear -->
        <div class="filters-section">
          <div class="filters">
            <BaseInput
              v-model="searchTerm"
              placeholder="Buscar ejercicios..."
              icon="fa-search"
              class="search-input"
            />
            <BaseSelect
              v-model="selectedCategory"
              :options="categoryOptions"
              placeholder="Todas las categorías"
              class="category-filter"
            />
          </div>
          <BaseButton @click="createNewExercise" variant="primary" class="create-exercise-btn">
            <i class="fa-solid fa-plus"></i> Nuevo Ejercicio
          </BaseButton>
        </div>

        <!-- Lista de ejercicios -->
        <div class="exercises-list">
          <div v-if="loading" class="loading-container">
            <LoadingSpinner message="Cargando ejercicios..." />
          </div>
          
          <div v-else-if="filteredExercises.length === 0" class="empty-state">
            <i class="fa-solid fa-search empty-icon"></i>
            <p>No se encontraron ejercicios</p>
            <BaseButton @click="createNewExercise" variant="primary" class="create-first-exercise-btn">
              <i class="fa-solid fa-plus"></i> Crear primer ejercicio
            </BaseButton>
          </div>
          
          <div v-else class="exercises-grid">
            <div
              v-for="exercise in filteredExercises"
              :key="exercise.id"
              class="exercise-card"
              :class="{ selected: isSelected(exercise.id) }"
              @click="toggleExercise(exercise.id)"
            >
              <div class="exercise-header">
                <h4>{{ exercise.title }}</h4>
                <span class="exercise-category">{{ getCategoryLabel(exercise.category) }}</span>
              </div>
              
              <p v-if="exercise.description" class="exercise-description">
                {{ exercise.description }}
              </p>
              
              <div class="exercise-objectives">
                <div v-if="exercise.tacticalObjectives" class="objective">
                  <i class="fa-solid fa-chess-board"></i>
                  <span>{{ exercise.tacticalObjectives }}</span>
                </div>
                <div v-if="exercise.technicalObjectives" class="objective">
                  <i class="fa-solid fa-cog"></i>
                  <span>{{ exercise.technicalObjectives }}</span>
                </div>
                <div v-if="exercise.physicalObjectives" class="objective">
                  <i class="fa-solid fa-running"></i>
                  <span>{{ exercise.physicalObjectives }}</span>
                </div>
              </div>
              
              <div class="selection-indicator">
                <i v-if="isSelected(exercise.id)" class="fa-solid fa-check"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <div class="selection-summary">
          <span>{{ selectedExercises.length }} ejercicios seleccionados</span>
        </div>
        <div class="modal-actions">
          <BaseButton @click="closeModal" variant="secondary">
            Cancelar
          </BaseButton>
          <BaseButton @click="confirmSelection" variant="primary" :disabled="selectedExercises.length === 0">
            Añadir Ejercicios
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { exerciseService } from "@/services/exerciseService";
import { notificationService } from "@/services/notificationService";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
  components: {
    BaseInput,
    BaseSelect,
    BaseButton,
    LoadingSpinner,
  },
  props: {
    teamId: {
      type: [String, Number],
      required: true,
    },
    selectedExercises: {
      type: Array,
      default: () => [],
    },
    initialState: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      exercises: [],
      loading: true,
      searchTerm: '',
      selectedCategory: '',
      selectedExerciseIds: new Set(this.selectedExercises.map(e => e.id)),
    };
  },
  computed: {
    categoryOptions() {
      // Obtener categorías únicas de los ejercicios
      const uniqueCategories = [...new Set(this.exercises
        .map(e => e.category)
        .filter(Boolean))];
      
      console.log('Categorías únicas encontradas:', uniqueCategories);
      
      const options = [
        { value: '', label: 'Todas las categorías' }
      ];
      
      // Agregar opciones para cada categoría
      uniqueCategories.forEach(category => {
        options.push({
          value: category,
          label: this.getCategoryLabel(category)
        });
      });
      
      console.log('Opciones de categoría generadas:', options);
      return options;
    },
    
    filteredExercises() {
      let filtered = this.exercises;
      
      if (this.searchTerm) {
        const term = this.searchTerm.toLowerCase();
        filtered = filtered.filter(exercise =>
          exercise.title.toLowerCase().includes(term) ||
          exercise.description?.toLowerCase().includes(term)
        );
      }
      
      if (this.selectedCategory) {
        filtered = filtered.filter(exercise => exercise.category === this.selectedCategory);
      }
      
      return filtered;
    },
  },
  methods: {
    async fetchExercises() {
      this.loading = true;
      try {
        const response = await exerciseService.getExercises();
        console.log('Ejercicios recibidos:', response);
        this.exercises = response;
        
        // Log para debuggear las categorías
        const categories = this.exercises
          .map(e => e.category)
          .filter(Boolean);
        console.log('Categorías encontradas:', categories);
        console.log('Opciones de categoría generadas:', this.categoryOptions);
      } catch (error) {
        console.error("Error al cargar ejercicios:", error);
        notificationService.showError("Error al cargar ejercicios");
      } finally {
        this.loading = false;
      }
    },
    
    isSelected(exerciseId) {
      return this.selectedExerciseIds.has(exerciseId);
    },
    
    toggleExercise(exerciseId) {
      if (this.isSelected(exerciseId)) {
        this.selectedExerciseIds.delete(exerciseId);
      } else {
        this.selectedExerciseIds.add(exerciseId);
      }
    },
    
    confirmSelection() {
      const exerciseIds = Array.from(this.selectedExerciseIds);
      this.$emit('select', exerciseIds);
    },
    
    closeModal() {
      this.$emit('close');
    },

    getCategoryLabel(category) {
      const labels = {
        'CALENTAMIENTO': 'Calentamiento',
        'TECNICO': 'Técnico',
        'TACTICO': 'Táctico',
        'FISICO': 'Físico',
        'PARTIDO_MODIFICADO': 'Partido Modificado',
        'TRANSICION': 'Transición',
        'FINALIZACION': 'Finalización',
        'POSESION': 'Posesión',
        'PRESSING': 'Pressing',
        'OTRO': 'Otro'
      };
      return labels[category] || category;
    },

    createNewExercise() {
      // Guardar el estado actual del modal
      const currentState = {
        searchTerm: this.searchTerm,
        selectedCategory: this.selectedCategory,
        selectedExerciseIds: Array.from(this.selectedExerciseIds)
      };
      
      // Emitir evento para crear nuevo ejercicio con el estado actual
      this.$emit('create-new-exercise', currentState);
    },

    restoreInitialState() {
      if (this.initialState.searchTerm) {
        this.searchTerm = this.initialState.searchTerm;
      }
      if (this.initialState.selectedCategory) {
        this.selectedCategory = this.initialState.selectedCategory;
      }
      if (this.initialState.selectedExerciseIds) {
        this.selectedExerciseIds = new Set(this.initialState.selectedExerciseIds);
      }
    },
  },
  mounted() {
    this.fetchExercises();
    
    // Restaurar estado inicial si existe
    if (this.initialState) {
      this.restoreInitialState();
    }
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: var(--border-radius-lg);
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-xl);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-6);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

.close-button {
  background: none;
  border: none;
  font-size: var(--font-size-xl);
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-2);
  border-radius: var(--border-radius-sm);
  transition: all 0.2s ease;
}

.close-button:hover {
  background-color: var(--color-background-soft);
  color: var(--color-text-primary);
}

.modal-body {
  flex: 1;
  padding: var(--spacing-6);
  overflow-y: auto;
}

.filters-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-6);
}

.filters {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: var(--spacing-4);
}

.search-input {
  min-width: 200px;
}

.category-filter {
  min-width: 150px;
}

.create-exercise-btn {
  min-width: 180px;
  justify-self: flex-end;
}

.create-first-exercise-btn {
  margin-top: var(--spacing-4);
  width: 100%;
}

.exercises-list {
  min-height: 400px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.empty-state {
  text-align: center;
  padding: var(--spacing-8);
  color: var(--color-text-secondary);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-4);
}

.exercises-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-4);
}

.exercise-card {
  padding: var(--spacing-4);
  border: 2px solid var(--color-border);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  background-color: var(--color-background-white);
}

.exercise-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
}

.exercise-card.selected {
  border-color: var(--color-primary);
  background-color: var(--color-primary-light);
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-3);
}

.exercise-header h4 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: var(--font-size-lg);
}

.exercise-category {
  background-color: var(--color-secondary);
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.exercise-description {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.4;
  margin-bottom: var(--spacing-3);
}

.exercise-objectives {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.objective {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.objective i {
  color: var(--color-primary);
  width: 16px;
}

.selection-indicator {
  position: absolute;
  top: var(--spacing-3);
  right: var(--spacing-3);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
}

.modal-footer {
  padding: var(--spacing-6);
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selection-summary {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.modal-actions {
  display: flex;
  gap: var(--spacing-3);
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: var(--spacing-4);
  }
  
  .filters {
    grid-template-columns: 1fr;
  }
  
  .filters-section {
    flex-direction: column;
    gap: var(--spacing-4);
  }

  .create-exercise-btn {
    width: 100%;
    justify-self: stretch;
  }

  .exercises-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-footer {
    flex-direction: column;
    gap: var(--spacing-4);
    align-items: stretch;
  }
  
  .modal-actions {
    justify-content: stretch;
  }
}
</style>
