<template>
    <div class="team-notifications-page">
        <h2 class="page-title">Notificaciones de {{ team.name }}</h2>
        <div v-if="loading" class="loading-message">
            <p>Cargando notificaciones...</p>
        </div>
        <div v-else>
            <div class="notifications-list">
                <div class="notification-item" v-for="notification in notifications" :key="notification.id">
                    <h4>{{ notification.title }}</h4>
                    <p>{{ notification.date }}</p>
                    <p>{{ notification.content }}</p>
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
            notifications: [
                { id: 1, title: "Entrenamiento cancelado", date: "2024-11-29", content: "El entrenamiento del viernes se cancela por lluvia." },
                { id: 2, title: "Nuevo partido agregado", date: "2024-11-28", content: "Se ha agregado un partido amistoso contra Equipo C." },
            ],
            loading: false,
        };
    },
};
</script>

<style scoped>
.team-notifications-page {
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

.notifications-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.notification-item {
    background: white;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.notification-item h4 {
    font-size: 1.2rem;
    font-weight: bold;
}

.notification-item p {
    font-size: 1rem;
    color: #555;
}
</style>