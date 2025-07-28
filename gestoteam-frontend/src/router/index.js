import { createRouter, createWebHistory } from "vue-router";
import authService from "@/services/auth"; // <-- CORRECCIÓN 1: Importación por defecto
import MainLayout from "@/components/layout/MainLayout.vue";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/pages/auth/LoginPage.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/",
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "", redirect: "/teams" },
      {
        path: "/teams",
        name: "Teams",
        component: () => import("@/pages/Team/TeamsPage.vue"),
      },
      {
        path: "/teams/add",
        name: "AddTeam",
        component: () => import("@/pages/Team/TeamForm.vue"),
      },
      {
        path: "/teams/:id",
        name: "TeamDetails",
        component: () => import("@/pages/Team/TeamDetails.vue"),
      },
      {
        path: "/teams/:id/edit",
        name: "EditTeam",
        component: () => import("@/pages/Team/TeamForm.vue"),
      },
      {
        path: "/teams/:id/players",
        name: "TeamPlayers",
        component: () => import("@/pages/Team/TeamPlayers.vue"),
      },
      {
        path: "/teams/:id/statistics",
        name: "TeamStatistics",
        component: () => import("@/pages/Team/TeamStatistics.vue"),
      },
      {
        path: "/teams/:id/matches",
        name: "TeamMatches",
        component: () => import("@/pages/Team/Matches/TeamMatches.vue"),
      },
      {
        path: "/teams/:id/notifications",
        name: "TeamNotifications",
        component: () => import("@/pages/Team/TeamNotifications.vue"),
      },
      {
        path: "/teams/:id/trainings",
        name: "TeamTrainings",
        component: () => import("@/pages/Team/TeamTrainings.vue"),
      },
      {
        path: "/teams/:teamId/add-match",
        name: "AddMatch",
        component: () => import("@/pages/Team/Matches/MatchForm.vue"),
      },
      {
        path: "teams/:teamId/matches/:id/edit",
        name: "EditMatch",
        component: () => import("../pages/Team/Matches/MatchForm.vue"),
      },
      {
        path: "teams/:teamId/matches/:id/stats",
        name: "ManageMatchStats",
        component: () => import("@/pages/Team/Matches/MatchStatsManager.vue"),
        meta: { requiresAuth: true },
      },
      {
        path: "/teams/:teamId/add-player",
        name: "AddPlayer",
        component: () => import("@/pages/player/PlayerForm.vue"),
      },
      {
        path: "/matches/:id",
        name: "MatchDetails",
        component: () => import("@/pages/Team/Matches/MatchDetails.vue"),
        props: true,
      },
      {
        path: "/players/:id",
        name: "PlayerDetails",
        component: () => import("@/pages/player/PlayerDetails.vue"),
      },
      {
        path: "/players/:id/edit",
        name: "EditPlayer",
        component: () => import("@/pages/player/PlayerForm.vue"),
      },
      {
        path: "/rivals/:teamId",
        name: "RivalDetails",
        component: () => import("@/pages/Team/Rivals/RivalsDetails.vue"),
      },
      {
        path: "/settings",
        name: "UserSettings",
        component: () => import("@/pages/settings/UserSettings.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.state.isAuthenticated;

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: "Login" });
  } else if (to.name === "Login" && isAuthenticated) {
    next({ name: "Teams" });
  } else {
    next();
  }
});

export default router;
