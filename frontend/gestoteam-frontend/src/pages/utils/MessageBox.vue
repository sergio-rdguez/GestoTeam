<template>
    <div v-if="show" class="message-box">
      <div class="message-content">
        <i :class="['fas', iconClass]"></i>
        <p class="message-text">{{ message }}</p>
        <button @click="closeMessage">Cerrar</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      message: {
        type: String,
        required: true,
      },
      type: {
        type: String,
        default: "info", // Puede ser "success", "error", "warning", "info"
      },
      show: {
        type: Boolean,
        default: true,
      },
    },
    data() {
      return {
        visible: this.show,
      };
    },
    computed: {
      iconClass() {
        switch (this.type) {
          case "success":
            return "fa-check-circle success-icon";
          case "error":
            return "fa-exclamation-circle error-icon";
          case "warning":
            return "fa-exclamation-triangle warning-icon";
          default: // info
            return "fa-info-circle info-icon";
        }
      },
    },
    methods: {
      closeMessage() {
        this.visible = false;
        this.$emit("close");
      },
    },
  };
  </script>
  
  <style scoped>
  .message-box {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.5); /* Fondo semitransparente */
    z-index: 1000;
  }
  
  .message-content {
    background-color: #e7f3fe; /* Fondo azul claro por defecto */
    color: #0c5460; /* Texto azul oscuro por defecto */
    padding: 20px;
    border: 1px solid #b8daff;
    border-radius: 8px;
    text-align: center;
    max-width: 400px;
    width: 90%;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .success-icon {
    font-size: 48px;
    color: #28a745; /* Verde para éxito */
    margin-bottom: 15px;
  }
  
  .error-icon {
    font-size: 48px;
    color: #dc3545; /* Rojo para error */
    margin-bottom: 15px;
  }
  
  .warning-icon {
    font-size: 48px;
    color: #ffc107; /* Amarillo para advertencia */
    margin-bottom: 15px;
  }
  
  .info-icon {
    font-size: 48px;
    color: #007bff; /* Azul para info */
    margin-bottom: 15px;
  }
  
  .message-text {
    font-size: 18px;
    margin: 10px 0;
  }
  
  button {
    background-color: #007bff; /* Botón azul */
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 14px;
    margin-top: 15px;
  }
  
  button:hover {
    background-color: #0056b3; /* Azul más oscuro al pasar el mouse */
  }
  </style>