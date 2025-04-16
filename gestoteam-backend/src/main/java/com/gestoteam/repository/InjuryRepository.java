package com.gestoteam.repository;

import com.gestoteam.model.Injury;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InjuryRepository extends JpaRepository<Injury, Long> {
}
