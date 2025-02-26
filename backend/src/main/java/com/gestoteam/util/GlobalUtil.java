package com.gestoteam.util;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.Season;
import com.gestoteam.repository.SeasonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class GlobalUtil {

    private static final Logger log = LoggerFactory.getLogger(GlobalUtil.class);
    private final SeasonRepository seasonRepository;

    public GlobalUtil(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    public Season getCurrentSeason() {
        LocalDate today = LocalDate.now();
        Optional<Season> seasonOpt = seasonRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);

        if (seasonOpt.isPresent()) {
            return seasonOpt.get();
        } else {
            // No existe temporada para la fecha actual, crearla automáticamente
            Season newSeason = calculateSeasonForDate(today);
            try {
                Season savedSeason = seasonRepository.save(newSeason);
                log.info("Se ha creado automáticamente la temporada: {}", savedSeason.getName());
                return savedSeason;
            } catch (Exception e) {
                log.error("Error al crear la temporada automáticamente", e);
                throw new GestoServiceException("Error al crear la temporada automáticamente. Por favor, inténtelo más tarde.");
            }
        }
    }

    private Season calculateSeasonForDate(LocalDate date) {
        int year = date.getYear();
        LocalDate septemberFirst = LocalDate.of(year, 9, 1);

        if (date.isBefore(septemberFirst)) {
            // Pertenece a la temporada que comenzó el año anterior
            LocalDate startDate = LocalDate.of(year - 1, 9, 1);
            LocalDate endDate = LocalDate.of(year, 8, 31);
            String name = (year - 1) + "/" + year;
            return new Season(null, name, startDate, endDate);
        } else {
            // Pertenece a la temporada que comenzó este año
            LocalDate startDate = LocalDate.of(year, 9, 1);
            LocalDate endDate = LocalDate.of(year + 1, 8, 31);
            String name = year + "/" + (year + 1);
            return new Season(null, name, startDate, endDate);
        }
    }
}