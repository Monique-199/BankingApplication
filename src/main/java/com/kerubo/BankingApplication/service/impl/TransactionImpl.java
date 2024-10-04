package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.dto.TransactionDto;
import com.kerubo.BankingApplication.entity.Transanction;
import com.kerubo.BankingApplication.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class TransactionImpl implements TransactionService{
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transanction transanction = Transanction.builder()
                .transanctionType(transactionDto.getTransanctionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transanction);
        System.out.println("Transaction saved successfully");

    }
}
