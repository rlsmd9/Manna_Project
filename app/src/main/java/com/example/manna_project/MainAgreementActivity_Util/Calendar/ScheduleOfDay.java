package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
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
    private int color;
    private ArrayList<TextView> textViews;

    final static String TAG = "MANNA_J1S";

    public ScheduleOfDay(Context context, Calendar date, TextView day, LinearLayout dayList) {
        this.context = context;
        this.date = date;
        this.textDay = day;
        this.dayList = dayList;
        this.eventsOfDay = new Events();
        this.textViews = new ArrayList<>();
    }

    public void setLayout() {
        // 일정 지우기
        dayList.removeAllViews();
        textViews.clear();

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(2,5,2,0);
        dayList.addView(this.textDay, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (eventsOfDay.getItems() == null) return;
        if (eventsOfDay.getItems().size() > 0) {
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
                textView.setMaxWidth(0);
                textView.setSingleLine(true);

                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextSize(8);

                // default
                textView.setBackgroundColor(Color.parseColor(Custom_Calendar.colorList[0]));

                // 달력 일정 색깔작업
                if (event.getColorId() != null) {
                    textView.setBackgroundColor(Color.parseColor(event.getColorId()));
                }

                textViews.add(textView);
                this.dayList.addView(textView, linearParams);
            }

        } else {
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

        list = eventsOfDay.getItems();

        if (list == null) {
            list = new ArrayList<>();
            eventsOfDay.setItems(list);
        }

        list.add(event);
    }

    public void setScheduleLining(boolean b) {
        Log.d(TAG, "setScheduleLining: ");
        if (eventsOfDay.getItems() == null) return;
        if (eventsOfDay.getItems().size() > 0) {
            LinearLayout.LayoutParams layoutParams;
            for (TextView view: textViews) {
                layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                view.setLayoutParams(layoutParams);
                if (b) layoutParams.height = 5;
                else layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }

        } else {
        }
    }


    @Override
    public String toString() {
        return "ScheduleOfDay{" +
                "date=" + date +
                ", textDay=" + textDay +
                ", dayList=" + dayList +
                ", eventsOfDay=" + eventsOfDay +
                ", context=" + context +
                ", textViews=" + textViews +
                '}';
    }
}



