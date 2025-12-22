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

public class Successful_Withdraw extends AppCompatActivity {
private TextView trans_val,amount_val,account_val;
private MaterialButton btn_back_home,btn_another_payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successful_withdraw);
        trans_val=findViewById(R.id.trans_val);
        amount_val=findViewById(R.id.amount_val);
        account_val=findViewById(R.id.account_val);
        btn_back_home=findViewById(R.id.btn_back_home);
        btn_another_payment=findViewById(R.id.btn_another_payment);
        btn_back_home.setOnClickListener(view -> {
            Intent intent = new Intent(Successful_Withdraw.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        btn_another_payment.setOnClickListener(view -> {
            Intent intent = new Intent(Successful_Withdraw.this, Withdraw.class);
            startActivity(intent);
            finish();
        });
        Intent intent = getIntent();

        trans_val.setText(intent.getStringExtra("TransactionId"));

        amount_val.setText(intent.getStringExtra("Amount"));

        account_val.setText(intent.getStringExtra("Account"));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}