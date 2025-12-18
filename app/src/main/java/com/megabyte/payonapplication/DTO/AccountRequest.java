package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;

public class AccountRequest {
    @SerializedName("bankName")
    private String bankName;

    @SerializedName("accountNumber")
    private String accountNumber;

    @SerializedName("accountHolder")
    private String accountHolder;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("adminId")
    private Long adminId;

    public AccountRequest(String bankName, String accountNumber, String accountHolder, Long userId, Long adminId) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.userId = userId;
        this.adminId = adminId;
    }
}