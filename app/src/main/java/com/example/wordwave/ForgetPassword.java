package com.example.wordwave;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {


    private EditText forgetpassword_email;
    private TextView forgetpassword_textview;
    private ProgressBar progressBar_forgetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        androidx.appcompat.widget.Toolbar forgetpassword_toolbar = findViewById(R.id.forgetpassword_toolbar);
        setSupportActionBar(forgetpassword_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        forgetpassword_textview = findViewById(R.id.forgetpassword_textview);
        forgetpassword_email = findViewById(R.id.forgetpassword_email);
        progressBar_forgetpassword = findViewById(R.id.progressBar_forgetpassword);

        forgetpassword_textview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String Email = forgetpassword_email.getText().toString();
                        inProgress(true);
                        if (Email.equals("")) {
                            forgetpassword_email.setError("Enter Email");
                            inProgress(false);
                        } else if ((!Email.contains("@")) || (!Email.contains("."))) {
                            forgetpassword_email.setError("Invalid Email");
                            inProgress(false);
                        } else {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(Email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ToastMaker.show(getApplicationContext(),"code sent to your email");
                                            }
                                            else{
                                                ToastMaker.show(getApplicationContext(),"failed to sent code");
                                            }
                                            inProgress(false);
                                        }
                                    });
                        }
                    }
                }
        );


    }

    protected void inProgress(boolean isProgress){
        if(isProgress){
            progressBar_forgetpassword.setVisibility(View.VISIBLE);
            forgetpassword_textview.setVisibility(View.INVISIBLE);
            forgetpassword_textview.setEnabled(false);
        }
        else{
            progressBar_forgetpassword.setVisibility(View.INVISIBLE);
            forgetpassword_textview.setVisibility(View.VISIBLE);
            forgetpassword_textview.setEnabled(true);
        }

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}