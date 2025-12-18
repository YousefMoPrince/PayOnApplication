package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class WalletRequest {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("adminId")
    private Long adminId;

    @SerializedName("balance")
    private BigDecimal balance;

    public WalletRequest(Long userId, Long adminId, BigDecimal balance) {
        this.userId = userId;
        this.adminId = adminId;
        this.balance = balance;
    }
}