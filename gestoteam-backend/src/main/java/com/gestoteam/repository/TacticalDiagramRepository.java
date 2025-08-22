package com.gestoteam.repository;

import com.gestoteam.model.TacticalDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TacticalDiagramRepository extends JpaRepository<TacticalDiagram, Long> {

    List<TacticalDiagram> findByCreatedByIdAndDeletedFalse(Long userId);

    Optional<TacticalDiagram> findByIdAndDeletedFalse(Long id);

    @Query("SELECT td FROM TacticalDiagram td " +
           "WHERE td.deleted = false AND td.createdBy.id = :userId " +
           "ORDER BY td.updatedAt DESC")
    List<TacticalDiagram> findRecentByUser(@Param("userId") Long userId);
}
