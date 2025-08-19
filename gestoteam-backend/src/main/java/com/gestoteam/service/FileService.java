package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Player;
import com.gestoteam.model.User;
import com.gestoteam.repository.PlayerRepository;
import com.gestoteam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    /**
     * Obtiene el ID del usuario actual basado en el username
     * 
     * @param username Nombre de usuario
     * @return ID del usuario
     * @throws GestoServiceException si el usuario no existe
     */
    public Long getCurrentUserId(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GestoServiceException("Usuario no encontrado: " + username));
        return user.getId();
    }

    /**
     * Sube una foto para un jugador específico
     * 
     * @param playerId ID del jugador
     * @param file Archivo de imagen a subir
     * @param userId ID del usuario que realiza la subida
     * @return URL pública de la imagen subida
     * @throws GestoServiceException si el jugador no existe o no hay permisos
     */
    public String uploadPlayerPhoto(Long playerId, MultipartFile file, Long userId) {
        log.info("Subiendo foto para jugador {} por usuario {}", playerId, userId);

        Player player = playerRepository.findByIdAndDeletedFalse(playerId)
                .filter(p -> p.getTeam().getOwnerId().equals(userId))
                .orElseThrow(() -> new GestoServiceException("Jugador no encontrado o no tienes permisos para acceder a él."));

        if (file.isEmpty()) {
            throw new GestoServiceException("El fichero está vacío");
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

            String publicUrl = "/api/files/" + relativePath;
            log.info("Foto subida exitosamente para jugador {}: {}", playerId, publicUrl);
            return publicUrl;
        } catch (IOException ex) {
            log.error("Error guardando fichero de foto para jugador {}", playerId, ex);
            throw new GestoServiceException("No se pudo guardar el fichero: " + ex.getMessage());
        }
    }

    /**
     * Sirve un archivo de jugador desde el sistema de archivos
     * 
     * @param filename Nombre del archivo
     * @return Resource del archivo
     * @throws GestoServiceException si el archivo no existe o no es legible
     */
    public Resource servePlayerFile(String filename) {
        log.info("Sirviendo archivo de jugador: {}", filename);
        
        try {
            Path filePath = Paths.get("uploads", "players", filename);
            log.debug("Ruta del archivo: {}", filePath.toAbsolutePath());
            
            Resource resource = new UrlResource(filePath.toUri());
            log.debug("Resource creado: {}", resource);
            
            if (resource.exists() && resource.isReadable()) {
                log.info("Archivo encontrado y legible: {}", filename);
                return resource;
            } else {
                log.warn("Archivo no encontrado o no legible: {}", filename);
                log.debug("Resource existe: {}, Resource legible: {}", resource.exists(), resource.isReadable());
                throw new GestoServiceException("Archivo no encontrado: " + filename);
            }
        } catch (GestoServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error sirviendo archivo de jugador: {}", filename, e);
            throw new GestoServiceException("Error al servir el archivo: " + e.getMessage());
        }
    }

    /**
     * Sirve un archivo genérico desde el sistema de archivos
     * 
     * @param filename Nombre del archivo
     * @return Resource del archivo
     * @throws GestoServiceException si el archivo no existe o no es legible
     */
    public Resource serveFile(String filename) {
        log.info("Sirviendo archivo genérico: {}", filename);
        
        try {
            Path filePath = Paths.get("uploads", filename);
            log.debug("Ruta del archivo: {}", filePath.toAbsolutePath());
            
            Resource resource = new UrlResource(filePath.toUri());
            log.debug("Resource creado: {}", resource);
            
            if (resource.exists() && resource.isReadable()) {
                log.info("Archivo encontrado y legible: {}", filename);
                return resource;
            } else {
                log.warn("Archivo no encontrado o no legible: {}", filename);
                log.debug("Resource existe: {}, Resource legible: {}", resource.exists(), resource.isReadable());
                throw new GestoServiceException("Archivo no encontrado: " + filename);
            }
        } catch (GestoServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error sirviendo archivo genérico: {}", filename, e);
            throw new GestoServiceException("Error al servir el archivo: " + e.getMessage());
        }
    }

    /**
     * Determina el tipo de contenido MIME basado en la extensión del archivo
     * 
     * @param filename Nombre del archivo
     * @return Tipo de contenido MIME
     */
    public String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}
