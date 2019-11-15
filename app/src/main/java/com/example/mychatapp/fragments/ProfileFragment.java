package com.example.mychatapp.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mychatapp.Chat;
import com.example.mychatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment  extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int PERMISSION_CODE = 222;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private TextInputEditText edtName, edtCurrentPassword, edtNewPassword, edtComfirm;
    private String name, currentPassword, newPassword, confirm, ext;
    private TextView txtName;
    private CircleImageView imgAvatar;

    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        edtName = view.findViewById(R.id.edt_name);
        edtCurrentPassword = view.findViewById(R.id.edt_current_password);
        edtNewPassword = view.findViewById(R.id.edt_new_password);
        edtComfirm = view.findViewById(R.id.edt_comfirm);

        txtName = view.findViewById(R.id.txt_name);
        imgAvatar = view.findViewById(R.id.img_avatar);

        Bitmap bm = ((Chat)getContext()).bitmap;
        if(bm != null){
            imgAvatar.setImageBitmap(bm);
        }
        edtName.setText(firebaseUser.getDisplayName());

        txtName.setText(firebaseUser.getDisplayName());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_load:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(((AppCompatActivity)getContext()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED
                    ){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        chooseImage();
                    }
                }
                else{
                    chooseImage();
                }

                return true;
            case R.id.item_save:
                save(item);
                return true;
        }
        return false;
    }

    public void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void save(final MenuItem item){

        name = edtName.getText().toString();
        currentPassword = edtCurrentPassword.getText().toString();
        newPassword = edtNewPassword.getText().toString();
        confirm = edtComfirm.getText().toString();

        if(!TextUtils.isEmpty(name)){
            if(TextUtils.isEmpty(currentPassword)){
                Toast.makeText(getContext(), "Current Password must not be empty", Toast.LENGTH_LONG).show();
            }
            else{
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(confirm)){
                                if(newPassword.equals(confirm)){
                                    if(filePath != null){
                                        reference.child(firebaseUser.getUid()).child("name").setValue(name);
                                        firebaseUser.updatePassword(newPassword);
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                        firebaseUser.updateProfile(profileUpdates);
                                        uploadImage(item);
                                    }
                                    else{
                                        reference.child(firebaseUser.getUid()).child("name").setValue(name);
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                        firebaseUser.updateProfile(profileUpdates);
                                        firebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Successfully!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                                else{
                                    Toast.makeText(getContext(), "Not match", Toast.LENGTH_LONG).show();
                                }
                            }
                            else if(TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(confirm)){
                                if(filePath != null){
                                    reference.child(firebaseUser.getUid()).child("name").setValue(name);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    firebaseUser.updateProfile(profileUpdates);
                                    uploadImage(item);
                                }
                                else{
                                    reference.child(firebaseUser.getUid()).child("name").setValue(name);
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Successfully!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                            else if(TextUtils.isEmpty(newPassword)){
                                Toast.makeText(getContext(), "New password must not be empty", Toast.LENGTH_LONG).show();
                            }
                            else if(TextUtils.isEmpty(confirm)){
                                Toast.makeText(getContext(), "Confirm new password must not be empty", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else{
            Toast.makeText(getContext(), "Name must not be empty", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImage(final MenuItem item){
        if(filePath != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
            Date date = new Date();
            final StorageReference ref = storageReference.child(firebaseUser.getUid() +  "/image-" + sdf.format(date) + "." + ext);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    item.setTitle("SAVE");
                    Toast.makeText(getContext(), "Successfully!", Toast.LENGTH_LONG).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child(firebaseUser.getUid()).child("imageUrl").setValue(uri.toString());
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    item.setTitle("SAVE");
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_LONG).show();
                    Log.d("error_log", e.getMessage());
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    item.setTitle("SAVING...");
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMAGE_REQUEST:
                if(data != null){
                   filePath = data.getData();
                    String mimeType = ((AppCompatActivity)getContext()).getContentResolver().getType(filePath);
                    ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(((AppCompatActivity)getContext()).getContentResolver(), filePath);
                        imgAvatar.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
