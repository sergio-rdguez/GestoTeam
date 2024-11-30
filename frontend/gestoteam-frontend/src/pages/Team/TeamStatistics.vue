<template>
    <div class="team-statistics-page">
        <h2 class="page-title">Estadísticas de {{ team.name }}</h2>
        <div v-if="loading" class="loading-message">
            <p>Cargando estadísticas...</p>
        </div>
        <div v-else>
            <div class="statistics-container">
                <div class="stat-item">
                    <h4>Partidos Ganados</h4>
                    <p>{{ statistics.wins }}</p>
                </div>
                <div class="stat-item">
                    <h4>Partidos Perdidos</h4>
                    <p>{{ statistics.losses }}</p>
                </div>
                <div class="stat-item">
                    <h4>Goles a Favor</h4>
                    <p>{{ statistics.goalsFor }}</p>
                </div>
                <div class="stat-item">
                    <h4>Goles en Contra</h4>
                    <p>{{ statistics.goalsAgainst }}</p>
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
            statistics: {
                wins: 10,
                losses: 5,
                goalsFor: 25,
                goalsAgainst: 18,
            },
            loading: false,
        };
    },
};
</script>

<style scoped>
.team-statistics-page {
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

.statistics-container {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    justify-content: center;
}

.stat-item {
    background: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    text-align: center;
    flex: 1 1 calc(40% - 10px);
}

.stat-item h4 {
    font-size: 1.2rem;
    color: #555;
}

.stat-item p {
    font-size: 1.8rem;
    font-weight: bold;
    color: #007bff;
}
</style>