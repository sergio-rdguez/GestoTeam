package com.gestoteam.service;

import com.gestoteam.dto.request.TacticalDiagramRequest;
import com.gestoteam.dto.response.TacticalDiagramResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.TacticalDiagram;
import com.gestoteam.model.User;
import com.gestoteam.repository.TacticalDiagramRepository;
import com.gestoteam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TacticalDiagramService {

    private final TacticalDiagramRepository tacticalDiagramRepository;
    private final UserRepository userRepository;

    /**
     * Obtiene todos los diagramas tácticos de un usuario
     */
    public List<TacticalDiagramResponse> getTacticalDiagramsByUser(Long userId) {
        log.info("Obteniendo diagramas tácticos para usuario {}", userId);
        
        List<TacticalDiagram> diagrams = tacticalDiagramRepository.findByCreatedByIdAndDeletedFalse(userId);
        
        return diagrams.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene diagramas recientes de un usuario
     */
    public List<TacticalDiagramResponse> getRecentTacticalDiagrams(Long userId, int limit) {
        log.info("Obteniendo {} diagramas tácticos recientes para usuario {}", limit, userId);
        
        List<TacticalDiagram> diagrams = tacticalDiagramRepository.findRecentByUser(userId);
        
        return diagrams.stream()
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapea un TacticalDiagram a TacticalDiagramResponse
     */
    private TacticalDiagramResponse mapToResponse(TacticalDiagram diagram) {
        return TacticalDiagramResponse.builder()
                .id(diagram.getId())
                .title(diagram.getTitle())
                .description(diagram.getDescription())
                .imageUrl(diagram.getFilePath())
                .createdAt(diagram.getCreatedAt())
                .updatedAt(diagram.getUpdatedAt())
                .createdByUsername(diagram.getCreatedBy() != null ? diagram.getCreatedBy().getUsername() : "Usuario")
                .createdById(diagram.getCreatedBy() != null ? diagram.getCreatedBy().getId() : null)
                .build();
    }
}
