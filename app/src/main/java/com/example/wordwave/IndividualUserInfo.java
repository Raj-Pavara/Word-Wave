package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class IndividualUserInfo extends AppCompatActivity {

    private String targetUserId;
    private ImageView profilepic;
    private TextView username, fullname, email, phoneno;
    private androidx.appcompat.widget.Toolbar toolbar;
    private LinearLayout audiolayout, videolayout, sharelayout;
    private String collecteddata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_user_info);

        initlization();
    }

    private void initlization() {

        toolbar = findViewById(R.id.toolbar_individualuserinfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button_individualchat_heading);

        targetUserId = getIntent().getExtras().getString("userId");
        profilepic = findViewById(R.id.profilepic_individualinfo);
        username = findViewById(R.id.username_individualinfo);
        fullname = findViewById(R.id.fullname_individualinfo);
        email = findViewById(R.id.email_individualinfo);
        phoneno = findViewById(R.id.phoneno_individualinfo);
        audiolayout = findViewById(R.id.audiocall_layout_individualinfo);
        videolayout = findViewById(R.id.videocall_layout_individualinfo);
        sharelayout = findViewById(R.id.share_layout_individualinfo);


        FirebaseFirestore.getInstance().collection("users").document(targetUserId).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {

                    String fn = documentSnapshot.getString("fullName");
                    String un = documentSnapshot.getString("userName");
                    String e = documentSnapshot.getString("email");
                    String p = documentSnapshot.getString("phone");
                    String ppu = documentSnapshot.getString("profilePicUri");
                    username.setText(un);
                    fullname.setText("~" + fn);
                    Glide.with(IndividualUserInfo.this).load(ppu).into(profilepic);
                    email.setText(e);
                    phoneno.setText("+91" + p);
                    collecteddata = "WORD WAVE ACCOUNT INFO\n\n" + "USERNAME :- " + un + "\nFULLNAME :- " + fn + "\nEMAIL :- " + e + "\nPHONE NO :- " + p;

                    sharelayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "word wave account info");
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, collecteddata);
                            startActivity(Intent.createChooser(intent, "Share Via"));
                        }
                    });


                    ZegoSendCallInvitationButton callIcon = findViewById(R.id.callicon_individual_userinfo);
                    ZegoSendCallInvitationButton videocallIcon = findViewById(R.id.videocallicon_individual_userinfo);

                    videocallIcon.setIsVideoCall(true);
                    videocallIcon.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
                    videocallIcon.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId, un)));

                    callIcon.setIsVideoCall(false);
                    callIcon.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
                    callIcon.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId, un)));

                    callIcon.setClickable(false);
                    videocallIcon.setClickable(false);
                    videolayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videocallIcon.callOnClick();
                            videocallIcon.setClickable(false);
                        }
                    });

                    audiolayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callIcon.callOnClick();
                            callIcon.setClickable(false);
                        }
                    });

                    if (targetUserId.equals(FirebaseAuth.getInstance().getUid().toString())) {
                        videolayout.setEnabled(false);
                        audiolayout.setEnabled(false);
                    }

                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen").setValue("Online").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
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
        FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen").setValue(lastSeen).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        super.onPause();
    }
}