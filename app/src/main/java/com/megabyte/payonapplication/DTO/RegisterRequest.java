package com.megabyte.payonapplication.DTO;

import android.text.Editable;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("userName")
    private String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("fullName")
    private String fullName;

    public RegisterRequest(String userName, String password, String email, String phone, String fullName) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
    }
}