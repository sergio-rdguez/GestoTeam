import api from './api';

class ExerciseService {
  async getAllExercises() {
    try {
      const response = await api.get('/exercises');
      return response.data;
    } catch (error) {
      console.error('Error fetching exercises:', error);
      throw error;
    }
  }

  async getExerciseById(id) {
    try {
      const response = await api.get(`/exercises/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching exercise:', error);
      throw error;
    }
  }

  async createExercise(exerciseData) {
    try {
      const response = await api.post('/exercises', exerciseData);
      return response.data;
    } catch (error) {
      console.error('Error creating exercise:', error);
      throw error;
    }
  }

  async updateExercise(id, exerciseData) {
    try {
      const response = await api.put(`/exercises/${id}`, exerciseData);
      return response.data;
    } catch (error) {
      console.error('Error updating exercise:', error);
      throw error;
    }
  }

  async deleteExercise(id) {
    try {
      await api.delete(`/exercises/${id}`);
    } catch (error) {
      console.error('Error deleting exercise:', error);
      throw error;
    }
  }

  async getExercisesByCategory(category) {
    try {
      const response = await api.get(`/exercises/category/${category}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching exercises by category:', error);
      throw error;
    }
  }
}

export default new ExerciseService();
