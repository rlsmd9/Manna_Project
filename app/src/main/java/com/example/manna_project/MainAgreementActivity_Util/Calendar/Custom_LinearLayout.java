package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class Custom_LinearLayout extends LinearLayout {
    Custom_Calendar.CalendarType calendarType;
    ArrayList<ScheduleOfDay> scheduleOfDays;
    Calendar date;
    Custom_LinearLayout calendar_root;
    final static String TAG = "MANNA_DONE";

    public void setCalendar_root(Custom_LinearLayout calendar_root) {
        this.calendar_root = calendar_root;
    }

    ListView listView;

    public Custom_LinearLayout(Context context) {
        super(context);
    }

    public Custom_LinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setScheduleOfDays(ArrayList<ScheduleOfDay> scheduleOfDays) {
        this.scheduleOfDays = scheduleOfDays;
    }

    private float dragY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptTouchEvent: " + event.getAction());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            dragY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && Math.abs(event.getY() - dragY) >= 50.0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouch: " + event.getAction() + " : " + event.getY());
        if (event.getAction() == MotionEvent.ACTION_UP && event.getY() - dragY >= 50.0) {
            moveCalendarType(Custom_Calendar.TouchType.DOWN);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && event.getY() - dragY <= -50.0) {
            moveCalendarType(Custom_Calendar.TouchType.UP);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void moveCalendarType(Custom_Calendar.TouchType touchType) {
        if (touchType == Custom_Calendar.TouchType.UP) {
            switch (calendarType) {
                case FULL_CALENDAR:
                    calendarType = Custom_Calendar.CalendarType.HALF_CALENDAR;
                    break;
                case HALF_CALENDAR:
                    calendarType = Custom_Calendar.CalendarType.WEEK_CALENDAR;
                    break;
                case WEEK_CALENDAR:
                    break;
            }
        } else if (touchType == Custom_Calendar.TouchType.DOWN) {
            switch (calendarType) {
                case FULL_CALENDAR:
                    break;
                case HALF_CALENDAR:
                    calendarType = Custom_Calendar.CalendarType.FULL_CALENDAR;
                    break;
                case WEEK_CALENDAR:
                    calendarType = Custom_Calendar.CalendarType.HALF_CALENDAR;
                    break;
            }
        }

        this.setCalendarType(calendarType);
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public void setCalendarType(Custom_Calendar.CalendarType calendarType) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) listView.getLayoutParams();
        param.width = 0;

        switch (calendarType) {
            case FULL_CALENDAR:
                param.weight = 0f;
                highlight_week(-1);
                break;
            case HALF_CALENDAR:
                param.weight = 2f;
                highlight_week(-1);
                break;
            case WEEK_CALENDAR:
                highlight_week(date.get(Calendar.WEEK_OF_MONTH));
                break;
        }

        listView.setLayoutParams(param);
    }

    private void initWeek() {
        GridLayout.LayoutParams param;
        LinearLayout.LayoutParams layoutParams_root = (LinearLayout.LayoutParams) calendar_root.getLayoutParams();

        for (int i = 0; i < this.scheduleOfDays.size(); i++) {
            param = (GridLayout.LayoutParams) scheduleOfDays.get(i).getDayList().getLayoutParams();

            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f);
            layoutParams_root.weight = 1.5f;

            scheduleOfDays.get(i).getDayList().setLayoutParams(param);
        }

        calendar_root.setLayoutParams(layoutParams_root);
    }

    public void highlight_week() {
        GridLayout.LayoutParams param;
        LinearLayout.LayoutParams layoutParams_root = (LinearLayout.LayoutParams) calendar_root.getLayoutParams();
        int week = -1;
        initWeek();

        if (calendarType == Custom_Calendar.CalendarType.WEEK_CALENDAR) week = this.date.get(Calendar.WEEK_OF_MONTH);

        for (int i = 0; i < this.scheduleOfDays.size(); i++) {
            param = (GridLayout.LayoutParams) scheduleOfDays.get(i).getDayList().getLayoutParams();
            param.height = 0;
            if (week == -1) {
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f);
                layoutParams_root.weight = 1.5f;
            } else if (scheduleOfDays.get(i).getDate().get(Calendar.WEEK_OF_MONTH) != week || scheduleOfDays.get(i).getDate().get(Calendar.MONTH) != this.date.get(Calendar.MONTH)) {
                layoutParams_root.weight = 0;
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,0f);
            } else {
                param.height = LayoutParams.WRAP_CONTENT;
            }

                if (calendarType != Custom_Calendar.CalendarType.FULL_CALENDAR) {
                scheduleOfDays.get(i).setScheduleLining(true);
            } else scheduleOfDays.get(i).setScheduleLining(false);

            scheduleOfDays.get(i).getDayList().setLayoutParams(param);
        }

        calendar_root.setLayoutParams(layoutParams_root);
    }

    public void highlight_week(int week){
        GridLayout.LayoutParams param;
        LinearLayout.LayoutParams layoutParams_root = (LinearLayout.LayoutParams) calendar_root.getLayoutParams();
        initWeek();

        for (int i = 0; i < this.scheduleOfDays.size(); i++) {
            param = (GridLayout.LayoutParams) scheduleOfDays.get(i).getDayList().getLayoutParams();
            param.height = 0;
            if (week == -1) {
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f);
                layoutParams_root.weight = 1.5f;
            } else if (scheduleOfDays.get(i).getDate().get(Calendar.WEEK_OF_MONTH) != week || scheduleOfDays.get(i).getDate().get(Calendar.MONTH) != this.date.get(Calendar.MONTH)) {
                layoutParams_root.weight = 0;
                param.rowSpec = GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,0f);
            } else {
                param.height = LayoutParams.WRAP_CONTENT;
            }

            if (calendarType != Custom_Calendar.CalendarType.FULL_CALENDAR) {
                scheduleOfDays.get(i).setScheduleLining(true);
            } else scheduleOfDays.get(i).setScheduleLining(false);

            scheduleOfDays.get(i).getDayList().setLayoutParams(param);
        }

        calendar_root.setLayoutParams(layoutParams_root);
    }
}
