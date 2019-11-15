package com.example.mychatapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mychatapp.Model.User;
import com.example.mychatapp.tic_tac_toe.PlayActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvitationDialog extends DialogFragment {

    private String msg;
    private String key;
    private DatabaseReference reference, reference1;
    private ArrayList<String> listKey;
    private FirebaseUser firebaseUser;
    private String id;
    private String nameInviter;
    public InvitationDialog(String msg, String key, ArrayList<String> listKey, FirebaseUser firebaseUser, String id, String nameInviter){

        this.msg = msg;
        this.key = key;
        this.listKey = listKey;
        this.firebaseUser = firebaseUser;
        this.id = id;
        this.nameInviter = nameInviter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
        builder.setMessage(msg);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference = FirebaseDatabase.getInstance().getReference("tic-tac-toe").child("room");
                Map<String, Object> map = new HashMap<>();

                map.put("idPlayer2", firebaseUser.getUid());
                map.put("idPlayer1", id);
                String roomId = reference.push().getKey();
                reference.child(roomId).updateChildren(map);

                Bundle bundle = new Bundle();
                bundle.putString("room_id", roomId);
                bundle.putString("nameInviter", nameInviter);
                bundle.putInt("player", 2);
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        reference = FirebaseDatabase.getInstance().getReference("tic-tac-toe");
        reference.child("invitation").child(key).removeValue();
        listKey.remove(listKey.indexOf(key));
    }

}
