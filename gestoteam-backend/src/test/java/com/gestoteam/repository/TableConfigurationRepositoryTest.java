package com.gestoteam.repository;

import com.gestoteam.model.TableConfiguration;
import com.gestoteam.model.TableColumnConfiguration;
import com.gestoteam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TableConfigurationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TableConfigurationRepository tableConfigurationRepository;

    private TableConfiguration testConfiguration1;
    private TableConfiguration testConfiguration2;

    @BeforeEach
    void setUp() {
        // Limpiar base de datos antes de cada test
        entityManager.clear();
    }

    @Test
    void findByUserIdAndTableNameOrderByUpdatedAtDesc_ExistingConfiguration_ReturnsConfiguration() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration config = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        config.addColumnConfiguration(column);
        
        entityManager.persistAndFlush(config);

        // Act
        Optional<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdAndTableNameOrderByUpdatedAtDesc(user.getId(), "teams");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("teams", result.get().getTableName());
        assertEquals(user.getId(), result.get().getUserId());
        assertEquals(10, result.get().getPageSize());
    }

    @Test
    void findByUserIdAndTableNameOrderByUpdatedAtDesc_NonExistingConfiguration_ReturnsEmpty() {
        // Act
        Optional<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdAndTableNameOrderByUpdatedAtDesc(1L, "nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserIdAndTableNameOrderByUpdatedAtDesc_DifferentUser_ReturnsEmpty() {
        // Act
        Optional<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdAndTableNameOrderByUpdatedAtDesc(2L, "teams");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserIdOrderByTableNameAsc_ReturnsOrderedList() {
        // Arrange
        User user = new User();
        user.setUsername("testuser2");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration config1 = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("players")
            .pageSize(25)
            .defaultSortKey("fullName")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .build();
        
        TableColumnConfiguration column1 = TableColumnConfiguration.builder()
            .columnKey("fullName")
            .columnLabel("Nombre Completo")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("250px")
            .isDefault(true)
            .build();
        config1.addColumnConfiguration(column1);
        
        TableConfiguration config2 = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        TableColumnConfiguration column2 = TableColumnConfiguration.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        config2.addColumnConfiguration(column2);
        
        entityManager.persistAndFlush(config1);
        entityManager.persistAndFlush(config2);

        // Act
        List<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdOrderByTableNameAsc(user.getId());

        // Assert
        assertEquals(2, result.size());
        // Verificar orden alfabético
        assertEquals("players", result.get(0).getTableName());
        assertEquals("teams", result.get(1).getTableName());
    }

    @Test
    void findByUserIdOrderByTableNameAsc_DifferentUser_ReturnsEmptyList() {
        // Act
        List<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdOrderByTableNameAsc(2L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUserIdAndTableName_ExistingConfiguration_ReturnsTrue() {
        // Arrange
        User user = new User();
        user.setUsername("testuser3");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration config = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        config.addColumnConfiguration(column);
        
        entityManager.persistAndFlush(config);

        // Act
        boolean exists = tableConfigurationRepository.existsByUserIdAndTableName(user.getId(), "teams");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndTableName_NonExistingConfiguration_ReturnsFalse() {
        // Act
        boolean exists = tableConfigurationRepository.existsByUserIdAndTableName(1L, "nonexistent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByUserIdAndTableName_DifferentUser_ReturnsFalse() {
        // Act
        boolean exists = tableConfigurationRepository.existsByUserIdAndTableName(2L, "teams");

        // Assert
        assertFalse(exists);
    }

    @Test
    void save_NewConfiguration_PersistsCorrectly() {
        // Arrange
        User user = new User();
        user.setUsername("testuser4");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration newConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("matches")
            .pageSize(15)
            .defaultSortKey("date")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("date")
            .columnLabel("Fecha")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("150px")
            .isDefault(true)
            .build();
        newConfig.addColumnConfiguration(column);

        // Act
        TableConfiguration saved = tableConfigurationRepository.save(newConfig);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("matches", saved.getTableName());
        assertEquals(user.getId(), saved.getUserId());
        assertEquals(15, saved.getPageSize());
    }

    @Test
    void save_UpdatedConfiguration_UpdatesCorrectly() {
        // Arrange
        User user = new User();
        user.setUsername("testuser5");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration config = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        config.addColumnConfiguration(column);
        
        TableConfiguration savedConfig = entityManager.persistAndFlush(config);
        
        // Modificar la configuración
        savedConfig.setPageSize(50);
        savedConfig.setDefaultSortKey("category");

        // Act
        TableConfiguration updated = tableConfigurationRepository.save(savedConfig);

        // Assert
        assertEquals(50, updated.getPageSize());
        assertEquals("category", updated.getDefaultSortKey());
    }

    @Test
    void deleteByUserIdAndTableName_ExistingConfiguration_DeletesCorrectly() {
        // Arrange
        User user = new User();
        user.setUsername("testuser6");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration config = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .build();
        
        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        config.addColumnConfiguration(column);
        
        entityManager.persistAndFlush(config);

        // Act
        tableConfigurationRepository.deleteByUserIdAndTableName(user.getId(), "teams");

        // Assert
        Optional<TableConfiguration> deleted = tableConfigurationRepository
            .findByUserIdAndTableNameOrderByUpdatedAtDesc(user.getId(), "teams");
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteByUserIdAndTableName_NonExistingConfiguration_NoError() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> {
            tableConfigurationRepository.deleteByUserIdAndTableName(999L, "nonexistent");
        });
    }

    @Test
    void findByUserIdAndTableNameOrderByUpdatedAtDesc_MultipleConfigurations_ReturnsMostRecent() {
        // Arrange - Crear configuración más reciente para teams
        User user = new User();
        user.setUsername("testuser7");
        user.setPassword("password");
        entityManager.persistAndFlush(user);

        TableConfiguration newerConfig = TableConfiguration.builder()
            .userId(user.getId())
            .tableName("teams")
            .pageSize(100)
            .defaultSortKey("location")
            .defaultSortOrder(TableConfiguration.SortOrder.DESC)
            .createdAt(LocalDateTime.now().plusHours(1))
            .updatedAt(LocalDateTime.now().plusHours(1))
            .build();

        TableColumnConfiguration column = TableColumnConfiguration.builder()
            .columnKey("location")
            .columnLabel("Ubicación")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(true)
            .build();
        newerConfig.addColumnConfiguration(column);

        entityManager.persistAndFlush(newerConfig);

        // Act
        Optional<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdAndTableNameOrderByUpdatedAtDesc(user.getId(), "teams");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100, result.get().getPageSize());
        assertEquals("location", result.get().getDefaultSortKey());
    }

    @Test
    void findByUserIdOrderByTableNameAsc_EmptyResult_ReturnsEmptyList() {
        // Act
        List<TableConfiguration> result = tableConfigurationRepository
            .findByUserIdOrderByTableNameAsc(999L);

        // Assert
        assertTrue(result.isEmpty());
    }
}
