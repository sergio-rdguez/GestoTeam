import CryptoJS from "crypto-js";

const SECRET_KEY = "b7E3aX8Zp4Q2r9Wc"; // Asegúrate de que coincide con el backend

export const getAudit = () => {
  // Obtener el usuario del almacenamiento local
  const user = localStorage.getItem("audit_user");

  if (!user) {
    console.warn("No se encontró un usuario autenticado para el audit.");
    return null; // Si no hay usuario, devuelve null
  }

  // Crear el objeto audit
  const audit = {
    user: user,
  };

  // Convertir el JSON a una cadena y encriptar
  const auditString = JSON.stringify(audit);
  const encryptedAudit = CryptoJS.AES.encrypt(auditString, CryptoJS.enc.Utf8.parse(SECRET_KEY), {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  }).toString();

  return encryptedAudit;
};
