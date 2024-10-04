package com.kerubo.BankingApplication.util;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "A user already has an account created" ;
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided details does not exist!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE= "The requested account has been found!";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account credited successfully";
    public static final String INSUFFICIENT_ACCOUNT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE= "Insufficient balance!";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE  = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been debited!";
    public static final String TRANSFER_ACCOUNT_SUCCESS_CODE = "008";
    public static final String TRANSFER_ACCOUNT_SUCCESS_MESSAGE = "Transfer success";


    public  static String generateAccountNumber(){
        //2023 + random 6 digits
        Year currentyear = Year.now();
        int min = 100000;
        int max = 999999;
        //generate random number between max and min
        int randNumber = (int)Math.floor(Math.random() * (max - min + 1) + min);
        //convert current and randomnumber to strings then concatenate them

        String year = String.valueOf(currentyear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();

    }

}
