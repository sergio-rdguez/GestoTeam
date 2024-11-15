package com.gestoteam.dto.response;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import lombok.Data;

@Data
public class PlayerResponse {

    private Long id;
    private String name;
    private String surnameFirst;
    private String surnameSecond;
    private String fullName;
    private Position position;
    private int number;
    private int age;
    private PlayerStatus status;
    private Long teamId;
    private String teamName;
}
