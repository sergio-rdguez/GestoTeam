import api from './api';

export const fileService = {
  /**
   * Sube una imagen para un ejercicio específico
   * @param {number} exerciseId - ID del ejercicio
   * @param {File|Blob} file - Archivo de imagen a subir
   * @returns {Promise<string>} URL pública de la imagen subida
   */
  async uploadExerciseImage(exerciseId, file) {
    const formData = new FormData();
    formData.append('file', file, 'exercise-image.png');

    const response = await api.post(`/files/exercises/${exerciseId}/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  /**
   * Convierte un canvas HTML a un Blob
   * @param {HTMLCanvasElement} canvas - Canvas a convertir
   * @param {string} mimeType - Tipo MIME de la imagen (por defecto 'image/png')
   * @param {number} quality - Calidad de la imagen (0-1, solo para JPEG)
   * @returns {Promise<Blob>} Blob de la imagen
   */
  async canvasToBlob(canvas, mimeType = 'image/png', quality = 1.0) {
    return new Promise((resolve) => {
      canvas.toBlob(resolve, mimeType, quality);
    });
  },

  /**
   * Convierte un canvas HTML a un archivo File
   * @param {HTMLCanvasElement} canvas - Canvas a convertir
   * @param {string} filename - Nombre del archivo
   * @param {string} mimeType - Tipo MIME de la imagen (por defecto 'image/png')
   * @param {number} quality - Calidad de la imagen (0-1, solo para JPEG)
   * @returns {Promise<File>} Archivo de la imagen
   */
  async canvasToFile(canvas, filename, mimeType = 'image/png', quality = 1.0) {
    const blob = await this.canvasToBlob(canvas, mimeType, quality);
    return new File([blob], filename, { type: mimeType });
  },

  /**
   * Obtiene la URL de una imagen de ejercicio
   * @param {string} imagePath - Ruta de la imagen
   * @returns {string} URL completa de la imagen
   */
  getExerciseImageUrl(imagePath) {
    if (!imagePath) return null;
    return `${process.env.VUE_APP_API_URL || ''}/api/files/${imagePath}`;
  }
};
