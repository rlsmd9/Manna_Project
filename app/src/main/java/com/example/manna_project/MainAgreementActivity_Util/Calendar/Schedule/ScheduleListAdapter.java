package com.example.manna_project.MainAgreementActivity_Util.Calendar.Schedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.manna_project.R;

import java.util.ArrayList;

public class ScheduleListAdapter extends BaseAdapter {
    private ArrayList<ScheduleListItem> list;
    private Context context;
    private LayoutInflater inflater;
    private int layout;

    public ScheduleListAdapter(ArrayList<ScheduleListItem> list, Context context, int layout) {
        this.list = list;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("MANNA_JS", "notifyDataSetChanged: 1");
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        Log.d("MANNA_JS", "notifyDataSetInvalidated: 1");
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

        ScheduleListItem scheduleListItem = list.get(position);

        // 참가자 이미지 추가 할 경우 작업
//        if (scheduleListItem.getImg().length > 0) {
//        }

        Log.d("MANNA_JS", "getView: " + scheduleListItem.getTitle());

        TextView title = convertView.findViewById(R.id.calendar_schedule_title_item);
        TextView place = convertView.findViewById(R.id.calendar_schedule_place_item);
        TextView startDate = convertView.findViewById(R.id.calendar_schedule_startDate_item);
        TextView endDate = convertView.findViewById(R.id.calendar_schedule_endDate_item);
//        int img[];

//        title.setText(scheduleListItem.getTitle());
//        place.setText(scheduleListItem.getPlace());
//        startDate.setText(scheduleListItem.getStartDate());
//        endDate.setText(scheduleListItem.getEndDate());

        title.setText(scheduleListItem.getTitle() + "1");
        place.setText(scheduleListItem.getPlace() + "2");
        startDate.setText(scheduleListItem.getStartDate() + "3");
        endDate.setText(scheduleListItem.getEndDate() + "4");

        return convertView;
    }
}
