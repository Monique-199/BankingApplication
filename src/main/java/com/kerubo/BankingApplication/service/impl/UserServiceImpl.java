package com.kerubo.BankingApplication.service.impl;

import com.kerubo.BankingApplication.config.JwtTokenProvider;
import com.kerubo.BankingApplication.dto.*;
import com.kerubo.BankingApplication.entity.Role;
import com.kerubo.BankingApplication.entity.User;
import com.kerubo.BankingApplication.repository.UserRepository;
import com.kerubo.BankingApplication.util.AccountUtils;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Method to create a new account for a user
    public BankResponse createAccount(UserRequest userRequest) {
        // Check if an account already exists with the provided email
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode("001")
                    .responseMessage("A user already has an account created")
                    .accountInfo(null)
                    .build();
        } else {
            // Build a new User object with provided details
            User newUser = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .gender(userRequest.getGender())
                    .othername(userRequest.getOthername())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .password(this.passwordEncoder.encode(userRequest.getPassword()))
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .status("ACTIVE")
                    .role(Role.valueOf("ROLE_ADMIN"))
                    .build();

            // Save the user to the database
            User savedUser = this.userRepository.save(newUser);

            // Build email details for account creation notification
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(savedUser.getEmail())
                    .subject("ACCOUNT CREATION")
                    .messageBody("Congratulations your account has been successfully created!\n" +
                            "Your account details:\n" +
                            "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOthername() + "\n" +
                            "Account number: " + savedUser.getAccountNumber())
                    .build();

            // Send email notification
            this.emailService.sendEmailAlert(emailDetails);

            // Return success response with account details
            return BankResponse.builder()
                    .responseCode("002")
                    .responseMessage("Account has been successfully created!")
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(savedUser.getAccountBalance())
                            .accountNumber(savedUser.getAccountNumber())
                            .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOthername())
                            .build())
                    .build();
        }
    }

    // Method for user login
    public BankResponse login(LoginDto loginDto) {
        // Authenticate user credentials
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        // Send email notification for successful login
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDto.getEmail())
                .messageBody("You logged in to your account. If you did not initiate this transaction, please contact your bank.")
                .build();
        this.emailService.sendEmailAlert(loginAlert);

        // Return success response with JWT token
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(this.jwtTokenProvider.generateToken(authentication))
                .build();
    }

    // Method for balance inquiry
    public BankResponse balanceInquiry(InquiryRequest request) {
        // Check if the account exists
        boolean isAccountExist = this.userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("User with the provided details does not exist!")
                    .accountInfo(null)
                    .build();
        } else {
            // Retrieve user details
            User foundUser = this.userRepository.findByAccountNumber(request.getAccountNumber());

            // Return response with account balance details
            return BankResponse.builder()
                    .responseCode("004")
                    .responseMessage("The requested account has been found!")
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(foundUser.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOthername())
                            .build())
                    .build();
        }
    }

    // Additional methods (nameInquiry, creditAccount, debitAccount, transfer) are similarly structured
    // Each method checks for necessary conditions, retrieves or updates data, sends email notifications,
    // and returns a structured response to indicate success or failure

    @Generated
    public UserServiceImpl(final UserRepository userRepository,
                            final AuthenticationManager authenticationManager,
                            final EmailService emailService,
                            final TransactionService transactionService,
                            final JwtTokenProvider jwtTokenProvider,
                            final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.transactionService = transactionService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
}
