package com.flightmanagement.repository;

import com.flightmanagement.entity.TicketClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketClassRepository extends JpaRepository<TicketClass, Integer> {

    @Query("SELECT tc FROM TicketClass tc WHERE tc.deletedAt IS NULL")
    List<TicketClass> findAllActive();

    Page<TicketClass> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT tc FROM TicketClass tc WHERE tc.ticketClassId = ?1 AND tc.deletedAt IS NULL")
    Optional<TicketClass> findActiveById(Integer id);

    @Query("SELECT tc FROM TicketClass tc WHERE tc.ticketClassName = ?1 AND tc.deletedAt IS NULL")
    Optional<TicketClass> findByTicketClassName(String ticketClassName);
}
