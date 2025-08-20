package com.gestoteam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "table_configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "page_size", nullable = false)
    @Builder.Default
    private Integer pageSize = 10;

    @Column(name = "default_sort_key", length = 100)
    private String defaultSortKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_sort_order", nullable = false, length = 10)
    @Builder.Default
    private SortOrder defaultSortOrder = SortOrder.ASC;

    @OneToMany(mappedBy = "tableConfiguration", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TableColumnConfiguration> columnConfigurations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SortOrder {
        ASC, DESC
    }

    // Métodos helper para gestionar la relación bidireccional
    public void addColumnConfiguration(TableColumnConfiguration columnConfig) {
        columnConfigurations.add(columnConfig);
        columnConfig.setTableConfiguration(this);
    }

    public void removeColumnConfiguration(TableColumnConfiguration columnConfig) {
        columnConfigurations.remove(columnConfig);
        columnConfig.setTableConfiguration(null);
    }
}
