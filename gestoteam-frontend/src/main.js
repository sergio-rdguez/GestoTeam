import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import authService from './services/auth';

// Importar FontAwesome
import '@fortawesome/fontawesome-free/css/all.css';

// Importar estilos globales
import './assets/styles/main.css';

const app = createApp(App);

app.use(router);

// Inicializar la validación de autenticación antes de montar la app
authService.checkAuth().then(() => {
  app.mount('#app');
});