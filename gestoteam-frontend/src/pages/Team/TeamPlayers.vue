<template>
    <div class="team-players-page">
        <h2 class="page-title">{{ team.name }}</h2>
        <div v-if="loading" class="loading-message">
            <p>Cargando jugadores...</p>
        </div>
        <div v-else>
            <!-- Contador de jugadores totales -->
            <div class="total-players">
                <p>Total de Jugadores: <strong>{{ totalPlayers }}</strong></p>
            </div>

            <!-- Lista de jugadores -->
            <div v-if="players.length > 0" class="player-list">
                <div v-for="player in players" :key="player.id" class="player-card"
                    @click="viewPlayerDetails(player.id)">
                    <div class="player-info">
                        <h4 class="player-name">{{ player.fullName }}</h4>
                        <p class="player-details">{{ player.position }} | #{{ player.number }}</p>
                    </div>
                    <span class="player-status" :class="player.status.toLowerCase()">
                        {{ player.status }}
                    </span>
                </div>
            </div>
            <p v-else class="no-players-message">No hay jugadores en este equipo.</p>
        </div>

        <!-- FAB Actions -->
        <div class="fab-container">
            <button class="fab" @click="toggleFabMenu">
                <i class="fa-solid fa-bars"></i>
            </button>
            <div v-if="showFabMenu" class="fab-actions">
                <button @click="goBack" class="fab-action">Volver</button>
                <button @click="addPlayer" class="fab-action">Añadir Jugador</button>
            </div>
        </div>
    </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
    data() {
        return {
            team: [],
            players: [],
            loading: true,
            totalPlayers: 0,
            showFabMenu: false,
        };
    },
    methods: {
        async fetchPlayers() {
            try {
                const teamId = this.$route.params.id;
                const response = await apiClient.get(`/players/team/${teamId}`);

                this.players = response.data.players.map((player) => ({
                    id: player.id,
                    fullName: player.name,
                    number: player.number,
                    position: player.position,
                    status: player.status,
                }));

                this.team = {
                    id: response.data.teamId,
                    name: response.data.teamName,
                };
                this.totalPlayers = response.data.totalPlayers;
            } catch (error) {
                console.error("Error al cargar los jugadores:", error);
            } finally {
                this.loading = false;
            }
        },
        toggleFabMenu() {
            this.showFabMenu = !this.showFabMenu;
        },
        goBack() {
            this.$router.push({ name: "TeamDetails", params: { id: this.team.id } });
        },
        addPlayer() {
            this.$router.push({ name: "AddPlayer", params: { teamId: this.team.id } });
        },
        viewPlayerDetails(playerId) {
            this.$router.push({ name: "PlayerDetails", params: { id: playerId } });
        },
    },
    mounted() {
        this.fetchPlayers();
    },
};
</script>

<style scoped>
/* General */
.team-players-page {
    padding: 20px;
    font-family: 'Arial', sans-serif;
    background-color: #f9f9f9;
    min-height: 100vh;
}

.page-title {
    text-align: center;
    font-size: 2rem;
    color: #333;
    margin-bottom: 20px;
}

/* Total Players */
.total-players {
    text-align: center;
    margin-bottom: 20px;
    font-size: 1.2rem;
    color: #555;
}

.total-players strong {
    font-size: 1.5rem;
    color: #007bff;
}

/* Loading */
.loading-message {
    text-align: center;
    font-size: 1.2rem;
    color: #666;
}

/* Player List */
.player-list {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
}

.player-card {
    flex: 1 1 calc(48% - 15px);
    background: white;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.player-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* Player Info */
.player-info {
    display: flex;
    flex-direction: column;
}

.player-name {
    font-size: 1.2rem;
    font-weight: bold;
    color: #222;
    margin: 0;
}

.player-details {
    font-size: 0.9rem;
    color: #666;
    margin: 0;
}

/* Player Status */
.player-status {
    font-size: 0.9rem;
    font-weight: bold;
    padding: 5px 10px;
    border-radius: 12px;
    text-transform: capitalize;
    color: white;
}

.player-status.activo {
    background-color: #28a745;
}

.player-status.lesionado {
    background-color: #dc3545;
}

.player-status.suspendido {
    background-color: #ffc107;
}

/* No Players */
.no-players-message {
    text-align: center;
    color: #666;
    font-size: 1rem;
    margin-top: 15px;
}

/* FAB Actions */
.fab-container {
    position: fixed;
    bottom: 20px;
    right: 20px;
    display: flex;
    flex-direction: column-reverse;
    align-items: flex-end;
}

.fab {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #007bff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: none;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.fab i {
    font-size: 1.5em;
    color: white;
}

.fab-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 10px;
}

.fab-action {
    background: white;
    border: none;
    border-radius: 8px;
    padding: 10px 15px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    cursor: pointer;
}

.fab-action:hover {
    background: #f1f1f1;
}

/* Responsiveness */
@media (min-width: 768px) {
    .player-card {
        flex: 1 1 calc(31% - 15px);
    }
}
</style>