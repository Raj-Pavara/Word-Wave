package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PhoneNumber_Authentication extends AppCompatActivity {

    private com.hbb20.CountryCodePicker countryCodePicker;
    private EditText phonenumber;                //phonenumber is actual phone number edittext which is input by user.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_number_authentication);

        initlization();
    }


    protected void initlization() {
        androidx.appcompat.widget.Toolbar phonenumber_toolbar = findViewById(R.id.phonenumber_toolbar);
        setSupportActionBar(phonenumber_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countryCodePicker = findViewById(R.id.contryCodePicker);
        phonenumber = findViewById(R.id.phonenumber);
        countryCodePicker.registerCarrierNumberEditText(phonenumber);

    }


    public void getOTP_OnClick(View view) {
        if (!countryCodePicker.isValidFullNumber()) {                                      //return true if phone number is valid (10 digit and only numbers)
            phonenumber.setError("invalid phone number");
            return;
        }

        Intent intent = new Intent(PhoneNumber_Authentication.this, OTP_Verification.class);
        intent.putExtra("phonenumber", countryCodePicker.getFullNumberWithPlus());               //return number with contry code +916352008602 like this.
        intent.putExtra("sign", getIntent().getExtras().getString("sign"));           //send sign in / sing up
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}