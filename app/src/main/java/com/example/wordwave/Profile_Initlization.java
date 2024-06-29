package com.example.wordwave;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TotpSecret;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile_Initlization extends AppCompatActivity {

    private EditText phoneNumber, Email, fullName, userName;
    private Button finishButton;
    private ProgressBar progressBar;
    private TextView usernameTakenOrNot;
    private ImageView profileinitlization_approvalicon;
    private com.google.android.material.imageview.ShapeableImageView profilePic, changeProfilePicButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private StorageReference storageReference;
    private String sign, isNewUser, comeFrom;             //comeFrom indicate that from which method we try to signin/signup from email/google/phonenumber
    private String userID;                              //userID is store userid of current user.
    private int REQUEST_CODE_OPENGARALRYINTENT;
    private FirebaseUser currentUser;
    private Bundle bundle;
    private String profilePicUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_initlization);
        firstinitlization();
    }

    //first initlization is about initialize all view and some other variable.
    protected void firstinitlization() {

        fullName = findViewById(R.id.profileinitlization_fullname);
        userName = findViewById(R.id.profileinitlization_username);
        Email = findViewById(R.id.profileinitlization_email);
        phoneNumber = findViewById(R.id.profileinitlization_phonenumber);
        finishButton = findViewById(R.id.profileinitlization_finish_button);
        progressBar = findViewById(R.id.profileinitlization_progressbar);
        usernameTakenOrNot = findViewById(R.id.profileinitlization_usernametakenornot_dis);
        profileinitlization_approvalicon = findViewById(R.id.profileinitlization_smallicon);
        profilePic = findViewById(R.id.setupprofile_profilepic);
        changeProfilePicButton = findViewById(R.id.profileinitlization_profilepicButton);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        bundle = getIntent().getExtras();
        sign = bundle.getString("sign");
        isNewUser = bundle.getString("isNewUser");
        comeFrom = bundle.getString("comeFrom");
        userID = mAuth.getCurrentUser().getUid();
        REQUEST_CODE_OPENGARALRYINTENT = 1000;
        currentUser = mAuth.getCurrentUser();

        secondinitlization();

    }


    //here in second initialization we initlize and setup profile picture and also define code of what happen when you click on add profile pic button
    protected void secondinitlization() {


        //here total 6 case occuer
        //comfrom,sign,isNewUser
        //1.phone,signin,new
        //2.phone,signup,old
        //3.phone,signup,new
        //4.google,signin,new
        //5.google,signup,old
        //6.google,signup,new
        //7.email,signup,new
        //8.editprofile,null,old


        //for old user set privious profile pic
        //cover case 2 , 5 and 8
        if (isNewUser.equals("false")) {
            setInProgress(true);
            StorageReference imageRef = storageReference.child("users/" + userID + "/profile.jpg");
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePic);
                    setInProgress(false);
                    profilePicUri = uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    ToastMaker.show(Profile_Initlization.this, "Failed to load image");
                    setInProgress(false);
                }
            });

        }
        //cover case 4 and 6
        //if new user is come from google display google's profile pic to imageview and commpress this image and then store this image to firebase.
        else if (comeFrom.equals("Google")) {
            setInProgress(true);
            Picasso.get().load(currentUser.getPhotoUrl()).into(profilePic, new Callback() {                    //this method is call when picasso successfully add email's pic to profilePic imageview. after imageview upload to screen then this callback method is called.

                @Override
                public void onSuccess() {
                    //this method is call when picasso successfully add email's pic to profilePic imageview.
                    uploadImageToFirebase();
                    setInProgress(false);
                }

                @Override
                public void onError(Exception e) {
                    ToastMaker.show(Profile_Initlization.this, "Error to load an image " + e);
                    setInProgress(false);
                }
            });

        }
        //cover case 1 , 3 and 7
        else if (comeFrom.equals("Phone") || comeFrom.equals("Email")) {
            profilePic.setImageResource(R.drawable.default_profile_icon);
            uploadImageToFirebase();

        }


        changeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalaryIntent = new Intent();
                openGalaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                openGalaryIntent.setType("image/*");
                startActivityForResult(openGalaryIntent, REQUEST_CODE_OPENGARALRYINTENT);
            }
        });

        thirdinitlization();

    }


    //this method is upload image of our ImageView to firebase.
    protected void uploadImageToFirebase() {
        setInProgress(true);
        Drawable drawable = profilePic.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageData = baos.toByteArray();
        StorageReference imageRef = storageReference.child("users/" + userID + "/profile.jpg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            ToastMaker.show(Profile_Initlization.this, "Image is succesfully uploaded ");
            //upload download uri to firebase firestore
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profilePicUri = uri.toString();
            }).addOnFailureListener(exception -> {
                Toast.makeText(getApplicationContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
            setInProgress(false);
        }).addOnFailureListener(exception -> {
            ToastMaker.show(Profile_Initlization.this, "Failed to upload image");
            setInProgress(false);
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPENGARALRYINTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                Picasso.get().load(imageUri).into(profilePic, new Callback() {                    //this method is call when picasso successfully add email's pic to profilePic imageview. after imageview upload to screen then this callback method is called.

                    @Override
                    public void onSuccess() {
                        //this method is call when picasso successfully add email's pic to profilePic imageview.
                        uploadImageToFirebase();
                    }

                    @Override
                    public void onError(Exception e) {
                        ToastMaker.show(Profile_Initlization.this, "Error to load an image " + e);
                    }
                });
            }
        }
    }


    //here in this thirdinitlization we set 4 fields and usernametakenornot disctiption.
    protected void thirdinitlization() {

        //here total 8 case occuer
        //comfrom,sign,isNewUser
        //1.phone,signin,new
        //2.phone,signup,old
        //3.phone,signup,new
        //4.google,signin,new
        //5.google,signup,old
        //6.google,signup,new
        //7.email,signup,new
        //8.editprofile,null,old

        //for old user set privious profile pic
        //cover case 2 , 5 and 8
        if (isNewUser.equals("false")) {

            DocumentReference documentReference = fstore.collection("users").document(userID);         //here if userID document is exist in users collection then it return it refernce else it crete one collection named user and in this collection one document named with userID was created.
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if (documentSnapshot != null) {
                        fullName.setText(documentSnapshot.getString("fullName"));
                        userName.setText(documentSnapshot.getString("userName"));
                        Email.setText(documentSnapshot.getString("email"));
                        phoneNumber.setText(documentSnapshot.getString("phone"));

                        //for old user that you can't modify userName
                        usernameTakenOrNot.setText("Username is already set");
                        userName.setEnabled(false);
                        usernameTakenOrNot.setTextColor(getResources().getColor(R.color.black));
                        profileinitlization_approvalicon.setImageDrawable(null);
                    }
                }
            });

        }
        //cover case 4 and 6
        else if (comeFrom.equals("Google")) {
            Email.setText(currentUser.getEmail().toString());
        }
        //cover case 1 and 3
        else if (comeFrom.equals("Phone")) {
            String s = currentUser.getPhoneNumber();
            phoneNumber.setText(s.substring(s.length() - 10)); //remove +91
        }
        //cover case 7
        else if (comeFrom.equals("Email")) {
            Email.setText(bundle.getString("email"));
            Email.setEnabled(false);
            userName.setText(bundle.getString("username"));
            usernameTakenOrNot.setText("Username is already set");
            userName.setEnabled(false);
            usernameTakenOrNot.setTextColor(getResources().getColor(R.color.black));
            profileinitlization_approvalicon.setImageDrawable(null);
        }


        //for new user set usernameisexistornot discription
        if (isNewUser.equals("true")) {
            userName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fstore.collection("users").whereEqualTo("userName", s.toString().trim())       //for check that any userName is equal to s or not.
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    if (!task.getResult().isEmpty()) {                    // means username is not available.
                                        changeDiscription(false);
                                        finishButton.setEnabled(false);
                                    } else {
                                        changeDiscription(true);
                                        finishButton.setEnabled(true);
                                    }

                                } else {
                                    Log.d("error", "firebase is fail to check that username is available or not.");
                                }
                            });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


    }


    protected void setInProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            finishButton.setTextColor(getResources().getColor(android.R.color.transparent));
        } else {
            progressBar.setVisibility(View.GONE);
            finishButton.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void changeDiscription(boolean isAvailable) {
        //if username is available then isAvailable is true then discription is green else discription is red
        if (isAvailable) {
            usernameTakenOrNot.setText("Username available");
            usernameTakenOrNot.setTextColor(getResources().getColor(R.color.green));
            profileinitlization_approvalicon.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.aprove_img));
        } else {
            usernameTakenOrNot.setText("Username is already taken");
            usernameTakenOrNot.setTextColor(getResources().getColor(R.color.red));
            profileinitlization_approvalicon.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.reject_img));
        }
    }


    public void finishButtonclicked(View view) {
        setInProgress(true);
        String fullname = fullName.getText().toString().trim();
        String username = userName.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String phonenumber = phoneNumber.getText().toString().trim();

        if (fullname.equals("")) {
            fullName.setError("Enter full name");
            setInProgress(false);
        } else if (username.equals("")) {
            userName.setError("Enter user name");
            setInProgress(false);
        } else if (username.length() < 6) {
            userName.setError("Username must be at least 6 characters");
            setInProgress(false);
        } else if (email.equals("")) {
            Email.setError("Enter email");
            setInProgress(false);
        } else if (phonenumber.equals("")) {
            phoneNumber.setError("Enter phonenumber");
            setInProgress(false);
        } else if (phonenumber.length() < 10) {
            phoneNumber.setError("Invalid phonenumber");
            setInProgress(false);
        } else {

            if (profilePicUri.equals("")) {
                ToastMaker.show(Profile_Initlization.this, "Error storing data in Firestore");
                return;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("fullName", fullname);
            data.put("userName", username);
            data.put("email", email);
            data.put("phone", phonenumber);
            data.put("profilePicUri", profilePicUri);
            DocumentReference reference = fstore.collection("users").document(userID);
            reference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    //handle 6 cases.
                    if (task.isSuccessful()) {                        //here when we add data then if data is already presant then data will be updated otherwise data will be uploaded.
                        ToastMaker.show(Profile_Initlization.this, "data added succesfully");
                        if (sign.equals("signUp")) {
                            mAuth.signOut();
                            startActivity(new Intent(Profile_Initlization.this, Authentication.class));
                            finish();
                        } else {
                            startActivity(new Intent(Profile_Initlization.this, MainActivity.class));
                            finish();
                        }

                    } else {
                        ToastMaker.show(Profile_Initlization.this, "Error storing data in Firestore:" + task.getException());
                    }
                    setInProgress(false);
                }
            });


        }

    }

}