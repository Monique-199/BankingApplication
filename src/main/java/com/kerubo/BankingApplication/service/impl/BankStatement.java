// This class handles the generation of bank statements for users and sends them via email.
package com.kerubo.BankingApplication.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.kerubo.BankingApplication.dto.EmailDetails;
import com.kerubo.BankingApplication.entity.Transanction;
import com.kerubo.BankingApplication.entity.User;
import com.kerubo.BankingApplication.repository.TransactionRepository;
import com.kerubo.BankingApplication.repository.UserRepository;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BankStatement {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(BankStatement.class);

    private final TransactionRepository transactionRepository; // Repository to fetch transactions.
    private final UserRepository userRepository; // Repository to fetch user data.
    private EmailService emailService; // Service to send emails.

    public static final String FILE = "E:\\Downloads\\MyStatements.pdf"; // File path for the generated PDF.

    /**
     * Generates a bank statement for a given account within a specified date range.
     *
     * @param accountNumber The account number for which the statement is generated.
     * @param startDate     The start date of the statement period in ISO format (yyyy-MM-dd).
     * @param endDate       The end date of the statement period in ISO format (yyyy-MM-dd).
     * @return A list of filtered transactions for the given account and date range.
     * @throws FileNotFoundException If the file cannot be created.
     * @throws DocumentException     If an error occurs while creating the PDF document.
     */
    public List<Transanction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        // Parse the start and end dates.
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // Filter transactions based on account number and date range.
        List<Transanction> filteredTransactions = this.transactionRepository.findAll().stream()
            .filter(transanction -> transanction.getAccountNumber().equals(accountNumber))
            .filter(transanction -> transanction.getCreatedAt() != null)
            .filter(transanction -> !transanction.getCreatedAt().isBefore(start) && !transanction.getCreatedAt().isAfter(end))
            .collect(Collectors.toList());

        // Fetch the user details for the account number.
        User user = this.userRepository.findByAccountNumber(accountNumber);
        if (user == null) {
            log.error("User with account number " + accountNumber + " not found.");
            throw new IllegalArgumentException("No user found with account number: " + accountNumber);
        }

        // Prepare user information for the statement.
        String customerName = user.getFirstName() + " " + user.getLastName() + 
            (user.getOthername() != null ? " " + user.getOthername() : "");

        // Create the PDF document.
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        OutputStream outputStream = new FileOutputStream(FILE); // File location.
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Create a table for the bank's information.
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Kerubo Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20.0F);
        PdfPCell bankAddress = new PdfPCell(new Phrase("72, Keroka, Kisii, Kenya"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        // Create a table for statement and customer details.
        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date " + endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name : " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer address: " + user.getAddress()));
        address.setBorder(0);
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        // Create a table for transaction details.
        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);
        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        // Add transaction rows to the table.
        filteredTransactions.forEach(transanction -> {
            transactionsTable.addCell(new Phrase(transanction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transanction.getTransanctionType()));
            transactionsTable.addCell(new Phrase(transanction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transanction.getStatus()));
        });

        // Add tables to the document.
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close(); // Close the PDF document.

        // Send the statement via email.
        EmailDetails emailDetails = EmailDetails.builder()
            .recipient(user.getEmail())
            .subject("STATEMENT OF ACCOUNT")
            .messageBody("Kindly find your requested statement attached!")
            .attachment(FILE)
            .build();
        this.emailService.sendEmailWithAttachment(emailDetails);

        // Return the filtered transactions.
        return filteredTransactions;
    }

    // Constructor to initialize repositories and email service.
    @Generated
    public BankStatement(final TransactionRepository transactionRepository, final UserRepository userRepository, final EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
