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

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView user, balance, account_number;
    private ImageView visibility, transfer, withdraw, deposit;


    private boolean isHidden = true;
    private String realBalance = "";
    private String realAccountNumber = "";
    private String currentSource = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        loadUserData();
        updateDisplay();
        setupClickListeners();
        applyWindowInsets();
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
        Intent intent = getIntent();
        currentSource = intent.getStringExtra("source");
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        if ("ActivitySignUp".equals(currentSource)) {
            realBalance = prefs.getString("BALANCE", "0.00");
            realAccountNumber = prefs.getString("ACCOUNT_NUMBER", "000000000000");
            user.setText(prefs.getString("USERNAME", "User"));
        } else {
            realBalance = prefs.getString("BALANCELOGGED", "0.00");
            realAccountNumber = prefs.getString("ACCOUNT_NUMBERLOGGED", "000000000000");
            user.setText(prefs.getString("USERNAMELOGGED", "User"));
        }
    }

    private void setupClickListeners() {
        visibility.setOnClickListener(v -> {
            isHidden = !isHidden;
            updateDisplay();
        });
        deposit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Deposit.class);
            intent.putExtra("source", currentSource);
            startActivity(intent);
        });


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