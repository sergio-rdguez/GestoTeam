package com.gestoteam.service;

import com.gestoteam.dto.request.TableConfigurationRequest;
import com.gestoteam.dto.response.TableColumnConfigurationResponse;
import com.gestoteam.dto.response.TableConfigurationResponse;
import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.TableConfiguration;
import com.gestoteam.model.TableColumnConfiguration;
import com.gestoteam.repository.TableConfigurationRepository;
import com.gestoteam.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TableConfigurationService extends BaseService {

    private final TableConfigurationRepository tableConfigurationRepository;
    private final ModelMapper modelMapper;

    public TableConfigurationService(UserRepository userRepository, TableConfigurationRepository tableConfigurationRepository, ModelMapper modelMapper) {
        super(userRepository);
        this.tableConfigurationRepository = tableConfigurationRepository;
        this.modelMapper = modelMapper;
    }

    public TableConfigurationResponse getTableConfiguration(String tableName) {
        Long userId = getCurrentUserId();
        log.info("Obteniendo configuración de tabla '{}' para el usuario: {}", tableName, userId);

        try {
            TableConfiguration config = tableConfigurationRepository
                .findByUserIdAndTableNameOrderByUpdatedAtDesc(userId, tableName)
                .orElseGet(() -> createDefaultConfiguration(userId, tableName));
            
            log.info("Configuración de tabla '{}' obtenida para el usuario: {}", tableName, userId);
            return mapToResponse(config);
        } catch (Exception e) {
            log.error("Error al obtener configuración de tabla '{}' para el usuario: {}", tableName, userId, e);
            throw new GestoServiceException("Error al obtener la configuración de la tabla. Por favor, inténtelo más tarde.");
        }
    }

    @Transactional
    public TableConfigurationResponse saveTableConfiguration(TableConfigurationRequest request) {
        Long userId = getCurrentUserId();
        log.info("Guardando configuración de tabla '{}' para el usuario: {}", request.getTableName(), userId);

        try {
            // Buscar configuración existente
            TableConfiguration config = tableConfigurationRepository
                .findByUserIdAndTableNameOrderByUpdatedAtDesc(userId, request.getTableName())
                .orElseGet(() -> createDefaultConfiguration(userId, request.getTableName()));

            // Actualizar configuración general
            config.setPageSize(request.getPageSize());
            config.setDefaultSortKey(request.getDefaultSortKey());
            config.setDefaultSortOrder(TableConfiguration.SortOrder.valueOf(request.getDefaultSortOrder()));

            // Limpiar columnas existentes
            config.getColumnConfigurations().clear();

            // Crear y agregar nuevas columnas
            request.getColumnConfigurations().forEach(colReq -> {
                TableColumnConfiguration colConfig = TableColumnConfiguration.builder()
                    .tableConfiguration(config)
                    .columnKey(colReq.getColumnKey())
                    .columnLabel(colReq.getColumnLabel())
                    .visible(colReq.getVisible())
                    .sortable(colReq.getSortable())
                    .sortOrder(colReq.getSortOrder())
                    .width(colReq.getWidth())
                    .isDefault(false)
                    .build();
                
                config.addColumnConfiguration(colConfig);
            });

            // Guardar la configuración
            TableConfiguration savedConfig = tableConfigurationRepository.save(config);
            log.info("Configuración de tabla '{}' guardada para el usuario: {}", request.getTableName(), userId);
            
            return mapToResponse(savedConfig);
        } catch (Exception e) {
            log.error("Error al guardar configuración de tabla '{}' para el usuario: {}", request.getTableName(), userId, e);
            throw new GestoServiceException("Error al guardar la configuración de la tabla. Por favor, inténtelo más tarde.");
        }
    }

    public List<TableConfigurationResponse> getAllTableConfigurations() {
        Long userId = getCurrentUserId();
        log.info("Obteniendo todas las configuraciones de tabla para el usuario: {}", userId);

        try {
            List<TableConfiguration> configs = tableConfigurationRepository
                .findByUserIdOrderByTableNameAsc(userId);
            
            log.info("Se encontraron {} configuraciones de tabla para el usuario: {}", configs.size(), userId);
            return configs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener configuraciones de tabla para el usuario: {}", userId, e);
            throw new GestoServiceException("Error al obtener las configuraciones de tabla. Por favor, inténtelo más tarde.");
        }
    }

    @Transactional
    public void deleteTableConfiguration(String tableName) {
        Long userId = getCurrentUserId();
        log.info("Eliminando configuración de tabla '{}' para el usuario: {}", tableName, userId);

        try {
            tableConfigurationRepository.deleteByUserIdAndTableName(userId, tableName);
            log.info("Configuración de tabla '{}' eliminada para el usuario: {}", tableName, userId);
        } catch (Exception e) {
            log.error("Error al eliminar configuración de tabla '{}' para el usuario: {}", tableName, userId, e);
            throw new GestoServiceException("Error al eliminar la configuración de la tabla. Por favor, inténtelo más tarde.");
        }
    }

    private TableConfiguration createDefaultConfiguration(Long userId, String tableName) {
        log.info("Creando configuración por defecto para tabla '{}' y usuario: {}", tableName, userId);
        
        // Crear configuración con valores por defecto específicos para cada tabla
        TableConfiguration config = TableConfiguration.builder()
            .userId(userId)
            .tableName(tableName)
            .pageSize(10)
            .defaultSortKey(getDefaultSortKey(tableName))
            .defaultSortOrder(getDefaultSortOrder(tableName))
            .build();
        
        // Agregar columnas por defecto
        List<TableColumnConfiguration> defaultColumns = getDefaultColumnsForTable(tableName, config);
        defaultColumns.forEach(config::addColumnConfiguration);
        
        // Guardar la configuración
        TableConfiguration savedConfig = tableConfigurationRepository.save(config);
        
        log.info("Configuración por defecto creada para tabla '{}' y usuario: {}", tableName, userId);
        
        return savedConfig;
    }

    private String getDefaultSortKey(String tableName) {
        switch (tableName.toLowerCase()) {
            case "players":
                return "fullName";
            case "teams":
                return "name";
            case "matches":
                return "date";
            case "opponents":
                return "name";
            case "player-stats":
                return "starter";
            default:
                return "id";
        }
    }

    private TableConfiguration.SortOrder getDefaultSortOrder(String tableName) {
        switch (tableName.toLowerCase()) {
            case "matches":
            case "player-stats":
                return TableConfiguration.SortOrder.DESC;
            default:
                return TableConfiguration.SortOrder.ASC;
        }
    }

    private List<TableColumnConfiguration> getDefaultColumnsForTable(String tableName, TableConfiguration tableConfig) {
        List<TableColumnConfiguration> columns = new ArrayList<>();
        
        switch (tableName.toLowerCase()) {
            case "players":
                columns.add(createDefaultColumn("photoUrl", "Foto", true, false, 1, true, tableConfig));
                columns.add(createDefaultColumn("fullName", "Nombre", true, true, 2, true, tableConfig));
                columns.add(createDefaultColumn("position", "Posición", true, true, 3, true, tableConfig));
                columns.add(createDefaultColumn("foot", "Pie", true, true, 4, true, tableConfig));
                columns.add(createDefaultColumn("number", "Dorsal", true, true, 5, true, tableConfig));
                columns.add(createDefaultColumn("status", "Estado", true, true, 6, true, tableConfig));
                break;
            case "teams":
                columns.add(createDefaultColumn("name", "Nombre", true, true, 1, true, tableConfig));
                columns.add(createDefaultColumn("category", "Categoría", true, true, 2, true, tableConfig));
                columns.add(createDefaultColumn("division", "División", true, true, 3, true, tableConfig));
                columns.add(createDefaultColumn("location", "Ubicación", true, true, 4, true, tableConfig));
                break;
            case "matches":
                columns.add(createDefaultColumn("date", "Fecha", true, true, 1, true, tableConfig));
                columns.add(createDefaultColumn("opponent", "Rival", true, true, 2, true, tableConfig));
                columns.add(createDefaultColumn("homeAway", "Local/Visitante", true, true, 3, true, tableConfig));
                columns.add(createDefaultColumn("result", "Resultado", true, false, 4, true, tableConfig));
                break;
            case "opponents":
                columns.add(createDefaultColumn("name", "Nombre", true, true, 1, true, tableConfig));
                columns.add(createDefaultColumn("field", "Campo", true, true, 2, true, tableConfig));
                columns.add(createDefaultColumn("observations", "Observaciones", true, false, 3, true, tableConfig));
                break;
            case "player-stats":
                columns.add(createDefaultColumn("playerFullName", "Jugador", true, true, 1, true, tableConfig));
                columns.add(createDefaultColumn("minutesPlayed", "Minutos", true, true, 2, true, tableConfig));
                columns.add(createDefaultColumn("goals", "Goles", true, true, 3, true, tableConfig));
                columns.add(createDefaultColumn("assists", "Asist.", true, true, 4, true, tableConfig));
                columns.add(createDefaultColumn("cards", "Tarjetas", true, false, 5, true, tableConfig));
                break;
            default:
                columns.add(createDefaultColumn("id", "ID", true, true, 1, true, tableConfig));
                columns.add(createDefaultColumn("name", "Nombre", true, true, 2, true, tableConfig));
                break;
        }
        
        return columns;
    }

    private TableColumnConfiguration createDefaultColumn(String key, String label, boolean visible, boolean sortable, int order, boolean isDefault, TableConfiguration tableConfig) {
        return TableColumnConfiguration.builder()
            .tableConfiguration(tableConfig)
            .columnKey(key)
            .columnLabel(label)
            .visible(visible)
            .sortable(sortable)
            .sortOrder(order)
            .isDefault(isDefault)
            .build();
    }

    private TableConfigurationResponse mapToResponse(TableConfiguration config) {
        TableConfigurationResponse response = modelMapper.map(config, TableConfigurationResponse.class);
        
        // Mapear configuraciones de columnas
        List<TableColumnConfigurationResponse> columnResponses = config.getColumnConfigurations()
            .stream()
            .map(this::mapColumnToResponse)
            .collect(Collectors.toList());
        
        response.setColumnConfigurations(columnResponses);
        return response;
    }

    private TableColumnConfigurationResponse mapColumnToResponse(TableColumnConfiguration col) {
        return modelMapper.map(col, TableColumnConfigurationResponse.class);
    }
}
