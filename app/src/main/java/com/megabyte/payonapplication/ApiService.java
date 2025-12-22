package com.megabyte.payonapplication;

import com.megabyte.payonapplication.DTO.AccountRequest;
import com.megabyte.payonapplication.DTO.AccountResponse;
import com.megabyte.payonapplication.DTO.AdminLoginRequest;
import com.megabyte.payonapplication.DTO.AdminLoginResponse;
import com.megabyte.payonapplication.DTO.ContactRequest;
import com.megabyte.payonapplication.DTO.ContactResponse;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.LoginRequest;
import com.megabyte.payonapplication.DTO.LoginResponse;
import com.megabyte.payonapplication.DTO.RegisterRequest;
import com.megabyte.payonapplication.DTO.RegisterResponse;
import com.megabyte.payonapplication.DTO.Status;
import com.megabyte.payonapplication.DTO.TransactionRequest;
import com.megabyte.payonapplication.DTO.TransactionResponse;
import com.megabyte.payonapplication.DTO.TransactionStatusResponse;
import com.megabyte.payonapplication.DTO.WalletRequest;
import com.megabyte.payonapplication.DTO.WalletResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @POST("api/LogReg/Login")
    Call<GeneralApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);


    @POST("api/LogReg/register")
    Call<GeneralApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);


    @POST("api/admins/Adlogin")
    Call<GeneralApiResponse<AdminLoginResponse>> adminLogin(@Body AdminLoginRequest request);


    @POST("api/accmanager/accountreg")
    Call<GeneralApiResponse<AccountResponse>> accountRegister(@Body AccountRequest request);


    @GET("api/accmanager/findacc")
    Call<GeneralApiResponse<AccountResponse>> findAccount(@Query("userId") Long userId);


    @POST("api/wallets/createwall")
    Call<GeneralApiResponse<WalletResponse>> createWallet(@Body WalletRequest request);


    @GET("api/wallets/wallet")
    Call<GeneralApiResponse<WalletResponse>> getWallet(@Query("userId") Long userId);
    @POST("api/LogReg/sync-contacts")
    Call<GeneralApiResponse<List<ContactResponse>>> syncContacts(@Body ContactRequest req);


    @POST("api/transactions/deposit")
    Call<GeneralApiResponse<TransactionResponse>> deposit(@Body TransactionRequest request);


    @POST("api/transactions/withdraw")
    Call<GeneralApiResponse<TransactionResponse>> withdraw(@Body TransactionRequest request);


    @POST("api/transactions/transfer")
    Call<GeneralApiResponse<TransactionResponse>> transfer(@Body TransactionRequest request);


    @PUT("api/transactions/{transactionId}/withdrawstatus")
    Call<GeneralApiResponse<TransactionStatusResponse>> withdrawStatus(@Path ("transactionId") String transactionId, @Body Status status);


    @PUT("api/transactions/{transactionId}/transferstatus")
    Call<GeneralApiResponse<TransactionStatusResponse>> transferStatus(@Path("transactionId") String transactionId, @Body Status status);
}