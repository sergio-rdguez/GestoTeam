package com.gestoteam.util;

import com.gestoteam.dto.Audit;
import com.gestoteam.model.Team;
import com.gestoteam.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationUtil {

    private final AuditUtil auditUtil;

    public Audit validateAudit(String audit) {
        try {
            return auditUtil.decryptAudit(audit);
        } catch (Exception e) {
            log.error("Audit inválido: {}", audit, e);
            throw new RuntimeException("Audit inválido, acceso no autorizado");
        }
    }

    public void validateOwner(String ownerId, Team team) {
        if (!team.getOwnerId().equals(ownerId)) {
            log.warn("Acceso denegado: El usuario {} no es el propietario del equipo con ID {}", ownerId, team.getId());
            throw new RuntimeException("Acceso no autorizado al equipo");
        }
    }
}
