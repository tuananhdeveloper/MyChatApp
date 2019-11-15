package com.example.mychatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mychatapp.R;
import com.example.mychatapp.tic_tac_toe.TicTacToeActivity;

public class FragmentGames extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.games_fragment, container, false);
        View viewTicTacToe = v.findViewById(R.id.layout_tic_tac_toe);
        viewTicTacToe.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_tic_tac_toe:
                Intent intent = new Intent(getContext(), TicTacToeActivity.class);
                startActivity(intent);
                break;
        }
    }


}
