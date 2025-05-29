package com.gestoteam.model.listener;

import com.gestoteam.model.Season;
import com.gestoteam.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonEntityListener {

    private static SeasonRepository seasonRepository;

    @Autowired
    public void init(SeasonRepository seasonRepository) {
        SeasonEntityListener.seasonRepository = seasonRepository;
    }

    @PrePersist
    @PreUpdate
    public void validate(Season season) {
        if (season.getStartDate() == null || season.getEndDate() == null) {
            return;
        }
        List<Season> overlapping = seasonRepository.findOverlappingSeasons(season.getStartDate(), season.getEndDate())
                .stream()
                .filter(s -> season.getId() == null || !s.getId().equals(season.getId()))
                .collect(Collectors.toList());
        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Season dates overlap with an existing season.");
        }
    }
}
