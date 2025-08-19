<template>
    <div class="form-page-container">
        <PageHeader 
            :title="isEditMode ? 'Editar Jugador' : 'Añadir Jugador'" 
            show-back-button 
            @back="goBack" 
        />
        <BaseCard>
            <form @submit.prevent="submitForm">
                <div class="form-grid">
                    <BaseInput v-model="player.name" label="Nombre" id="name" required />
                    <BaseInput v-model="player.surnameFirst" label="Primer Apellido" id="surnameFirst" required />
                    <BaseInput v-model="player.surnameSecond" label="Segundo Apellido" id="surnameSecond" />
                    <BaseInput v-model="player.birthDate" label="Fecha de Nacimiento" id="birthDate" type="date" required />
                    <BaseInput v-model.number="player.number" label="Dorsal" id="number" type="number" min="1" max="99" required />
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
                notificationService.showError("Error al cargar las opciones del formulario.");
            }
        },
        async fetchPlayer() {
            try {
                const playerId = this.$route.params.playerId;
                const response = await apiClient.get(`/players/${playerId}`);
                // El backend ya nos da el teamId, lo usamos directamente
                this.player = response.data;
            } catch (error) {
                notificationService.showError("Error al cargar los datos del jugador.");
            }
        },
        async submitForm() {
            this.isSaving = true;
            try {
                if (this.isEditMode) {
                    await apiClient.put(`/players/${this.player.id}`, this.player);
                    notificationService.showSuccess('Jugador actualizado con éxito');
                    this.$router.push({ name: "PlayerDetails", params: { playerId: this.player.id } });
                } else {
                    await apiClient.post("/players", this.player);
                    notificationService.showSuccess('Jugador añadido con éxito');
                    this.$router.push({ name: "TeamPlayers", params: { teamId: this.player.team.id } });
                }
            } catch (error) {
                // El interceptor de api.js se encarga de mostrar el error
            } finally {
                this.isSaving = false;
            }
        },
        goBack() {
            const teamId = this.isEditMode ? this.player.team.id : this.$route.params.teamId;
            this.$router.push({ name: "TeamPlayers", params: { teamId: teamId } });
        },
    },
    created() {
        this.isEditMode = !!this.$route.params.playerId;
        this.fetchEnums();
        if (this.isEditMode) {
            this.fetchPlayer();
        }
    },
};
</script>

<style scoped>
.form-page-container {
  max-width: 900px;
  margin: 0 auto;
}
.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: var(--spacing-5);
}
.form-actions {
  margin-top: var(--spacing-6);
  display: flex;
  justify-content: flex-end;
}
</style>