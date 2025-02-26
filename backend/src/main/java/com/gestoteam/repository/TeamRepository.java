package com.gestoteam.repository;

import com.gestoteam.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByOwnerIdAndDeletedFalse(String ownerId);
    Optional<Team> findByIdAndOwnerIdAndDeletedFalse(Long id, String ownerId);
    Optional<Team> findByIdAndOwnerId(Long id, String ownerId);
    boolean existsByIdAndOwnerIdAndDeletedFalse(Long id, String ownerId);
}
