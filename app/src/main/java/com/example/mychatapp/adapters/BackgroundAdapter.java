package com.example.mychatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mychatapp.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundAdapter extends BaseAdapter {

    private List<Integer> list = new ArrayList<>();
    private Context context;
    public BackgroundAdapter(Context context, List<Integer> list) {
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

        LayoutInflater inflater = LayoutInflater.from(context);
        int id = list.get(position);

        View view = inflater.inflate(R.layout.item_background_layout, parent, false);
        ImageView imageView = view.findViewById(R.id.img);
        imageView.setImageResource(id);

        return view;
    }
}
