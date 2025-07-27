<template>
    <div class="form-page">
        <PageHeader 
            :title="isEditMode ? 'Editar Jugador' : 'Añadir Jugador'" 
            show-back-button 
            @back="cancel" 
        />
        <BaseCard>
            <form @submit.prevent="submitForm">
                <div class="form-grid">
                    <BaseInput v-model="player.name" label="Nombre" id="name" required />
                    <BaseInput v-model="player.surnameFirst" label="Primer Apellido" id="surnameFirst" required />
                    <BaseInput v-model="player.surnameSecond" label="Segundo Apellido" id="surnameSecond" />
                    <BaseInput v-model="player.birthDate" label="Fecha de Nacimiento" id="birthDate" type="date" required />
                    <BaseInput v-model="player.number" label="Número" id="number" type="number" min="1" max="99" required />
                    <BaseSelect v-model="player.position" label="Posición" id="position" :options="positionOptions" required />
                    <BaseSelect v-model="player.status" label="Estado" id="status" :options="statusOptions" required />
                </div>
                <div class="form-actions">
                    <BaseButton type="submit" :loading="isSaving" variant="primary">
                        {{ isSaving ? "Guardando..." : (isEditMode ? "Guardar Cambios" : "Guardar Jugador") }}
                    </BaseButton>
                </div>
            </form>
        </BaseCard>
    </div>
</template>

<script>
import { notificationService } from '@/services/notificationService';
import apiClient from "@/services/api";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseSelect from "@/components/base/BaseSelect.vue";
import BaseButton from "@/components/base/BaseButton.vue";

export default {
    components: {
        PageHeader,
        BaseCard,
        BaseInput,
        BaseSelect,
        BaseButton,
    },
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
            isSaving: false,
        };
    },
    computed: {
        positionOptions() {
            return this.positions.map(p => ({ value: p.code, text: p.description }));
        },
        statusOptions() {
            return this.statuses.map(s => ({ value: s.code, text: s.description }));
        }
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
            }
        },
        async fetchPlayer() {
            try {
                const playerId = this.$route.params.id;
                const response = await apiClient.get(`/players/${playerId}`);
                this.player = { ...response.data, teamId: response.data.team?.id || this.player.teamId };
            } catch (error) {
                console.error("Error al cargar el jugador:", error);
            }
        },
        async submitForm() {
            this.isSaving = true;
            try {
                if (this.isEditMode) {
                    await apiClient.put(`/players/${this.player.id}`, this.player);
                    this.$router.push({ name: "PlayerDetails", params: { id: this.player.id } });
                } else {
                    await apiClient.post("/players", this.player);
                    this.$router.push({ name: "TeamPlayers", params: { id: this.player.teamId } });
                }
                notificationService.showSuccess('Jugador guardado con éxito');
            } catch (error) {
                console.error("Error al guardar el jugador:", error);
            } finally {
                this.isSaving = false;
            }
        },
        cancel() {
            const teamId = this.isEditMode ? this.player.team.id : this.player.teamId;
            this.$router.push({ name: "TeamPlayers", params: { id: teamId } });
        },
    },
};
</script>

<style scoped>
.form-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
}
.form-actions {
  margin-top: 2rem;
}
</style>