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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.Status;
import com.megabyte.payonapplication.DTO.TransactionRequest;
import com.megabyte.payonapplication.DTO.TransactionResponse;
import com.megabyte.payonapplication.DTO.TransactionStatusResponse;
import com.megabyte.payonapplication.DTO.WalletResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

public class Withdraw extends AppCompatActivity {
private EditText et_amount;
private EditText et_password;
private ImageView visibility;
private MaterialButton btn_confirm;
private TextView balance;
private TextView account_number;
private boolean isHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_withdraw);
        et_amount = findViewById(R.id.et_amount);
        et_password = findViewById(R.id.et_password);
        visibility = findViewById(R.id.visibility);
        btn_confirm = findViewById(R.id.btn_confirm);
        balance = findViewById(R.id.balance);
        account_number = findViewById(R.id.account_number);
        getInfo();


        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        visibility.setOnClickListener(view -> {
            updateDisplay();
        });
        btn_confirm.setOnClickListener(view -> {
            if (et_amount.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()) {
                Toast.makeText(Withdraw.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else if (!validatePassword()) {
                Toast.makeText(Withdraw.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }else {
                String amount = et_amount.getText().toString();
                BigDecimal amountDecimal = new BigDecimal(amount);
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                TransactionRequest transactionRequest = new TransactionRequest(Long.parseLong(prefs.getString("USER_ID", "0")), null, 5L, amountDecimal, "Withdraw");
                apiService.withdraw(transactionRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionResponse>>() {

                    @Override
                    public void onResponse(Call<GeneralApiResponse<TransactionResponse>> call, Response<GeneralApiResponse<TransactionResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TransactionResponse transactionData = response.body().getData();
                            String transactionId = transactionData.getTransaction_id().toString();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Withdraw.this);
                            builder.setTitle("Transaction Confirmation");
                            builder.setMessage("Do you want to confirm this transaction?");
                            builder.setPositiveButton("Confirm", (dialog, which) -> {
                                apiService.withdrawStatus(transactionId, Status.COMPLETED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>() {

                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            apiService.getWallet(Long.parseLong(prefs.getString("USER_ID", "0"))).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                                @Override
                                                public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        String updatedBalance = response.body().getData().getBalance().toString();

                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("BALANCELOGGED", updatedBalance);
                                                        editor.putString("BALANCE", updatedBalance);
                                                        editor.apply();

                                                        Intent intent = new Intent(Withdraw.this, Successful_Withdraw.class);
                                                        intent.putExtra("TransactionId", transactionId);
                                                        intent.putExtra("Account", account_number.getText().toString());
                                                        intent.putExtra("Amount", amount);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                                    Toast.makeText(Withdraw.this, "Balance update failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Intent intent = new Intent(Withdraw.this, Opreation_Failed.class);
                                            intent.putExtra("ERROR_MSG", "Server Error: " + response.code());
                                            intent.putExtra("source", "ActivityWithdraw");
                                            startActivity(intent);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {
                                        Toast.makeText(Withdraw.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                                        System.out.println(t.getMessage());
                                    }
                                });

                            });
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                apiService.withdrawStatus(transactionId, Status.CANCELLED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>() {
                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Toast.makeText(Withdraw.this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {

                                    }
                                });
                                dialog.dismiss();
                            });
                            builder.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralApiResponse<TransactionResponse>> call, Throwable t) {
                    Toast.makeText(Withdraw.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        System.out.println(t.getMessage());

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
    public boolean validatePassword(){
        String password = et_password.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        String currentSource = intent.getStringExtra("source");
        if ("ActivitySignUp".equals(currentSource)) {
            String storedPassword = sharedPreferences.getString("PASSWORD", "");
            return password.equals(storedPassword);
        } else {
            String storedPassword = sharedPreferences.getString("PASSWORDLOGGED", "");
            return password.equals(storedPassword);
        }


    }
    public void getInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
       String currentSource = intent.getStringExtra("source");
        if ("ActivitySignUp".equals(currentSource)) {
            balance.setText(sharedPreferences.getString("BALANCE", "0.00"));
            account_number.setText(sharedPreferences.getString("ACCOUNT_NUMBER", "000000000000"));
        } else {
            balance.setText(sharedPreferences.getString("BALANCELOGGED", "0.00"));
            account_number.setText(sharedPreferences.getString("ACCOUNT_NUMBERLOGGED", "000000000000"));
        }
    }    public void updateDisplay() {
        if (isHidden) {
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            visibility.setImageResource(R.drawable.visibility);
            isHidden = false;
        } else {
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            visibility.setImageResource(R.drawable.visibility_off);
            isHidden = true;
        }

        et_password.setSelection(et_password.getText().length());
    }
}