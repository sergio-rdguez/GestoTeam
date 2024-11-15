import CryptoJS from 'crypto-js';

const SECRET_KEY = "b7E3aX8Zp4Q2r9Wc"; // Asegúrate de que coincide con el backend

export const getAudit = () => {
  // Audit en formato JSON
  const audit = {
    user: "admin",
  };

  // Convertir el JSON a una cadena y encriptar
  const auditString = JSON.stringify(audit);
  const encryptedAudit = CryptoJS.AES.encrypt(auditString, CryptoJS.enc.Utf8.parse(SECRET_KEY), {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  }).toString();

  return encryptedAudit;
};
