import api from './api';

export const exerciseService = {
  /**
   * Obtiene todos los ejercicios del usuario autenticado
   */
  async getExercises() {
    const response = await api.get('/exercises');
    return response.data;
  },

  /**
   * Obtiene un ejercicio específico por ID
   */
  async getExerciseById(id) {
    const response = await api.get(`/exercises/${id}`);
    return response.data;
  },

  /**
   * Crea un nuevo ejercicio
   */
  async createExercise(exerciseData) {
    const response = await api.post('/exercises', exerciseData);
    return response.data;
  },

  /**
   * Actualiza un ejercicio existente
   */
  async updateExercise(id, exerciseData) {
    const response = await api.put(`/exercises/${id}`, exerciseData);
    return response.data;
  },

  /**
   * Elimina un ejercicio (borrado lógico)
   */
  async deleteExercise(id) {
    await api.delete(`/exercises/${id}`);
  },

  /**
   * Obtiene las categorías de ejercicios disponibles
   */
  async getExerciseCategories() {
    const response = await api.get('/enums/exercise-categories');
    return response.data;
  }
};
