package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service // Marks this class as a Spring service component, allowing it to be injected as a dependency.
@Slf4j // Lombok annotation to enable logging.
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender; // JavaMailSender is used for sending emails.

    @Value("${spring.mail.username}") // Injects the sender's email address from the application properties file.
    private String senderEmail;

    /**
     * Sends a simple email without any attachments.
     *
     * @param emailDetails The details of the email to be sent, including the recipient, subject, and body.
     */
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            // Create a simple email message.
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail); // Set the sender's email address.
            mailMessage.setTo(emailDetails.getRecipient()); // Set the recipient's email address.
            mailMessage.setText(emailDetails.getMessageBody()); // Set the body of the email.
            mailMessage.setSubject(emailDetails.getSubject()); // Set the subject of the email.

            // Send the email using JavaMailSender.
            javaMailSender.send(mailMessage);
            System.out.println("Email sent successfully!"); // Confirm email was sent successfully.
        } catch (MailException e) {
            // Handle exceptions that may occur during email sending.
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email with an attachment.
     *
     * @param emailDetails The details of the email to be sent, including the recipient, subject, body, and attachment path.
     */
    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        // Create a MIME message, which allows for attachments.
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            // Initialize the MIME message helper with the MIME message and set it to allow attachments.
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail); // Set the sender's email address.
            mimeMessageHelper.setTo(emailDetails.getRecipient()); // Set the recipient's email address.
            mimeMessageHelper.setText(emailDetails.getMessageBody()); // Set the body of the email.
            mimeMessageHelper.setSubject(emailDetails.getSubject()); // Set the subject of the email.

            // Create a resource for the file to be attached.
            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            // Add the attachment to the email.
            mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            // Send the email with the attachment.
            javaMailSender.send(mimeMessage);

            // Log the successful sending of the email with the attachment.
            log.info(file.getFilename() + " has been sent to user with email " + emailDetails.getRecipient());
        } catch (MessagingException e) {
            // Handle exceptions that may occur during email sending with attachments.
            throw new RuntimeException(e);
        }
    }
}
