package com.gestoteam.controller;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Player;
import com.gestoteam.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final PlayerRepository playerRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/player/{playerId}")
    public ResponseEntity<String> uploadPlayerPhoto(@PathVariable Long playerId, @RequestParam("file") MultipartFile file) {
        String username = getCurrentUsername();
        log.info("Subiendo foto para jugador {} por usuario {}", playerId, username);

        Player player = playerRepository.findByIdAndDeletedFalse(playerId)
                .filter(p -> p.getTeam().getOwnerId().equals(username))
                .orElseThrow(() -> new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él."));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El fichero está vacío");
        }

        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0) {
                extension = originalFilename.substring(dot);
            }
            String newFilename = "player-" + playerId + "-" + UUID.randomUUID() + extension;

            Path uploadDir = Paths.get("uploads", "players");
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = Paths.get("players", newFilename).toString().replace('\\', '/');
            player.setPhotoPath(relativePath);
            playerRepository.save(player);

            String publicUrl = "/files/" + relativePath;
            return ResponseEntity.status(HttpStatus.CREATED).body(publicUrl);
        } catch (IOException ex) {
            log.error("Error guardando fichero de foto para jugador {}", playerId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el fichero");
        }
    }
}


