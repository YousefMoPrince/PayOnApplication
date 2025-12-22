package com.megabyte.payonapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.megabyte.payonapplication.DTO.AccountRequest;
import com.megabyte.payonapplication.DTO.AccountResponse;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.RegisterRequest;
import com.megabyte.payonapplication.DTO.RegisterResponse;
import com.megabyte.payonapplication.DTO.WalletRequest;
import com.megabyte.payonapplication.DTO.WalletResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    private Button signUpButton;
    private EditText username;
    private EditText fullName;
    private EditText password;
    private EditText email;
    private EditText phoneNumber;
    private EditText verifyPassword;
    private TextView signInText ;
    private ImageView visibility;
    private ImageView visibility2;
    private boolean isHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        signInText = findViewById(R.id.loginText);
        username = findViewById(R.id.usernameReg);
        fullName = findViewById(R.id.fullNameReg);
        password = findViewById(R.id.passwordReg);
        email = findViewById(R.id.emailReg);
        phoneNumber = findViewById(R.id.phoneReg);
        verifyPassword = findViewById(R.id.verifyPasswordReg);
        signUpButton = findViewById(R.id.signUpButton);
        visibility = findViewById(R.id.visibility);
        visibility2 = findViewById(R.id.visibility2);
        visibility.setOnClickListener(view -> {
            isHidden = !isHidden;
            updateDisplay();
        });
        visibility2.setOnClickListener(view -> {
            isHidden = !isHidden;
            updateDisplay2();
        });
        signInText.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });


        signUpButton.setOnClickListener(view -> {
            if (username.getText().toString().isEmpty() || fullName.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty() || verifyPassword.getText().toString().isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(verifyPassword.getText().toString())) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!email.getText().toString().contains("@")) {
                Toast.makeText(SignUp.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                String account_number = AccountUtils.generateAccountNumber();
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                RegisterRequest registerRequest = new RegisterRequest(
                        username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        phoneNumber.getText().toString(),
                        fullName.getText().toString()
                );

                System.out.println(new Gson().toJson(registerRequest));


                apiService.register(registerRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<RegisterResponse>>() {

                    @Override
                    public void onResponse(Call<GeneralApiResponse<RegisterResponse>> call, Response<GeneralApiResponse<RegisterResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {


                            RegisterResponse registerData = response.body().getData();

                            if (registerData == null || response.body().getMessage().contains("already exists")) {
                                Toast.makeText(SignUp.this, response.body().getMessage() != null ? response.body().getMessage() : "Registration failed", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Long userIdLong = registerData.getUserId();

                            if (userIdLong == null) {
                                Toast.makeText(SignUp.this, "UserId is missing in response", Toast.LENGTH_SHORT).show();
                                System.out.println("RegisterResponse: " + new Gson().toJson(response.body()));
                                return;
                            }

                            String userId = userIdLong.toString();
                            String username = registerData.getUserName();
                            String phone = registerData.getPhone();
                            String password = registerData.getPassword();
                            String account_holder_name = SignUp.this.fullName.getText().toString();

                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("USER_ID", userId);
                            editor.putString("USERNAME", username);
                            editor.putString("PHONE", phone);
                            editor.putString("PASSWORD", password);
                            editor.putString("FULL_NAME", account_holder_name);
                            editor.apply();
                            System.out.println("Register Success: " + userId);
                            Toast.makeText(SignUp.this, "Register Success", Toast.LENGTH_SHORT).show();
                            System.out.println(new Gson().toJson(response.body()));


                            AccountRequest accountRequest = new AccountRequest("masr bank",account_number,account_holder_name,Long.parseLong(userId),5L);




                            apiService.accountRegister(accountRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<AccountResponse>>() {
                                @Override
                                public void onResponse(Call<GeneralApiResponse<AccountResponse>> call, Response<GeneralApiResponse<AccountResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null) {

                                        AccountResponse accountData = response.body().getData();

                                        if (accountData == null) {
                                            Toast.makeText(SignUp.this, "Account Registration failed", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String accountNumber = accountData.getAccountNumber();
                                        String bankName = accountData.getBankName();
                                        String accountHolder = accountData.getAccountHolder();
                                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("ACCOUNT_NUMBER", accountNumber);
                                        editor.putString("BANK_NAME", bankName);
                                        editor.putString("ACCOUNT_HOLDER", accountHolder);
                                        editor.apply();
                                        System.out.println("Account Register Success: " + accountData.getAccountNumber());
                                        Toast.makeText(SignUp.this, "Account Register Success", Toast.LENGTH_SHORT).show();

                                        WalletRequest walletRequest = new WalletRequest(Long.parseLong(userId),5L,new BigDecimal("0"));
                                        System.out.println(new Gson().toJson(walletRequest));

                                        apiService.createWallet(walletRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                            @Override
                                            public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                                if (response.isSuccessful() && response.body() != null) {

                                                    WalletResponse walletData = response.body().getData();

                                                    if (walletData == null) {
                                                        Toast.makeText(SignUp.this, "Wallet Create Failed", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    String walletId = walletData.getWalletId().toString();
                                                    String balance = walletData.getBalance().toString();
                                                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = prefs.edit();
                                                    editor.putString("WALLET_ID", walletId);
                                                    editor.putString("BALANCE", balance);
                                                    editor.apply();
                                                    System.out.println("Wallet Create Success: " + walletData.getWalletId());
                                                    Toast.makeText(SignUp.this, "Wallet Create Success", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                    intent.putExtra("source", "ActivitySignUp");
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    System.out.println("Wallet Create Failed: " + response.code());
                                                    Toast.makeText(SignUp.this, "Wallet Create Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                                System.out.println("Wallet Create Failed: " + t.getMessage());
                                                Toast.makeText(SignUp.this, "Wallet Create Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        });


                                    } else {
                                        System.out.println("Account Register Failed: " + response.code());
                                        Toast.makeText(SignUp.this, "Account Register Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<GeneralApiResponse<AccountResponse>> call, Throwable t) {
                                    System.out.println("Error: " + t.getMessage());
                                    Toast.makeText(SignUp.this, "Account Register Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            System.out.println("Register Failed: " + response.code());
                            Toast.makeText(SignUp.this, "Register Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralApiResponse<RegisterResponse>> call, Throwable t) {
                        System.out.println("Error: " + t.getMessage());
                        Toast.makeText(SignUp.this, "Register Failed: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void updateDisplay() {
        if (isHidden) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            visibility.setImageResource(R.drawable.visibility_off);
        } else {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            visibility.setImageResource(R.drawable.visibility);
        }
    }
    public void updateDisplay2() {
        if (isHidden) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            visibility2.setImageResource(R.drawable.visibility_off);
        } else {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            visibility2.setImageResource(R.drawable.visibility);
        }
    }
}