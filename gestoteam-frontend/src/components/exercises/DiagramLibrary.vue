<template>
  <div v-if="isOpen" class="diagram-library-modal">
    <div class="modal-content">
      <button @click="closeModal" class="close-btn">
        <i class="fas fa-times"></i>
      </button>
      
      <div class="library-header">
        <h2>Biblioteca de Diagramas Tácticos</h2>
        <div class="search-filters">
          <input 
            v-model="searchTerm" 
            placeholder="Buscar diagramas..." 
            class="search-input"
          />
        </div>
      </div>
      
      <div class="diagrams-grid">
        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i> Cargando diagramas...
        </div>
        
        <div v-else-if="filteredDiagrams.length === 0" class="empty-state">
          <i class="fas fa-folder-open"></i>
          <p>No hay diagramas disponibles</p>
          <button @click="closeModal" class="btn-primary">Crear Nuevo Diagrama</button>
        </div>
        
        <div 
          v-else
          v-for="diagram in filteredDiagrams" 
          :key="diagram.id" 
          class="diagram-card"
          @click="selectDiagram(diagram)"
        >
          <div class="diagram-image">
            <img :src="diagram.imageUrl" :alt="diagram.title" />
          </div>
          <div class="diagram-info">
            <h4>{{ diagram.title }}</h4>
            <p v-if="diagram.description">{{ diagram.description }}</p>
            <div class="diagram-meta">
              <span class="date">{{ formatDate(diagram.createdAt) }}</span>
            </div>
          </div>
          <div class="diagram-actions">
            <button @click.stop="selectDiagram(diagram)" class="btn-select">
              <i class="fas fa-plus"></i> Seleccionar
            </button>
          </div>
        </div>
      </div>
      
      <div class="modal-actions">
        <button @click="closeModal" class="btn-cancel">Cancelar</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { tacticalDiagramService } from '@/services/tacticalDiagramService';

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['close', 'diagram-selected']);

const loading = ref(false);
const diagrams = ref([]);
const searchTerm = ref('');

const filteredDiagrams = computed(() => {
  if (!searchTerm.value.trim()) {
    return diagrams.value;
  }
  
  const term = searchTerm.value.toLowerCase();
  return diagrams.value.filter(diagram => 
    diagram.title.toLowerCase().includes(term) ||
    (diagram.description && diagram.description.toLowerCase().includes(term))
  );
});

const closeModal = () => {
  emit('close');
};

const selectDiagram = (diagram) => {
  emit('diagram-selected', diagram);
  closeModal();
};

const loadDiagrams = async () => {
  try {
    loading.value = true;
    const userDiagrams = await tacticalDiagramService.getUserTacticalDiagrams();
    diagrams.value = userDiagrams;
  } catch (error) {
    console.error('Error al cargar diagramas:', error);
    // Aquí podrías mostrar una notificación de error
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};

onMounted(() => {
  if (props.isOpen) {
    loadDiagrams();
  }
});

// Recargar diagramas cuando se abre el modal
watch(() => props.isOpen, (newValue) => {
  if (newValue) {
    loadDiagrams();
  }
});
</script>

<style scoped>
.diagram-library-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.8);
  z-index: 5000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.modal-content {
  background-color: #1a2634;
  border-radius: 12px;
  width: 100%;
  max-width: 1200px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.3);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  z-index: 1000;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background-color: rgba(0, 0, 0, 0.5);
  transform: scale(1.1);
}

.library-header {
  padding: 30px 30px 20px;
  border-bottom: 1px solid #333;
}

.library-header h2 {
  margin: 0 0 20px 0;
  color: #fff;
  text-align: center;
}

.search-filters {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.search-input {
  padding: 12px 20px;
  border: 1px solid #444;
  border-radius: 25px;
  background-color: #2a3744;
  color: #fff;
  font-size: 16px;
  width: 300px;
  max-width: 100%;
}

.search-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.search-input::placeholder {
  color: #888;
}

.diagrams-grid {
  flex: 1;
  padding: 20px 30px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.loading {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #fff;
  font-size: 18px;
}

.loading i {
  font-size: 24px;
  margin-right: 10px;
  color: #007bff;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #888;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 20px;
  color: #666;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 18px;
}

.diagram-card {
  background-color: #2a3744;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.diagram-card:hover {
  transform: translateY(-5px);
  border-color: #007bff;
  box-shadow: 0 8px 25px rgba(0, 123, 255, 0.3);
}

.diagram-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #1a2634;
}

.diagram-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.diagram-info {
  padding: 15px;
}

.diagram-info h4 {
  margin: 0 0 8px 0;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

.diagram-info p {
  margin: 0 0 10px 0;
  color: #ccc;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.diagram-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #888;
}

.diagram-actions {
  padding: 0 15px 15px;
}

.btn-select {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s ease;
}

.btn-select:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.modal-actions {
  padding: 20px 30px;
  border-top: 1px solid #333;
  display: flex;
  justify-content: center;
}

.btn-cancel {
  padding: 12px 30px;
  background-color: #6c757d;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s ease;
}

.btn-cancel:hover {
  background-color: #545b62;
  transform: translateY(-2px);
}

.btn-primary {
  padding: 12px 24px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

/* Responsive */
@media (max-width: 768px) {
  .diagram-library-modal {
    padding: 10px;
  }
  
  .modal-content {
    max-height: 95vh;
  }
  
  .library-header {
    padding: 20px 20px 15px;
  }
  
  .search-input {
    width: 100%;
    max-width: 250px;
  }
  
  .diagrams-grid {
    padding: 15px 20px;
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .diagram-card {
    max-width: 100%;
  }
  
  .modal-actions {
    padding: 15px 20px;
  }
}
</style>
