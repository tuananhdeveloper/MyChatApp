package com.example.mychatapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.R;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.adapters.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentList extends Fragment {

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ArrayList<User> mList;
    private UserAdapter userAdapter;

    private RecyclerView recyclerView;

    private Map<String, Bitmap> bitmaps;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Bitmap currentBitmap;
    private View mFragmentView;
    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        reference = FirebaseDatabase.getInstance("https://chatapp-5d713.firebaseio.com/").getReference("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        bitmaps = new HashMap<>();
        mList= new ArrayList<>();
        storage = FirebaseStorage.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mFragmentView != null){
            return mFragmentView;
        }
        mFragmentView = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = mFragmentView.findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        readUsers();
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void readUsers(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                bitmaps.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!firebaseUser.getUid().equals(user.getId())){
                        mList.add(user);
                        downloadBitmap(user);
                    }
                }
                userAdapter = new UserAdapter(mList, bitmaps, getContext());
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void downloadBitmap(final User mUser){
        if(!mUser.getImageUrl().equals("default")){
            storageReference = storage.getReferenceFromUrl(mUser.getImageUrl());
            String tmp[] = mUser.getImageUrl().split("\\?");
            String extension = tmp[0].charAt(tmp[0].length()-3) + "" + tmp[0].charAt(tmp[0].length() - 2) + "" + tmp[0].charAt(tmp[0].length() - 1);
            try {
                final File localFile = File.createTempFile("images", extension);
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Download", "success");
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        bitmaps.put(mUser.getId(), bitmap);
                        userAdapter = new UserAdapter(mList, bitmaps, getContext());
                        recyclerView.setAdapter(userAdapter);
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
