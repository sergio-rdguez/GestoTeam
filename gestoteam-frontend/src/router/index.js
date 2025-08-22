import { createRouter, createWebHashHistory } from 'vue-router';
import LoginPage from '@/pages/auth/LoginPage.vue';
import RegisterPage from '@/pages/auth/RegisterPage.vue';
import DashboardPage from '@/pages/DashboardPage.vue';
import TeamsPage from '@/pages/Team/TeamsPage.vue';
import MainLayout from '@/components/layout/MainLayout.vue';
import TeamDetails from '@/pages/Team/TeamDetails.vue';
import TeamPlayers from '@/pages/Team/TeamPlayers.vue';
import PlayerDetails from '@/pages/player/PlayerDetails.vue';
import PlayerForm from '@/pages/player/PlayerForm.vue';
import TeamForm from '@/pages/Team/TeamForm.vue';
import UserSettings from '@/pages/settings/UserSettings.vue';
import TeamMatches from '@/pages/Team/Matches/TeamMatches.vue';
import MatchDetails from '@/pages/Team/Matches/MatchDetails.vue';
import MatchForm from '@/pages/Team/Matches/MatchForm.vue';
import MatchStatsManager from '@/pages/Team/Matches/MatchStatsManager.vue';
import OpponentsPage from '@/pages/Team/Opponents/OpponentsPage.vue';
import OpponentForm from '@/pages/Team/Opponents/OpponentForm.vue';
import OpponentDetails from '@/pages/Team/Opponents/OpponentDetails.vue';
import ExerciseListPage from '@/pages/exercises/ExerciseListPage.vue';
import ExerciseDetailsPage from '@/pages/exercises/ExerciseDetailsPage.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterPage,
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: DashboardPage },
      { path: 'teams', name: 'Teams', component: TeamsPage },
      { path: 'teams/new', name: 'NewTeam', component: TeamForm, meta: { isNew: true } },
      { path: 'teams/:teamId/edit', name: 'EditTeam', component: TeamForm, props: true, meta: { isNew: false } },
      { path: 'teams/:teamId', name: 'TeamDetails', component: TeamDetails, props: true },
      { path: 'teams/:teamId/players', name: 'TeamPlayers', component: TeamPlayers, props: true },
      { path: 'teams/:teamId/players/new', name: 'NewPlayer', component: PlayerForm, props: true, meta: { isNew: true } },
      { path: 'teams/:teamId/players/:playerId/edit', name: 'EditPlayer', component: PlayerForm, props: true, meta: { isNew: false } },
      { path: 'players/:playerId', name: 'PlayerDetails', component: PlayerDetails, props: true },
      { path: 'settings', name: 'UserSettings', component: UserSettings },
      { path: 'teams/:teamId/matches', name: 'TeamMatches', component: TeamMatches, props: true },
      { path: 'teams/:teamId/matches/new', name: 'NewMatch', component: MatchForm, props: true, meta: { isNew: true } },
      { path: 'teams/:teamId/matches/:matchId/edit', name: 'EditMatch', component: MatchForm, props: true, meta: { isNew: false } },
      { path: 'teams/:teamId/matches/:matchId/stats', name: 'MatchStatsManager', component: MatchStatsManager, props: true },
      { path: 'teams/:teamId/opponents', name: 'Opponents', component: OpponentsPage, props: true },
      { path: 'teams/:teamId/opponents/new', name: 'NewOpponent', component: OpponentForm, props: true, meta: { isNew: true } },
      { path: 'teams/:teamId/opponents/:opponentId/edit', name: 'EditOpponent', component: OpponentForm, props: true, meta: { isNew: false } },
      { path: 'matches/:matchId', name: 'MatchDetails', component: MatchDetails, props: true },
      { path: 'opponents/:opponentId', name: 'OpponentDetails', component: OpponentDetails, props: true },
      
      // Nuevas rutas para el Centro de Recursos
      { path: 'my-resources', name: 'MyResources', component: () => import('@/pages/MyResourcesPage.vue') },
      { path: 'my-resources/exercises', name: 'Exercises', component: ExerciseListPage },
      { path: 'my-resources/exercises/:id', name: 'ExerciseDetails', component: ExerciseDetailsPage, props: true },
      { path: 'my-resources/tactical-board', name: 'TacticalBoard', component: () => import('@/pages/TacticalBoardPage.vue') },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('authToken');
  const isAuthRoute = to.name === 'Login' || to.name === 'Register';
  if (!loggedIn && !isAuthRoute) {
    next({ name: 'Login' });
  } else if (loggedIn && isAuthRoute) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;