import { createRouter, createWebHashHistory } from 'vue-router';
import LoginPage from '@/pages/auth/LoginPage.vue';
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
import RivalsDetails from "@/pages/Team/Rivals/RivalsDetails.vue";

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/teams' },
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
      { path: 'matches/:matchId', name: 'MatchDetails', component: MatchDetails, props: true },
      { path: 'opponents/:opponentId', name: 'RivalsDetails', component: RivalsDetails, props: true },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('user-token');
  if (to.name !== 'Login' && !loggedIn) {
    next({ name: 'Login' });
  } else if (to.name === 'Login' && loggedIn) {
    next({ name: 'Teams' });
  } else {
    next();
  }
});

export default router;