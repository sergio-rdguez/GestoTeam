package com.gestoteam.controller;

import com.gestoteam.service.EnumService;
import com.gestoteam.service.EnumService.EnumResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    private final EnumService enumService;

    public EnumController(EnumService enumService) {
        this.enumService = enumService;
    }

    @GetMapping("/player-status")
    public List<EnumResponse> getPlayerStatuses() {
        return enumService.getPlayerStatuses();
    }

    @GetMapping("/positions")
    public List<EnumResponse> getPositions() {
        return enumService.getPositions();
    }
}
