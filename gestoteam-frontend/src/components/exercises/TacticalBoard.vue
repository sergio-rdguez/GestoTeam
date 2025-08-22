<template>
  <div v-if="isOpen" class="tactical-board-modal">
    <div class="modal-content">
      <button class="close-btn" @click="closeModal" title="Cerrar">
        <i class="fas fa-times"></i> </button>

      <aside class="controls-panel">
        <h2>Controles</h2>

        <div class="control-group">
          <label>Añadir Jugadores</label>
          <div class="button-grid">
            <button @click="addPlayer('A')" class="btn-teamA">Equipo A</button>
            <button @click="addPlayer('B')" class="btn-teamB">Equipo B</button>
          </div>
        </div>
        
        <div class="control-group">
          <label>Añadir Balón</label>
          <button @click="addBall" class="btn-ball">Balón</button>
        </div>

        <div class="control-group">
          <label>Herramientas</label>
          <div class="button-grid">
            <button @click="selectTool('select')" :class="['btn-tool', { active: currentTool === 'select' }]">Mover</button>
            <button @click="selectTool('line')" :class="['btn-tool', { active: currentTool === 'line' }]">Línea</button>
          </div>
        </div>
        
        <div class="state-io">
          <div class="control-group">
            <label>Gestión del Tablero</label>
            <button @click="saveState" class="btn-save">Guardar</button>
            <button @click="clearBoard" class="btn-clear">Limpiar</button>
          </div>
        </div>
      </aside>

      <main class="board-container">
        <canvas ref="canvasEl"></canvas>
      </main>
      
      <!-- Formulario para guardar diagrama -->
      <div class="save-form">
        <h3>Guardar Diagrama</h3>
        <div class="form-group">
          <label for="diagramTitle">Título *</label>
          <input 
            id="diagramTitle"
            v-model="diagramTitle" 
            type="text" 
            placeholder="Ej: Formación 4-4-2" 
            required
          />
        </div>
        
        <div class="form-group">
          <label for="diagramDescription">Descripción</label>
          <textarea 
            id="diagramDescription"
            v-model="diagramDescription" 
            placeholder="Descripción opcional del diagrama..."
            rows="3"
          ></textarea>
        </div>
        
        <div class="save-actions">
          <button @click="saveAndReturn" class="btn-save-primary" :disabled="!diagramTitle.trim()">
            <i class="fas fa-save"></i> Guardar y Volver al Ejercicio
          </button>
          <button @click="closeModal" class="btn-cancel">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted, watch, nextTick } from 'vue';
import * as fabric from 'fabric';

// --- PROPS Y EMITS ---
const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  exerciseId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['close', 'diagram-saved']);

// --- REFERENCIAS Y ESTADO ---
const canvasEl = ref(null);
let fabricCanvas = null;

const currentTool = ref('select');
let teamACounter = 1;
let teamBCounter = 1;

let isDrawingLine = false;
let currentLine = null;

const diagramTitle = ref('');
const diagramDescription = ref('');

const PITCH_ASPECT_RATIO = 105 / 68;

// --- MÉTODOS DEL COMPONENTE ---
const closeModal = () => emit('close');

// --- LÓGICA DEL CANVAS ---
const initCanvas = () => {
  if (fabricCanvas || !canvasEl.value) return;

  fabricCanvas = new fabric.Canvas(canvasEl.value, {
    selection: true,
    backgroundColor: '#1a7431',
    preserveObjectStacking: true // Importante para que el campo esté siempre detrás
  });

  setupCanvasEvents();
  resizeCanvas();

  // Inicializar el formulario
  diagramTitle.value = '';
  diagramDescription.value = '';

  window.addEventListener('resize', resizeCanvas);
  window.addEventListener('keydown', handleKeyDown);
};

const destroyCanvas = () => {
  if (fabricCanvas) {
    fabricCanvas.dispose();
    fabricCanvas = null;
  }
  window.removeEventListener('resize', resizeCanvas);
  window.removeEventListener('keydown', handleKeyDown);
};

const resizeCanvas = () => {
  if (!fabricCanvas || !canvasEl.value) return;
  const container = canvasEl.value.parentElement;
  if (!container) return;

  const { width, height } = container.getBoundingClientRect();
  let canvasWidth = width;
  let canvasHeight = width / PITCH_ASPECT_RATIO;

  if (canvasHeight > height) {
    canvasHeight = height;
    canvasWidth = height * PITCH_ASPECT_RATIO;
  }

  fabricCanvas.setDimensions({ width: canvasWidth, height: canvasHeight });
  drawPitch();
  fabricCanvas.renderAll();
};

const drawPitch = () => {
  // Elimina las líneas viejas para redibujarlas
  fabricCanvas.getObjects('group').forEach(obj => {
      if (obj.customType === 'pitch_element') fabricCanvas.remove(obj);
  });

  const width = fabricCanvas.getWidth();
  const height = fabricCanvas.getHeight();

  const lineStyle = {
    stroke: 'rgba(255, 255, 255, 0.4)',
    strokeWidth: 2,
  };

  const centerCircle = new fabric.Circle({
    radius: height * 0.13,
    originX: 'center',
    originY: 'center',
    fill: 'transparent',
    ...lineStyle
  });
  
  const centerLine = new fabric.Line([0, 0, 0, height], { ...lineStyle });
  
  const pitchElements = new fabric.Group([centerCircle, centerLine], {
    left: width / 2,
    top: height / 2,
    originX: 'center',
    originY: 'center',
    selectable: false,
    evented: false,
    customType: 'pitch_element'
  });

  fabricCanvas.add(pitchElements);
  pitchElements.moveTo(-1); // Mueve el campo al fondo de la pila de objetos
};

const setupCanvasEvents = () => {
  fabricCanvas.on('mouse:down', (options) => {
    if (currentTool.value !== 'line') return;
    
    isDrawingLine = true;
    const pointer = fabricCanvas.getPointer(options.e);
    const points = [pointer.x, pointer.y, pointer.x, pointer.y];
    
    currentLine = new fabric.Line(points, {
      stroke: '#ffeb3b',
      strokeWidth: 4,
      selectable: false,
      evented: false,
      customType: 'drawing'
    });
    fabricCanvas.add(currentLine);
  });

  fabricCanvas.on('mouse:move', (options) => {
    if (!isDrawingLine || !currentLine) return;
    
    const pointer = fabricCanvas.getPointer(options.e);
    currentLine.set({ x2: pointer.x, y2: pointer.y });
    fabricCanvas.renderAll();
  });

  fabricCanvas.on('mouse:up', () => {
    if (!isDrawingLine) return;
    currentLine.setCoords();
    isDrawingLine = false;
    selectTool('select');
  });

  fabricCanvas.on('object:dblclick', (options) => {
    if (options.target && options.target.customType === 'player') {
      const currentNumber = options.target.item(1).text;
      const newNumber = prompt('Nuevo número:', currentNumber);
      if (newNumber && !isNaN(newNumber) && newNumber.trim() !== '') {
        options.target.item(1).set('text', newNumber);
        fabricCanvas.renderAll();
      }
    }
  });
};

// --- CREACIÓN DE OBJETOS ---
const createPlayer = (team, number) => {
  const isTeamA = team === 'A';
  const circle = new fabric.Circle({
    radius: 20,
    fill: isTeamA ? '#007bff' : '#dc3545',
    stroke: '#fff',
    strokeWidth: 2,
    originX: 'center',
    originY: 'center'
  });

  const text = new fabric.Text(String(number), {
    fontSize: 22,
    fill: '#fff',
    fontWeight: 'bold',
    originX: 'center',
    originY: 'center'
  });
  
  const leftPosition = 50 + (45 * ((team === 'A' ? teamACounter : teamBCounter) -1));
  const topPosition = isTeamA ? fabricCanvas.getHeight() - 40 : 40;

  return new fabric.Group([circle, text], {
    left: leftPosition,
    top: topPosition,
    hasControls: false,
    hasBorders: false,
    lockScalingX: true,
    lockScalingY: true,
    lockRotation: true,
    customType: 'player',
    team: team
  });
};

const createBall = () => {
  return new fabric.Circle({
    radius: 12,
    fill: '#f8f9fa',
    stroke: '#000',
    strokeWidth: 1,
    left: fabricCanvas.getWidth() / 2,
    top: fabricCanvas.getHeight() / 2,
    originX: 'center',
    originY: 'center',
    hasControls: false,
    hasBorders: false,
    lockScalingX: true,
    lockScalingY: true,
    lockRotation: true,
    customType: 'ball'
  });
};

// --- ACCIONES DEL PANEL DE CONTROL ---
const addPlayer = (team) => {
  if (!fabricCanvas) return;
  const player = createPlayer(team, team === 'A' ? teamACounter++ : teamBCounter++);
  fabricCanvas.add(player);
};

const addBall = () => {
  if (!fabricCanvas) return;
  const existingBall = fabricCanvas.getObjects().find(obj => obj.customType === 'ball');
  if (!existingBall) {
    fabricCanvas.add(createBall());
  }
};

const selectTool = (tool) => {
  currentTool.value = tool;
  if (!fabricCanvas) return;

  if (tool === 'select') {
    fabricCanvas.defaultCursor = 'default';
    fabricCanvas.selection = true;
    fabricCanvas.getObjects().forEach(obj => {
      if (obj.customType !== 'pitch_element') {
        obj.set({ selectable: true, evented: true });
      }
    });
  } else if (tool === 'line') {
    fabricCanvas.defaultCursor = 'crosshair';
    fabricCanvas.selection = false;
    fabricCanvas.getObjects().forEach(obj => {
      obj.set({ selectable: false, evented: false });
    });
  }
};

const clearBoard = () => {
  if (!fabricCanvas) return;
  if (confirm('¿Estás seguro de que quieres limpiar la pizarra?')) {
    const objectsToRemove = fabricCanvas.getObjects().filter(obj => obj.customType !== 'pitch_element');
    objectsToRemove.forEach(obj => fabricCanvas.remove(obj));
    teamACounter = 1;
    teamBCounter = 1;
    fabricCanvas.renderAll();
  }
};

const saveState = () => {
  if (!fabricCanvas) return;
  const json = JSON.stringify(fabricCanvas.toDatalessJSON(['customType', 'team']));
  console.log("Estado del tablero:", json);
  alert("Estado guardado en consola (para desarrollo)");
};

const saveAndReturn = async () => {
  if (!diagramTitle.value.trim()) {
    alert('El título es obligatorio');
    return;
  }
  
  try {
    // 1. Exportar canvas como PNG
    const dataURL = fabricCanvas.toDataURL({ format: 'png', quality: 1 });
    
    // 2. Convertir a Blob
    const blob = await fetch(dataURL).then(r => r.blob());
    
    // 3. Crear FormData
    const formData = new FormData();
    formData.append('file', blob, 'diagram.png');
    formData.append('title', diagramTitle.value);
    formData.append('description', diagramDescription.value);
    
    // 4. Subir al backend
    const response = await fetch('/api/files/tactical-diagram', {
      method: 'POST',
      body: formData
    });
    
    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }
    
    const imageUrl = await response.text();
    
    // 5. Emitir evento con el diagrama guardado
    emit('diagram-saved', {
      title: diagramTitle.value,
      description: diagramDescription.value,
      imageUrl: imageUrl
    });
    
    // 6. Cerrar modal
    emit('close');
  } catch (error) {
    console.error('Error al guardar diagrama:', error);
    alert('Error al guardar el diagrama: ' + error.message);
  }
};

// --- MANEJADORES DE TECLADO ---
const handleKeyDown = (e) => {
  if (!fabricCanvas) return;
  if (e.key === 'Delete' || e.key === 'Backspace') {
    const activeObjects = fabricCanvas.getActiveObjects();
    if (activeObjects.length > 0) {
      activeObjects.forEach(obj => fabricCanvas.remove(obj));
      fabricCanvas.discardActiveObject().renderAll();
    }
  }
};

// --- CICLO DE VIDA DEL COMPONENTE ---
watch(() => props.isOpen, (newValue) => {
  if (newValue) {
    nextTick(initCanvas);
  } else {
    destroyCanvas();
  }
});

onUnmounted(() => {
  destroyCanvas();
});
</script>

<style scoped>
:root {
  --color-primary: #007bff;
  --color-secondary: #dc3545;
  --color-background: #0f1923;
  --panel-width: 240px;
}

.tactical-board-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: var(--color-background);
  z-index: 5000;
  display: flex;
  overflow: hidden;
}

.modal-content {
  display: flex;
  width: 100%;
  height: 100%;
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

.controls-panel {
  width: var(--panel-width);
  height: 100vh;
  background-color: #1a2634;
  color: #fff;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 25px;
  box-shadow: 5px 0 15px rgba(0,0,0,0.2);
  z-index: 100;
  overflow-y: auto;
}

.controls-panel h2 {
  text-align: center;
  margin: 0 0 10px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--color-primary);
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.control-group label {
  font-weight: bold;
  font-size: 0.9em;
  color: #ccc;
  text-transform: uppercase;
}

.control-group .button-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

button {
  padding: 12px 10px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s ease;
  color: #fff;
  font-size: 0.95em;
  text-align: center;
}

.btn-teamA { background-color: var(--color-primary); }
.btn-teamB { background-color: var(--color-secondary); }
.btn-ball { background-color: #6c757d; grid-column: 1 / -1; } /* Ocupa todo el ancho */
.btn-save { background-color: #28a745; }
.btn-clear { background-color: #343a40; }
.btn-tool { background-color: #495057; }

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  filter: brightness(1.1);
}

.btn-tool.active {
  background-color: var(--color-primary);
  box-shadow: inset 0 2px 4px rgba(0,0,0,0.4);
}

.state-io {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid #333;
}

.board-container {
  flex-grow: 1;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
  box-sizing: border-box;
}

.board-container canvas {
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.save-form {
  width: var(--panel-width);
  height: 100vh;
  background-color: #1a2634;
  color: #fff;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-shadow: -5px 0 15px rgba(0,0,0,0.2);
  z-index: 100;
  overflow-y: auto;
}

.save-form h3 {
  text-align: center;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--color-primary);
  color: #fff;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: bold;
  font-size: 0.9em;
  color: #ccc;
}

.form-group input,
.form-group textarea {
  padding: 10px;
  border: 1px solid #444;
  border-radius: 5px;
  background-color: #2a3744;
  color: #fff;
  font-size: 14px;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #888;
}

.save-actions {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn-save-primary {
  background-color: #28a745;
  padding: 15px;
  font-size: 16px;
  font-weight: bold;
}

.btn-save-primary:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  transform: none;
}

.btn-save-primary:disabled:hover {
  transform: none;
  box-shadow: none;
}

.btn-cancel {
  background-color: #6c757d;
}

/* Responsive para tablets y móviles */
@media (max-width: 768px) {
  .modal-content {
    flex-direction: column-reverse;
  }
  .controls-panel {
    width: 100%;
    height: auto;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: flex-start;
    padding: 10px;
    box-shadow: 0 -5px 15px rgba(0,0,0,0.2);
  }
  .controls-panel h2 { display: none; }
  .control-group { flex: 1 1 150px; }
  .state-io { margin-top: 0; border-top: none; padding-top: 0; }
  .board-container { height: calc(100vh - 180px); /* Ajustar altura */ }
  .close-btn { top: 10px; right: 10px; }
  
  .save-form {
    width: 100%;
    height: auto;
    order: -1;
    box-shadow: 0 -5px 15px rgba(0,0,0,0.2);
  }
  
  .save-form h3 { display: none; }
  .form-group { flex: 1 1 200px; }
  .save-actions { 
    flex-direction: row; 
    margin-top: 20px;
  }
  .btn-save-primary,
  .btn-cancel { flex: 1; }
}
</style>