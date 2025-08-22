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
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                throw new GestoServiceException("Nombre de archivo inválido");
            }
            originalFilename = StringUtils.cleanPath(originalFilename);
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
            log.info("Foto subida exitosamente para jugador {}", playerId);
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

    /**
     * Sube un diagrama táctico
     * 
     * @param file Archivo de imagen a subir
     * @param title Título del diagrama
     * @param description Descripción del diagrama
     * @param userId ID del usuario que realiza la subida
     * @return URL pública de la imagen subida
     * @throws GestoServiceException si hay error en la subida
     */
    public String uploadTacticalDiagram(MultipartFile file, String title, String description, Long userId) {
        log.info("Subiendo diagrama táctico por usuario {}", userId);

        if (file.isEmpty()) {
            throw new GestoServiceException("El fichero está vacío");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                throw new GestoServiceException("Nombre de archivo inválido");
            }
            
            String extension = getFileExtension(originalFilename);
            String newFilename = "tactical-diagram-" + UUID.randomUUID() + extension;

            Path uploadDir = Paths.get("uploads", "tactical-diagrams");
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = Paths.get("tactical-diagrams", newFilename).toString().replace('\\', '/');
            
            String publicUrl = "/api/files/tactical-diagrams/" + newFilename;
            log.info("Diagrama táctico subido exitosamente");
            return publicUrl;
        } catch (IOException ex) {
            log.error("Error guardando diagrama táctico", ex);
            throw new GestoServiceException("No se pudo guardar el fichero: " + ex.getMessage());
        }
    }

    /**
     * Sirve un diagrama táctico desde el sistema de archivos
     * 
     * @param filename Nombre del archivo
     * @return Resource del archivo
     * @throws GestoServiceException si el archivo no existe o no es legible
     */
    public Resource serveTacticalDiagram(String filename) {
        log.info("Sirviendo diagrama táctico: {}", filename);
        
        try {
            Path filePath = Paths.get("uploads", "tactical-diagrams", filename);
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
            log.error("Error sirviendo diagrama táctico: {}", filename, e);
            throw new GestoServiceException("Error al servir el archivo: " + e.getMessage());
        }
    }

    /**
     * Obtiene la extensión de un archivo
     * 
     * @param filename Nombre del archivo
     * @return Extensión del archivo
     */
    private String getFileExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(dot);
        }
        return "";
    }
}
