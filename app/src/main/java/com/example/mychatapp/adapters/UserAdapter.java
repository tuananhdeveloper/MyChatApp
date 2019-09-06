package com.example.mychatapp.adapters;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    ArrayList<User> userList;
    Context context;
    public UserAdapter(ArrayList<User> userList, Context context){
        this.userList = userList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView txt_name;
        TextView txt_status;
        private ItemClickListener itemClickListener;
        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_item);
            txt_name = view.findViewById(R.id.name_item);
            txt_status = view.findViewById(R.id.status);
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
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        User mUser = userList.get(i);
        myViewHolder.txt_name.setText(mUser.getName());
        myViewHolder.imageView.setImageResource(R.drawable.baseline_person_black_48);
        if(mUser.getOnline()){
            myViewHolder.txt_status.setText("Online");
            myViewHolder.txt_status.setTextColor(Color.GREEN);
        }
        else{
            myViewHolder.txt_status.setText("Offline");
            myViewHolder.txt_status.setTextColor(Color.RED);
        }

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                    User user = userList.get(position);
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new FragmentMessage(user, context)).addToBackStack(null);
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
