<template>
    <div class="user-settings-page">
        <h1>Configuración de Usuario</h1>

        <!-- Formulario de Configuración -->
        <form @submit.prevent="updateSettings">
            <div class="form-group">
                <label for="maxPlayersPerTeam">Máximo de jugadores en plantilla</label>
                <input type="number" id="maxPlayersPerTeam" v-model="settings.maxPlayersPerTeam" min="1" required />
            </div>

            <div class="form-group">
                <label for="maxPlayersPerMatch">Máximo de jugadores convocados</label>
                <input type="number" id="maxPlayersPerMatch" v-model="settings.maxPlayersPerMatch" min="1" required />
            </div>

            <div class="form-group">
                <label for="yellowCardsForSuspension">Tarjetas amarillas para sanción</label>
                <input type="number" id="yellowCardsForSuspension" v-model="settings.yellowCardsForSuspension" min="1"
                    required />
            </div>

            <div class="form-group">
                <label for="maxTrainingsPerWeek">Máximo de entrenamientos por semana</label>
                <input type="number" id="maxTrainingsPerWeek" v-model="settings.maxTrainingsPerWeek" min="1" required />
            </div>

            <div class="form-group">
                <label for="notifyBeforeMatch">Notificar antes de un partido</label>
                <input type="checkbox" id="notifyBeforeMatch" v-model="settings.notifyBeforeMatch" />
            </div>

            <div class="form-group">
                <label for="notifyBeforeTraining">Notificar antes de un entrenamiento</label>
                <input type="checkbox" id="notifyBeforeTraining" v-model="settings.notifyBeforeTraining" />
            </div>

            <div class="buttons">
                <button type="submit" class="btn-save">Guardar Configuración</button>
                <button type="button" class="btn-cancel" @click="cancel">Cancelar</button>
            </div>
        </form>
    </div>
</template>

<script>
import apiClient from "@/services/api";

export default {
    data() {
        return {
            settings: {
                maxPlayersPerTeam: 25,
                maxPlayersPerMatch: 18,
                yellowCardsForSuspension: 5,
                maxTrainingsPerWeek: 4,
                notifyBeforeMatch: true,
                notifyBeforeTraining: true,
            },
        };
    },
    methods: {
        async fetchSettings() {
            try {
                const response = await apiClient.get("/user-settings");
                this.settings = response.data;
            } catch (error) {
                console.error("Error al obtener configuraciones:", error);
                alert("No se pudieron cargar las configuraciones. Intente nuevamente.");
            }
        },
        async updateSettings() {
            try {
                await apiClient.put("/user-settings", this.settings);
                alert("Configuraciones guardadas correctamente.");
                this.$router.go(-1);
            } catch (error) {
                console.error("Error al guardar configuraciones:", error);
                alert("Error al guardar configuraciones. Intente nuevamente.");
            }
        },
        cancel() {
            this.$router.go(-1);
        },
    },
    mounted() {
        this.fetchSettings();
    },
};
</script>

<style scoped>
.user-settings-page {
    padding: 20px;
    max-width: 600px;
    margin: auto;
    font-family: Arial, sans-serif;
    background: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

h1 {
    text-align: center;
    color: #333;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    font-weight: bold;
    margin-bottom: 5px;
    color: #555;
}

input[type="number"],
input[type="checkbox"] {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
    font-size: 1rem;
}

input[type="checkbox"] {
    width: auto;
    display: inline-block;
    margin-right: 10px;
}

.buttons {
    display: flex;
    justify-content: space-between;
    gap: 10px;
}

.btn-save,
.btn-cancel {
    flex: 1;
}

.btn-save {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 10px 15px;
    font-size: 1rem;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.btn-cancel {
    background-color: #dc3545;
    color: white;
    border: none;
    padding: 10px 15px;
    font-size: 1rem;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.btn-cancel:hover {
    background-color: #c82333;
}
</style>
