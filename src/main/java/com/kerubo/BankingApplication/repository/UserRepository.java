package com.kerubo.BankingApplication.repository;

import com.kerubo.BankingApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // This checks if an email exists in the database
    Boolean existsByEmail(String email);

    // This checks if an account number exists in the database
    Boolean existsByAccountNumber(String accountNumber);

    // Remove static, and let Spring Data JPA implement this method automatically
    User findByAccountNumber(String accountNumber);
}
