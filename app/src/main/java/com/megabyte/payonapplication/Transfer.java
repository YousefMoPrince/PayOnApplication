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
    private EditText et_recipient;
    private ImageButton btn_open_contacts;
    private EditText et_amount;
    private EditText et_password;
    private ImageView visibility;
    private TextView balance;
    private TextView account_number;
    private EditText et_description;
    private MaterialButton btn_confirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer);
        et_recipient = findViewById(R.id.et_recipient);
        btn_open_contacts = findViewById(R.id.btn_open_contacts);
        et_amount = findViewById(R.id.et_amount);
        et_password = findViewById(R.id.et_password);
        et_description = findViewById(R.id.et_description);
        balance = findViewById(R.id.balance);
        account_number = findViewById(R.id.account_number);
        btn_confirm = findViewById(R.id.btn_confirm);
        visibility =  findViewById(R.id.visibility);
        String amount = et_amount.getText().toString();
        BigDecimal amountDecimal = new BigDecimal(amount);
        String balanceStr = balance.getText().toString();
        String description = et_description.getText().toString();
        getInfo();
        visibility.setOnClickListener(view -> {
            visibility.setImageResource(R.drawable.visibility);
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        });
        btn_open_contacts.setOnClickListener(view -> {
            Intent intent = new Intent(Transfer.this, Contacts.class);
            startActivity(intent);
        });
        Intent intent = getIntent();
        String targetName = intent.getStringExtra("TARGET_NAME");

        String targetUserId = intent.getStringExtra("TARGET_USER_ID");

        String targetPhone = intent.getStringExtra("TARGET_PHONE");
        et_recipient.setText(targetName + "\n" + targetPhone);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        btn_confirm.setOnClickListener(v -> {
        if (et_recipient.getText().toString().isEmpty() || et_amount.getText().toString().isEmpty() || et_password.getText().toString().isEmpty()) {
            Toast.makeText(Transfer.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else if (!validatePassword()) {
            Toast.makeText(Transfer.this, "Incorrect password", Toast.LENGTH_SHORT).show();
        } else if (amount.compareTo(balanceStr) >= 0) {
            Toast.makeText(Transfer.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
        } else {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            TransactionRequest transactionRequest = new TransactionRequest(Long.parseLong(prefs.getString("USER_ID", "0")), Long.parseLong(targetUserId), 5L, amountDecimal, description);
            apiService.transfer(transactionRequest).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionResponse>>() {
                @Override
                public void onResponse(Call<GeneralApiResponse<TransactionResponse>> call, Response<GeneralApiResponse<TransactionResponse>> response) {
                    TransactionResponse transactionData = response.body().getData();
                    String transactionId = transactionData.getTransaction_id().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Transfer.this);
                    builder.setTitle("Transaction Confirmation");
                    builder.setMessage("Do you want to confirm this transaction?");
                    builder.setPositiveButton("Confirm", (dialog, which) -> {
                        apiService.transferStatus(transactionId, Status.COMPLETED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>() {

                            @Override
                            public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {

                                apiService.getWallet(Long.parseLong(prefs.getString("USER_ID", "0"))).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
                                    @Override
                                    public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            String updatedBalance = response.body().getData().getBalance().toString();

                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString("BALANCELOGGED", updatedBalance);
                                            editor.putString("BALANCE", updatedBalance);
                                            editor.apply();

                                            Intent intent = new Intent(Transfer.this, Successful_Transfer.class);
                                            intent.putExtra("TransactionId", transactionId);
                                            intent.putExtra("Account", account_number.getText().toString());
                                            intent.putExtra("Amount", et_amount.getText().toString());
                                            intent.putExtra("Description", et_description.getText().toString());
                                            intent.putExtra("Recipient", targetName);
                                            intent.putExtra(("PhoneNumber"), targetPhone);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                                        Toast.makeText(Transfer.this, "Balance update failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {
                                Intent intent = new Intent(Transfer.this, Opreation_Failed.class);
                                intent.putExtra("ERROR_MSG", "Server Error: " + response.code());
                                intent.putExtra("source", "ActivityTransfer");
                                startActivity(intent);
                            Toast.makeText(Transfer.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                                System.out.println(t.getMessage());
                            }
                        });
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        apiService.transferStatus(transactionId, Status.CANCELLED).enqueue(new retrofit2.Callback<GeneralApiResponse<TransactionStatusResponse>>(){

                            @Override
                            public void onResponse(Call<GeneralApiResponse<TransactionStatusResponse>> call, Response<GeneralApiResponse<TransactionStatusResponse>> response) {
                                Toast.makeText(Transfer.this, "Transaction cancelled", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Transfer.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<GeneralApiResponse<TransactionStatusResponse>> call, Throwable t) {
                            System.out.println(t.getMessage());
                            }
                        });
                    });
                }

                @Override
                public void onFailure(Call<GeneralApiResponse<TransactionResponse>> call, Throwable t) {
                Toast.makeText(Transfer.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Transfer.this, Opreation_Failed.class);
                intent.putExtra("ERROR_MSG", "Server Error: " + t.getMessage());
                startActivity(intent);
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
    public void getInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Intent intent = getIntent();
        String currentSource = intent.getStringExtra("source");
        if ("ActivitySignUp".equals(currentSource)) {
            String balance = sharedPreferences.getString("BALANCE", "0.00");
            String account_number = sharedPreferences.getString("ACCOUNT_NUMBER", "000000000000");
            this.balance.setText("E£ " + balance);
            this.account_number.setText(account_number);
        }else {
            String balance = sharedPreferences.getString("BALANCELOGGED", "0.00");
            String account_number = sharedPreferences.getString("ACCOUNT_NUMBERLOGGED", "000000000000");
            this.balance.setText("E£ " + balance);
            this.account_number.setText(account_number);
        }
    }
    public boolean validatePassword(){
        String password = et_password.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
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
}