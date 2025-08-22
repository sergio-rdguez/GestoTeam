import api from './api';

export const tacticalDiagramService = {
  /**
   * Obtiene todos los diagramas tácticos del usuario actual
   */
  async getUserTacticalDiagrams() {
    try {
      const response = await api.get('/tactical-diagrams/user');
      return response.data;
    } catch (error) {
      console.error('Error al obtener diagramas tácticos del usuario:', error);
      throw error;
    }
  },

  /**
   * Obtiene todos los diagramas tácticos de un ejercicio específico
   */
  async getExerciseTacticalDiagrams(exerciseId) {
    try {
      const response = await api.get(`/tactical-diagrams/exercise/${exerciseId}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener diagramas tácticos del ejercicio:', error);
      throw error;
    }
  },

  /**
   * Vincula un diagrama táctico a un ejercicio
   */
  async linkDiagramToExercise(exerciseId, diagramId) {
    try {
      const response = await api.post(`/tactical-diagrams/exercise/${exerciseId}/link/${diagramId}`);
      return response.data;
    } catch (error) {
      console.error('Error al vincular diagrama al ejercicio:', error);
      throw error;
    }
  },

  /**
   * Desvincula un diagrama táctico de un ejercicio
   */
  async unlinkDiagramFromExercise(exerciseId, diagramId) {
    try {
      const response = await api.delete(`/tactical-diagrams/exercise/${exerciseId}/unlink/${diagramId}`);
      return response.data;
    } catch (error) {
      console.error('Error al desvincular diagrama del ejercicio:', error);
      throw error;
    }
  },

  /**
   * Obtiene los diagramas tácticos más recientes del usuario
   */
  async getRecentTacticalDiagrams(limit = 10) {
    try {
      const response = await api.get(`/tactical-diagrams/recent?limit=${limit}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener diagramas tácticos recientes:', error);
      throw error;
    }
  },

  /**
   * Sube un diagrama táctico como imagen
   */
  async uploadTacticalDiagram(file, title, description = '') {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', title);
      formData.append('description', description);

      const response = await api.post('/files/tactical-diagram', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      return response.data;
    } catch (error) {
      console.error('Error al subir diagrama táctico:', error);
      throw error;
    }
  }
};
