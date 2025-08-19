package com.gestoteam.dto.request;

import lombok.Data;

@Data
public class OpponentRequest {
    private String name;
    private String field;
    private String observations;
    private Long teamId;
}
