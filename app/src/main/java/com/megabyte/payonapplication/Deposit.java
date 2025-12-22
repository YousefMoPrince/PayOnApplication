package com.megabyte.payonapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.TransactionRequest;
import com.megabyte.payonapplication.DTO.TransactionResponse;
import com.megabyte.payonapplication.DTO.WalletResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

public class Deposit extends AppCompatActivity {
    private EditText etAmount, etPassword;
    private TextView accountNumber, balance;
    private MaterialButton btnConfirm;
    private ImageView visibility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposit);

        etAmount = findViewById(R.id.et_amount);
        etPassword = findViewById(R.id.et_password);
        accountNumber = findViewById(R.id.account_number);
        balance = findViewById(R.id.balance);
        btnConfirm = findViewById(R.id.btn_confirm);
        visibility = findViewById(R.id.visibility);
        loadUserData();
        visibility.setOnClickListener(view -> {
            visibility.setImageResource(R.drawable.visibility);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        });
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        btnConfirm.setOnClickListener(view -> {
            String amountStr = etAmount.getText().toString();
            String passStr = etPassword.getText().toString();

            if (amountStr.isEmpty() || passStr.isEmpty()) {
                Toast.makeText(Deposit.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                BigDecimal amount = new BigDecimal(amountStr);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    Toast.makeText(Deposit.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                } else if (!validPassword()){
                    Toast.makeText(Deposit.this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                } else {
                    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                    TransactionRequest transactionRequest = new TransactionRequest(Long.parseLong(prefs.getString("USER_ID", "0")), null, 5L, amount, "Deposit");

                    apiService.deposit(transactionRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionResponse>>() {
                        @Override
                        public void onResponse(Call<GeneralApiResponse<TransactionResponse>> call, Response<GeneralApiResponse<TransactionResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                TransactionResponse transactionData = response.body().getData();
                                String transactionId = transactionData.getTransaction_id().toString();
                                String amountRes = transactionData.getAmount().toString();

                                apiService.getWallet(Long.parseLong(prefs.getString("USER_ID", "0"))).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            String updatedBalance = response.body().getData().getBalance().toString();

                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("BALANCELOGGED", updatedBalance);
                                            editor.putString("BALANCE", updatedBalance);
                                            editor.apply();

                                            Intent intent = new Intent(Deposit.this, Successful_Deposit.class);
                                            intent.putExtra("TransactionId", transactionId);
                                            intent.putExtra("Account", accountNumber.getText().toString());
                                            intent.putExtra("Amount", amountRes);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                        Toast.makeText(Deposit.this, "Balance update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Intent intent = new Intent(Deposit.this, Opreation_Failed.class);
                                intent.putExtra("ERROR_MSG", "Server Error: " + response.code());
                                intent.putExtra("source", "ActivityDeposit");
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralApiResponse<TransactionResponse>> call, Throwable t) {
                            Intent intent = new Intent(Deposit.this, Opreation_Failed.class);
                            intent.putExtra("ERROR_MSG", t.getMessage());
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserData() {
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        if ("ActivitySignUp".equals(source)) {
            balance.setText(prefs.getString("BALANCE", "0.00"));
            accountNumber.setText(prefs.getString("ACCOUNT_NUMBER", "000000000000"));
        } else {
            balance.setText(prefs.getString("BALANCELOGGED", "0.00"));
            accountNumber.setText(prefs.getString("ACCOUNT_NUMBERLOGGED", "000000000000"));
        }
    }

    private boolean validPassword() {
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String enteredPassword = etPassword.getText().toString();
        String savedPassword = "ActivitySignUp".equals(source) ? prefs.getString("PASSWORD", "") : prefs.getString("PASSWORDLOGGED", "");
        return enteredPassword.equals(savedPassword);
    }
}