package com.beta.tp3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessagesListAdapter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messagesItems;

    public MessagesListAdapter(Context context, List<Message> items) {
        super(context, 0, items);
        this.context = context;
        this.messagesItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message msg = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messagesItems.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left, null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(msg.getMessage());
        lblFrom.setText(msg.getFromName());

        return convertView;
    }
}
