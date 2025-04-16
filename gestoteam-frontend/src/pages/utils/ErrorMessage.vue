<template>
  <div v-if="show" class="info-box">
    <div class="info-content">
      <i class="fas fa-info-circle info-icon"></i>
      <p class="info-message">{{ message }}</p>
      <button @click="closeError">Cerrar</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    message: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      show: true,
    };
  },
  methods: {
    closeError() {
      this.show = false;
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.info-box {
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

.info-content {
  background-color: #e7f3fe; /* Fondo azul claro */
  color: #0c5460; /* Texto azul oscuro */
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

.info-icon {
  font-size: 48px;
  color: #007bff; /* Azul de FontAwesome */
  margin-bottom: 15px;
}

.info-message {
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
