import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// 1. Importa nuestro sistema de diseño global
import '@/assets/styles/main.css';

// 2. Importa librerías de terceros
import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.css';

// 3. Importa plugins de Vue
import { createVfm } from 'vue-final-modal';

const vfm = createVfm();
const app = createApp(App);

app.use(router);
app.use(vfm);

app.mount('#app');