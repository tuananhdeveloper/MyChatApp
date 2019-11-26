package com.example.mychatapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessageService extends Service {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private DatabaseReference reference;
    private String id;
    private int isInApp = 0;
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    private ValueEventListener valueEventListener;
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */

    @Override
    public void onCreate() {
        reference = FirebaseDatabase.getInstance().getReference("Messages");


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                case ACTION_START_FOREGROUND_SERVICE:
                    startForcegroundService(intent);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForcegroundService();
                    break;
            }
        }
        else {
            startForcegroundService(intent);
        }
        return START_STICKY;
    }

    public void startForcegroundService(Intent intent){
        id = FirebaseAuth.getInstance().getUid();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.child("idReceiver").getValue(String.class).equals(id) && snapshot.child("isNotified").getValue(Boolean.class) == false){
                        snapshot.child("isNotified").getRef().setValue(true);
                        String bodyText = snapshot.child("msg").getValue(String.class);
                        String title = snapshot.child("nameSender").getValue(String.class);
                        showNotification(title, bodyText, "TUANANH", snapshot.child("idSender").getValue(String.class),
                                snapshot.child("nameSender").getValue(String.class),
                                snapshot.child("imageUrl").getValue(String.class)
                        );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(valueEventListener);

    }

    public void stopForcegroundService(){
        stopSelf();
    }

    public void showNotification(String title, String bodyText, String channelID, String idSender, String nameSender, String imageUrlSender){
        Intent intent = new Intent(this, Chat.class);

        intent.putExtra("idSender", idSender);
        intent.putExtra("nameSender", nameSender);
        intent.putExtra("imageUrl", imageUrlSender);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setSmallIcon(R.drawable.ic_message_black_24dp);
        builder.setContentTitle(title);
        builder.setContentText(bodyText);
        builder.setTicker(title);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);
        createNotificationChannel(channelID);
        startForeground(222, builder.build());
    }

    private void createNotificationChannel(String channelID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
