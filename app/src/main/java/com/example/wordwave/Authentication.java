package com.example.wordwave;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Authentication extends AppCompatActivity {

    private TabLayout authenticatoin_tablayout;
    private androidx.constraintlayout.widget.ConstraintLayout view_signup, view_signin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);

        initlization();


    }


    protected void initlization() {

        authenticatoin_tablayout = findViewById(R.id.authentication_tablayout);
        view_signin = findViewById(R.id.view_signin);
        view_signup = findViewById(R.id.view_signup);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();                 //return null if you don't logeed in or you logout in past else return current user.
        if (currentUser != null) {
            startActivity(new Intent(Authentication.this, MainActivity.class));
            finish();
        }

        authenticatoin_tablayout.selectTab(authenticatoin_tablayout.getTabAt(1));
        view_signup.setVisibility(View.GONE);
        view_signin.setVisibility(View.VISIBLE);


        authenticatoin_tablayout.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getPosition() == 1) {
                            view_signup.setVisibility(View.GONE);
                            view_signin.setVisibility(View.VISIBLE);
                        } else {
                            view_signin.setVisibility(View.GONE);
                            view_signup.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );

    }


    //call when sign in or sing up google textview is clicked.
    public void google_OnClick(View view) {
        initlizationGoogle();
        signInGoogle();
    }


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    protected void initlizationGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("403203569963-q2ejs6dt3r5379thv49l3046digtuqn4.apps.googleusercontent.com")        // here i put default webclint id as arquments of  request Id Token method.
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signInGoogle() {

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Check if the user's email is already registered

                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                //here 4 case occuer
                                //1.old user and click on sign in (go to main activity)
                                //2.new user and click on sign in (go to profile initlization acitivty and then after setup profile goto main activity
                                //3.old user and click on sign up(go to profile initlization activity and then after update profile go back to authentication activity)
                                //4.new user and click on sing up (go to profile initlization activity and then after setup profile go back to authentication activity)

                                //for case 3,4 must sign out before go on other activity
                                //for every case must finish activity first

                                if (!isNewUser && authenticatoin_tablayout.getSelectedTabPosition() == 1) {
                                    startActivity(new Intent(Authentication.this, MainActivity.class));
                                    finish();
                                    return;
                                }


                                Intent intent = new Intent(Authentication.this, Profile_Initlization.class);
                                intent.putExtra("isNewUser", isNewUser + "");
                                if (authenticatoin_tablayout.getSelectedTabPosition() == 0) {
                                    intent.putExtra("sign", "signUp");
                                } else {
                                    intent.putExtra("sign", "signIn");
                                }

                                // upper 6 lines of code indicate that when we select one gmail of google then we go into profile_inilization activity here we can pass the information that indicate that our email is new or old or we try to signup or singin.

                                if (authenticatoin_tablayout.getSelectedTabPosition() == 0) {
                                    mAuth.signOut();
                                }
                                startActivity(intent);
                                finish();

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Authentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void phone_OnClick(View view) {
        Intent intent = new Intent(Authentication.this, PhoneNumber_Authentication.class);

        if (authenticatoin_tablayout.getSelectedTabPosition() == 1) {
            intent.putExtra("sign", "signIn");
        }
        else{
            intent.putExtra("sign", "signUp");
        }

        startActivity(intent);
    }
}