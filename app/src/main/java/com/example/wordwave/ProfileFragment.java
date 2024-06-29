package com.example.wordwave;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView userName1, userName2, fullName1, fullName2, email, phoneNumber;
    private com.google.android.material.imageview.ShapeableImageView profilePic;
    private View view;
    private androidx.appcompat.widget.AppCompatButton logOut, editProfile;

    static String fn, un, e, p, ppu;

    ProfileFragment(String fn, String un, String e, String p, String ppu) {
        this.fn = fn;
        this.un = un;
        this.e = e;
        this.p = p;
        this.ppu = ppu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        userName1 = view.findViewById(R.id.username_textview_1_profilefragment);
        userName2 = view.findViewById(R.id.username_textview_2_profilefragment);
        fullName1 = view.findViewById(R.id.fullname_textview_1_profilefragment);
        fullName2 = view.findViewById(R.id.fullname_textview_2_profilefragment);
        email = view.findViewById(R.id.email_textview_profilefragment);
        phoneNumber = view.findViewById(R.id.phonenumber_textview_profilefragment);
        profilePic = view.findViewById(R.id.profilepic_profilefragment);
        logOut = view.findViewById(R.id.logout_button_profilefragment);
        editProfile = view.findViewById(R.id.editprofile_button_profilefragment);
        Picasso.get().load(ppu).into(profilePic);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        fullName2.setText(fn);
        fullName1.setText(fn);
        userName1.setText(un);
        userName2.setText(un);
        email.setText(e);
        phoneNumber.setText("+91" + p);

        logOut.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd MMM", Locale.getDefault());
                        String formattedDate = sdf.format(date);
                        String lastSeen = "last seen at " + formattedDate;
                        FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen")
                                .setValue(lastSeen).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                MainActivity.issignout = true;
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(getContext(), Authentication.class));
                                                getActivity().finish();
                                            }
                                        }
                                );
                    }
                }
        );

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Profile_Initlization.class);
                i.putExtra("isNewUser", "false");
                i.putExtra("comeFrom", "editProfile");
                i.putExtra("sign", "null");
                startActivity(i);
                getActivity().finish();
            }
        });
        return view;
    }

}