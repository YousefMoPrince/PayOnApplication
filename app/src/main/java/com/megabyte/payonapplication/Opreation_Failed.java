package com.megabyte.payonapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Opreation_Failed extends AppCompatActivity {
    private MaterialButton btn_back_home, btn_retry;
    private TextView error_description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opreation_failed);
        btn_back_home = findViewById(R.id.btn_back_home);
        btn_retry = findViewById(R.id.btn_retry);
        error_description = findViewById(R.id.error_description);
        Intent intent = getIntent();
        String errorMessage = intent.getStringExtra("ERROR_MSG");
        showError(errorMessage);
        btn_back_home.setOnClickListener(view -> {
            Intent intent1 = new Intent(Opreation_Failed.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
        btn_retry.setOnClickListener(view -> {
        retry();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showError(String errorMessage) {
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if ("ActivityWithdraw".equals(source)) {
        error_description.setText(errorMessage);}
        else if ("ActivityDeposit".equals(source)) {
        error_description.setText(errorMessage);}
        else if ("ActivityTransfer".equals(source)) {
        error_description.setText(errorMessage);}

    }
    public void retry() {
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if ("ActivityWithdraw".equals(source)) {
            Intent intent1 = new Intent(Opreation_Failed.this, Withdraw.class);
            startActivity(intent1);
            finish();}
        else if ("ActivityDeposit".equals(source)) {
            Intent intent1 = new Intent(Opreation_Failed.this, Deposit.class);
            startActivity(intent1);
            finish();}
        else if ("ActivityTransfer".equals(source)) {
            Intent intent1 = new Intent(Opreation_Failed.this, Transfer.class);
            startActivity(intent1);
            finish();}
    }
}