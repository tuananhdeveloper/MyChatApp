package com.example.mychatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychatapp.Model.User;
import com.example.mychatapp.R;

import java.util.List;

public class UserOnlineAdapter extends BaseAdapter {

    private List<User> list;
    private Context context;
    public UserOnlineAdapter(Context context, List<User> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User mUser = list.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        TextView txtName = view.findViewById(R.id.name_item);
        ImageView imgStatus = view.findViewById(R.id.status);
        imgStatus.setImageResource(R.drawable.ic_online);
        txtName.setText(mUser.getName());
        return view;
    }
}
