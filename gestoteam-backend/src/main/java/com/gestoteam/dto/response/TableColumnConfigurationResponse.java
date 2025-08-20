package com.gestoteam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnConfigurationResponse {
    
    private String columnKey;
    private String columnLabel;
    private Boolean visible;
    private Boolean sortable;
    private Integer sortOrder;
    private String width;
    private Boolean isDefault;
}
