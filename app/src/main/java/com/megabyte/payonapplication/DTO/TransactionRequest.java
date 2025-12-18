package com.megabyte.payonapplication.DTO;

import java.math.BigDecimal;

public class TransactionRequest {
    private Long fromUserId;
    private Long toUserId;
    private Long adminId;
    private BigDecimal amount;
    private String description;
    public TransactionRequest(Long fromUserId, Long toUserId, Long adminId, BigDecimal amount, String description){
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.adminId = adminId;
        this.amount = amount;
        this.description = description;
    }

}
