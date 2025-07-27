package com.gestoteam.service;

import com.gestoteam.enums.Category;
import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnumServiceTest {

    private EnumService enumService;

    @BeforeEach
    void setUp() {
        enumService = new EnumService();
    }

    @Test
    void getPlayerStatuses_ShouldReturnAllStatuses() {
        List<EnumService.EnumResponse> statuses = enumService.getPlayerStatuses();
        assertThat(statuses).hasSize(PlayerStatus.values().length);
        assertThat(statuses.get(0).getCode()).isEqualTo(PlayerStatus.ACTIVO.name());
        assertThat(statuses.get(0).getDescription()).isEqualTo(PlayerStatus.ACTIVO.getDescripcion());
    }

    @Test
    void getPositions_ShouldReturnAllPositions() {
        List<EnumService.EnumResponse> positions = enumService.getPositions();
        assertThat(positions).hasSize(Position.values().length);
        assertThat(positions.get(0).getCode()).isEqualTo(Position.POR.name());
        assertThat(positions.get(0).getDescription()).isEqualTo(Position.POR.getDescripcion());
    }

    @Test
    void getCategories_ShouldReturnAllCategories() {
        List<EnumService.EnumResponse> categories = enumService.getCategories();
        assertThat(categories).hasSize(Category.values().length);
        assertThat(categories.get(0).getCode()).isEqualTo(Category.SENIOR.name());
        assertThat(categories.get(0).getDescription()).isEqualTo(Category.SENIOR.getName());
    }
}