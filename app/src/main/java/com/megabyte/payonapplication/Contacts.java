package com.megabyte.payonapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.megabyte.payonapplication.DTO.ContactRequest;
import com.megabyte.payonapplication.DTO.ContactResponse;
import com.megabyte.payonapplication.DTO.GeneralApiResponse;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Contacts extends AppCompatActivity {

    private ListView lvContacts;
    private EditText etSearch;
    private BottomNavigationView bottomNavigationView;

    private List<ContactResponse> registeredUsers = new ArrayList<>();
    private ArrayList<String> displayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Map<String, String> phoneToNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        lvContacts = findViewById(R.id.lv_contacts);
        etSearch = findViewById(R.id.et_search);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            if (registeredUsers != null && !registeredUsers.isEmpty()) {
                ContactResponse selectedUser = registeredUsers.get(position);
                Intent intent = new Intent(Contacts.this, Transfer.class);
                intent.putExtra("TARGET_USER_ID", selectedUser.getUserId());
                intent.putExtra("TARGET_PHONE", selectedUser.getPhone());
                String nameOnPhone = phoneToNameMap.get(selectedUser.getPhone());
                intent.putExtra("TARGET_NAME", nameOnPhone != null ? nameOnPhone : "PayOn User");
                startActivity(intent);
                finish();
            }
        });

        setupNavigation();
        checkPermissionAndSync();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.getFilter().filter(s);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void checkPermissionAndSync() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            fetchLocalContactsAndSync();
        }
    }

    private void fetchLocalContactsAndSync() {
        List<String> numbersToSync = new ArrayList<>();
        phoneToNameMap.clear();
        ContentResolver cr = getContentResolver();

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)) {
            if (cursor == null) {
                Toast.makeText(this, "Could not fetch contacts.", Toast.LENGTH_SHORT).show();
                return;
            }

            int nameColumnIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberColumnIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameColumnIndex);
                String number = cursor.getString(numberColumnIndex);
                if (number == null || number.isEmpty()) continue;

                String cleanNumber = number.replaceAll("[\\s\\-\\(\\)\\+]", "");

                if (cleanNumber.startsWith("201") && cleanNumber.length() == 12) {
                    cleanNumber = "0" + cleanNumber.substring(2);
                } else if (cleanNumber.startsWith("1") && cleanNumber.length() == 10) {
                    cleanNumber = "0" + cleanNumber;
                }

                if (cleanNumber.length() == 11 && cleanNumber.startsWith("01")) {
                    if (!numbersToSync.contains(cleanNumber)) {
                        numbersToSync.add(cleanNumber);
                        phoneToNameMap.put(cleanNumber, name);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ContactsError", "Error reading contacts", e);
        }

        if (!numbersToSync.isEmpty()) {
            List<String> limitedNumbers = numbersToSync.subList(0, Math.min(numbersToSync.size(), 100));
            syncWithBackend(limitedNumbers);
        } else {
            registeredUsers.clear();
            showRegisteredUsers();
        }
    }

    private void syncWithBackend(List<String> numbers) {
        ContactRequest request = new ContactRequest();
        request.setContactNumbers(numbers);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.syncContacts(request).enqueue(new Callback<GeneralApiResponse<List<ContactResponse>>>() {
            @Override
            public void onResponse(Call<GeneralApiResponse<List<ContactResponse>>> call, Response<GeneralApiResponse<List<ContactResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registeredUsers = response.body().getData();
                    showRegisteredUsers();
                } else {
                    Toast.makeText(Contacts.this, "Sync failed: Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeneralApiResponse<List<ContactResponse>>> call, Throwable t) {
                Log.e("API_ERROR", "Reason: " + t.getMessage());
                Toast.makeText(Contacts.this, "Network error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegisteredUsers() {
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, R.layout.contact_item, R.id.tv_contact_name, displayList);
            lvContacts.setAdapter(adapter);
        }

        displayList.clear();
        if (registeredUsers != null && !registeredUsers.isEmpty()) {
            for (ContactResponse user : registeredUsers) {
                String nameOnPhone = phoneToNameMap.get(user.getPhone());
                String finalDisplay = (nameOnPhone != null ? nameOnPhone : "PayOn User") + "\n" + user.getPhone();
                displayList.add(finalDisplay);
            }
        } else {
            displayList.add("No contacts found on PayOn");
        }
        adapter.notifyDataSetChanged();
    }

    private void setupNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_contacts);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_contacts) return true;
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_account) {
                startActivity(new Intent(this, Account.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocalContactsAndSync();
        } else {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }
}