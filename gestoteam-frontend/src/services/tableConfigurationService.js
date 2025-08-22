import api from './api';

export const tableConfigurationService = {
  /**
   * Obtiene la configuración de una tabla específica
   * @param {string} tableName - Nombre de la tabla
   * @returns {Promise<Object>} Configuración de la tabla
   */
  async getTableConfiguration(tableName) {
    try {
      const response = await api.get(`/table-configurations/${tableName}`);
      return response.data;
    } catch (error) {
      console.error('Error al obtener configuración de tabla:', error);
      // Retornar configuración por defecto si hay error
      return this.getDefaultConfiguration(tableName);
    }
  },

  /**
   * Guarda la configuración de una tabla
   * @param {Object} configuration - Configuración a guardar
   * @returns {Promise<Object>} Configuración guardada
   */
  async saveTableConfiguration(configuration) {
    try {
      const response = await api.post('/table-configurations', configuration);
      return response.data;
    } catch (error) {
      console.error('Error al guardar configuración de tabla:', error);
      throw error;
    }
  },

  /**
   * Obtiene todas las configuraciones del usuario
   * @returns {Promise<Array>} Lista de configuraciones
   */
  async getAllTableConfigurations() {
    try {
      const response = await api.get('/table-configurations');
      return response.data;
    } catch (error) {
      console.error('Error al obtener configuraciones:', error);
      return [];
    }
  },

  /**
   * Elimina la configuración de una tabla
   * @param {string} tableName - Nombre de la tabla
   * @returns {Promise<void>}
   */
  async deleteTableConfiguration(tableName) {
    try {
      await api.delete(`/table-configurations/${tableName}`);
    } catch (error) {
      console.error('Error al eliminar configuración:', error);
      throw error;
    }
  },

  /**
   * Configuraciones por defecto para cada tipo de tabla
   * @param {string} tableName - Nombre de la tabla
   * @returns {Object} Configuración por defecto
   */
  getDefaultConfiguration(tableName) {
    const defaults = {
      players: {
        tableName: 'players',
        pageSize: 10,
        defaultSortKey: 'fullName',
        defaultSortOrder: 'ASC',
        columnConfigurations: [
          { columnKey: 'photoUrl', columnLabel: 'Foto', visible: true, sortable: false, sortOrder: 1, width: '80px' },
          { columnKey: 'fullName', columnLabel: 'Nombre', visible: true, sortable: true, sortOrder: 2, width: '200px' },
          { columnKey: 'position', columnLabel: 'Posición', visible: true, sortable: true, sortOrder: 3, width: '120px' },
          { columnKey: 'foot', columnLabel: 'Pie', visible: true, sortable: true, sortOrder: 4, width: '80px' },
          { columnKey: 'number', columnLabel: 'Dorsal', visible: true, sortable: true, sortOrder: 5, width: '80px' },
          { columnKey: 'status', columnLabel: 'Estado', visible: true, sortable: true, sortOrder: 6, width: '120px' }
        ]
      },
      teams: {
        tableName: 'teams',
        pageSize: 10,
        defaultSortKey: 'name',
        defaultSortOrder: 'ASC',
        columnConfigurations: [
          { columnKey: 'name', columnLabel: 'Nombre', visible: true, sortable: true, sortOrder: 1, width: '200px' },
          { columnKey: 'category', columnLabel: 'Categoría', visible: true, sortable: true, sortOrder: 2, width: '120px' },
          { columnKey: 'division', columnLabel: 'División', visible: true, sortable: true, sortOrder: 3, width: '120px' },
          { columnKey: 'location', columnLabel: 'Ubicación', visible: true, sortable: true, sortOrder: 4, width: '150px' }
        ]
      },
      matches: {
        tableName: 'matches',
        pageSize: 10,
        defaultSortKey: 'date',
        defaultSortOrder: 'DESC',
        columnConfigurations: [
          { columnKey: 'date', columnLabel: 'Fecha', visible: true, sortable: true, sortOrder: 1, width: '120px' },
          { columnKey: 'opponent', columnLabel: 'Rival', visible: true, sortable: true, sortOrder: 2, width: '150px' },
          { columnKey: 'homeAway', columnLabel: 'Local/Visitante', visible: true, sortable: true, sortOrder: 3, width: '120px' },
          { columnKey: 'result', columnLabel: 'Resultado', visible: true, sortable: false, sortOrder: 4, width: '100px' }
        ]
      },
      opponents: {
        tableName: 'opponents',
        pageSize: 10,
        defaultSortKey: 'name',
        defaultSortOrder: 'ASC',
        columnConfigurations: [
          { columnKey: 'name', columnLabel: 'Nombre', visible: true, sortable: true, sortOrder: 1, width: '200px' },
          { columnKey: 'field', columnLabel: 'Campo', visible: true, sortable: true, sortOrder: 2, width: '120px' },
          { columnKey: 'observations', columnLabel: 'Observaciones', visible: true, sortable: false, sortOrder: 3, width: '200px' }
        ]
      },
      'player-stats': {
        tableName: 'player-stats',
        pageSize: 10,
        defaultSortKey: 'starter',
        defaultSortOrder: 'DESC',
        columnConfigurations: [
          { columnKey: 'playerFullName', columnLabel: 'Jugador', visible: true, sortable: true, sortOrder: 1, width: '200px' },
          { columnKey: 'minutesPlayed', columnLabel: 'Minutos', visible: true, sortable: true, sortOrder: 2, width: '100px' },
          { columnKey: 'goals', columnLabel: 'Goles', visible: true, sortable: true, sortOrder: 3, width: '80px' },
          { columnKey: 'assists', columnLabel: 'Asist.', visible: true, sortable: true, sortOrder: 4, width: '80px' },
          { columnKey: 'cards', columnLabel: 'Tarjetas', visible: true, sortable: false, sortOrder: 5, width: '100px' }
        ]
      },
      trainings: {
        tableName: 'trainings',
        pageSize: 10,
        defaultSortKey: 'date',
        defaultSortOrder: 'DESC',
        columnConfigurations: [
          { columnKey: 'date', columnLabel: 'Fecha', visible: true, sortable: true, sortOrder: 1, width: '120px' },
          { columnKey: 'trainingType', columnLabel: 'Tipo', visible: true, sortable: true, sortOrder: 2, width: '120px' },
          { columnKey: 'location', columnLabel: 'Ubicación', visible: true, sortable: true, sortOrder: 3, width: '150px' },
          { columnKey: 'observations', columnLabel: 'Observaciones', visible: true, sortable: false, sortOrder: 4, width: '200px' }
        ]
      },
      attendance: {
        tableName: 'attendance',
        pageSize: 10,
        defaultSortKey: 'playerName',
        defaultSortOrder: 'ASC',
        columnConfigurations: [
          { columnKey: 'playerName', columnLabel: 'Jugador', visible: true, sortable: true, sortOrder: 1, width: '200px' },
          { columnKey: 'status', columnLabel: 'Estado', visible: true, sortable: true, sortOrder: 2, width: '120px' },
          { columnKey: 'notes', columnLabel: 'Notas', visible: true, sortable: false, sortOrder: 3, width: '200px' }
        ]
      },
      exercises: {
        tableName: 'exercises',
        pageSize: 10,
        defaultSortKey: 'title',
        defaultSortOrder: 'ASC',
        columnConfigurations: [
          { columnKey: 'title', columnLabel: 'Título', visible: true, sortable: true, sortOrder: 1, width: '200px' },
          { columnKey: 'category', columnLabel: 'Categoría', visible: true, sortable: true, sortOrder: 2, width: '120px' },
          { columnKey: 'description', columnLabel: 'Descripción', visible: true, sortable: false, sortOrder: 3, width: '250px' },
          { columnKey: 'tacticalObjectives', columnLabel: 'Obj. Tácticos', visible: true, sortable: false, sortOrder: 4, width: '150px' }
        ]
      }
    };

    return defaults[tableName] || defaults.teams;
  },

  /**
   * Aplica la configuración de columnas a una tabla existente
   * @param {Array} currentColumns - Columnas actuales de la tabla
   * @param {Object} userConfiguration - Configuración del usuario
   * @returns {Array} Columnas configuradas
   */
  applyColumnConfiguration(currentColumns, userConfiguration) {
    if (!userConfiguration || !userConfiguration.columnConfigurations) {
      return currentColumns.map(col => ({ ...col, visible: col.visible !== false }));
    }

    // Mapear configuración del usuario a las columnas actuales
    const configuredColumns = currentColumns.map(col => {
      const userColConfig = userConfiguration.columnConfigurations.find(
        config => config.columnKey === col.key
      );

      if (userColConfig) {
        return {
          ...col,
          visible: userColConfig.visible,
          sortable: userColConfig.sortable,
          width: userColConfig.width
        };
      } else {
        return { ...col, visible: col.visible !== false };
      }
    });

    // Ordenar columnas según la configuración del usuario
    return configuredColumns.sort((a, b) => {
      const configA = userConfiguration.columnConfigurations.find(
        config => config.columnKey === a.key
      );
      const configB = userConfiguration.columnConfigurations.find(
        config => config.columnKey === b.key
      );
      
      const orderA = configA ? configA.sortOrder : 999;
      const orderB = configB ? configB.sortOrder : 999;
      
      return orderA - orderB;
    });
  }
};
