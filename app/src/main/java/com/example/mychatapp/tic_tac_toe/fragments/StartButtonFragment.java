package com.example.mychatapp.tic_tac_toe.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mychatapp.R;

public class StartButtonFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_start_fragment, container, false);
        Button btnStart = view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(((AppCompatActivity)getContext()).getAssets(), "fonts/my_font.ttf");
        btnStart.setTypeface(typeface);
        return view;

    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.start_fragment, new UserOnlineFragment());
        transaction.commit();
    }
}
