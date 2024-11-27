package com.gestoteam.service;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumService {

    public List<EnumResponse> getPlayerStatuses() {
        return Arrays.stream(PlayerStatus.values())
                .map(status -> new EnumResponse(status.name(), status.getDescripcion()))
                .collect(Collectors.toList());
    }

    public List<EnumResponse> getPositions() {
        return Arrays.stream(Position.values())
                .map(position -> new EnumResponse(position.name(), position.getDescripcion()))
                .collect(Collectors.toList());
    }

    // Clase interna para representar la respuesta del enum
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
