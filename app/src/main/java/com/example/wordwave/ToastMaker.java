package com.example.wordwave;

import android.content.Context;
import android.widget.Toast;

public class ToastMaker {
    public static void show(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
