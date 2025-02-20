package com.gestoteam.util;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Season;
import com.gestoteam.repository.SeasonRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class GlobalUtil {

    private final SeasonRepository seasonRepository;

    public GlobalUtil(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public Season getCurrentSeason() {
        LocalDate today = LocalDate.now();
        Optional<Season> seasonOpt = seasonRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
        return seasonOpt.orElseThrow(() -> new GestoServiceException("No hay ninguna temporada en el sistema que coincida con la fecha de hoy"));
    }
}
