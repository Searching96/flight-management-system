package com.flightmanagement.repository;

import com.flightmanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    
    @Query("SELECT a FROM Account a WHERE a.deletedAt IS NULL")
    List<Account> findAllActive();
    
    @Query("SELECT a FROM Account a WHERE a.accountId = ?1 AND a.deletedAt IS NULL")
    Optional<Account> findActiveById(Integer id);
    
    @Query("SELECT a FROM Account a WHERE a.email = ?1 AND a.deletedAt IS NULL")
    Optional<Account> findByEmail(String email);
    
    @Query("SELECT a FROM Account a WHERE a.citizenId = ?1 AND a.deletedAt IS NULL")
    Optional<Account> findByCitizenId(String citizenId);
    
    @Query("SELECT a FROM Account a WHERE a.accountType = ?1 AND a.deletedAt IS NULL")
    List<Account> findByAccountType(Integer accountType);

    @Query("SELECT a FROM Account a WHERE a.accountName = ?1 AND a.deletedAt IS NULL")
    Optional<Account> findByAccountName(String accountName);
}
