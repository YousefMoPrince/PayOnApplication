package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class WalletResponse {
    @SerializedName("walletId")
    private Long walletId;

    @SerializedName("balance")
    private BigDecimal balance;

    public Long getWalletId() { return walletId; }
    public BigDecimal getBalance() { return balance; }
}