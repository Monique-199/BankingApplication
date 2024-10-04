package com.kerubo.BankingApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class TransactionDto {
    private String transanctionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;

}
