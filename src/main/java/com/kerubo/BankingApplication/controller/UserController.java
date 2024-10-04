package com.kerubo.BankingApplication.controller;

import com.kerubo.BankingApplication.dto.*;
import com.kerubo.BankingApplication.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account management apis")

public class UserController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "Create a new user account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP status 201 created"
    )
    @PostMapping


    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return  userService.createAccount(userRequest);
    }
    @Operation(
            summary = "Inquire account balance",
            description = "Checking the account balance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP status 200 SUCCESS"
    )
    @GetMapping("/balanceInquiry")
    public BankResponse balanceInquiry(@RequestBody InquiryRequest request){
        return userService.balanceInquiry(request);
    }
    @GetMapping("nameInquiry")
    public String nameInquiry(@RequestBody InquiryRequest request){
        return userService.nameInquiry(request);
    }
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);

    }
    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}
