package com.gestoteam.repository;

import com.gestoteam.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t WHERE t.user.id = :userId AND t.deleted = false")
    List<Training> findByUserIdAndDeletedFalse(@Param("userId") Long userId);

    @Query("SELECT t FROM Training t WHERE t.id = :id AND t.user.id = :userId AND t.deleted = false")
    Optional<Training> findByIdAndUserIdAndDeletedFalse(@Param("id") Long id, @Param("userId") Long userId);
}
