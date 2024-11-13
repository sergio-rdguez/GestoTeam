package com.gestoteam.repository;

import com.gestoteam.model.TeamExpense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamExpenseRepository extends JpaRepository<TeamExpense, Long> {
}
