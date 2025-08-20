<template>
  <div class="data-table-container">
    <div class="table-controls">
      <div class="filter-section">
        <input
          type="text"
          v-model="globalFilter"
          placeholder="Buscar en la tabla..."
          class="global-filter-input"
        />
      </div>
      <div class="table-actions">
        <button @click="showColumnToggle = !showColumnToggle" class="btn-toggle-columns">
          <i class="fa-solid fa-eye"></i> Columnas
        </button>
        <div v-if="showColumnToggle" class="column-list">
          <div v-for="col in internalColumns" :key="col.key">
            <input type="checkbox" :id="`col-${col.key}`" v-model="col.visible" />
            <label :for="`col-${col.key}`">{{ col.label }}</label>
          </div>
        </div>
      </div>
    </div>

    <div class="table-responsive">
      <table class="data-table">
        <thead>
          <tr>
            <th
              v-for="col in visibleColumns"
              :key="col.key"
              @click="col.sortable ? sortBy(col.key) : null"
              :class="{ sortable: col.sortable }"
            >
              {{ col.label }}
              <span v-if="col.sortable" class="sort-icon">
                <i v-if="sortKey === col.key && sortOrder === 'asc'" class="fa-solid fa-arrow-up"></i>
                <i v-else-if="sortKey === col.key && sortOrder === 'desc'" class="fa-solid fa-arrow-down"></i>
                <i v-else class="fa-solid fa-sort"></i>
              </span>
            </th>
            <th v-if="hasActions">Acciones</th>
          </tr>
        </thead>

        <tbody v-if="loading">
          <tr>
            <td :colspan="visibleColumns.length + (hasActions ? 1 : 0)" class="loading-state">
              Cargando...
            </td>
          </tr>
        </tbody>

        <tbody v-else-if="paginatedItems.length > 0">
          <tr v-for="item in paginatedItems" :key="item.id" @click="$emit('row-click', item)">
            <td v-for="col in visibleColumns" :key="col.key" :data-label="col.label">
              <slot :name="`cell-${col.key}`" :item="item" :value="getNestedValue(item, col.key)">
                {{ getNestedValue(item, col.key) }}
              </slot>
            </td>
            <td v-if="hasActions" data-label="Acciones">
              <slot name="actions" :item="item"></slot>
            </td>
          </tr>
        </tbody>
        
        <tbody v-else>
          <tr>
            <td :colspan="visibleColumns.length + (hasActions ? 1 : 0)" class="no-data">
              No se encontraron resultados.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="!loading && totalPages > 0" class="pagination-controls">
      <div class="page-size-selector">
        <label for="pageSize">Filas por página:</label>
        <select id="pageSize" v-model.number="pageSize">
          <option v-for="size in pageSizeOptions" :key="size" :value="size">{{ size }}</option>
        </select>
      </div>
      <div class="pagination-nav">
        <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">Anterior</button>
        <span>Página {{ currentPage }} de {{ totalPages }}</span>
        <button @click="changePage(currentPage + 1)" :disabled="currentPage >= totalPages">Siguiente</button>
      </div>
    </div>
  </div>
</template>

<script>
import { tableConfigurationService } from '@/services/tableConfigurationService';

export default {
  name: "DataTable",
  props: {
    items: {
      type: Array,
      required: true,
    },
    columns: {
      type: Array,
      required: true,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    hasActions: {
      type: Boolean,
      default: false,
    },
    defaultSortKey: {
      type: String,
      default: 'id',
    },
    tableName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      pageSizeOptions: [5, 10, 25, 50],
      sortKey: this.defaultSortKey,
      sortOrder: 'asc',
      globalFilter: '',
      showColumnToggle: false,
      internalColumns: [],
      userConfiguration: null
    };
  },
  computed: {
    visibleColumns() {
      return this.internalColumns.filter(c => c.visible);
    },
    filteredItems() {
      if (!this.globalFilter) {
        return this.items;
      }
      const filter = this.globalFilter.toLowerCase();
      return this.items.filter(item => {
        return this.visibleColumns.some(col => {
          const value = this.getNestedValue(item, col.key);
          return String(value).toLowerCase().includes(filter);
        });
      });
    },
    sortedItems() {
      const sorted = [...this.filteredItems];
      if (this.sortKey) {
        const sortColumn = this.columns.find(c => c.key === this.sortKey);
        const sortField = sortColumn && sortColumn.sortOn ? sortColumn.sortOn : this.sortKey;

        sorted.sort((a, b) => {
          const valA = this.getNestedValue(a, sortField);
          const valB = this.getNestedValue(b, sortField);
          
          if (typeof valA === 'string' && typeof valB === 'string') {
            return this.sortOrder === 'asc' ? valA.localeCompare(valB) : valB.localeCompare(valA);
          }
          
          if (valA < valB) return this.sortOrder === 'asc' ? -1 : 1;
          if (valA > valB) return this.sortOrder === 'asc' ? 1 : -1;
          return 0;
        });
      }
      return sorted;
    },
    paginatedItems() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.sortedItems.slice(start, end);
    },
    totalPages() {
      if (this.pageSize === 0) return 0;
      return Math.ceil(this.filteredItems.length / this.pageSize);
    },
  },
  methods: {
    sortBy(key) {
      if (this.sortKey === key) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortKey = key;
        this.sortOrder = 'asc';
      }
      // Guardar automáticamente sin que se note
      this.saveConfigurationSilently();
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page;
      }
    },
    getNestedValue(obj, path) {
      return path.split('.').reduce((acc, part) => acc && acc[part], obj);
    },

    async loadUserConfiguration() {
      try {
        this.userConfiguration = await tableConfigurationService.getTableConfiguration(this.tableName);
      } catch (error) {
        console.warn('No se pudo cargar configuración del usuario, usando valores por defecto');
        // Usar configuración por defecto si falla
        this.userConfiguration = tableConfigurationService.getDefaultConfiguration(this.tableName);
      }
    },

    initializeTable() {
      if (this.userConfiguration) {
        // Aplicar configuración del usuario
        this.pageSize = this.userConfiguration.pageSize;
        this.sortKey = this.userConfiguration.defaultSortKey;
        this.sortOrder = this.userConfiguration.defaultSortOrder.toLowerCase();
        
        // Aplicar configuración de columnas
        this.applyColumnConfiguration();
      } else {
        // Usar configuración por defecto
        this.internalColumns = this.columns.map(col => ({ ...col, visible: col.visible !== false }));
      }
    },

    applyColumnConfiguration() {
      if (!this.userConfiguration) return;

      // Mapear configuración del usuario a las columnas
      this.internalColumns = this.columns.map(col => {
        const userColConfig = this.userConfiguration.columnConfigurations.find(
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
      this.internalColumns.sort((a, b) => {
        const configA = this.userConfiguration.columnConfigurations.find(
          config => config.columnKey === a.key
        );
        const configB = this.userConfiguration.columnConfigurations.find(
          config => config.columnKey === b.key
        );
        
        const orderA = configA ? configA.sortOrder : 999;
        const orderB = configB ? configB.sortOrder : 999;
        
        return orderA - orderB;
      });
    },

    // Método invisible que guarda en background
    async saveConfigurationSilently() {
      try {
        const configuration = {
          tableName: this.tableName,
          pageSize: this.pageSize,
          defaultSortKey: this.sortKey,
          defaultSortOrder: this.sortOrder.toUpperCase(),
          columnConfigurations: this.internalColumns.map((col, index) => ({
            columnKey: col.key,
            columnLabel: col.label,
            visible: col.visible,
            sortable: col.sortable !== false,
            sortOrder: index + 1,
            width: col.width || '100px',
            isDefault: false
          }))
        };

        await tableConfigurationService.saveTableConfiguration(configuration);
      } catch (error) {
        // Silencioso, no mostrar al usuario
        console.warn('No se pudo guardar configuración de tabla');
      }
    }
  },
  watch: {
    globalFilter() {
      this.currentPage = 1;
    },
    pageSize() {
      this.currentPage = 1;
      // Guardar automáticamente cuando cambia el tamaño de página
      this.saveConfigurationSilently();
    },
    items() {
      this.currentPage = 1;
    },
    // Observar cambios en las columnas internas para guardar automáticamente
    internalColumns: {
      handler() {
        // Guardar automáticamente cuando cambian las columnas
        this.saveConfigurationSilently();
      },
      deep: true
    }
  },
  async created() {
    // Cargar configuración del usuario
    await this.loadUserConfiguration();
    this.initializeTable();
  },
};
</script>

<style scoped>
.data-table-container {
  font-family: 'Arial', sans-serif;
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.table-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}
.global-filter-input {
  padding: 0.6rem 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}
.table-actions {
  display: flex;
  gap: 0.5rem;
  position: relative;
}

.column-toggler {
  position: relative;
}
.btn-toggle-columns {
  padding: 0.6rem 1rem;
  background: #f7f7f7;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-toggle-columns:hover {
  background: #e9e9e9;
  border-color: #ccc;
}

.column-list {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 1rem;
  z-index: 10;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.column-list div {
  display: flex;
  align-items: center;
  white-space: nowrap;
  padding: 0.25rem 0;
}
.column-list label {
  margin-left: 0.5rem;
}
.table-responsive {
  overflow-x: auto;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th, .data-table td {
  padding: 0.8rem 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}
.data-table th {
  font-weight: 600;
  color: #333;
  background-color: #f9fafb;
}
.data-table th.sortable {
  cursor: pointer;
  user-select: none;
}
.sort-icon {
  margin-left: 0.5rem;
  color: #999;
}
.data-table tbody tr {
  transition: background-color 0.15s ease;
}
.data-table tbody tr:hover {
  background-color: #f5f5f5;
}
.no-data, .loading-state {
  text-align: center;
  padding: 2.5rem;
  color: #777;
  font-style: italic;
}
.pagination-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  color: #555;
  flex-wrap: wrap;
  gap: 1rem;
}
.page-size-selector {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
#pageSize {
  padding: 0.25rem;
  border-radius: 4px;
  border: 1px solid #ddd;
}
.pagination-nav button {
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.pagination-nav button:hover:not(:disabled) {
  background-color: #0056b3;
}
.pagination-nav button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}
.pagination-nav {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
@media (max-width: 768px) {
  .data-table thead {
    display: none;
  }
  .data-table tr {
    display: block;
    margin-bottom: 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 1rem;
  }
  .data-table td {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    padding: 0.75rem 0;
  }
  .data-table td:last-child {
    border-bottom: none;
  }
  .data-table td::before {
    content: attr(data-label);
    font-weight: bold;
    margin-right: 1rem;
  }
}
</style>