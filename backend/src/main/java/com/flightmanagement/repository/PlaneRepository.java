package com.flightmanagement.repository;

import com.flightmanagement.entity.Plane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaneRepository extends JpaRepository<Plane, Integer> {
    
    @Query("SELECT p FROM Plane p WHERE p.deletedAt IS NULL")
    List<Plane> findAllActive();

    Page<Plane> findByDeletedAtIsNull(Pageable pageable);
    
    @Query("SELECT p FROM Plane p WHERE p.planeId = ?1 AND p.deletedAt IS NULL")
    Optional<Plane> findActiveById(Integer id);
    
    @Query("SELECT p FROM Plane p WHERE p.planeCode = ?1 AND p.deletedAt IS NULL")
    Optional<Plane> findByPlaneCode(String planeCode);
    
    @Query("SELECT p FROM Plane p WHERE p.planeType = ?1 AND p.deletedAt IS NULL")
    List<Plane> findByPlaneType(String planeType);
}
