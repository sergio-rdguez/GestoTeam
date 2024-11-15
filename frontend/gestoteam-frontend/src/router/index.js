import { createRouter, createWebHistory } from 'vue-router';
import MainLayout from '@/layouts/MainLayout.vue';
import TeamsPage from '@/pages/TeamsPage.vue';
import AddTeamPage from '@/pages/AddTeamPage.vue';

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/teams' },
      { path: '/teams', component: TeamsPage },
      { path: '/teams/add', component: AddTeamPage },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
