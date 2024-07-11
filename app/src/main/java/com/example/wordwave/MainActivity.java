package com.example.wordwave;


import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static boolean issignout;
    private ViewPager2 viewPager_mainactivity;
    private TabLayout tabLayout_mainactivity;
    private androidx.constraintlayout.widget.ConstraintLayout constraintLayout;
    private androidx.recyclerview.widget.RecyclerView recyclerView_mainactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        issignout = false;

        constraintLayout = findViewById(R.id.constraintlayout_mainactivity);
        recyclerView_mainactivity = findViewById(R.id.recyclerview_mainactivity);
        recyclerView_mainactivity.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarmainactivity);
        setSupportActionBar(toolbar);

        viewPager_mainactivity = findViewById(R.id.viewpager2_mainactivity);

        setupTabLayout();

    }

    FirebaseAuth mAuth;

    private void setupTabLayout() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("users").document(mAuth.getCurrentUser().getUid());         //here if userID document is exist in users collection then it return it refernce else it crete one collection named user and in this collection one document named with userID was created.

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {

                    String fn = documentSnapshot.getString("fullName");
                    String un = documentSnapshot.getString("userName");
                    String e = documentSnapshot.getString("email");
                    String p = documentSnapshot.getString("phone");
                    String ppu = documentSnapshot.getString("profilePicUri");
                    startServiceForZegoCloud(FirebaseAuth.getInstance().getUid(),un);

                    Custom_Viewpager_Adapter cva = new Custom_Viewpager_Adapter(getSupportFragmentManager(), getLifecycle(), fn, un, e, p, ppu);
                    viewPager_mainactivity.setAdapter(cva);

                    listshow(un);
                }

            }
        });

        tabLayout_mainactivity = findViewById(R.id.tablayout_mainactivity);
        tabLayout_mainactivity.setOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager_mainactivity.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                }
        );

        viewPager_mainactivity.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout_mainactivity.selectTab(tabLayout_mainactivity.getTabAt(position));
            }
        });

    }

    RecyclerAdapterSearchBar ras;
    ArrayList<Row_RecyclerView_SearchBar> data_searchlist = new ArrayList<>();

    private void listshow(String un) {
        ras = new RecyclerAdapterSearchBar(MainActivity.this, data_searchlist);
        recyclerView_mainactivity.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView_mainactivity.setAdapter(ras);
        FirebaseFirestore.getInstance().collection("users").orderBy("userName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            ToastMaker.show(getApplicationContext(), error.toString());
                            return;
                        }
                        //here when new user was add then data_serchlist was clear and then all user's list was updated
                        data_searchlist.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (un.equals(doc.getString("userName"))) {
                                data_searchlist.add(new Row_RecyclerView_SearchBar(doc.getString("profilePicUri"), doc.getString("userName") + " (You)", doc.getString("fullName"), doc.getId().toString()));
                            } else {
                                data_searchlist.add(new Row_RecyclerView_SearchBar(doc.getString("profilePicUri"), doc.getString("userName"), doc.getString("fullName"), doc.getId().toString()));
                            }
                        }
                        ras.fun(data_searchlist);
                    }
                });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_menu_id);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Enter Username");

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    recyclerView_mainactivity.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                } else {
                    recyclerView_mainactivity.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayList<Row_RecyclerView_SearchBar> tempAL = new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tempAL.clear();
                for (int i = 0; i < data_searchlist.size(); i++) {
                    if (data_searchlist.get(i).username.contains(newText)) {
                        tempAL.add(data_searchlist.get(i));
                    }
                }
                ras.fun(tempAL);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int temp = item.getItemId();
        if (temp == R.id.logout_menu_id) {
            issignout = true;
            long timestamp = System.currentTimeMillis();
            String lastSeen = formatTimestamp(timestamp);
            FirebaseDatabase.getInstance().getReference().child("userStatus").child(FirebaseAuth.getInstance().getUid().toString()).child("lastseen")
                    .setValue(lastSeen).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.signOut();
                                    startActivity(new Intent(MainActivity.this, Authentication.class));
                                    finish();
                                }
                            }
                    );

        }
        return true;
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
        if (!issignout) {                                          //here when we signout then onpause method called here we use current user's data so we couldn't use that data because we signed out.
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
        }
        super.onPause();
    }



    void startServiceForZegoCloud(String UID,String un) {

        //this method is set user name with one id and we can identify individual user with his userId.

        Application application = getApplication(); // Android's application context
        long appID = 937749117;   // yourAppID
        String appSign = "3dc2f95ecd6b0e9f7993a122bd7349d9251bb5a087d1652ec21624d2abb197ca";  // yourAppSign
        String userID = UID; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName = un;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName, callInvitationConfig);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();            // this method is performs cleanup operations specific to this zegocloud service .
    }
}