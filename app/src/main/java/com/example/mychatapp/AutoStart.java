package com.example.mychatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class AutoStart extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent arg1) {
        sharedPreferences = context.getSharedPreferences("notify", Context.MODE_PRIVATE);
        if(sharedPreferences.getInt("state", -1) != 0){
            Intent intent = new Intent(context,MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }
}
