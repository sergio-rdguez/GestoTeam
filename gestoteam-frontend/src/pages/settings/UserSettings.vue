<template>
    <div class="form-page">
        <PageHeader title="Configuración de Usuario" show-back-button @back="cancel" />

        <BaseCard>
            <form @submit.prevent="updateSettings" v-if="!loading">
                <div class="form-grid">
                    <BaseInput
                        v-model.number="settings.maxPlayersPerTeam"
                        label="Máximo de jugadores en plantilla"
                        type="number"
                        id="maxPlayersPerTeam"
                        min="1"
                        required
                    />
                    <BaseInput
                        v-model.number="settings.maxPlayersPerMatch"
                        label="Máximo de jugadores convocados"
                        type="number"
                        id="maxPlayersPerMatch"
                        min="1"
                        required
                    />
                    <BaseInput
                        v-model.number="settings.yellowCardsForSuspension"
                        label="Tarjetas amarillas para sanción"
                        type="number"
                        id="yellowCardsForSuspension"
                        min="1"
                        required
                    />
                     <BaseInput
                        v-model.number="settings.maxTrainingsPerWeek"
                        label="Máximo de entrenamientos por semana"
                        type="number"
                        id="maxTrainingsPerWeek"
                        min="1"
                        required
                    />
                </div>
                <div class="checkbox-grid">
                    <BaseCheckbox
                        v-model="settings.notifyBeforeMatch"
                        label="Notificar antes de un partido"
                        id="notifyBeforeMatch"
                    />
                    <BaseCheckbox
                        v-model="settings.notifyBeforeTraining"
                        label="Notificar antes de un entrenamiento"
                        id="notifyBeforeTraining"
                    />
                </div>

                <div class="form-actions">
                    <BaseButton type="submit" variant="primary" :loading="isSaving">
                        Guardar Configuración
                    </BaseButton>
                </div>
            </form>
             <div v-else class="loading-container">
                <LoadingSpinner message="Cargando configuración..." />
            </div>
        </BaseCard>
    </div>
</template>

<script>
import api from "@/services/api";
import PageHeader from "@/components/layout/PageHeader.vue";
import BaseCard from "@/components/base/BaseCard.vue";
import BaseInput from "@/components/base/BaseInput.vue";
import BaseButton from "@/components/base/BaseButton.vue";
import BaseCheckbox from "@/components/base/BaseCheckbox.vue";
import LoadingSpinner from "@/components/common/LoadingSpinner.vue";

export default {
    components: {
        PageHeader,
        BaseCard,
        BaseInput,
        BaseButton,
        BaseCheckbox,
        LoadingSpinner,
    },
    data() {
        return {
            loading: true,
            isSaving: false,
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
            this.loading = true;
            try {
                const response = await api.get("/user-settings");
                this.settings = response.data;
            } catch (error) {
                console.error("Error al obtener configuraciones:", error);
            } finally {
                this.loading = false;
            }
        },
        async updateSettings() {
            this.isSaving = true;
            try {
                await api.put("/user-settings", this.settings);
                // Idealmente, aquí mostraríamos un toast/snackbar de éxito
                this.$router.go(-1);
            } catch (error) {
                console.error("Error al guardar configuraciones:", error);
            } finally {
                this.isSaving = false;
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
.form-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 2rem;
}
.checkbox-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
    margin-bottom: 2rem;
}
.form-actions {
  margin-top: 1.5rem;
}
.loading-container {
    min-height: 200px;
    display: flex;
    justify-content: center;
    align-items: center;
}
@media (max-width: 768px) {
    .form-grid, .checkbox-grid {
        grid-template-columns: 1fr;
    }
}
</style>