package com.example.mychatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Model.Message;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.R;
import com.example.mychatapp.fragments.FragmentMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final int MSG_TYPE_LEFT = -1;
    private final int MSG_TYPE_RIGHT = 1;

    private List<Message> list;
    private Context context;
    private FirebaseUser firebaseUser;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Bitmap bitmap;

    public MessageAdapter(List<Message> list, Bitmap bitmap, Context context) {
        this.context = context;
        this.list = list;
        this.bitmap = bitmap;
        storage = FirebaseStorage.getInstance();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_body;
        CircleImageView imgAvatar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_body = itemView.findViewById(R.id.text_message_body);
            imgAvatar = itemView.findViewById(R.id.avatar);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            CircleImageView img = view.findViewById(R.id.avatar);
            if(bitmap != null){
                img.setImageBitmap(bitmap);
            }
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message msg = list.get(position);
        holder.txt_body.setText(msg.getMsg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Message msg = list.get(position);
        if(msg.getIdSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else return MSG_TYPE_LEFT;
    }

}
