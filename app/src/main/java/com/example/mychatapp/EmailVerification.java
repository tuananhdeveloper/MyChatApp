package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private Button btnSend, btnRefresh;
    private TextView txtLogout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_layout);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseUser.reload();
        if(firebaseUser.isEmailVerified()){
            navigateToChat();
        }
        btnSend = findViewById(R.id.btn_send);
        btnRefresh = findViewById(R.id.btn_refresh);
        txtLogout = findViewById(R.id.txt_logout);

        btnSend.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                send();
                break;
            case R.id.btn_refresh:
                refresh();
                break;
            case R.id.txt_logout:
                logout();
                break;
        }
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(EmailVerification.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void send(){
        btnSend.setEnabled(false);
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                btnSend.setEnabled(true);
                if(task.isSuccessful()){
                    Toast.makeText(EmailVerification.this,
                            "Verification email sent to " + firebaseUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EmailVerification.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(){
        firebaseUser.reload();
        if(firebaseUser.isEmailVerified()){
            navigateToChat();
        }
    }

    public void navigateToChat(){
        Intent intent = new Intent(EmailVerification.this, Chat.class);
        startActivity(intent);
        finish();
    }
}
