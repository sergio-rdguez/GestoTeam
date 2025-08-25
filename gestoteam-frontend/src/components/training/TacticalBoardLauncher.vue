<template>
  <div class="tactical-board-launcher">
    <BaseButton
      @click="openTacticalBoard"
      class="w-full"
      variant="primary"
      size="lg"
    >
      <template #icon>
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </template>
      Abrir Pizarra Táctica
    </BaseButton>

    <!-- Modal de confirmación para exportar a ejercicio -->
    <div v-if="showExportModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 class="text-lg font-semibold mb-4">¿Deseas exportar la pizarra a un ejercicio?</h3>
        <p class="text-gray-600 mb-6">
          Puedes exportar la imagen de la pizarra táctica directamente a un ejercicio existente o crear uno nuevo.
        </p>
        <div class="flex gap-3">
          <BaseButton @click="closeExportModal" variant="secondary" class="flex-1">
            Cancelar
          </BaseButton>
          <BaseButton @click="openTacticalBoard" variant="primary" class="flex-1">
            Continuar
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BaseButton from '@/components/base/BaseButton.vue';
import { useNotification } from '@/composables/useNotification';

export default {
  name: 'TacticalBoardLauncher',
  components: {
    BaseButton
  },
  props: {
    // Si se pasa un exerciseId, se puede exportar directamente
    exerciseId: {
      type: [Number, String],
      default: null
    },
    // Si se pasa un trainingId, se puede crear un ejercicio vinculado
    trainingId: {
      type: [Number, String],
      default: null
    }
  },
  setup() {
    const { showNotification } = useNotification();
    return { showNotification };
  },
  data() {
    return {
      showExportModal: false
    };
  },
  methods: {
    openTacticalBoard() {
      // Cerrar modal si está abierto
      this.showExportModal = false;
      
      // Preparar la integración con el frontend
      this.prepareIntegration();
      
      // Abrir la pizarra táctica en una nueva ventana
      const boardWindow = window.open(
        '/tactical-board.html',
        'tactical-board',
        'width=1200,height=800,scrollbars=yes,resizable=yes'
      );
      
      // Configurar la comunicación entre ventanas
      this.setupWindowCommunication(boardWindow);
    },
    
    prepareIntegration() {
      // Exponer los servicios del frontend globalmente para que la pizarra los pueda usar
      window.exerciseService = this.$exerciseService;
      window.showNotification = this.showNotification;
      
      // También exponer el localStorage del frontend
      window.frontendLocalStorage = localStorage;
    },
    
    setupWindowCommunication(boardWindow) {
      if (!boardWindow) return;
      
      // Verificar que la ventana se abrió correctamente
      const checkWindow = setInterval(() => {
        if (boardWindow.closed) {
          clearInterval(checkWindow);
          return;
        }
        
        try {
          // Intentar establecer la comunicación
          if (boardWindow.document && boardWindow.document.readyState === 'complete') {
            clearInterval(checkWindow);
            
            // La pizarra táctica ya puede acceder a los servicios expuestos
            console.log('Pizarra táctica abierta y lista para integración');
          }
        } catch (error) {
          // Error de cross-origin, continuar intentando
        }
      }, 100);
    },
    
    closeExportModal() {
      this.showExportModal = false;
    }
  },
  
  mounted() {
    // Si se pasa un exerciseId, mostrar el modal de confirmación
    if (this.exerciseId) {
      this.showExportModal = true;
    }
  }
};
</script>

<style scoped>
.tactical-board-launcher {
  /* Estilos específicos del componente */
}
</style>
