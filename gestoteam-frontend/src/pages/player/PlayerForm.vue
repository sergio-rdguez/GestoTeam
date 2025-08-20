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
                    <div class="file-upload">
                        <label for="photoFile">Foto del Jugador</label>
                        <div v-if="currentPhotoUrl" class="current-photo">
                            <img :src="currentPhotoUrl" alt="Foto actual" class="preview-photo" />
                            <small>Foto actual</small>
                        </div>
                        <input id="photoFile" type="file" accept="image/*" @change="onFileSelected" />
                        <small class="form-help">Formatos soportados: JPG, PNG, GIF, WEBP. Tamaño máximo: 5MB</small>
                    </div>
                    <BaseInput v-model="player.birthDate" label="Fecha de Nacimiento" id="birthDate" type="date" required />
                    <BaseInput v-model.number="player.number" label="Dorsal" id="number" type="number" min="1" max="99" required />
                    <BaseSelect v-model="player.position" label="Posición" id="position" :options="positionOptions" required />
                    <BaseSelect v-model="player.foot" label="Pie" id="foot" :options="footOptions" required />
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
import api from "@/services/api";
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
                birthDate: "",
                number: null,
                position: "",
                foot: "DIESTRO",
                status: "ACTIVO",
                teamId: this.$route.params.teamId,
            },
            positions: [],
            foots: [],
            statuses: [],
            isEditMode: false,
            isSaving: false,
            selectedFile: null,
            currentPhotoUrl: null,
        };
    },
    computed: {
        positionOptions() {
            return this.positions.map(p => ({ value: p.code, text: p.description }));
        },
        footOptions() {
            return this.foots.map(f => ({ value: f.code, text: f.description }));
        },
        statusOptions() {
            return this.statuses.map(s => ({ value: s.code, text: s.description }));
        }
    },
    methods: {
        async fetchEnums() {
            try {
                const [positionsResponse, footsResponse, statusesResponse] = await Promise.all([
                    api.get("/enums/positions"),
                    api.get("/enums/foots"),
                    api.get("/enums/player-status"),
                ]);
                this.positions = positionsResponse.data;
                this.foots = footsResponse.data;
                this.statuses = statusesResponse.data;
            } catch (error) {
                notificationService.showError("Error al cargar las opciones del formulario.");
            }
        },
        async fetchPlayer() {
            try {
                const playerId = this.$route.params.playerId;
                const response = await api.get(`/players/${playerId}`);
                
                // Mapear los datos del jugador
                this.player = {
                    id: response.data.id,
                    name: response.data.name || "",
                    surnameFirst: response.data.surnameFirst || "",
                    surnameSecond: response.data.surnameSecond || "",
                    birthDate: response.data.birthDate ? response.data.birthDate.split('T')[0] : "",
                    number: response.data.number || null,
                    position: response.data.position || "",
                    foot: response.data.foot || "DIESTRO",
                    status: response.data.status || "ACTIVO",
                    teamId: response.data.team ? response.data.team.id : this.$route.params.teamId,
                    team: response.data.team
                };
                
                // Asegurar que siempre tengamos un teamId válido
                if (!this.player.teamId && this.$route.params.teamId) {
                    this.player.teamId = this.$route.params.teamId;
                }
                
                // Construir la URL de la foto actual
                if (response.data.photoUrl) {
                    this.currentPhotoUrl = response.data.photoUrl;
                }
                
                console.log("Player data loaded:", this.player);
            } catch (error) {
                notificationService.showError("Error al cargar los datos del jugador.");
                console.error("Error fetching player:", error);
            }
        },
        onFileSelected(event) {
            const files = event.target.files;
            this.selectedFile = files && files[0] ? files[0] : null;
            
            // Mostrar preview de la nueva imagen seleccionada
            if (this.selectedFile) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.currentPhotoUrl = e.target.result;
                };
                reader.readAsDataURL(this.selectedFile);
            }
        },
        async submitForm() {
            this.isSaving = true;
            try {
                // Validar que los campos requeridos estén presentes
                if (!this.player.name || !this.player.surnameFirst || !this.player.birthDate || 
                    !this.player.position || !this.player.status || !this.player.teamId) {
                    notificationService.showError("Por favor, completa todos los campos obligatorios.");
                    return;
                }

                const payload = {
                    name: this.player.name.trim(),
                    surnameFirst: this.player.surnameFirst.trim(),
                    surnameSecond: this.player.surnameSecond ? this.player.surnameSecond.trim() : null,
                    birthDate: this.player.birthDate,
                    number: this.player.number,
                    position: this.player.position,
                    foot: this.player.foot,
                    status: this.player.status,
                    teamId: this.player.teamId
                };

                if (this.isEditMode) {
                    await api.put(`/players/${this.player.id}`, payload);
                    
                    // Si se seleccionó una foto, subirla
                    if (this.selectedFile) {
                        const formData = new FormData();
                        formData.append('file', this.selectedFile);
                        await api.post(`/files/player/${this.player.id}`, formData, {
                            headers: { 'Content-Type': 'multipart/form-data' }
                        });
                    }
                    
                    notificationService.showSuccess('Jugador actualizado con éxito');
                    // Redirigir a PlayerDetails con el teamId
                    this.$router.push({ 
                        name: "PlayerDetails", 
                        params: { 
                            playerId: this.player.id 
                        } 
                    });
                } else {
                    // Crear el jugador
                    const response = await api.post("/players", payload);
                    
                    // Verificar que la respuesta sea válida
                    if (!response || !response.data) {
                        throw new Error("Respuesta inválida del servidor al crear el jugador");
                    }
                    
                    // El backend ahora devuelve directamente el ID del jugador creado
                    const newPlayerId = response.data;
                    
                    console.log("ID del jugador creado:", newPlayerId);
                    
                    if (!newPlayerId) {
                        throw new Error("No se pudo obtener el ID del jugador creado");
                    }
                    
                    // Si se seleccionó una foto, subirla
                    if (this.selectedFile) {
                        const formData = new FormData();
                        formData.append('file', this.selectedFile);
                        
                        console.log("Subiendo foto para el jugador ID:", newPlayerId);
                        
                        await api.post(`/files/player/${newPlayerId}`, formData, {
                            headers: { 'Content-Type': 'multipart/form-data' }
                        });
                        
                        console.log("Foto subida exitosamente");
                    }
                    
                    notificationService.showSuccess('Jugador añadido con éxito');
                    // Redirigir a TeamPlayers con el teamId
                    this.$router.push({ 
                        name: "TeamPlayers", 
                        params: { 
                            teamId: this.player.teamId 
                        } 
                    });
                }
            } catch (error) {
                console.error("Error submitting form:", error);
                if (error.response && error.response.data && error.response.data.message) {
                    notificationService.showError(error.response.data.message);
                } else {
                    notificationService.showError("Error al guardar el jugador. Por favor, inténtalo de nuevo.");
                }
            } finally {
                this.isSaving = false;
            }
        },
        goBack() {
            // Intentar obtener el teamId de múltiples fuentes
            let teamId = null;
            
            // 1. Del objeto player actual
            if (this.player && this.player.teamId) {
                teamId = this.player.teamId;
            }
            // 2. Del objeto team si existe
            else if (this.player && this.player.team && this.player.team.id) {
                teamId = this.player.team.id;
            }
            // 3. De los parámetros de la ruta
            else if (this.$route.params.teamId) {
                teamId = this.$route.params.teamId;
            }
            
            if (teamId) {
                this.$router.push({ 
                    name: "TeamPlayers", 
                    params: { teamId: teamId } 
                });
            } else {
                console.error("No se puede volver: falta teamId");
                // Fallback: ir a la lista de equipos
                this.$router.push({ name: "Teams" });
            }
        },
    },
    created() {
        this.isEditMode = !!this.$route.params.playerId;
        
        // Asegurar que siempre tengamos un teamId válido desde el inicio
        if (!this.player.teamId && this.$route.params.teamId) {
            this.player.teamId = this.$route.params.teamId;
        }
        
        console.log("PlayerForm created - Edit mode:", this.isEditMode, "TeamId:", this.player.teamId);
        
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

.file-upload {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-2);
}

.file-upload label {
  font-weight: 600;
  color: var(--text-primary);
}

.file-upload input[type="file"] {
  padding: var(--spacing-2);
  border: 2px dashed var(--border-color);
  border-radius: var(--border-radius);
  background-color: var(--background-secondary);
  cursor: pointer;
}

.file-upload input[type="file"]:hover {
  border-color: var(--primary-color);
  background-color: var(--background-hover);
}

.form-help {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-top: var(--spacing-1);
}

.current-photo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-2);
  margin-bottom: var(--spacing-3);
}

.preview-photo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid var(--border-color);
  object-fit: cover;
}

.current-photo small {
  color: var(--text-secondary);
  font-size: 0.75rem;
}
</style>