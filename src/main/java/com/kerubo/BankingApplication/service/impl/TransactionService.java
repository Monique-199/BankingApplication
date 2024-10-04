package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.dto.TransactionDto;
import com.kerubo.BankingApplication.entity.Transanction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
