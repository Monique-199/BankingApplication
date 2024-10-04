package com.kerubo.BankingApplication.repository;

import com.kerubo.BankingApplication.entity.Transanction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transanction,String> {
}
