package com.megabyte.payonapplication.DTO;

import com.google.gson.annotations.SerializedName;

public class GeneralApiResponse<T> {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
