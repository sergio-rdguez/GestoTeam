import api from './api';

export const trainingService = {
  /**
   * Obtiene todos los entrenamientos del usuario autenticado
   */
  async getTrainings() {
    const response = await api.get('/trainings');
    return response.data;
  },

  /**
   * Obtiene un entrenamiento específico por ID
   */
  async getTrainingById(id) {
    const response = await api.get(`/trainings/${id}`);
    return response.data;
  },

  /**
   * Crea un nuevo entrenamiento
   */
  async createTraining(trainingData) {
    const response = await api.post('/trainings', trainingData);
    return response.data;
  },

  /**
   * Actualiza un entrenamiento existente
   */
  async updateTraining(id, trainingData) {
    const response = await api.put(`/trainings/${id}`, trainingData);
    return response.data;
  },

  /**
   * Elimina un entrenamiento (borrado lógico)
   */
  async deleteTraining(id) {
    await api.delete(`/trainings/${id}`);
  },

  /**
   * Obtiene la lista de asistencia de un entrenamiento
   */
  async getTrainingAttendance(trainingId) {
    const response = await api.get(`/trainings/${trainingId}/attendance`);
    return response.data;
  },

  /**
   * Actualiza la asistencia de un jugador en un entrenamiento
   */
  async updatePlayerAttendance(trainingId, playerId, attendanceData) {
    const response = await api.put(`/trainings/${trainingId}/attendance/${playerId}`, attendanceData);
    return response.data;
  },

  /**
   * Añade ejercicios a un entrenamiento
   */
  async addExercisesToTraining(trainingId, exerciseIds) {
    const response = await api.post(`/trainings/${trainingId}/exercises`, exerciseIds);
    return response.data;
  },

  /**
   * Elimina ejercicios de un entrenamiento
   */
  async removeExercisesFromTraining(trainingId, exerciseIds) {
    const response = await api.delete(`/trainings/${trainingId}/exercises`, { data: exerciseIds });
    return response.data;
  },

  /**
   * Obtiene los ejercicios de un entrenamiento
   */
  async getTrainingExercises(trainingId) {
    const response = await api.get(`/trainings/${trainingId}/exercises`);
    return response.data;
  }
};
