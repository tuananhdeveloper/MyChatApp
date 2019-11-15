package com.example.mychatapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BackgroundChatManager {

    private Activity activity;
    private static BackgroundChatManager backgroundChatManager;
    private SharedPreferences preferences;
    private static final String NAME = "background_chat";
    private static final String KEY = "item";

    private BackgroundChatManager() {

    }

    public static BackgroundChatManager getInstance(){
        if(backgroundChatManager == null){
           backgroundChatManager = new BackgroundChatManager();
        }
        return backgroundChatManager;
    }

    public void changeBackground(Activity activity, ViewGroup layout){
        this.activity = activity;
        preferences = activity.getSharedPreferences(NAME, activity.MODE_PRIVATE);
        int value = preferences.getInt(KEY, -1);
        if(value != -1){
            if(value == 0){
                layout.setBackgroundColor(Color.WHITE);
            }
            else{
                layout.setBackgroundResource(activity.getResources().getIdentifier("bg"+value, "drawable", activity.getPackageName()));
            }
        }
    }

    public void setBackground(Activity activity, Integer position){
        this.activity = activity;
        preferences = activity.getSharedPreferences(NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY, position);
        editor.apply();
    }

    public int getItemSelected(Activity activity){
        this.activity = activity;
        preferences = activity.getSharedPreferences(NAME, activity.MODE_PRIVATE);
        return preferences.getInt(KEY, -1);
    }
}
