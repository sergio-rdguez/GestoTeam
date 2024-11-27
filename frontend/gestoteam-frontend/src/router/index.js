import { createRouter, createWebHistory } from 'vue-router';
import MainLayout from '@/layouts/MainLayout.vue';
import TeamsPage from '@/pages/Team/TeamsPage.vue';
import AddTeamPage from '@/pages/Team/AddTeamPage.vue';
import TeamDetails from '@/pages/Team/TeamDetails.vue';
import AddPlayer from '@/pages/player/AddPlayer.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/teams' },
      { path: '/teams', name: 'Teams', component: TeamsPage },
      { path: '/teams/add', component: AddTeamPage },
      { path: '/teams/:id', name: 'TeamDetails', component: TeamDetails },
      { path: '/teams/:teamId/add-player', name: 'AddPlayer', component: AddPlayer,},
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
