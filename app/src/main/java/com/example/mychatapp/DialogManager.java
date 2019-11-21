package com.example.mychatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mychatapp.InvitationDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public final class DialogManager{

    private static DatabaseReference reference;
    private static ArrayList<String> listKey;
    public static void showDialog(final FragmentManager fragmentManager, final FirebaseUser firebaseUser){
        listKey = new ArrayList<>();
        try{
            reference = FirebaseDatabase.getInstance().getReference("tic-tac-toe");
            reference.child("invitation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(listKey.contains(snapshot.getKey())) continue;
                        if(snapshot.child("idUser2").getValue().equals(firebaseUser.getUid())){
                            DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag("dialog_tictactoe");
                            if(fragment != null){
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.remove(fragment);
                                transaction.commit();
                            }
                            String name = (String) snapshot.child("Name").getValue();
                            String msg = name + " invites you to play Tic Tac Toe";
                            InvitationDialog dialog = new InvitationDialog(msg, snapshot.getKey(), listKey, firebaseUser, (String)snapshot.child("idUser1").getValue(), name);
                            dialog.setCancelable(false);
                            try {
                                dialog.show(fragmentManager, "dialog_tictactoe");
                                listKey.add(snapshot.getKey());
                            }
                            catch (IllegalStateException e){

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
