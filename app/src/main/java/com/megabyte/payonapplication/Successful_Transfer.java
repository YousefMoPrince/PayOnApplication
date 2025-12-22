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

public class Successful_Transfer extends AppCompatActivity {
private MaterialButton btn_home, btn_another;

private TextView  recipient_name,  phone_val,  amount_val,  account_val,  transaction_val,  note_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successful_transfer);
        btn_home = findViewById(R.id.btn_home);
        btn_another = findViewById(R.id.btn_another);
        recipient_name = findViewById(R.id.recipient_name);
        phone_val = findViewById(R.id.phone_val);
        amount_val = findViewById(R.id.amount_val);
        account_val = findViewById(R.id.account_val);
        transaction_val = findViewById(R.id.transaction_val);
        note_val = findViewById(R.id.note_val);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(Successful_Transfer.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        btn_another.setOnClickListener(view -> {
            Intent intent = new Intent(Successful_Transfer.this, Transfer.class);
            startActivity(intent);
            finish();
        });
        Intent intent = getIntent();
        String recipient = intent.getStringExtra("Recipient");
        String phone = intent.getStringExtra("PhoneNumber");
        String amount = intent.getStringExtra("Amount");
        String account = intent.getStringExtra("Account");
        String transaction = intent.getStringExtra("TransactionId");
        String note = intent.getStringExtra("Description");
        recipient_name.setText(recipient);
        phone_val.setText(phone);
        amount_val.setText(amount);
        account_val.setText(account);
        transaction_val.setText(transaction);
        note_val.setText(note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}