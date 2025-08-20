package com.gestoteam.dto.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TableConfigurationRequestTest {

    private TableConfigurationRequest validRequest;
    private TableColumnConfigurationRequest validColumnRequest;

    @BeforeEach
    void setUp() {
        validColumnRequest = TableColumnConfigurationRequest.builder()
            .columnKey("name")
            .columnLabel("Nombre")
            .visible(true)
            .sortable(true)
            .sortOrder(1)
            .width("200px")
            .build();

        validRequest = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .defaultSortKey("name")
            .defaultSortOrder("ASC")
            .columnConfigurations(Arrays.asList(validColumnRequest))
            .build();
    }

    @Test
    void testBuilder() {
        // Assert
        assertNotNull(validRequest);
        assertEquals("teams", validRequest.getTableName());
        assertEquals(10, validRequest.getPageSize());
        assertEquals("name", validRequest.getDefaultSortKey());
        assertEquals("ASC", validRequest.getDefaultSortOrder());
        assertEquals(1, validRequest.getColumnConfigurations().size());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        TableConfigurationRequest request = TableConfigurationRequest.builder()
            .tableName("test")
            .pageSize(5)
            .columnConfigurations(Arrays.asList(validColumnRequest))
            .build();

        // Assert
        assertEquals("ASC", request.getDefaultSortOrder());
    }

    @Test
    void testNoArgsConstructor() {
        // Arrange
        TableConfigurationRequest request = new TableConfigurationRequest();

        // Act
        request.setTableName("test");
        request.setPageSize(15);

        // Assert
        assertEquals("test", request.getTableName());
        assertEquals(15, request.getPageSize());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        TableConfigurationRequest request = new TableConfigurationRequest(
            "players", 25, "fullName", "DESC", Arrays.asList(validColumnRequest)
        );

        // Assert
        assertEquals("players", request.getTableName());
        assertEquals(25, request.getPageSize());
        assertEquals("fullName", request.getDefaultSortKey());
        assertEquals("DESC", request.getDefaultSortOrder());
        assertEquals(1, request.getColumnConfigurations().size());
    }

    @Test
    void testSetAndGetMethods() {
        // Act
        validRequest.setTableName("matches");
        validRequest.setPageSize(50);
        validRequest.setDefaultSortKey("date");
        validRequest.setDefaultSortOrder("DESC");

        // Assert
        assertEquals("matches", validRequest.getTableName());
        assertEquals(50, validRequest.getPageSize());
        assertEquals("date", validRequest.getDefaultSortKey());
        assertEquals("DESC", validRequest.getDefaultSortOrder());
    }

    @Test
    void testColumnConfigurations() {
        // Arrange
        TableColumnConfigurationRequest column2 = TableColumnConfigurationRequest.builder()
            .columnKey("category")
            .columnLabel("Categoría")
            .visible(true)
            .sortable(true)
            .sortOrder(2)
            .build();

        // Act
        validRequest.setColumnConfigurations(Arrays.asList(validColumnRequest, column2));

        // Assert
        assertEquals(2, validRequest.getColumnConfigurations().size());
        assertEquals("name", validRequest.getColumnConfigurations().get(0).getColumnKey());
        assertEquals("category", validRequest.getColumnConfigurations().get(1).getColumnKey());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TableConfigurationRequest request1 = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .columnConfigurations(Arrays.asList(validColumnRequest))
            .build();

        TableConfigurationRequest request2 = TableConfigurationRequest.builder()
            .tableName("teams")
            .pageSize(10)
            .columnConfigurations(Arrays.asList(validColumnRequest))
            .build();

        TableConfigurationRequest request3 = TableConfigurationRequest.builder()
            .tableName("players")
            .pageSize(10)
            .columnConfigurations(Arrays.asList(validColumnRequest))
            .build();

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Act
        String toString = validRequest.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("teams"));
        assertTrue(toString.contains("10"));
    }

    @Test
    void testEdgeCases() {
        // Arrange
        TableConfigurationRequest request = TableConfigurationRequest.builder()
            .tableName("")
            .pageSize(1)
            .columnConfigurations(Collections.emptyList())
            .build();

        // Assert
        assertEquals("", request.getTableName());
        assertEquals(1, request.getPageSize());
        assertTrue(request.getColumnConfigurations().isEmpty());
    }

    @Test
    void testNullValues() {
        // Act
        validRequest.setDefaultSortKey(null);
        validRequest.setColumnConfigurations(null);

        // Assert
        assertNull(validRequest.getDefaultSortKey());
        assertNull(validRequest.getColumnConfigurations());
    }

    @Test
    void testLargeValues() {
        // Act
        validRequest.setPageSize(1000);
        validRequest.setTableName("a".repeat(100));

        // Assert
        assertEquals(1000, validRequest.getPageSize());
        assertEquals("a".repeat(100), validRequest.getTableName());
    }

    @Test
    void testSortOrderValues() {
        // Act
        validRequest.setDefaultSortOrder("ASC");
        validRequest.setDefaultSortOrder("DESC");

        // Assert
        assertEquals("DESC", validRequest.getDefaultSortOrder());
    }

    @Test
    void testColumnConfigurationsModification() {
        // Arrange
        TableColumnConfigurationRequest newColumn = TableColumnConfigurationRequest.builder()
            .columnKey("new")
            .columnLabel("Nuevo")
            .visible(false)
            .sortable(false)
            .sortOrder(99)
            .build();

        List<TableColumnConfigurationRequest> newList = new ArrayList<>(validRequest.getColumnConfigurations());
        newList.add(newColumn);

        // Act
        validRequest.setColumnConfigurations(newList);

        // Assert
        assertEquals(2, validRequest.getColumnConfigurations().size());
        assertEquals("new", validRequest.getColumnConfigurations().get(1).getColumnKey());
    }
}
