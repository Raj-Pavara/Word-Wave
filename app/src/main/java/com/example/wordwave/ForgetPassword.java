package com.example.wordwave;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        androidx.appcompat.widget.Toolbar forgetpassword_toolbar = findViewById(R.id.forgetpassword_toolbar);
        setSupportActionBar(forgetpassword_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}