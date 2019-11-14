package com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AcceptInvitationListAdapter extends BaseAdapter {
    private ArrayList<AcceptInvitationListItem> list;
    private Context context;
    private LayoutInflater inflater;
    private int layout;

    public AcceptInvitationListAdapter(ArrayList<AcceptInvitationListItem> list, Context context, int layout) {
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

        AcceptInvitationListItem acceptInvitationListItem = list.get(position);

        TextView titleTextView = convertView.findViewById(R.id.activity_main_accept_title_item);
        TextView closeBtn = convertView.findViewById(R.id.activity_main_accept_closeBtn_item);
        TextView leaderTextView = convertView.findViewById(R.id.activity_main_accept_learder_item);
        TextView dateTextView = convertView.findViewById(R.id.activity_main_accept_date_item);
        TextView placeTextView = convertView.findViewById(R.id.activity_main_accept_place_item);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // X버튼 클릭시 삭제 구현
            }
        });

        titleTextView.setText(acceptInvitationListItem.getTitle());
        leaderTextView.setText(acceptInvitationListItem.getLeader().getName());


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        dateTextView.setText(simpleDateFormat.format(new Date(acceptInvitationListItem.getDate().getTimeInMillis())));
        placeTextView.setText(acceptInvitationListItem.getPlace());

        return convertView;
    }
}
