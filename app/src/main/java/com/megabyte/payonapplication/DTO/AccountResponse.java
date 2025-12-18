package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;

public class AccountResponse {
    @SerializedName("accountNumber")
    private String accountNumber;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("accountHolder")
    private String accountHolder;

    public String getAccountNumber() { return accountNumber; }
    public String getBankName() { return bankName; }
    public String getAccountHolder() { return accountHolder; }
}