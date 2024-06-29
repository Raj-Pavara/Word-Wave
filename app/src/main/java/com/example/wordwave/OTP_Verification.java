package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTP_Verification extends AppCompatActivity {


    private EditText otpInput;
    private TextView resendOtpTextView,otpverification_number,otpverification_TextView;   // this is verify otp button
    private ProgressBar progressBar;
    private String phonenumber;
    private String verificatinoCode;  // this is otp
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private Long timeoutSeconds = 60L;                                   //60L denotes that value is in Long type like for float 60f , 60L means 60 second.
    private FirebaseAuth mAuth;
    private String sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        initlization();
    }

    protected void initlization(){
        androidx.appcompat.widget.Toolbar forgetpassword_toolbar = findViewById(R.id.otpverification_toolbar);
        setSupportActionBar(forgetpassword_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phonenumber = getIntent().getExtras().getString("phonenumber").trim();
        otpverification_number = findViewById(R.id.otpverification_number);
        otpverification_number.setText(phonenumber);
        otpInput = findViewById(R.id.otpverification_edittext);
        otpverification_TextView = findViewById(R.id.otpverification_TextView);
        progressBar = findViewById(R.id.progressBar);
        resendOtpTextView = findViewById(R.id.resendOtpTextView);
        mAuth = FirebaseAuth.getInstance();
        sign = getIntent().getExtras().getString("sign");

        sendOtp(phonenumber, false);

        resendOtpTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendOtp(phonenumber,true);
                    }
                }
        );

        otpverification_TextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String enteredOtp = otpInput.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificatinoCode,enteredOtp);
                        signIn(credential);
                        setInProgress(true);
                    }
                }
        );


    }


    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);


       PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)                                              //set phone number on which you went to send otp. here phone no is should be in string format
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)                             //after these time you otp has been expire (timeoutseconds are long value)   TimeUnit.SECONDS specify that your entered value is in seconds
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                        //This method is called when the verification process is completed successfully. It means the user's phone number has been verified automatically without needing the user to input the OTP manually. For example, if the user's device supports automatic SMS verification, this method would be triggered when the verification is successful. You typically use this method to sign in the user or perform any necessary actions once the verification is completed.
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        ToastMaker.show(OTP_Verification.this,"verification Failed "+e);
                        setInProgress(false);
                        finish();
                        //This method is called when the verification process encounters an error and fails. It provides details about the failure through the FirebaseException parameter. You can use this method to handle errors gracefully and inform the user about the failure. For instance, you might display a message like "Verification failed. Please try again" to the user
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        //when otp is send succesfully then this method is called
                        verificatinoCode = s;                //otp that send via sms.
                        resendingToken = forceResendingToken;          //used for resend otp
                        ToastMaker.show(OTP_Verification.this,"OTP send sucessfully");
                        setInProgress(false);
                        /*This method is called when the OTP code is successfully sent to the user's phone via SMS. It provides the verification ID (s) and a token (forceResendingToken) that can be used to resend the OTP if needed. You can use this method to inform the user that the OTP has been sent successfully and provide any necessary instructions. For example, you might display a message like "OTP sent successfully. Please check your messages" to the user.*/
                    }
                });
           if(isResend){
               PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());       //when this method is called otp is resended
           }
           else{
               PhoneAuthProvider.verifyPhoneNumber(builder.build());             //when this method is called otp is sended
           }


    }


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            otpverification_TextView.setVisibility(View.INVISIBLE);
            otpverification_TextView.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            otpverification_TextView.setVisibility(View.VISIBLE);
            otpverification_TextView.setEnabled(true);
        }
    }



    void signIn(PhoneAuthCredential phoneAuthCredential) {
        //log in and go on next activity

        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setInProgress(false);
                        if(task.isSuccessful()){
                            //call when correct otp entred

                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            //here 4 case occuer
                            //1.old user and click on sign in (go to main activity) finish privious all activity)
                            //2.new user and click on sign in (go to profile initlization acitivty and then after setup profile goto main activity
                            //3.old user and click on sign up(go to profile initlization activity and then after update profile go back to authentication activity)
                            //4.new user and click on sing up (go to profile initlization activity and then after setup profile go back to authentication activity)

                            //case 1
                            if(!isNewUser && sign.equals("signIn")){
                                Intent intent = new Intent(OTP_Verification.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                 //this will finish all activity in stack.
                                startActivity(intent);
                                finish();
                                return;
                            }

                            Intent intent = new Intent(OTP_Verification.this, Profile_Initlization.class);
                            intent.putExtra("isNewUser",isNewUser+"");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                 //this will finish all activity in stack.

                            if(sign.equals("signIn")){
                                intent.putExtra("sign","signIn");
                            }
                            else{
                                intent.putExtra("sign","signUp");
                            }
                            intent.putExtra("comeFrom","Phone");
                            startActivity(intent);
                            finish();

                        }
                        else{
                            //call when incorrect otp entred
                            ToastMaker.show(OTP_Verification.this,"Incorrect OTP");
                        }
                    }
                }
        );
    }

    void startResendTimer(){
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {          //thread run in background

                        timeoutSeconds--;
                        try {
                            resendOtpTextView.setText("Resend otp in "+timeoutSeconds);
                            if(timeoutSeconds == 0){
                                resendOtpTextView.setText("Resend otp");
                            }
                        }
                        catch (Exception e){
                            Log.d("error e",e.toString());
                        }
                        if(timeoutSeconds <= 0){
                            timeoutSeconds = 60L;
                            timer.cancel();
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            resendOtpTextView.setEnabled(true);
                                        }
                                    }
                            );
                        }
                    }
                },0,1000);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
