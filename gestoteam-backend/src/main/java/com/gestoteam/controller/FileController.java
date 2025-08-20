package com.gestoteam.controller;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Archivos", description = "API para gestión de archivos e imágenes")
public class FileController {

    private final FileService fileService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Long getCurrentUserId() {
        String username = getCurrentUsername();
        return fileService.getCurrentUserId(username);
    }

    @PostMapping("/player/{playerId}")
    @Operation(
        summary = "Subir foto de jugador",
        description = "Sube una foto para un jugador específico. Solo el propietario del equipo puede subir fotos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Foto subida exitosamente",
            content = @Content(schema = @Schema(example = "/api/files/players/player-1-uuid.png"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Archivo vacío o inválido"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "No tienes permisos para acceder a este jugador"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Jugador no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<String> uploadPlayerPhoto(
        @Parameter(description = "ID del jugador", required = true) @PathVariable Long playerId,
        @Parameter(description = "Archivo de imagen (JPG, PNG, GIF, WEBP)", required = true) @RequestParam("file") MultipartFile file
    ) {
        try {
            if (playerId == null || playerId <= 0) {
                return ResponseEntity.badRequest().body("ID de jugador inválido");
            }
            
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo es obligatorio y no puede estar vacío");
            }
            
            Long userId = getCurrentUserId();
            String publicUrl = fileService.uploadPlayerPhoto(playerId, file, userId);
            return ResponseEntity.status(201).body(publicUrl);
            
        } catch (GestoServiceException e) {
            log.warn("Error al subir foto para jugador {}: {}", playerId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error interno al subir foto para jugador {}: {}", playerId, e.getMessage());
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    @GetMapping("/players/{filename:.+}")
    @Operation(
        summary = "Obtener foto de jugador",
        description = "Sirve una foto de jugador desde el sistema de archivos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Foto obtenida exitosamente",
            content = @Content(mediaType = "image/*")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Archivo no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Resource> servePlayerFile(
        @Parameter(description = "Nombre del archivo", required = true) @PathVariable String filename
    ) {
        log.info("Sirviendo archivo de jugador: {}", filename);
        
        try {
            Resource resource = fileService.servePlayerFile(filename);
            String contentType = fileService.determineContentType(filename);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (Exception e) {
            log.error("Error al servir archivo de jugador {}: {}", filename, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{filename:.+}")
    @Operation(
        summary = "Obtener archivo genérico",
        description = "Sirve un archivo genérico desde el sistema de archivos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Archivo obtenido exitosamente",
            content = @Content(mediaType = "application/octet-stream")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Archivo no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Resource> serveFile(
        @Parameter(description = "Nombre del archivo", required = true) @PathVariable String filename
    ) {
        log.info("Sirviendo archivo genérico: {}", filename);
        
        try {
            Resource resource = fileService.serveFile(filename);
            String contentType = fileService.determineContentType(filename);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (Exception e) {
            log.error("Error al servir archivo genérico {}: {}", filename, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}


