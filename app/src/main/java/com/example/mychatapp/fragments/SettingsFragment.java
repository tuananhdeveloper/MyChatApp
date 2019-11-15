package com.example.mychatapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mychatapp.BackgroundChatManager;
import com.example.mychatapp.Chat;
import com.example.mychatapp.MessageService;
import com.example.mychatapp.R;
import com.example.mychatapp.ThemeManager;
import com.example.mychatapp.adapters.BackgroundAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    SharedPreferences sharedPreferences;
    private Switch mSwitch;
    private MaterialButton btnPickColor;
    private Spinner mSpinner;

    public static final String RED = "red";
    public static final String PURPLE = "deepPurple";
    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String YELLOW = "yellow";
    public static final String ORANGE = "orange";
    public static final int count = 12;

    private ThemeManager themeManager;
    private List<Integer> idBackground = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("notify", Context.MODE_PRIVATE);

        idBackground.add(R.drawable.blank_bg);
        for(int i = 1; i <= count; i++){
            idBackground.add(getResources().getIdentifier("bg"+i, "drawable", ((AppCompatActivity)getContext()).getPackageName()));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        mSwitch = view.findViewById(R.id.my_switch);
        btnPickColor = view.findViewById(R.id.btn_pick_color);
        mSpinner = view.findViewById(R.id.my_spinner);
        BackgroundAdapter adapter = new BackgroundAdapter(getContext(), idBackground);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);

        BackgroundChatManager backgroundChatManager = BackgroundChatManager.getInstance();
        mSpinner.setSelection(backgroundChatManager.getItemSelected(getActivity()));

        mSwitch.setChecked(false);
        if(sharedPreferences.getInt("state", -1) != 0){
            mSwitch.setChecked(true);
        }

        mSwitch.setOnCheckedChangeListener(this);
        btnPickColor.setOnClickListener(this);
        return view;
    }

    public void turnOnNotification(){
        Intent intent = new Intent(getContext(), MessageService.class);
        intent.setAction(MessageService.ACTION_START_FOREGROUND_SERVICE);
        ((AppCompatActivity)getContext()).startService(intent);
    }

    public void turnOffNotification(){
        Intent intent = new Intent(getContext(), MessageService.class);
        intent.setAction(MessageService.ACTION_STOP_FOREGROUND_SERVICE);
        ((AppCompatActivity)getContext()).startService(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            turnOnNotification();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("state", 1);
            editor.commit();
        }
        else{
            turnOffNotification();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("state", 0);
            editor.commit();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        ColorPicker colorPicker = new ColorPicker(getActivity());
        colorPicker.setColors(Color.parseColor(getResources().getString(R.color.red)),
                Color.parseColor(getResources().getString(R.color.deepPurple)),
                Color.parseColor(getResources().getString(R.color.blue)),
                Color.parseColor(getResources().getString(R.color.green)),
                Color.parseColor(getResources().getString(R.color.yellow)),
                Color.parseColor(getResources().getString(R.color.orange))
                );
        colorPicker.show();

        themeManager = new ThemeManager((Chat)getActivity());

        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                switch (position){
                    case 0:
                        themeManager.saveTheme(RED);
                        break;
                    case 1:
                        themeManager.saveTheme(PURPLE);
                        break;
                    case 2:
                        themeManager.saveTheme(BLUE);
                        break;
                    case 3:
                        themeManager.saveTheme(GREEN);
                        break;
                    case 4:
                        themeManager.saveTheme(YELLOW);
                        break;
                    case 5:
                        themeManager.saveTheme(ORANGE);
                        break;
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        BackgroundChatManager backgroundChatManager = BackgroundChatManager.getInstance();
        backgroundChatManager.setBackground(getActivity(), position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
