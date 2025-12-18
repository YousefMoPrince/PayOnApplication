package com.megabyte.payonapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Opreation_Failed extends AppCompatActivity {
private MaterialButton btn_back_home, btn_retry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opreation_failed);
        btn_back_home = findViewById(R.id.btn_back_home);
        btn_retry = findViewById(R.id.btn_retry);
btn_back_home.setOnClickListener(view -> {
    Intent intent = new Intent(Opreation_Failed.this, MainActivity.class);
    startActivity(intent);
    finish();
});

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}