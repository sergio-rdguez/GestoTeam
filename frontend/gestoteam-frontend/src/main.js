import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// Bootstrap primero
import 'bootstrap/dist/css/bootstrap.min.css';

// Font Awesome después
import '@fortawesome/fontawesome-free/css/all.css';

// Vue Final Modal
import { createVfm } from 'vue-final-modal';

// Crear la instancia de Vue Final Modal
const vfm = createVfm();

// Crear la aplicación
const app = createApp(App);

// Usar el enrutador y Vue Final Modal
app.use(router);
app.use(vfm);

// Montar la aplicación
app.mount('#app');
