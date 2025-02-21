import { createRouter, createWebHistory } from 'vue-router';
import { authService } from '@/services/auth';
import MainLayout from '@/layouts/MainLayout.vue';
import TeamsPage from '@/pages/Team/TeamsPage.vue';
import PlayerDetails from '@/pages/player/PlayerDetails.vue';
import PlayerForm from '@/pages/player/PlayerForm.vue';
import TeamForm from '@/pages/Team/TeamForm.vue';
import UserSettings from '@/pages/settings/UserSettings.vue';
import TeamDetails from "@/pages/Team/TeamDetails.vue";
import TeamPlayers from "@/pages/Team/TeamPlayers.vue";
import TeamStatistics from "@/pages/Team/TeamStatistics.vue";
import TeamMatches from "@/pages/Team/Matches/TeamMatches.vue";
import AddMatch from "@/pages/Team/Matches/AddMatch.vue";
import MatchDetails from '@/pages/Team/Matches/MatchDetails.vue';
import TeamNotifications from "@/pages/Team/TeamNotifications.vue";
import TeamTrainings from "@/pages/Team/TeamTrainings.vue";
import LoginPage from '@/pages/auth/LoginPage.vue';


const routes = [
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/teams' },
      { path: '/teams', name: 'Teams', component: TeamsPage },
      { path: '/teams/:id', name: 'TeamDetails', component: TeamDetails },
      { path: "/teams/:id/players", name: "TeamPlayers", component: TeamPlayers },
      { path: "/teams/:id/statistics", name: "TeamStatistics", component: TeamStatistics },
      { path: "/teams/:id/matches", name: "TeamMatches", component: TeamMatches },
      { path: "/teams/:teamId/add-match", name: "AddMatch", component: AddMatch },
      { path: "/matches/:id",name: "MatchDetails", component: MatchDetails, props: true,},
      { path: "/teams/:id/notifications", name: "TeamNotifications", component: TeamNotifications },
      { path: "/teams/:id/trainings", name: "TeamTrainings", component: TeamTrainings },
      { path: '/teams/:teamId/add-player', name: 'AddPlayer', component: PlayerForm },
      { path: '/players/:id', name: 'PlayerDetails', component: PlayerDetails },
      { path: '/players/:id/edit', name: 'EditPlayer', component: PlayerForm },
      { path: '/teams/add', name: 'AddTeam', component: TeamForm },
      { path: '/teams/:id/edit', name: 'EditTeam', component: TeamForm },
      { path: '/settings', name: 'UserSettings', component: UserSettings },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.isAuthenticated();
  // Permitir acceso a Swagger sin autenticación
  if (to.path.startsWith('/swagger-ui') || to.path.startsWith('/v3/api-docs')) {
    next(); // Permitir acceso
    return;
  }

  if (!isAuthenticated && to.name !== 'LoginPage') {
    next({ name: 'LoginPage' });
  } else if (isAuthenticated && to.name === 'LoginPage') {
    next({ name: 'Teams' });
  } else {
    next();
  }
});

export default router;
