package com.kerubo.BankingApplication.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kerubo.BankingApplication.dto.EmailDetails;
import com.kerubo.BankingApplication.entity.Transanction;
import com.kerubo.BankingApplication.entity.User;
import com.kerubo.BankingApplication.repository.TransactionRepository;
import com.kerubo.BankingApplication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private EmailService emailService;

    public static final String FILE = "E:\\Downloads\\MyStatements.pdf";

    /**
     * Retrieve a list of transactions within a date range given an account number.
     * Generate a PDF file of the transactions.
     * Send the file via email.
     */
    public List<Transanction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transanction> filteredTransactions = transactionRepository.findAll().stream()
                .filter(transanction -> transanction.getAccountNumber().equals(accountNumber))
                .filter(transanction -> transanction.getCreatedAt() != null)
                .filter(transanction -> !transanction.getCreatedAt().isBefore(start) && !transanction.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());

        // Fetch the user associated with the account number
        User user = userRepository.findByAccountNumber(accountNumber);

        // Check if the user is null
        if (user == null) {
            log.error("User with account number " + accountNumber + " not found.");
            throw new IllegalArgumentException("No user found with account number: " + accountNumber);
        }

        // Generate customer name
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + (user.getOthername() != null ? user.getOthername() : "");

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Bank info table
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Kerubo Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);
        PdfPCell bankAddress = new PdfPCell(new Phrase("72, Keroka, Kisii, Kenya"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        // Statement info table
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

        // Transactions table
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

        filteredTransactions.forEach(transanction -> {
            transactionsTable.addCell(new Phrase(transanction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transanction.getTransanctionType()));
            transactionsTable.addCell(new Phrase(transanction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transanction.getStatus()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);
        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested statement attached!")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return filteredTransactions;
    }
}
