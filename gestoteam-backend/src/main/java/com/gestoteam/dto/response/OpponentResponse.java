package com.gestoteam.dto.response;

import lombok.Data;

@Data
public class OpponentResponse {
    private Long id;
    private String name;
    private String field;
    private String observations;
    private Long teamId;
}
