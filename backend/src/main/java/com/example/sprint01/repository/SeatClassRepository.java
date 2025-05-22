package com.example.sprint01.repository;

import com.example.sprint01.entity.SeatClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatClassRepository extends JpaRepository<SeatClass, Long> {
    @Query("SELECT sc FROM SeatClass sc WHERE sc.deletedAt IS NULL")
    List<SeatClass> findAllActive();

    @Query("SELECT sc FROM SeatClass sc WHERE sc.id = ?1 AND sc.deletedAt IS NULL")
    Optional<SeatClass> findActiveById(Long id);
}