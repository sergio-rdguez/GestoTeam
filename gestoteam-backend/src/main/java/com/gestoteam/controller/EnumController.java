package com.gestoteam.controller;

import com.gestoteam.service.EnumService;
import com.gestoteam.service.EnumService.EnumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enums")
public class EnumController {

    private final EnumService enumService;

    @GetMapping("/player-status")
    public List<EnumResponse> getPlayerStatuses() {
        return enumService.getPlayerStatuses();
    }

    @GetMapping("/positions")
    public List<EnumResponse> getPositions() {
        return enumService.getPositions();
    }

    @GetMapping("/categories")
    public List<EnumResponse> getCategories() {
        return enumService.getCategories();
    }

}
