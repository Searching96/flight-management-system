package com.example.sprint01.repository;

import com.example.sprint01.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    // Custom query methods can be defined here if needed
}
