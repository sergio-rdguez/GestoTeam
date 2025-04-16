package com.gestoteam.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestoteam.dto.Audit;
import com.gestoteam.exception.InvalidAuditException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${audit.secret-key}")
    private String secretKey;

    public Audit decryptAudit(String encryptedAudit) {
        try {
            String decryptedJson = decrypt(encryptedAudit);
            return objectMapper.readValue(decryptedJson, Audit.class);
        } catch (Exception e) {
            log.error("Error al desencriptar o convertir el audit: ", e);
            throw new InvalidAuditException("Audit inválido, usuario no autorizado");
        }
    }

    private String decrypt(String encryptedAudit) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decodedValue = Base64.getDecoder().decode(encryptedAudit);
            byte[] decryptedValue = cipher.doFinal(decodedValue);

            return new String(decryptedValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error al desencriptar datos del audit: ", e);
            throw new InvalidAuditException("Audit inválido, usuario no autorizado");
        }
    }
}
