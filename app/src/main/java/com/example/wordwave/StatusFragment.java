package com.example.wordwave;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatusFragment extends Fragment {

    private static final int REQUEST_CODE_OPENGARALRYINTENT = 100;
    com.google.android.material.imageview.ShapeableImageView addstatusbutton;
    androidx.recyclerview.widget.RecyclerView recyclerViewstatus;
    ImageView storestatus; // this imageview is used to store status temporary
    String un;

    StatusFragment(String un) {
        this.un = un;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        recyclerViewstatus = view.findViewById(R.id.recyclerview_mainactivity_statusfragment);
        addstatusbutton = view.findViewById(R.id.statusfragment_addstatusbutton);
        storestatus = view.findViewById(R.id.imageview_store_status);

        recyclerViewstatus.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerAdapterPhotoStatus raps = new RecyclerAdapterPhotoStatus(getActivity(), new ArrayList<>());
        recyclerViewstatus.setAdapter(raps);

        addstatusbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openGalaryIntent = new Intent();
                        openGalaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        openGalaryIntent.setType("image/*");
                        startActivityForResult(openGalaryIntent, REQUEST_CODE_OPENGARALRYINTENT);
                    }
                }
        );
        ArrayList<String> connections = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Connections").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        connections.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            connections.add(key);
                            Log.d("Firebase", "Connection node key: " + key);
                        }

                        FirebaseDatabase.getInstance().getReference().child("photoStatus")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<Row_RecyclerView_Photo_Status> rows = new ArrayList<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String key = snapshot.getKey();
                                            if (connections.contains(key) || key.equals(FirebaseAuth.getInstance().getUid())) {
                                                rows.add(snapshot.getValue(Row_RecyclerView_Photo_Status.class));
                                                if (key.equals(FirebaseAuth.getInstance().getUid())) {
                                                    rows.get(rows.size() - 1).userName = " My Status";
                                                    rows.add(0, rows.get(rows.size() - 1));
                                                    rows.remove(rows.size() - 1);
                                                }
                                            }

                                        }
                                        raps.fun(rows);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Firebase", "Failed to read connections: " + databaseError.getMessage());
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read connections: " + databaseError.getMessage());
                    }
                });


        FirebaseDatabase.getInstance().getReference().child("photoStatus")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Row_RecyclerView_Photo_Status> rows = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            if (connections.contains(key) || key.equals(FirebaseAuth.getInstance().getUid())) {
                                rows.add(snapshot.getValue(Row_RecyclerView_Photo_Status.class));
                                if (key.equals(FirebaseAuth.getInstance().getUid())) {
                                    rows.get(rows.size() - 1).userName = " My Status";
                                    rows.add(0, rows.get(rows.size() - 1));
                                    rows.remove(rows.size() - 1);
                                }
                            }
                        }
                        raps.fun(rows);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read connections: " + databaseError.getMessage());
                    }
                });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPENGARALRYINTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                Picasso.get().load(imageUri).into(storestatus, new Callback() {
                    @Override
                    public void onSuccess() {
                        uploadImageToFirebase();
                    }

                    @Override
                    public void onError(Exception e) {
                        ToastMaker.show(getActivity(), "Error to load an image " + e);
                    }
                });
            }
        }
    }

    protected void uploadImageToFirebase() {
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("status updating...");
        pd.show();
        Drawable drawable = storestatus.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageData = baos.toByteArray();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("photoStatus/" + FirebaseAuth.getInstance().getUid() + "/photostatus.jpg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            ToastMaker.show(getActivity(), "Status updated sucesfully. ");
            //upload download uri to firebase firestore
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                long timestamp = System.currentTimeMillis();
                String timeStamp = formatTimestamp(timestamp);
                Row_RecyclerView_Photo_Status r = new Row_RecyclerView_Photo_Status(timeStamp, uri.toString(), un);
                FirebaseDatabase.getInstance().getReference().child("photoStatus").child(FirebaseAuth.getInstance().getUid().toString()).setValue(r)
                        .addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                }
                        );
            }).addOnFailureListener(exception -> {
                Toast.makeText(getActivity(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
            pd.dismiss();
        }).addOnFailureListener(exception -> {
            ToastMaker.show(getActivity(), "Failed to upload image");
            pd.dismiss();
        });

    }


    public String formatTimestamp(long timestamp) {
        // Create a Date object from the timestamp
        Date date = new Date(timestamp);

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a , dd MMM", Locale.getDefault());

        // Format the date
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

}