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

        Promise promise = list.get(position);

        TextView titleTextView = convertView.findViewById(R.id.activity_main_accept_title_item);
        ImageView closeBtn = convertView.findViewById(R.id.activity_main_accept_closeBtn_item);
        TextView leaderTextView = convertView.findViewById(R.id.activity_main_accept_learder_item);
        TextView dateTextView = convertView.findViewById(R.id.activity_main_accept_date_item);
        TextView placeTextView = convertView.findViewById(R.id.activity_main_accept_place_item);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // X버튼 클릭시 삭제 구현

            }
        });

        titleTextView.setText(promise.getTitle());
        leaderTextView.setText((promise.getLeader()!=null?promise.getLeader().getName():""));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

        dateTextView.setText("시작시간 : "  + simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));
        placeTextView.setText(promise.getLongitude() + ", " + promise.getLatitude());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowDetailSchedule_Activity.class);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
