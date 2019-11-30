package com.example.manna_project.MainAgreementActivity_Util.Calendar.Schedule;

import android.content.Context;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.ListView;

import com.example.manna_project.FirebaseCommunicator;
import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Schedule_List {
    Events events;
    ListView listView;
    ArrayList<ScheduleListItem> itemList;
    Context context;
    ScheduleListAdapter scheduleListAdapter;

    public Schedule_List(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        this.itemList = new ArrayList<>();

        scheduleListAdapter = new ScheduleListAdapter(this.getArrayList(), this.context, R.layout.calendar_schedule_list_item);

        setList();
    }

    public void setItemList(ArrayList<ScheduleListItem> itemList) {
        this.itemList = itemList;
        this.scheduleListAdapter.notifyDataSetChanged();
    }

    public void setListItem(Events events) {
        setItemList(transferToArrayList(events));
    }

    private ArrayList<ScheduleListItem> transferToArrayList(Events events) {
        ArrayList<ScheduleListItem> arrayList = itemList;

        arrayList.clear();

        if (events.getItems() == null) return arrayList;

        for (Event event: events.getItems()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm");

            Date startDate = new Date();
            Date endDate = new Date();
            startDate.setTime(event.getStart().getDateTime().getValue());
            endDate.setTime(event.getEnd().getDateTime().getValue());

            arrayList.add(new ScheduleListItem(event.getSummary(), event.getLocation(), simpleDateFormat.format(startDate), simpleDateFormat.format(endDate), event.getDescription()));
        }

        Log.d("MANNA_JS", "transferToArrayList: why!!");
        for (int i = 0; i < arrayList.size(); i++) {
            Log.d("MANNA_JS", "transferToArrayList: " + arrayList.get(i).getTitle());
        }

        return arrayList;
    }

    public void setList() {
        listView.setAdapter(scheduleListAdapter);
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ArrayList<ScheduleListItem> getArrayList() {
        return itemList;
    }

    public void setArrayList(ArrayList<ScheduleListItem> arrayList) {
        this.itemList = arrayList;
    }

    public ScheduleListAdapter getScheduleListAdapter() {
        return scheduleListAdapter;
    }

    public void setScheduleListAdapter(ScheduleListAdapter scheduleListAdapter) {
        this.scheduleListAdapter = scheduleListAdapter;
    }
}
