package com.example.mychatapp.tic_tac_toe.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mychatapp.R;
import com.example.mychatapp.tic_tac_toe.PlayActivity;
import com.example.mychatapp.tic_tac_toe.TicTacToeActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EndGameDialog extends DialogFragment {

    private Context context;
    private String title, msg;
    private DatabaseReference reference;
    private String roomId;
    private GridView gridView;
    private ChildEventListener listener;
    public EndGameDialog(Context context, String title, String msg, DatabaseReference reference, String roomId, GridView gridView) {
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.reference = reference;
        this.roomId = roomId;
        this.gridView = gridView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, TicTacToeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reference.child("room").child(roomId).removeValue();
                getActivity().finish();
            }
        });
        return builder.create();
    }

}
