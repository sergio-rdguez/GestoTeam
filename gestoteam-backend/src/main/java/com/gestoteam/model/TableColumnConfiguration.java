package com.gestoteam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "table_column_configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_configuration_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TableConfiguration tableConfiguration;

    @Column(name = "column_key", nullable = false, length = 100)
    private String columnKey;

    @Column(name = "column_label", nullable = false, length = 100)
    private String columnLabel;

    @Column(name = "visible", nullable = false)
    @Builder.Default
    private Boolean visible = true;

    @Column(name = "sortable", nullable = false)
    @Builder.Default
    private Boolean sortable = true;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "width", length = 50)
    @Builder.Default
    private String width = "100";

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
}
