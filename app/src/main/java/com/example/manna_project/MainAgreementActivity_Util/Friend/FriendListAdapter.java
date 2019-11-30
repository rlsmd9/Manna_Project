package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.R;

import java.util.ArrayList;

public class FriendListAdapter extends BaseAdapter {
    private ArrayList<MannaUser> list;
    private boolean[] isCheckedUsers;
    private Context context;
    private LayoutInflater inflater;
    private int layout;

    public FriendListAdapter(ArrayList<MannaUser> list, Context context, int layout, boolean[] isCheckedUsers) {
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
        this.isCheckedUsers = isCheckedUsers;
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


    boolean check_switch = false;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        check_switch = true;

        MannaUser friendListItem = list.get(position);

        ImageView img = convertView.findViewById(R.id.friend_icon);
//        if (friendListItem.getUser().getIcon() != 0) img.setImageResource(friendListItem.getIcon());

        TextView email = convertView.findViewById(R.id.user_email);
        CheckBox chk = convertView.findViewById(R.id.user_chk);

        if (email != null && chk != null) {

            Log.d("JS ", "getView: " + isCheckedUsers[position] + ", position = " + position);
            chk.setChecked(isCheckedUsers[position]);

            email.setText(friendListItem.geteMail());

            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (check_switch) return;
                    isCheckedUsers[position] = isChecked;
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox box = v.findViewById(R.id.user_chk);
                    Log.d("JS", "onItemClick: " + box.isChecked());
                    box.setChecked(!box.isChecked());
                }
            });
        }

        TextView tx = convertView.findViewById(R.id.user_name);

        if (friendListItem.getNickName() == null || friendListItem.getNickName().isEmpty())
            tx.setText(friendListItem.getName());
        else
            tx.setText(friendListItem.getNickName());

        check_switch = false;
        return convertView;
    }

    public boolean[] getIsCheckedUsers() {
        return isCheckedUsers;
    }

    public void setIsCheckedUsers(boolean[] isCheckedUsers) {
        this.isCheckedUsers = isCheckedUsers;
    }
}
