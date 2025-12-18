package com.megabyte.payonapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Contacts extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    Intent intent = getIntent();
    String currentSource = intent.getStringExtra("source");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_contacts);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_contacts) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(Contacts.this, MainActivity.class);
                intent.putExtra("source", currentSource);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_account) {
                Intent intent = new Intent(Contacts.this, Account.class);
                intent.putExtra("source", currentSource);
                startActivity(intent);
                return true;
            }
            return false;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}