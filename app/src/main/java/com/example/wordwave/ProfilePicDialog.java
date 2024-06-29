package com.example.wordwave;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProfilePicDialog {
    protected static Dialog createDialog(Context context,String Username,String profilepicuri){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_pic_dialog);
        dialog.setCancelable(true);
        TextView un = dialog.findViewById(R.id.username_profile_pic_dialog);
        un.setText(Username);
        ImageView pp = dialog.findViewById(R.id.profilephoto_proilepic_dialog);
        Glide.with(context).load(profilepicuri).into(pp);
        return  dialog;
    }
}
