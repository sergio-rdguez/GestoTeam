package com.gestoteam.repository;

import com.gestoteam.model.PlayerPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerPaymentRepository extends JpaRepository<PlayerPayment, Long> {
}
