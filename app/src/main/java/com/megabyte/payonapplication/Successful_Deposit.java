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

public class Successful_Deposit extends AppCompatActivity {
private TextView amount, account, trans;
private MaterialButton btn_back_home, btn_another_payment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successful_deposit);
        amount = findViewById(R.id.tv_amount_val);
        account = findViewById(R.id.tv_account_val);
        trans = findViewById(R.id.tv_trans_val);
        btn_back_home = findViewById(R.id.btn_back_home);
        btn_another_payment = findViewById(R.id.btn_another_payment);
        Intent intent = getIntent();
        amount.setText("E£ "+intent.getStringExtra("Amount"));
        account.setText(intent.getStringExtra("Account"));
        trans.setText(intent.getStringExtra("TransactionId"));
        btn_back_home.setOnClickListener(view -> {
            Intent intent1 = new Intent(Successful_Deposit.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });
        btn_another_payment.setOnClickListener(view -> {
            Intent intent2 = new Intent(Successful_Deposit.this, Deposit.class);
            startActivity(intent2);
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}