package com.beta.tp3;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupListAdapter extends ArrayAdapter<Groups> {

    public GroupListAdapter(Context context, ArrayList<Groups> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Groups group = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_list_row, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.group_title);
        TextView tvHome = (TextView) convertView.findViewById(R.id.group_desc);

        tvName.setText(group.title);
        tvHome.setText(group.info);

        return convertView;
    }
}