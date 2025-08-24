package com.gestoteam.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FileStorageConfig {

    @Bean
    CommandLineRunner initFileStorage() {
        return args -> {
            try {
                // Crear directorio de uploads si no existe
                Path uploadsDir = Paths.get("uploads");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                    log.info("Directorio de uploads creado: {}", uploadsDir.toAbsolutePath());
                }

                // Crear subdirectorio para jugadores
                Path playersDir = uploadsDir.resolve("players");
                if (!Files.exists(playersDir)) {
                    Files.createDirectories(playersDir);
                    log.info("Directorio de fotos de jugadores creado: {}", playersDir.toAbsolutePath());
                }

                // Crear subdirectorio para ejercicios
                Path exercisesDir = uploadsDir.resolve("exercises");
                if (!Files.exists(exercisesDir)) {
                    Files.createDirectories(exercisesDir);
                    log.info("Directorio de imágenes de ejercicios creado: {}", exercisesDir.toAbsolutePath());
                }

                log.info("Sistema de archivos inicializado correctamente");
            } catch (Exception e) {
                log.error("Error al inicializar el sistema de archivos", e);
            }
        };
    }
}
