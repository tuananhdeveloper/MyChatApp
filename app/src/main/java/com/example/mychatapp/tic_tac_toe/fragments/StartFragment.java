package com.example.mychatapp.tic_tac_toe.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mychatapp.R;

public class StartFragment extends Fragment implements View.OnClickListener {

    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_tictactoe_fragment, container, false);
        textView = view.findViewById(R.id.txt);
        Typeface typeface = Typeface.createFromAsset(((AppCompatActivity)getContext()).getAssets(), "fonts/my_font.ttf");
        textView.setTypeface(typeface);
        addStartButtonFragment();
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.start_fragment, new UserOnlineFragment());
        transaction.commit();
    }

    public void addStartButtonFragment(){
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.start_fragment, new StartButtonFragment());
        transaction.commit();
    }
}
