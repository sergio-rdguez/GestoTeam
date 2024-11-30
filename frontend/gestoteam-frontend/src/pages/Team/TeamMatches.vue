<template>
    <div class="team-matches-page">
        <h2 class="page-title">Próximos Partidos de {{ team.name }}</h2>
        <div v-if="loading" class="loading-message">
            <p>Cargando próximos partidos...</p>
        </div>
        <div v-else>
            <div class="matches-list">
                <div class="match-item" v-for="match in matches" :key="match.id">
                    <h4>{{ match.opponent }}</h4>
                    <p>{{ match.date }} - {{ match.time }}</p>
                    <p>{{ match.venue }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            team: { name: "Equipo Dummy" },
            matches: [
                { id: 1, opponent: "Equipo A", date: "2024-12-01", time: "18:00", venue: "Estadio Local" },
                { id: 2, opponent: "Equipo B", date: "2024-12-08", time: "20:00", venue: "Estadio Visitante" },
            ],
            loading: false,
        };
    },
};
</script>

<style scoped>
.team-matches-page {
    padding: 20px;
    background-color: #f9f9f9;
    min-height: 100vh;
}

.page-title {
    text-align: center;
    font-size: 2rem;
    color: #333;
    margin-bottom: 20px;
}

.matches-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.match-item {
    background: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.match-item h4 {
    font-size: 1.2rem;
    font-weight: bold;
}

.match-item p {
    font-size: 1rem;
    color: #555;
}
</style>