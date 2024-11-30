package com.gestoteam.service;

import com.gestoteam.enums.Category;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import com.gestoteam.exception.GestoServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnumService {

    public List<EnumResponse> getPlayerStatuses() {
        log.info("Obteniendo estados de jugador...");
        try {
            List<EnumResponse> statuses = Arrays.stream(PlayerStatus.values())
                    .map(status -> new EnumResponse(status.name(), status.getDescripcion()))
                    .collect(Collectors.toList());
            log.info("Se obtuvieron {} estados de jugador.", statuses.size());
            return statuses;
        } catch (Exception e) {
            log.error("Error al obtener los estados de jugador.", e);
            throw new GestoServiceException("No se pudieron obtener los estados de jugador.");
        }
    }

    public List<EnumResponse> getPositions() {
        log.info("Obteniendo posiciones de jugador...");
        try {
            List<EnumResponse> positions = Arrays.stream(Position.values())
                    .map(position -> new EnumResponse(position.name(), position.getDescripcion()))
                    .collect(Collectors.toList());
            log.info("Se obtuvieron {} posiciones de jugador.", positions.size());
            return positions;
        } catch (Exception e) {
            log.error("Error al obtener las posiciones de jugador.", e);
            throw new GestoServiceException("No se pudieron obtener las posiciones de jugador.");
        }
    }

    public List<EnumResponse> getCategories() {
        log.info("Obteniendo categorías...");
        try {
            List<EnumResponse> categories = Arrays.stream(Category.values())
                    .map(category -> new EnumResponse(category.name(), category.getName()))
                    .collect(Collectors.toList());
            log.info("Se obtuvieron {} categorías.", categories.size());
            return categories;
        } catch (Exception e) {
            log.error("Error al obtener las categorías.", e);
            throw new GestoServiceException("No se pudieron obtener las categorías.");
        }
    }

    public static class EnumResponse {
        private final String code;
        private final String description;

        public EnumResponse(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
