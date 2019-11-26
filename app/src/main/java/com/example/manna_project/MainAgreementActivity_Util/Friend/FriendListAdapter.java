package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.R;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {
    private ArrayList<MannaUser> list;
    private Context context;
    private LayoutInflater inflater;
    private int layout;

    public FriendListAdapter(ArrayList<MannaUser> list, Context context, int layout) {
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        MannaUser friendListItem = list.get(position);

        ImageView img = convertView.findViewById(R.id.friend_icon);
//        if (friendListItem.getUser().getIcon() != 0) img.setImageResource(friendListItem.getIcon());

        TextView tx = convertView.findViewById(R.id.user_name);
        tx.setText(friendListItem.getName());

        return convertView;
    }
}
