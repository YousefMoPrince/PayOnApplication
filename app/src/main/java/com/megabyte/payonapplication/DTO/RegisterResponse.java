package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("username")
    private String username;

    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("password")
    private String password;



    public Long getUserId() { return userId; }
    public String getUserName() { return username; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }



}