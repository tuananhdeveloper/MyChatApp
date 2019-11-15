package com.example.mychatapp.tic_tac_toe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.mychatapp.R;

public class CustomBaseAdapter extends BaseAdapter {

    private Context context;
    public CustomBaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 64;
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
        View view = inflater.inflate(R.layout.layout_grid_item, parent, false);
        return view;

    }
}
