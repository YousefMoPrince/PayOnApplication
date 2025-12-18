package com.megabyte.payonapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Account extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView username,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(Account.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_contacts) {
                startActivity(new Intent(Account.this, Contacts.class));
                return true;
            } else if (itemId == R.id.navigation_account) {
                startActivity(new Intent(Account.this, Account.class));

                return true;
            }
            return false;
        });
        loadUserData();

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
        username.setText(prefs.getString("USERNAME", "User"));
        phone.setText(prefs.getString("PHONE", "00000000000"));
        } else {
            username.setText(prefs.getString("USERNAMELOGGED", "User"));
            phone.setText(prefs.getString("PHONELOGGED", "00000000000"));
        }
    }
}