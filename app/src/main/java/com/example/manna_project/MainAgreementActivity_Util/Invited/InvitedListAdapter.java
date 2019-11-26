package com.example.manna_project.MainAgreementActivity_Util.Invited;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;
import com.example.manna_project.ShowDetailSchedule_Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InvitedListAdapter extends BaseAdapter {
    private ArrayList<Promise> list;
    private Context context;
    private LayoutInflater inflater;
    private int layout;

    public InvitedListAdapter(ArrayList<Promise> list, Context context, int layout) {
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Promise getItem(int position) {
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

        Promise promise = list.get(position);

        TextView titleTextView = convertView.findViewById(R.id.activity_main_accept_title_item);
        TextView leaderTextView = convertView.findViewById(R.id.activity_main_accept_learder_item);
        TextView dateTextView = convertView.findViewById(R.id.activity_main_accept_date_item);
        TextView placeTextView = convertView.findViewById(R.id.activity_main_accept_place_item);

        titleTextView.setText(promise.getTitle());
        leaderTextView.setText((promise.getLeader()!=null?promise.getLeader().getName():""));

        StringBuilder txt = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));

        txt.append(" ~ ");

        simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getEndTime().getTimeInMillis())));

        dateTextView.setText(txt);

        placeTextView.setText(promise.getLongitude() + ", " + promise.getLatitude());

        return convertView;
    }
}
