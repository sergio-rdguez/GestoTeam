// Utilidades para manejar URLs de imágenes
const API_BASE_URL = 'http://localhost:8081';

/**
 * Construye la URL completa de una imagen
 * @param {string} photoUrl - URL relativa de la imagen (ej: /api/files/players/...)
 * @returns {string} URL completa de la imagen
 */
export function buildImageUrl(photoUrl) {
  if (!photoUrl) return null;
  
  // Si ya es una URL completa, devolverla tal como está
  if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) {
    return photoUrl;
  }
  
  // Si empieza con /api, construir la URL completa
  if (photoUrl.startsWith('/api/')) {
    return `${API_BASE_URL}${photoUrl}`;
  }
  
  // Si no empieza con /api, asumir que es relativa y añadir /api
  return `${API_BASE_URL}/api${photoUrl}`;
}

/**
 * Verifica si una imagen existe
 * @param {string} photoUrl - URL de la imagen
 * @returns {Promise<boolean>} true si la imagen existe, false en caso contrario
 */
export function imageExists(photoUrl) {
  if (!photoUrl) return Promise.resolve(false);
  
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => resolve(true);
    img.onerror = () => resolve(false);
    img.src = buildImageUrl(photoUrl);
  });
}
