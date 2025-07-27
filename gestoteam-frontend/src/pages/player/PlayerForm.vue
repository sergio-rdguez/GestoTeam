<template>
    <div class="player-form-page">
        <h2>{{ isEditMode ? "Editar Jugador" : "Añadir Jugador" }}</h2>
        <form @submit.prevent="submitForm">
            <div class="form-group">
                <label for="name">Nombre</label>
                <input type="text" v-model="player.name" id="name" required />
            </div>

            <div class="form-group">
                <label for="surnameFirst">Primer Apellido</label>
                <input type="text" v-model="player.surnameFirst" id="surnameFirst" required />
            </div>

            <div class="form-group">
                <label for="surnameSecond">Segundo Apellido</label>
                <input type="text" v-model="player.surnameSecond" id="surnameSecond" required />
            </div>

            <div class="form-group">
                <label for="birthDate">Fecha de Nacimiento</label>
                <input type="date" v-model="player.birthDate" id="birthDate" required />
            </div>

            <div class="form-group">
                <label for="number">Número</label>
                <input type="number" v-model="player.number" id="number" min="1" max="99" required />
            </div>

            <div class="form-group">
                <label for="position">Posición</label>
                <select v-model="player.position" id="position" required>
                    <option value="" disabled>Selecciona una posición</option>
                    <option v-for="pos in positions" :key="pos.code" :value="pos.code">
                        {{ pos.description }}
                    </option>
                </select>
            </div>

            <div class="form-group">
                <label for="status">Estado</label>
                <select v-model="player.status" id="status" required>
                    <option value="" disabled>Selecciona un estado</option>
                    <option v-for="status in statuses" :key="status.code" :value="status.code">
                        {{ status.description }}
                    </option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">{{ isEditMode ? "Guardar Cambios" : "Guardar" }}</button>
            <button type="button" @click="cancel" class="btn btn-secondary">Cancelar</button>
        </form>
    </div>
</template>

<script>
import apiClient from "@/services/api";
// ELIMINAMOS: import { getAudit } from "@/services/audit";

export default {
    data() {
        return {
            player: {
                id: null,
                name: "",
                surnameFirst: "",
                surnameSecond: "",
                birthDate: null,
                number: null,
                position: "",
                status: "",
                teamId: this.$route.params.teamId,
            },
            positions: [],
            statuses: [],
            isEditMode: false,
        };
    },
    created() {
        this.isEditMode = !!this.$route.params.id;
        this.fetchEnums();
        if (this.isEditMode) {
            this.fetchPlayer();
        }
    },
    methods: {
        async fetchEnums() {
            try {
                const [positionsResponse, statusesResponse] = await Promise.all([
                    apiClient.get("/enums/positions"),
                    apiClient.get("/enums/player-status"),
                ]);
                this.positions = positionsResponse.data;
                this.statuses = statusesResponse.data;
            } catch (error) {
                console.error("Error al cargar enums:", error);
                alert("No se pudieron cargar las posiciones o estados. Intenta recargar la página.");
            }
        },
        async fetchPlayer() {
            try {
                const playerId = this.$route.params.id;
                const response = await apiClient.get(`/players/${playerId}`);
                this.player = { ...response.data, teamId: response.data.team?.id || this.player.teamId };
            } catch (error) {
                console.error("Error al cargar el jugador:", error);
                alert("No se pudo cargar el jugador. Intenta recargar la página.");
            }
        },
        async submitForm() {
            try {
                if (this.isEditMode) {
                    await apiClient.put(`/players/${this.player.id}`, this.player);
                    this.$router.push({ name: "PlayerDetails", params: { id: this.player.id } });
                } else {
                    await apiClient.post("/players", this.player);
                    this.$router.push({ name: "TeamPlayers", params: { id: this.player.teamId } });
                }
            } catch (error) {
                console.error("Error al guardar el jugador:", error);
            }
        },
        cancel() {
            // Navegamos de vuelta a la lista de jugadores del equipo correcto
            const teamId = this.isEditMode ? this.player.team.id : this.player.teamId;
            this.$router.push({ name: "TeamPlayers", params: { id: teamId } });
        },
    },
};
</script>

<style scoped>
/* Los estilos se mantienen igual */
.player-form-page {
    padding: 20px;
    font-family: 'Arial', sans-serif;
    background: #f9f9f9;
    min-height: 100vh;
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #333;
}

form {
    max-width: 500px;
    margin: 0 auto;
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #666;
}

input,
select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
}

button {
    padding: 10px 20px;
    border: none;
    border-radius: 8px;
    font-size: 1rem;
    cursor: pointer;
    margin-right: 10px;
}

button.btn-primary {
    background-color: #007bff;
    color: white;
}

button.btn-secondary {
    background-color: #6c757d;
    color: white;
}
</style>