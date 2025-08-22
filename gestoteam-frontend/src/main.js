import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// Importar FontAwesome
import '@fortawesome/fontawesome-free/css/all.css';

// Importar estilos globales
import './assets/styles/main.css';

const app = createApp(App);

app.use(router);

app.mount('#app');