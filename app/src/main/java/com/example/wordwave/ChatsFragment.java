package com.example.wordwave;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//RecyclerAdapterSearchBar is used to display chat in fragment


public class ChatsFragment extends Fragment {

    String un;

    ChatsFragment(String un) {
        this.un = un;
    }

    androidx.recyclerview.widget.RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_mainactivity_chatfragment);
        RecyclerAdapterSearchBar rac = new RecyclerAdapterSearchBar(getActivity(), new ArrayList<Row_RecyclerView_SearchBar>());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(rac);
        ArrayList<Row_RecyclerView_SearchBar> al = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users").orderBy("userName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            ToastMaker.show(getActivity(), error.toString());
                            return;
                        }
                        al.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.getString("userName").equals(un)) {
                                al.add(new Row_RecyclerView_SearchBar(doc.getString("profilePicUri"), doc.getString("userName") + " (You)", doc.getString("fullName"), doc.getId().toString()));
                            } else {
                                al.add(new Row_RecyclerView_SearchBar(doc.getString("profilePicUri"), doc.getString("userName"), doc.getString("fullName"), doc.getId().toString()));
                            }
                        }

                        FirebaseDatabase.getInstance().getReference().child("Connections").child(FirebaseAuth.getInstance().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<String> connections = new ArrayList<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String key = snapshot.getKey();
                                            connections.add(key);
                                            Log.d("Firebase", "Connection node key: " + key);
                                        }

                                        // Do something with the list of connections
                                        // For example, update the UI or log the connections
                                        Log.d("Firebase", "List of connection nodes: " + connections);

                                        ArrayList<Row_RecyclerView_SearchBar> temp = new ArrayList<>();
                                        for (int i = 0; i < al.size(); i++) {
                                            if (connections.contains(al.get(i).userId)) {

                                                temp.add(al.get(i));
                                                if (al.get(i).userId.equals(FirebaseAuth.getInstance().getUid())) {
                                                    temp.get(temp.size() - 1).username = temp.get(temp.size() - 1).username;
                                                }
                                            }
                                        }
                                        rac.fun(temp);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Firebase", "Failed to read connections: " + databaseError.getMessage());
                                    }
                                });
                    }
                });


        FirebaseDatabase.getInstance().getReference().child("Connections").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> connections = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            connections.add(key);
                            Log.d("Firebase", "Connection node key: " + key);
                        }

                        // Do something with the list of connections
                        // For example, update the UI or log the connections
                        Log.d("Firebase", "List of connection nodes: " + connections);

                        ArrayList<Row_RecyclerView_SearchBar> temp = new ArrayList<>();
                        for (int i = 0; i < al.size(); i++) {
                            if (connections.contains(al.get(i).userId)) {

                                temp.add(al.get(i));
                                if (al.get(i).userId.equals(FirebaseAuth.getInstance().getUid())) {
                                    temp.get(temp.size() - 1).username = temp.get(temp.size() - 1).username;
                                }
                            }
                        }
                        rac.fun(temp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read connections: " + databaseError.getMessage());
                    }
                });
        return view;
    }
}