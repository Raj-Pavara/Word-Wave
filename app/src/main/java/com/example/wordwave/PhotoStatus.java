package com.example.wordwave;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PhotoStatus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_status);

        ImageView iv = findViewById(R.id.imageView);
        Glide.with(this).load(getIntent().getExtras().getString("photoStatus")).into(iv);

        ProgressBar progressBar = findViewById(R.id.progressbar_photostatus);
        progressBar.setMax(200);
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i <= 200; i++) {
                            progressBar.setProgress(i);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        finish();
                    }
                }
        );
        thread.start();

    }


    @Override
    protected void onResume() {
        FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen")
                .setValue("Online").addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }
                );
        super.onResume();
    }


    public String formatTimestamp(long timestamp) {
        // Create a Date object from the timestamp
        Date date = new Date(timestamp);

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd MMM", Locale.getDefault());

        // Format the date
        String formattedDate = sdf.format(date);

        // Return the formatted date string prefixed with "last seen at "
        return "last seen at " + formattedDate;
    }

    // Example usage

    @Override
    protected void onPause() {
        long timestamp = System.currentTimeMillis();
        String lastSeen = formatTimestamp(timestamp);
        FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen")
                .setValue(lastSeen).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }
                );

        super.onPause();
    }
}