package com.example.mychatapp.tic_tac_toe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.mychatapp.DialogManager;
import com.example.mychatapp.R;
import com.example.mychatapp.tic_tac_toe.fragments.StartFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class TicTacToeActivity extends AppCompatActivity {


    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        navigateToStartFragment();
        DialogManager.showDialog(getSupportFragmentManager(), firebaseUser);
    }

    public void navigateToStartFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, new StartFragment());
        transaction.commit();
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
