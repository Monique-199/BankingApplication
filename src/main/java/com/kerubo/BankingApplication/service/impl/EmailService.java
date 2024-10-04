package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);

}
