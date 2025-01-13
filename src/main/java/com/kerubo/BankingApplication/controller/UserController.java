package com.kerubo.BankingApplication.controller;

import com.kerubo.BankingApplication.dto.*;
import com.kerubo.BankingApplication.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController  // Marks this class as a REST controller
@RequestMapping("/api/user")  // Defines the base URL for the user API
@Tag(name = "User Account management apis")  // Swagger documentation tag for API grouping

public class UserController {
    
    @Autowired  // Automatically injects the UserService into this controller
    UserService userService;

    // Endpoint to create a new user account
    @Operation(
            summary = "Create a new user account",  // Description for Swagger UI
            description = "Creating a new user and assigning an account ID"  // Detailed description for Swagger UI
    )
    @ApiResponse(
            responseCode = "201",  // HTTP status code returned for successful account creation
            description = "HTTP status 201 created"  // Response description
    )
    @PostMapping  // POST request to create a new user account
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        // Calls the service method to create the account and returns the response
        return userService.createAccount(userRequest);
    }

    // Endpoint to inquire the account balance
    @Operation(
            summary = "Inquire account balance",  // Description for Swagger UI
            description = "Checking the account balance"  // Detailed description for Swagger UI
    )
    @ApiResponse(
            responseCode = "200",  // HTTP status code for successful balance inquiry
            description = "HTTP status 200 SUCCESS"  // Response description
    )
    @GetMapping("/balanceInquiry")  // GET request to inquire account balance
    public BankResponse balanceInquiry(@RequestBody InquiryRequest request) {
        // Calls the service method to check the balance and returns the response
        return userService.balanceInquiry(request);
    }

    // Endpoint to inquire the account holder's name
    @GetMapping("nameInquiry")  // GET request to inquire the account holder's name
    public String nameInquiry(@RequestBody InquiryRequest request) {
        // Calls the service method to get the name and returns the response
        return userService.nameInquiry(request);
    }

    // Endpoint to credit funds into an account
    @PostMapping("/credit")  // POST request to credit an account
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        // Calls the service method to credit the account and returns the response
        return userService.creditAccount(request);
    }

    // Endpoint to debit funds from an account
    @PostMapping("debit")  // POST request to debit an account
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        // Calls the service method to debit the account and returns the response
        return userService.debitAccount(request);
    }

    // Endpoint to transfer funds between accounts
    @PostMapping("/transfer")  // POST request for transferring funds
    public BankResponse transfer(@RequestBody TransferRequest request) {
        // Calls the service method to perform the transfer and returns the response
        return userService.transfer(request);
    }

    // Endpoint for user login
    @PostMapping("/login")  // POST request for user login
    public BankResponse login(@RequestBody LoginDto loginDto) {
        // Calls the service method to login the user and returns the response
        return userService.login(loginDto);
    }
}
