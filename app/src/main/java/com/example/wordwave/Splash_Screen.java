package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash_Screen extends AppCompatActivity {

    private float alpha = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(Splash_Screen.this, Authentication.class);
        TextView Splash_Screen_textView = findViewById(R.id.Splash_Screen_textView);

        Splash_Screen_textView.setAlpha(0f);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < 17; i++) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    alpha += (i + 1) / 40f;
                    Splash_Screen_textView.setAlpha(alpha);

                }
                startActivity(intent);
                finish();
            }
        });


        thread.start();
    }
}