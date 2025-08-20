package com.gestoteam.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableConfigurationTest {

    private TableConfiguration tableConfiguration;
    private TableColumnConfiguration columnConfig1;
    private TableColumnConfiguration columnConfig2;

    @BeforeEach
    void setUp() {
        tableConfiguration = TableConfiguration.builder()
            .id(1L)
            .userId(1L)
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder(TableConfiguration.SortOrder.ASC)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        columnConfig1 = TableColumnConfiguration.builder()
            .id(1L)
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .build();

        columnConfig2 = TableColumnConfiguration.builder()
            .id(2L)
            .columnKey("category")
            .columnLabel("Categoría")
            .visible(true)
            .sortable(true)
            .sortOrder(2)
            .build();
    }

    @Test
    void testBuilder() {
        // Assert
        assertNotNull(tableConfiguration);
        assertEquals(1L, tableConfiguration.getId());
        assertEquals(1L, tableConfiguration.getUserId());
        assertEquals("teams", tableConfiguration.getTableName());
        assertEquals(10, tableConfiguration.getPageSize());
        assertEquals("name", tableConfiguration.getDefaultSortKey());
        assertEquals(TableConfiguration.SortOrder.ASC, tableConfiguration.getDefaultSortOrder());
        assertNotNull(tableConfiguration.getCreatedAt());
        assertNotNull(tableConfiguration.getUpdatedAt());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        TableConfiguration config = TableConfiguration.builder()
            .userId(1L)
            .tableName("test")
            .build();

        // Assert
        assertEquals(10, config.getPageSize());
        assertEquals(TableConfiguration.SortOrder.ASC, config.getDefaultSortOrder());
        assertNotNull(config.getColumnConfigurations());
        assertTrue(config.getColumnConfigurations().isEmpty());
    }

    @Test
    void testAddColumnConfiguration() {
        // Act
        tableConfiguration.addColumnConfiguration(columnConfig1);

        // Assert
        assertEquals(1, tableConfiguration.getColumnConfigurations().size());
        assertTrue(tableConfiguration.getColumnConfigurations().contains(columnConfig1));
        assertEquals(tableConfiguration, columnConfig1.getTableConfiguration());
    }

    @Test
    void testAddMultipleColumnConfigurations() {
        // Act
        tableConfiguration.addColumnConfiguration(columnConfig1);
        tableConfiguration.addColumnConfiguration(columnConfig2);

        // Assert
        assertEquals(2, tableConfiguration.getColumnConfigurations().size());
        assertTrue(tableConfiguration.getColumnConfigurations().contains(columnConfig1));
        assertTrue(tableConfiguration.getColumnConfigurations().contains(columnConfig2));
        assertEquals(tableConfiguration, columnConfig1.getTableConfiguration());
        assertEquals(tableConfiguration, columnConfig2.getTableConfiguration());
    }

    @Test
    void testRemoveColumnConfiguration() {
        // Arrange
        tableConfiguration.addColumnConfiguration(columnConfig1);
        tableConfiguration.addColumnConfiguration(columnConfig2);

        // Act
        tableConfiguration.removeColumnConfiguration(columnConfig1);

        // Assert
        assertEquals(1, tableConfiguration.getColumnConfigurations().size());
        assertFalse(tableConfiguration.getColumnConfigurations().contains(columnConfig1));
        assertTrue(tableConfiguration.getColumnConfigurations().contains(columnConfig2));
        assertNull(columnConfig1.getTableConfiguration());
        assertEquals(tableConfiguration, columnConfig2.getTableConfiguration());
    }

    @Test
    void testRemoveColumnConfiguration_NotInList() {
        // Arrange
        tableConfiguration.addColumnConfiguration(columnConfig1);

        // Act
        tableConfiguration.removeColumnConfiguration(columnConfig2);

        // Assert
        assertEquals(1, tableConfiguration.getColumnConfigurations().size());
        assertTrue(tableConfiguration.getColumnConfigurations().contains(columnConfig1));
    }

    @Test
    void testSetAndGetMethods() {
        // Arrange
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);

        // Act
        tableConfiguration.setPageSize(25);
        tableConfiguration.setDefaultSortKey("category");
        tableConfiguration.setDefaultSortOrder(TableConfiguration.SortOrder.DESC);
        tableConfiguration.setUpdatedAt(newTime);

        // Assert
        assertEquals(25, tableConfiguration.getPageSize());
        assertEquals("category", tableConfiguration.getDefaultSortKey());
        assertEquals(TableConfiguration.SortOrder.DESC, tableConfiguration.getDefaultSortOrder());
        assertEquals(newTime, tableConfiguration.getUpdatedAt());
    }

    @Test
    void testSortOrderEnum() {
        // Assert
        assertEquals("ASC", TableConfiguration.SortOrder.ASC.name());
        assertEquals("DESC", TableConfiguration.SortOrder.DESC.name());
        assertEquals(2, TableConfiguration.SortOrder.values().length);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TableConfiguration config1 = TableConfiguration.builder()
            .id(1L)
            .userId(1L)
            .tableName("teams")
            .build();

        TableConfiguration config2 = TableConfiguration.builder()
            .id(1L)
            .userId(1L)
            .tableName("teams")
            .build();

        TableConfiguration config3 = TableConfiguration.builder()
            .id(2L)
            .userId(1L)
            .tableName("teams")
            .build();

        // Assert
        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    void testToString() {
        // Act
        String toString = tableConfiguration.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("teams"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testColumnConfigurationsInitialization() {
        // Assert
        assertNotNull(tableConfiguration.getColumnConfigurations());
        assertTrue(tableConfiguration.getColumnConfigurations() instanceof ArrayList);
    }

    @Test
    void testBidirectionalRelationship() {
        // Arrange
        tableConfiguration.addColumnConfiguration(columnConfig1);

        // Act
        tableConfiguration.removeColumnConfiguration(columnConfig1);

        // Assert
        assertNull(columnConfig1.getTableConfiguration());
        assertFalse(tableConfiguration.getColumnConfigurations().contains(columnConfig1));
    }

    @Test
    void testMultipleColumnConfigurationsOrder() {
        // Arrange
        tableConfiguration.addColumnConfiguration(columnConfig1);
        tableConfiguration.addColumnConfiguration(columnConfig2);

        // Act
        List<TableColumnConfiguration> configs = tableConfiguration.getColumnConfigurations();

        // Assert
        assertEquals(2, configs.size());
        assertEquals(columnConfig1, configs.get(0));
        assertEquals(columnConfig2, configs.get(1));
    }
}
