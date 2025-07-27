import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import authService from './services/auth'; // Importamos el servicio

// 1. Importa nuestro sistema de diseño global
import '@/assets/styles/main.css';

// 2. Importa librerías de terceros
import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.css';

// 3. Importa plugins de Vue
import { createVfm } from 'vue-final-modal';
import 'vue-final-modal/style.css'

// Creamos una función asíncrona para controlar el arranque
async function startApp() {
  // Primero, intentamos restaurar la sesión del usuario.
  // La aplicación esperará aquí hasta que checkAuth() termine.
  await authService.checkAuth();

  const vfm = createVfm();
  const app = createApp(App);

  app.use(router);
  app.use(vfm);

  // Solo montamos la aplicación cuando la comprobación ha finalizado.
  app.mount('#app');
}

// Llamamos a la función para iniciar la aplicación.
startApp();