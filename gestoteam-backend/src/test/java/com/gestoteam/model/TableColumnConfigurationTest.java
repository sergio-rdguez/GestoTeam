package com.gestoteam.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableColumnConfigurationTest {

    private TableColumnConfiguration columnConfig;
    private TableConfiguration tableConfiguration;

    @BeforeEach
    void setUp() {
        tableConfiguration = TableConfiguration.builder()
            .id(1L)
            .userId(1L)
            .tableName("teams")
            .build();

        columnConfig = TableColumnConfiguration.builder()
            .id(1L)
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .isDefault(false)
            .build();
    }

    @Test
    void testBuilder() {
        // Assert
        assertNotNull(columnConfig);
        assertEquals(1L, columnConfig.getId());
        assertEquals("name", columnConfig.getColumnKey());
        assertEquals("Nombre", columnConfig.getColumnLabel());
        assertTrue(columnConfig.getVisible());
        assertTrue(columnConfig.getSortable());
        assertEquals(1, columnConfig.getSortOrder());
        assertEquals("200px", columnConfig.getWidth());
        assertFalse(columnConfig.getIsDefault());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        TableColumnConfiguration config = TableColumnConfiguration.builder()
            .columnKey("test")
            .columnLabel("Test")
            .build();

        // Assert
        assertTrue(config.getVisible());
        assertTrue(config.getSortable());
        assertFalse(config.getIsDefault());
    }

    @Test
    void testSetAndGetMethods() {
        // Act
        columnConfig.setColumnKey("category");
        columnConfig.setColumnLabel("Categoría");
        columnConfig.setVisible(false);
        columnConfig.setSortable(false);
        columnConfig.setSortOrder(2);
        columnConfig.setWidth("150px");
        columnConfig.setIsDefault(true);

        // Assert
        assertEquals("category", columnConfig.getColumnKey());
        assertEquals("Categoría", columnConfig.getColumnLabel());
        assertFalse(columnConfig.getVisible());
        assertFalse(columnConfig.getSortable());
        assertEquals(2, columnConfig.getSortOrder());
        assertEquals("150px", columnConfig.getWidth());
        assertTrue(columnConfig.getIsDefault());
    }

    @Test
    void testTableConfigurationRelationship() {
        // Act
        columnConfig.setTableConfiguration(tableConfiguration);

        // Assert
        assertEquals(tableConfiguration, columnConfig.getTableConfiguration());
    }

    @Test
    void testNullTableConfiguration() {
        // Act
        columnConfig.setTableConfiguration(null);

        // Assert
        assertNull(columnConfig.getTableConfiguration());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TableColumnConfiguration config1 = TableColumnConfiguration.builder()
            .id(1L)
            .columnKey("name")
            .columnLabel("Nombre")
            .build();

        TableColumnConfiguration config2 = TableColumnConfiguration.builder()
            .id(1L)
            .columnKey("name")
            .columnLabel("Nombre")
            .build();

        TableColumnConfiguration config3 = TableColumnConfiguration.builder()
            .id(2L)
            .columnKey("name")
            .columnLabel("Nombre")
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
        String toString = columnConfig.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("name"));
        assertTrue(toString.contains("Nombre"));
    }

    @Test
    void testBuilderWithAllFields() {
        // Arrange
        TableColumnConfiguration fullConfig = TableColumnConfiguration.builder()
            .id(2L)
            .tableConfiguration(tableConfiguration)
            .columnKey("category")
            .columnLabel("Categoría")
            .visible(false)
            .sortable(false)
            .sortOrder(5)
            .width("300px")
            .isDefault(true)
            .build();

        // Assert
        assertEquals(2L, fullConfig.getId());
        assertEquals(tableConfiguration, fullConfig.getTableConfiguration());
        assertEquals("category", fullConfig.getColumnKey());
        assertEquals("Categoría", fullConfig.getColumnLabel());
        assertFalse(fullConfig.getVisible());
        assertFalse(fullConfig.getSortable());
        assertEquals(5, fullConfig.getSortOrder());
        assertEquals("300px", fullConfig.getWidth());
        assertTrue(fullConfig.getIsDefault());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        TableColumnConfiguration config = new TableColumnConfiguration();

        // Act
        config.setColumnKey("test");
        config.setColumnLabel("Test");

        // Assert
        assertEquals("test", config.getColumnKey());
        assertEquals("Test", config.getColumnLabel());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        TableColumnConfiguration config = new TableColumnConfiguration(
            3L, tableConfiguration, "test", "Test", true, true, 3, "100px", false
        );

        // Assert
        assertEquals(3L, config.getId());
        assertEquals(tableConfiguration, config.getTableConfiguration());
        assertEquals("test", config.getColumnKey());
        assertEquals("Test", config.getColumnLabel());
        assertTrue(config.getVisible());
        assertTrue(config.getSortable());
        assertEquals(3, config.getSortOrder());
        assertEquals("100px", config.getWidth());
        assertFalse(config.getIsDefault());
    }

    @Test
    void testEdgeCases() {
        // Arrange
        TableColumnConfiguration config = TableColumnConfiguration.builder()
            .columnKey("")
            .columnLabel("")
            .width("")
            .build();

        // Assert
        assertEquals("", config.getColumnKey());
        assertEquals("", config.getColumnLabel());
        assertEquals("", config.getWidth());
    }

    @Test
    void testNullValues() {
        // Act
        columnConfig.setColumnKey(null);
        columnConfig.setColumnLabel(null);
        columnConfig.setWidth(null);

        // Assert
        assertNull(columnConfig.getColumnKey());
        assertNull(columnConfig.getColumnLabel());
        assertNull(columnConfig.getWidth());
    }

    @Test
    void testSortOrderEdgeCases() {
        // Arrange - Crear una nueva instancia para este test
        TableColumnConfiguration testConfig = TableColumnConfiguration.builder()
            .columnKey("test")
            .columnLabel("Test")
            .build();
        
        // Act
        testConfig.setSortOrder(0);
        testConfig.setSortOrder(1);
        testConfig.setSortOrder(999);

        // Assert
        assertEquals(999, testConfig.getSortOrder());
    }
}
