package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.BankingApplication;
import com.kerubo.BankingApplication.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceInquiry(InquiryRequest request);
    String nameInquiry(InquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
    BankResponse login(LoginDto loginDto);

}
