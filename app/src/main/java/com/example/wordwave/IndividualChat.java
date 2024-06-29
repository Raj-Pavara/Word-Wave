package com.example.wordwave;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class IndividualChat extends AppCompatActivity {

    private String targetUserId, targetUserName, targetUserEmail, targetUserPhoneNo, targetUserProfilePicUri, targetUserFullName;
    private ImageView backButtonindividualchatHeading, profileicon_individualchat_heading, callicon_individualchat_heading, videocallicon_individualchat_heading;
    private TextView username_individualchat_heading, status_individualchat_heading;
    private LinearLayout linearlayout_individualchat_heading;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        initlization();
    }

    protected void initlization() {

        backButtonindividualchatHeading = findViewById(R.id.backbutton_individualchat_heading);
        profileicon_individualchat_heading = findViewById(R.id.profileicon_individualchat_heading);
        callicon_individualchat_heading = findViewById(R.id.callicon_individualchat_heading);
        videocallicon_individualchat_heading = findViewById(R.id.videocallicon_individualchat_heading);
        username_individualchat_heading = findViewById(R.id.username_individualchat_heading);
        status_individualchat_heading = findViewById(R.id.status_individualchat_heading);
        linearlayout_individualchat_heading = findViewById(R.id.linearlayout_individualchat_heading);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        targetUserId = getIntent().getExtras().getString("userId");

        FirebaseFirestore.getInstance().collection("users").document(targetUserId).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {

                    targetUserName = documentSnapshot.getString("userName");
                    targetUserEmail = documentSnapshot.getString("email");
                    targetUserPhoneNo = documentSnapshot.getString("phone");
                    targetUserFullName = documentSnapshot.getString("fullName");
                    targetUserProfilePicUri = documentSnapshot.getString("profilePicUri");

                    if (currentUserId.equals(targetUserId)) {
                        username_individualchat_heading.setText(documentSnapshot.getString("userName") + " (Me)");
                    } else {
                        username_individualchat_heading.setText(documentSnapshot.getString("userName"));
                    }

                    Glide.with(IndividualChat.this).load(targetUserProfilePicUri).into(profileicon_individualchat_heading);


                    profileicon_individualchat_heading.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialog dialog = ProfilePicDialog.createDialog(IndividualChat.this, username_individualchat_heading.getText().toString(), targetUserProfilePicUri);
                                    ImageView chat_icon = dialog.findViewById(R.id.chat_icon_profile_pic_dialog);
                                    ImageView info_icon = dialog.findViewById(R.id.info_icon_profile_pic_dialog);
                                    dialog.show();
                                    chat_icon.setOnClickListener(view -> {
                                        dialog.cancel();
                                    });
                                    info_icon.setOnClickListener(
                                            view -> {
                                                dialog.cancel();
                                                Intent intent = new Intent(IndividualChat.this, IndividualUserInfo.class);
                                                intent.putExtra("userId", targetUserId);
                                                startActivity(intent);
                                            }
                                    );
                                }
                            }
                    );
                }
            }
        });

        backButtonindividualchatHeading.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );


        linearlayout_individualchat_heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualChat.this, IndividualUserInfo.class);
                intent.putExtra("userId", targetUserId);
                startActivity(intent);
            }
        });


        callicon_individualchat_heading.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );

        videocallicon_individualchat_heading.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );


        setupChat();

    }

    FirebaseDatabase database;
    EditText messageEdittext;
    com.google.android.material.imageview.ShapeableImageView sendbutton;
    androidx.recyclerview.widget.RecyclerView recylerView_individualchat;
    RecyclerAdapterIndividualChat adapter;

    protected void setupChat() {
        recylerView_individualchat = findViewById(R.id.individualchat_recyclerview);
        sendbutton = findViewById(R.id.individualchat_sendbutton);
        messageEdittext = findViewById(R.id.individualchat_message_edittext);
        adapter = new RecyclerAdapterIndividualChat(IndividualChat.this, new ArrayList<MessageModel>(), currentUserId, targetUserId, getMenuInflater());
        recylerView_individualchat.setLayoutManager(new LinearLayoutManager(IndividualChat.this));
        recylerView_individualchat.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats").child(currentUserId + targetUserId).child("messages")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<MessageModel> al = new ArrayList<>();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    MessageModel mm = dataSnapshot.getValue(MessageModel.class);
                                    al.add(mm);
                                }
                                adapter.function(al);
                                recylerView_individualchat.scrollToPosition(al.size() - 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );


        sendbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = messageEdittext.getText().toString().trim();
                        if (!message.equals("")) {
                            messageEdittext.setText("");
                            Date date = new Date();
                            MessageModel messageModel = new MessageModel(message, currentUserId, date.getTime());
                            database = FirebaseDatabase.getInstance();
                            Map<String , Object> map = new HashMap<>();
                            map.put("key","value");
                            FirebaseDatabase.getInstance().getReference().child("Connections/" + currentUserId + "/" + targetUserId).updateChildren(map);
                            database.getReference().child("chats").child(currentUserId + targetUserId).child("messages").push()
                                    .setValue(messageModel).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!currentUserId.equals(targetUserId)) {
                                                        database.getReference().child("chats").child(targetUserId + currentUserId)
                                                                .child("messages").push().setValue(messageModel);

                                                        FirebaseDatabase.getInstance().getReference().child("Connections/" + targetUserId + "/" + currentUserId).updateChildren(map);

                                                    }
                                                }
                                            }
                                    );
                        }
                    }
                }
        );

        setupuserStatus();

    }

    protected void setupuserStatus() {

        database = FirebaseDatabase.getInstance();
        database.getReference().child("userStatus").child(targetUserId).child("lastseen")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String lastSeen = snapshot.getValue(String.class);

                                status_individualchat_heading.setText(lastSeen);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
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