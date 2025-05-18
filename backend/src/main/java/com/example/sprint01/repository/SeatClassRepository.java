package com.example.sprint01.repository;

import com.example.sprint01.entity.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatClassRepository extends JpaRepository<SeatClass, Long> {
    // Custom query methods can be defined here if needed
}
