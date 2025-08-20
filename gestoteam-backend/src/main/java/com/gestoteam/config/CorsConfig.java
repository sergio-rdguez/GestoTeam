package com.gestoteam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Permite todos los orígenes
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Añadido OPTIONS para preflight
                .allowedHeaders("*") // Permite todos los headers incluyendo Authorization
                .exposedHeaders("Authorization") // Expone el header Authorization
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight por 1 hora
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve ficheros desde el directorio local "uploads" relativo al directorio de trabajo
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
    }
}

