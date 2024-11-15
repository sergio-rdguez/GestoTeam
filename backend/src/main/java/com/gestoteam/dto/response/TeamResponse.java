package com.gestoteam.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String division;
    private String categoryName;
}
