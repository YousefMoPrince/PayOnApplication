package com.megabyte.payonapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;
import com.megabyte.payonapplication.DTO.WalletResponse;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView user, balance, account_number;
    private ImageView visibility, transfer, withdraw, deposit;

    private boolean isHidden = true;
    private String realBalance = "0.00";
    private String realAccountNumber = "";
    private String currentSource = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
        applyWindowInsets();

        loadUserData();
    }

    private void initViews() {
        user = findViewById(R.id.username);
        balance = findViewById(R.id.balance);
        account_number = findViewById(R.id.account_number);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        visibility = findViewById(R.id.visibility);
        deposit = findViewById(R.id.deposit_image);
        withdraw = findViewById(R.id.withdraw_image);
        transfer = findViewById(R.id.transfer_image);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String source = getIntent().getStringExtra("source");

        String userId;
        String username;
    //load data from phone storage
        if (source != null && source.equals("ActivitySignIn")) {
            userId = prefs.getString("USER_ID", "0");
            username = prefs.getString("USERNAMELOGGED", "User");
            realAccountNumber = prefs.getString("ACCOUNT_NUMBERLOGGED", "000000000000");
            realBalance = prefs.getString("BALANCELOGGED", "0.00");
            currentSource = "ActivitySignIn";
        } else if (source != null && source.equals("ActivitySignUp")) {
            // تصحيح else لتصبح else if
            userId = prefs.getString("USER_ID", "0");
            username = prefs.getString("USERNAME", "User");
            realAccountNumber = prefs.getString("ACCOUNT_NUMBER", "000000000000");
            realBalance = prefs.getString("BALANCE", "0.00");
            currentSource = "ActivitySignUp";
        } else {
            userId = prefs.getString("USER_ID", "0");
            username = prefs.getString("USERNAMELOGGED", "User");
            realAccountNumber = prefs.getString("ACCOUNT_NUMBERLOGGED", "000000000000");
            realBalance = prefs.getString("BALANCELOGGED", "0.00");
        }

        user.setText(username);
        updateDisplay();

        // تحديث الرصيد من السيرفر
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getWallet(Long.parseLong(userId)).enqueue(new retrofit2.Callback<GeneralApiResponse<WalletResponse>>() {
            @Override
            public void onResponse(Call<GeneralApiResponse<WalletResponse>> call, Response<GeneralApiResponse<WalletResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    realBalance = response.body().getData().getBalance().toString();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("BALANCELOGGED", realBalance);
                    editor.putString("BALANCE", realBalance);
                    editor.apply();

                    updateDisplay();
                }
            }

            @Override
            public void onFailure(Call<GeneralApiResponse<WalletResponse>> call, Throwable t) {
                updateDisplay();
            }
        });
    }
    //on click functions
    private void setupClickListeners() {
        visibility.setOnClickListener(v -> {
            isHidden = !isHidden;
            updateDisplay();
        });

        withdraw.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Withdraw.class);
            intent.putExtra("source", currentSource);
            startActivity(intent);
        });

        deposit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Deposit.class);
            intent.putExtra("source", currentSource);
            startActivity(intent);
        });

        transfer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Transfer.class);
            intent.putExtra("source", currentSource);
            startActivity(intent);
        });
        //set bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_contacts) {
                Intent intent = new Intent(MainActivity.this, Contacts.class);
                intent.putExtra("source", currentSource);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_account) {
                Intent intent = new Intent(MainActivity.this, Account.class);
                intent.putExtra("source", currentSource);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void updateDisplay() {
        if (isHidden) {
            balance.setText("********");
            account_number.setText("********");
            visibility.setImageResource(R.drawable.visibility_off);
        } else {
            balance.setText("E£ " + realBalance);
            account_number.setText(realAccountNumber);
            visibility.setImageResource(R.drawable.visibility);
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}