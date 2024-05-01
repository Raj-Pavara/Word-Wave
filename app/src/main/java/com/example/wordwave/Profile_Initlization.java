package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile_Initlization extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_initlization);

    }
    public void Buttonclicked(View view){
        String str = getIntent().getExtras().getString("sign");
        String str2 = getIntent().getExtras().getString("isNewUser");

        if(str.equals("signUp")){
            if(str2.equals("true")){
                Toast.makeText(this, "new user", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "old user", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(Profile_Initlization.this,Authentication.class));
            finish();
        }
        else{
            Toast.makeText(this, "sign in new user", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Profile_Initlization.this,MainActivity.class));
            finish();
        }
    }
}