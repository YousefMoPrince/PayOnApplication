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
import com.megabyte.payonapplication.DTO.AccountResponse;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.LoginRequest;
import com.megabyte.payonapplication.DTO.LoginResponse;
import com.megabyte.payonapplication.DTO.WalletResponse;

import retrofit2.Call;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
    private Button signInButton;
    private EditText useremail;
    private EditText password;
    private TextView signUpText ;
    private ImageView visibility;
    private boolean isHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        signUpText = findViewById(R.id.signUpText);
        signInButton = findViewById(R.id.signInButton);
        useremail = findViewById(R.id.useremail);
        password = findViewById(R.id.password);
        visibility = findViewById(R.id.visibility);
        visibility.setOnClickListener(view -> {
            updateDisplay();
        });
        signUpText.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
        });
        signInButton.setOnClickListener(view -> {
            if (useremail.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(SignIn.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();


            }else {
                //make login request
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                LoginRequest loginRequest = new LoginRequest(useremail.getText().toString(), password.getText().toString());
                System.out.println(new Gson().toJson(loginRequest));

                apiService.login(loginRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<GeneralApiResponse<LoginResponse>> call, Response<GeneralApiResponse<LoginResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            //handle response data
                            LoginResponse loginData = response.body().getData();
                            String message = response.body().getMessage();

                            if (loginData == null) {
                                Toast.makeText(SignIn.this, message != null ? message : "Login failed.", Toast.LENGTH_SHORT).show();
                                System.out.println("Login Failed: " + response.code() + " - Message: " + message);
                                return;
                            }

                            String userId = loginData.getUserId();
                            String username = loginData.getUsername();
                            String phone = loginData.getPhone();
                            String password = loginData.getPassword();
                            //save user data in mobile phone(local data)
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("USER_ID", userId);
                            editor.putString("USERNAMELOGGED", username);
                            editor.putString("PHONELOGGED", phone);
                            editor.putString("PASSWORDLOGGED", password);
                            editor.apply();
                            System.out.println("Login Success: " + userId);
                            Toast.makeText(SignIn.this, "Login Success", Toast.LENGTH_SHORT).show();
                            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                            apiService.findAccount(Long.parseLong(userId)).enqueue(new retrofit2.Callback<GeneralApiResponse<AccountResponse>>() {
                                @Override
                                public void onResponse(Call<GeneralApiResponse<AccountResponse>> call, Response<GeneralApiResponse<AccountResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                    AccountResponse accountData = response.body().getData();
                                        String message = response.body().getMessage();
                                        if (accountData != null) {
                                            String holderName = accountData.getAccountHolder();
                                            String bank = accountData.getBankName();
                                            String number = accountData.getAccountNumber();
                                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("BANK_NAMELOGGED", bank);
                                            editor.putString("ACCOUNT_NUMBERLOGGED", number);
                                            editor.apply();
                                            Toast.makeText(SignIn.this, "Found: " + holderName, Toast.LENGTH_SHORT).show();
                                            apiService.getWallet(Long.parseLong(userId)).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                                @Override
                                                public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        WalletResponse walletData = response.body().getData();
                                                        String message = response.body().getMessage();
                                                        if (walletData != null) {
                                                            String balance = walletData.getBalance().toString();
                                                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = prefs.edit();
                                                            editor.putString("BALANCELOGGED", balance);
                                                            editor.apply();
                                                            Toast.makeText(SignIn.this, "Wallet Found", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                                                            intent.putExtra("source", "ActivitySignIn");
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            System.out.println("Wallet not found: " + response.code() + " - Message: " + message);
                                                            Toast.makeText(SignIn.this, message != null ? message : "Wallet not found", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                                    System.out.println("Error: " + t.getMessage());
                                                    Toast.makeText(SignIn.this, "Wallet not found: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            System.out.println("Account not found: " + response.code() + " - Message: " + message);
                                            Toast.makeText(SignIn.this, message != null ? message : "Account not found", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<GeneralApiResponse<AccountResponse>> call, Throwable t) {
                                    System.out.println("Error: " + t.getMessage());
                                    Toast.makeText(SignIn.this, "Account not found: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            System.out.println("Login Failed: " + response.code());
                            Toast.makeText(SignIn.this, "Login Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralApiResponse<LoginResponse>> call, Throwable t) {
                        System.out.println("Error: " + t.getMessage());
                        Toast.makeText(SignIn.this, "Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            visibility.setImageResource(R.drawable.visibility);
            isHidden = false;
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            visibility.setImageResource(R.drawable.visibility_off);
            isHidden = true;
        }

        password.setSelection(password.getText().length());
    }
}