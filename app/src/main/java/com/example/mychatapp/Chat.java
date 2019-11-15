package com.example.mychatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Model.User;
import com.example.mychatapp.fragments.AboutFragment;
import com.example.mychatapp.fragments.FragmentGames;
import com.example.mychatapp.fragments.FragmentList;
import com.example.mychatapp.fragments.FragmentMessage;
import com.example.mychatapp.fragments.ProfileFragment;
import com.example.mychatapp.fragments.SettingsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser firebaseUser;
    DatabaseReference reference, reference2, reference3;
    FirebaseAuth auth;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<User> users;
    private DrawerLayout drawer;
    private TextView currentName;
    private CircleImageView img_icon;

    private NavigationView navigationView;
    private ArrayList<String> list;
    private String name;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    public Bitmap bitmap;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager themeManager = new ThemeManager(Chat.this);
        setTheme(themeManager.getSavedTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat);
        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reference = FirebaseDatabase.getInstance("https://chatapp-5d713.firebaseio.com/").getReference("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        if(firebaseUser.getPhotoUrl() != null){
            storageReference = storage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            downloadFile();
        }


        //Service
        if(sharedPreferences.getInt("state", -1) != 0){
            Intent intent = new Intent(Chat.this, MessageService.class);
            intent.setAction(MessageService.ACTION_START_FOREGROUND_SERVICE);
            startService(intent);
        }

        //fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, new FragmentList(), "home");
        fragmentTransaction.commit();

        //navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        View header = navigationView.getHeaderView(0);
        currentName = header.findViewById(R.id.txt_name);
        img_icon = header.findViewById(R.id.icon);

        firebaseUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                currentName.setText(firebaseUser.getDisplayName());
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //onClick
        navigationView.setNavigationItemSelectedListener(this);
        DialogManager.showDialog(getSupportFragmentManager(), firebaseUser);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                auth.signOut();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus(true);

        if(getIntent().getExtras() != null){

            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.container, new FragmentMessage(this,
                    getIntent().getExtras().getString("idSender"),
                    getIntent().getExtras().getString("nameSender"),
                    getIntent().getExtras().getString("imageUrl")),
                    "message")
                    .addToBackStack(null);
            fragmentTransaction2.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus(false);
    }

    public void setStatus(boolean isOnline){
        if(isOnline){
            reference.child(firebaseUser.getUid()).child("online").setValue(true);
        }
        else{
            reference.child(firebaseUser.getUid()).child("online").setValue(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(Gravity.LEFT);
        switch (item.getItemId()){
            case R.id.item_home:
                homeFragment();
                return true;
            case R.id.item_games:
                gamesFragment();
                return true;
            case R.id.item_settings:
                settingsFragment();
                return true;
            case R.id.item_profile:
                profileFragment();
                return true;
            case R.id.item_about:
                aboutFragment();
                return true;
        }
        return false;
    }

    private void aboutFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new AboutFragment(), "about");
        fragmentTransaction.commit();
    }

    private void profileFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new ProfileFragment(), "profile");
        fragmentTransaction.commit();
    }

    public void homeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        if(!currentFragment.getTag().equals("home")){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new FragmentList(), "home");
            fragmentTransaction.commit();
        }
    }


    public void gamesFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new FragmentGames(), "games");
        fragmentTransaction.commit();
    }

    public void settingsFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new SettingsFragment(), "settings");
        transaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && !currentFragment.getTag().equals("message")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Exit");
            builder.setMessage("Do you want to exit app?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.create().show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(sharedPreferences.getInt("state", -1) != 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
            if (currentFragment.getTag().equals("message")){
                Intent intent = new Intent(Chat.this, MessageService.class);
                intent.setAction(MessageService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void downloadFile(){
        String url = firebaseUser.getPhotoUrl().toString();
        if(url != null){
            String tmp[] = url.split("\\?");
            String extension = tmp[0].charAt(tmp[0].length()-3) + "" + tmp[0].charAt(tmp[0].length() - 2) + "" + tmp[0].charAt(tmp[0].length() - 1);

            try {
                final File localFile = File.createTempFile("images", extension);
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        img_icon.setImageBitmap(bitmap);
                        Log.d("Download", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Download", "Fail");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
