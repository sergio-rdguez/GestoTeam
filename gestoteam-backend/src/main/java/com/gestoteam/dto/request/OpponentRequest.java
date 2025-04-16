package com.gestoteam.dto.request;

import lombok.Data;

@Data
public class OpponentRequest {
    private String name;
    private String observations;
    private Long teamId;
}
