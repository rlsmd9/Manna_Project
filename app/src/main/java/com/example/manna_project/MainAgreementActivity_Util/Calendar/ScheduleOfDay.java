package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.UiThread;

import com.example.manna_project.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

public class ScheduleOfDay {
    private Calendar date;
    private TextView textDay;
    private LinearLayout dayList;
    private Events eventsOfDay;
    private Context context;

    final static String TAG = "MANNA_JS";

    public ScheduleOfDay(Context context, Calendar date, TextView day, LinearLayout dayList) {
        this.context = context;
        this.date = date;
        this.textDay = day;
        this.dayList = dayList;
        this.eventsOfDay = new Events();
    }

    public void setLayout() {
        if (eventsOfDay.getItems() == null) return;
        Log.d(TAG, "setLayout: 1");
        if (eventsOfDay.getItems().size() > 0) {
            Log.d(TAG, "setLayout: 2");
            // 일정 지우기
            dayList.removeAllViews();

            Log.d(TAG, "setLayout: 3");
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearParams.setMargins(0,5,0,0);
            dayList.addView(this.textDay, linearParams);
            Log.d(TAG, "setLayout: 4");
            // 일자별 존재하는 일정에 대해서 TextView 추가
            Log.d(TAG, "setLayout: items = " + eventsOfDay.getItems().size());
            for (Event event: eventsOfDay.getItems()) {
                Log.d(TAG, "setLayout: " + event.getSummary());
                TextView textView = new TextView(context);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                String title = event.getSummary();
                if (title == null) title = "";

                textView.setText(title);
                textView.setSingleLine(true);
                textView.setTextSize(8);
                textView.setBackgroundColor(Color.rgb(242,177, 112));
                Log.d(TAG, "setLayout: 4-1");
                Log.d(TAG, "setLayout: 4-2");
                this.dayList.addView(textView, linearParams);
            }

            Log.d(TAG, "setLayout: 5");
        } else {
//            Log.d(TAG, "setLayout: ");
        }
    }

    public LinearLayout getDayList() {
        return dayList;
    }

    public void setDayList(LinearLayout dayList) {
        this.dayList = dayList;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public TextView getTextDay() {
        return textDay;
    }

    public void setTextDay(TextView textDay) {
        this.textDay = textDay;
    }

    public Events getEventsOfDay() {
        return eventsOfDay;
    }

    public void setEventsOfDay(Events eventsOfDay) {
        this.eventsOfDay = eventsOfDay;
    }

    public void addEvent(Event event) {
        List<Event> list;

        Log.d(TAG, "addEvent: 0-1");
        list = eventsOfDay.getItems();

        if (list == null) {
            list = new ArrayList<>();
            eventsOfDay.setItems(list);
        }

        Log.d(TAG, "addEvent: 0");
        list.add(event);
        Log.d(TAG, "addEvent: end");
    }
}



