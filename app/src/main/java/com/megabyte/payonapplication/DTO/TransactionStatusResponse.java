package com.megabyte.payonapplication.DTO;

import java.math.BigDecimal;

public class TransactionStatusResponse {
    private Long transaction_id;
    private Long fromUser;
    private Long toUser;
    private Long adminId;
    private BigDecimal amount;
    private TransactionType transaction_type;
    private Status status;
    private String description;
    private String token;

    public Long getTransaction_id() {
        return transaction_id; }
    public Long getFromUser() {
        return fromUser; }
    public Long getToUser() {
        return toUser; }
    public Long getAdminId() {
        return adminId; }
    public BigDecimal getAmount() {
        return amount; }
    public TransactionType getTransaction_type() {
        return transaction_type; }
    public Status getStatus() {
        return status; }
    public String getDescription() {
        return description; }
    public String getToken() {
        return token; }
}
