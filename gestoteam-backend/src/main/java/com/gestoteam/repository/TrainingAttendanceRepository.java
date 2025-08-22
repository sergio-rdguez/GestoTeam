package com.gestoteam.repository;

import com.gestoteam.model.TrainingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, Long> {

    @Query("SELECT ta FROM TrainingAttendance ta WHERE ta.training.id = :trainingId AND ta.deleted = false")
    List<TrainingAttendance> findByTrainingIdAndNotDeleted(@Param("trainingId") Long trainingId);

    @Query("SELECT ta FROM TrainingAttendance ta WHERE ta.player.id = :playerId AND ta.deleted = false")
    List<TrainingAttendance> findByPlayerIdAndNotDeleted(@Param("playerId") Long playerId);

    @Query("SELECT ta FROM TrainingAttendance ta WHERE ta.training.id = :trainingId AND ta.player.id = :playerId AND ta.deleted = false")
    Optional<TrainingAttendance> findByTrainingIdAndPlayerIdAndNotDeleted(
        @Param("trainingId") Long trainingId, 
        @Param("playerId") Long playerId
    );

    @Query("SELECT ta FROM TrainingAttendance ta WHERE ta.training.team.id = :teamId AND ta.deleted = false")
    List<TrainingAttendance> findByTeamIdAndNotDeleted(@Param("teamId") Long teamId);
}
