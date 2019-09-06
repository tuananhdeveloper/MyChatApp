package com.example.mychatapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Model.User;
import com.example.mychatapp.fragments.FragmentList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseAuth auth;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<User> users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new FragmentList());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus(false);
    }

    public void setStatus(boolean isOnline){
        if(isOnline){
            reference.child(firebaseUser.getUid()).child("online").setValue(true);
        }
        else{
            reference.child(firebaseUser.getUid()).child("online").setValue(false);
        }
    }
}
