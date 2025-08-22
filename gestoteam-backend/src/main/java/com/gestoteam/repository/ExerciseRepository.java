package com.gestoteam.repository;

import com.gestoteam.enums.ExerciseCategory;
import com.gestoteam.model.Exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {



    @Query("SELECT e FROM Exercise e WHERE e.user.id = :userId AND e.category = :category AND e.deleted = false")
    List<Exercise> findByUserIdAndCategoryAndNotDeleted(
        @Param("userId") Long userId, 
        @Param("category") ExerciseCategory category
    );

    @Query("SELECT e FROM Exercise e WHERE e.user.id = :userId AND e.deleted = false")
    List<Exercise> findAllByUserIdAndNotDeleted(@Param("userId") Long userId);

    @Query("SELECT e FROM Exercise e WHERE e.id = :id AND e.user.id = :userId AND e.deleted = false")
    Optional<Exercise> findByIdAndUserIdAndNotDeleted(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT e FROM Exercise e WHERE e.user.id = :userId AND UPPER(e.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) AND e.deleted = false")
    List<Exercise> findByUserIdAndTitleContainingAndNotDeleted(
        @Param("userId") Long userId, 
        @Param("searchTerm") String searchTerm
    );
}
