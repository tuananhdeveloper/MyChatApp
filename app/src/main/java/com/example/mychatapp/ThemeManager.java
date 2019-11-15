package com.example.mychatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.mychatapp.fragments.SettingsFragment;


public class ThemeManager {

    private Activity activity;
    private SharedPreferences sharedPreferences;
    public ThemeManager(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("theme-manager", activity.MODE_PRIVATE);
    }

    public void saveTheme(String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", value);
        editor.commit();
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    public int getSavedTheme(){
       String theme = sharedPreferences.getString("theme", "");
        switch (theme){
            case SettingsFragment.RED:
                return R.style.CustomRedTheme;
            case SettingsFragment.BLUE:
                return R.style.CustomBlueTheme;
            case SettingsFragment.GREEN:
                return R.style.CustomGreenTheme;
            case SettingsFragment.ORANGE:
                return R.style.CustomOrangeTheme;
            case SettingsFragment.PURPLE:
                return R.style.CustomPurpleTheme;
            case SettingsFragment.YELLOW:
                return R.style.CustomYellowTheme;
        }
        return R.style.AppTheme;
    }

}
