package com.gestoteam.repository;

import com.gestoteam.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date1, LocalDate date2);

    @Query("SELECT s FROM Season s WHERE s.startDate <= :endDate AND s.endDate >= :startDate")
    List<Season> findOverlappingSeasons(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
