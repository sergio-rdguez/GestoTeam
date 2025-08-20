package com.gestoteam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableConfigurationResponse {
    
    private Long id;
    private String tableName;
    private Integer pageSize;
    private String defaultSortKey;
    private String defaultSortOrder;
    private List<TableColumnConfigurationResponse> columnConfigurations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
