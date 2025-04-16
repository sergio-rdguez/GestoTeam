package com.gestoteam.dto.response;

import lombok.Data;

@Data
public class TeamResponse {
    private Long id;
    private String name;
    private String field;
    private String location;
    private String division;
    private String category;
    private String description;
}
