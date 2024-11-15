package com.gestoteam.service;

import com.gestoteam.model.Category;
import com.gestoteam.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        log.info("Obteniendo todas las categorías");
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        log.info("Obteniendo la categoría con id: {}", id);
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        log.info("Creando una nueva categoría con código: {}", category.getCode());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Actualizando la categoría con id: {}", id);
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setCode(categoryDetails.getCode());
                    category.setName(categoryDetails.getName());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada con id: {}", id);
                    return new RuntimeException("Categoría no encontrada");
                });
    }

    public void deleteCategory(Long id) {
        log.info("Eliminando la categoría con id: {}", id);
        categoryRepository.deleteById(id);
    }
}
