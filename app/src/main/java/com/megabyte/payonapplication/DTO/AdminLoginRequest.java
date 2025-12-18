package com.megabyte.payonapplication.DTO;

public class AdminLoginRequest {
    private String adminName;
    private String adminPassword;
    public AdminLoginRequest(String adminName, String adminPassword) {
        this.adminName = adminName;
        this.adminPassword = adminPassword;
    }
}

