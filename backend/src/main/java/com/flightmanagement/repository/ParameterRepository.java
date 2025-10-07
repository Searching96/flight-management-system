package com.flightmanagement.repository;

import com.flightmanagement.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer> {
    
    @Query("SELECT p FROM Parameter p WHERE p.deletedAt IS NULL ORDER BY p.id DESC LIMIT 1")
    Optional<Parameter> findLatestParameter();
}
