package com.megabyte.payonapplication.DTO;

public class LoginRequest {
    private String usernameOrEmail;
    private String password;

    public LoginRequest(String usernameEmail, String password) {
        this.usernameOrEmail = usernameEmail;
        this.password = password;
    }

}
