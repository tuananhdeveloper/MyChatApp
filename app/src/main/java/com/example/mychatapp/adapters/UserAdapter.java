package com.example.mychatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.ItemClickListener;
import com.example.mychatapp.R;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.fragments.FragmentMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    private ArrayList<User> userList;
    private Context context;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Map<String, Bitmap> bitmaps;
    private Bitmap currentBitmap;

    public UserAdapter(ArrayList<User> userList, Map<String, Bitmap> bitmaps, Context context){
        this.userList = userList;
        this.context = context;
        storage = FirebaseStorage.getInstance();
        this.bitmaps = bitmaps;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView txt_name;
        ImageView img_status;
        private ItemClickListener itemClickListener;
        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_item);
            txt_name = view.findViewById(R.id.name_item);
            img_status = view.findViewById(R.id.status);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
           itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        User mUser = userList.get(i);

        //set bitmap
        if(bitmaps.size() != 0){
            if(bitmaps.get(mUser.getId()) != null){
                myViewHolder.imageView.setImageBitmap(bitmaps.get(mUser.getId()));
            }
        }

        myViewHolder.txt_name.setText(mUser.getName());
        if(mUser.isOnline()){
            myViewHolder.img_status.setImageResource(R.drawable.ic_online);

        }
        else{
            myViewHolder.img_status.setImageResource(R.drawable.ic_offline);
        }

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                    User user = userList.get(position);
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new FragmentMessage(user, context), "message").addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else{

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
