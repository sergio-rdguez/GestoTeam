<template>
    <div class="team-trainings-page">
        <h2 class="page-title">Entrenamientos de {{ team.name }}</h2>
        <div v-if="loading" class="loading-message">
            <p>Cargando entrenamientos...</p>
        </div>
        <div v-else>
            <div class="trainings-list">
                <div class="training-item" v-for="training in trainings" :key="training.id">
                    <h4>{{ training.date }} - {{ training.time }}</h4>
                    <p>{{ training.location }}</p>
                    <p>{{ training.notes }}</p>
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
            trainings: [
                { id: 1, date: "2024-12-01", time: "18:00", location: "Cancha 1", notes: "Enfoque en tácticas defensivas." },
                { id: 2, date: "2024-12-03", time: "20:00", location: "Cancha 2", notes: "Ejercicios de velocidad." },
            ],
            loading: false,
        };
    },
};
</script>

<style scoped>
.team-trainings-page {
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

.trainings-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.training-item {
    background: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.training-item h4 {
    font-size: 1.2rem;
    font-weight: bold;
}

.training-item p {
    font-size: 1rem;
    color: #555;
}
</style>