package com.megabyte.payonapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class Transfer extends AppCompatActivity {
    private EditText et_recipient, et_amount, et_password, et_description;
    private ImageButton btn_open_contacts;
    private ImageView visibility;
    private TextView balance, account_number;
    private MaterialButton btn_confirm;
    private boolean isHidden = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer);

        // Initialize Views
        et_recipient = findViewById(R.id.et_recipient);
        btn_open_contacts = findViewById(R.id.btn_open_contacts);
        et_amount = findViewById(R.id.et_amount);
        et_password = findViewById(R.id.et_password);
        et_description = findViewById(R.id.et_description);
        balance = findViewById(R.id.balance);
        account_number = findViewById(R.id.account_number);
        btn_confirm = findViewById(R.id.btn_confirm);
        visibility = findViewById(R.id.visibility);

        getInfo();


        visibility.setOnClickListener(view -> {
            updateDisplay();
        });

        btn_open_contacts.setOnClickListener(view -> {
            Intent contactIntent = new Intent(Transfer.this, Contacts.class);
            startActivity(contactIntent);
        });

        // Get Intent Data from Contacts
        Intent intent = getIntent();
        String targetName = intent.getStringExtra("TARGET_NAME");
        String targetUserId = intent.getStringExtra("TARGET_USER_ID");
        String targetPhone = intent.getStringExtra("TARGET_PHONE");

        if (targetPhone != null) {
            et_recipient.setText(targetName + "\n" + targetPhone);
        }

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        btn_confirm.setOnClickListener(v -> {
            String amountStr = et_amount.getText().toString();
            BigDecimal amountDec = new BigDecimal(et_amount.getText().toString());
            BigDecimal balanceDec = new BigDecimal(balance.getText().toString().substring(2));
            String description = et_description.getText().toString();
            // Check fields
            if (et_recipient.getText().toString().isEmpty() || amountStr.isEmpty() || et_password.getText().toString().isEmpty()) {
                Toast.makeText(Transfer.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!validatePassword()) {
                Toast.makeText(Transfer.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            } else if (amountDec.compareTo(balanceDec) > 0) {
                Toast.makeText(Transfer.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
        } else {
                BigDecimal amountDecimal = new BigDecimal(amountStr);
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

                // Create Request
                TransactionRequest transactionRequest = new TransactionRequest(
                        Long.parseLong(prefs.getString("USER_ID", "0")),
                        Long.parseLong(targetUserId != null ? targetUserId : "0"),
                        5L,
                        amountDecimal,
                        description.isEmpty() ? "Transfer" : description
                );
                // Send transfer Request
                apiService.transfer(transactionRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionResponse>>() {
                    @Override
                    public void onResponse(Call<GeneralApiResponse<TransactionResponse>> call, Response<GeneralApiResponse<TransactionResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TransactionResponse transactionData = response.body().getData();
                            String transactionId = transactionData.getTransaction_id().toString();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Transfer.this);
                            builder.setTitle("Transaction Confirmation");
                            builder.setMessage("Do you want to confirm this transfer?");
                            builder.setPositiveButton("Confirm", (dialog, which) -> {
                                // Update status
                                apiService.transferStatus(transactionId, Status.COMPLETED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>() {
                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {
                                        if (response.isSuccessful()) {
                                            // Update balance
                                            apiService.getWallet(Long.parseLong(prefs.getString("USER_ID", "0"))).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                                @Override
                                                public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        String updatedBalance = response.body().getData().getBalance().toString();

                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("BALANCELOGGED", updatedBalance);
                                                        editor.putString("BALANCE", updatedBalance);
                                                        editor.apply();

                                                        Intent successIntent = new Intent(Transfer.this, Successful_Transfer.class);
                                                        successIntent.putExtra("TransactionId", transactionId);
                                                        successIntent.putExtra("Account", account_number.getText().toString());
                                                        successIntent.putExtra("Amount", amountStr);
                                                        successIntent.putExtra("Description", description);
                                                        successIntent.putExtra("Recipient", targetName);
                                                        successIntent.putExtra("PhoneNumber", targetPhone);
                                                        startActivity(successIntent);
                                                        finish();
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                                    Toast.makeText(Transfer.this, "Balance update failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {
                                        Toast.makeText(Transfer.this, "Status update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                            // Cancelation
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                apiService.transferStatus(transactionId, Status.CANCELLED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>() {
                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {
                                        Toast.makeText(Transfer.this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {}
                                });
                                dialog.dismiss();
                            });
                            builder.show();
                        } else {
                            Toast.makeText(Transfer.this, "Transfer failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralApiResponse<TransactionResponse>> call, Throwable t) {
                        Toast.makeText(Transfer.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
// Load user data from SharedPreferences
    public void getInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        String currentSource = intent.getStringExtra("source");
        String b, acc;
        if ("ActivitySignUp".equals(currentSource)) {
            b = sharedPreferences.getString("BALANCE", "0.00");
            acc = sharedPreferences.getString("ACCOUNT_NUMBER", "000000000000");
        } else {
            b = sharedPreferences.getString("BALANCELOGGED", "0.00");
            acc = sharedPreferences.getString("ACCOUNT_NUMBERLOGGED", "000000000000");
        }
        balance.setText("E£ " + b);
        account_number.setText(acc);
    }
// Validate password
    public boolean validatePassword() {
        String password = et_password.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        String currentSource = intent.getStringExtra("source");
        String storedPassword = "ActivitySignUp".equals(currentSource) ?
                sharedPreferences.getString("PASSWORD", "") :
                sharedPreferences.getString("PASSWORDLOGGED", "");
        return password.equals(storedPassword);
    }
    // Update password visibility
    public void updateDisplay() {
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