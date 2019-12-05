package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Schedule.Schedule_List;
import com.example.manna_project.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Custom_Calendar implements View.OnClickListener {
//    final static String TAG = "manna_js";
    final static String TAG = "manna_JS";

    enum CalendarType {
        FULL_CALENDAR, HALF_CALENDAR, WEEK_CALENDAR;
    }

    enum TouchType {
        DOWN, UP;
    }

    public static String colorList[] = {"#FF9C7E", "#157DA1", "#FF7077", "#706492", "#4BC8BD",
            "#FFA9AB", "#FFBCA2", "#FFA9AB", "#FFD2E1", "#AFD4E0", "#C9CECF",
            "#F6EFD0", "#C9DCCC", "#C9DCCC", "#FF8E7E", "#FF8E7E", "#44C19D"};

    Context context;
    LayoutInflater layout;
    Custom_LinearLayout calendar_root;
    GridLayout calendar_layout;
    ListView listView;
    TextView viewDate;
    // 일정 데이터 받아서 저장할 자료구조
    ArrayList<ScheduleOfDay> scheduleOfDays;
    MainAgreementActivity mainAgreementActivity;
    Schedule_List schedule_list;

    public Custom_Calendar(MainAgreementActivity mainAgreementActivity, Context context, Custom_LinearLayout calendar_root, GridLayout calendar_layout, ListView listView, TextView viewDate, Calendar date) {
        this.context = context;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listView = listView;
        this.calendar_layout = calendar_layout;
        calendar_root.setListView(this.listView);
        this.calendar_root = calendar_root;
        this.calendar_root.setCalendar_root(this.calendar_root);
        this.calendar_root.date = Calendar.getInstance();
        this.calendar_root.date.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE),0,0,0);
        this.scheduleOfDays = new ArrayList<>();
        this.viewDate = viewDate;
        this.mainAgreementActivity = mainAgreementActivity;
        this.calendar_root.calendarType = CalendarType.FULL_CALENDAR;
        this.schedule_list = new Schedule_List(this.context, listView);
        // 일자별 Linear 레이아웃 생성
        makeCalendar();
        // 생성한 일자 자료구조 전달
        calendar_root.setScheduleOfDays(this.scheduleOfDays);

        setCalendar();
        selectDay();
    }

    public void showView() {
        int index;
        ScheduleOfDay sod = null;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);

                if (calendar_root.calendarType == CalendarType.FULL_CALENDAR) scheduleOfDay.setScheduleLining(false);
                else scheduleOfDay.setScheduleLining(true);
                scheduleOfDay.setLayout();
            }
        }

        sod = findDateLayout(getDate());
        if (sod != null)
            this.schedule_list.setListItem(sod.getEventsOfDay());

        if (calendar_root.calendarType != CalendarType.FULL_CALENDAR) {
            for (int i = 0; i < scheduleOfDays.size(); i++) {
                scheduleOfDays.get(i).setScheduleLining(true);
            }
        }
    }

    public void setSchedule(Events events) {
        int start;
        int index;

        Calendar c = Calendar.getInstance();

        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);

        initCalendarUI();

        if (events == null) return;

        int itemCount = 0;

        for (Event event: events.getItems()) {
            DateTime startEventTime = event.getStart().getDateTime();
            DateTime endEventTime = event.getEnd().getDateTime();

            if (startEventTime == null)
                startEventTime = event.getStart().getDate();
            if (endEventTime == null)
                endEventTime = event.getEnd().getDate();

            Calendar eventDay = Calendar.getInstance();
            eventDay.setTime(new Date(startEventTime.getValue()));

            Calendar eventEndDay = Calendar.getInstance();
            eventEndDay.setTime(new Date(endEventTime.getValue()));

            long days = TimeUnit.MILLISECONDS.toDays(eventEndDay.getTimeInMillis() - eventDay.getTimeInMillis()) + 1;

            Calendar temp = (Calendar) eventDay.clone();

            Log.d(TAG, "setSchedule: " + (temp.get(Calendar.HOUR_OF_DAY) + Math.abs(eventEndDay.get(Calendar.HOUR_OF_DAY) - temp.get(Calendar.HOUR_OF_DAY))));
            Log.d(TAG, "setSchedule: temp : " + temp.get(Calendar.DAY_OF_MONTH));
            temp.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY) + Math.abs(eventEndDay.get(Calendar.HOUR_OF_DAY) - temp.get(Calendar.HOUR_OF_DAY)));
            Log.d(TAG, "setSchedule: temp : " + temp.get(Calendar.DAY_OF_MONTH));

            for (int i = 0; i < days; i++) {

                if (eventDay.get(Calendar.YEAR) == getDate().get(Calendar.YEAR) && eventDay.get(Calendar.MONTH) == getDate().get(Calendar.MONTH)) {
                    index = eventDay.get(Calendar.DAY_OF_MONTH) + start - 2;
                    ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);

                    if (itemCount >= colorList.length)
                        event.setColorId("#CE3");
                    else
                        event.setColorId(colorList[itemCount]);
                    scheduleOfDay.addEvent(event);
                }

                eventDay.set(Calendar.DAY_OF_MONTH, eventDay.get(Calendar.DAY_OF_MONTH) + 1);
            }

            itemCount++;

//            if (days > 1) {
//                Log.d(TAG, "setSchedule: time = " + days);
//
//
//            } else {
//                index = eventDay.get(Calendar.DAY_OF_MONTH) + start - 2;
//                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
//                scheduleOfDay.addEvent(event);
//            }
        }
    }

    public void initCalendarUI() {
        int index;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
                scheduleOfDay.getEventsOfDay().clear();
            }
        }
    }

    public void setDate(Calendar date) {
        if (date.get(Calendar.YEAR) != calendar_root.date.get(Calendar.YEAR) || date.get(Calendar.MONTH) != calendar_root.date.get(Calendar.MONTH)) {
            this.calendar_root.date = date;
            setCalendar();
            mainAgreementActivity.downloadGoogleCalendarData();
        } else {
            this.calendar_root.date = date;
        }
    }

    public void setDate(int day) {
        this.calendar_root.date.set(Calendar.DATE, day);
    }

    // month index range is 0 to 11
    public void setDate(int year, int month) {
        int day = 1;

        if (month == Calendar.getInstance().get(Calendar.MONTH)) day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (year != calendar_root.date.get(Calendar.YEAR) || month != calendar_root.date.get(Calendar.MONTH)) {
            this.calendar_root.date.set(year, month, day);
            setCalendar();

            mainAgreementActivity.downloadGoogleCalendarData();
        } else {
            this.calendar_root.date.set(year, month, day);
            setCalendar();

        }
        selectDay();
    }

    // month index range is 0 to 11
    public void setDate(int year, int month, int day) {
        this.getDate().set(year, month, day);

        setCalendar();
        selectDay();
    }

    public void makeCalendar() {
        // activity_main_agreement의 약속탭에 달력 만들기
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                LinearLayout ly = (LinearLayout) layout.inflate(R.layout.activity_main_agreement_calendar_item, null);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                        GridLayout.UNDEFINED,GridLayout.FILL, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL,1f));
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(context);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setBackgroundColor(Color.argb(255,0,0,0));
                textView.setTypeface(null,Typeface.BOLD);

                textView.setText("init");

                ly.setOnClickListener(this);
                ly.addView(textView,linearParams);
                calendar_layout.addView(ly,param);

                scheduleOfDays.add(new ScheduleOfDay(context,null, textView, ly));
            }
        }
    }

    protected void setCleanBackground() {
        for (int i = 0; i < scheduleOfDays.size(); i++) {
            scheduleOfDays.get(i).getDayList().setBackground(calendar_layout.getBackground());
        }
    }

    @Override
    public void onClick(View v) {
        ScheduleOfDay selected_scheduleOfDay = findDateLayout(v);
        this.setDate(selected_scheduleOfDay.getDate());

        this.schedule_list.setListItem(selected_scheduleOfDay.getEventsOfDay());

        selectDay();
        Log.d(TAG, this.getDate().get(Calendar.DAY_OF_MONTH)+"");
    }

    // 현재 저장된 Date를 선택
    protected void selectDay() {
        setCleanBackground();
        ScheduleOfDay current_scheduleOfDay = findDateLayout(this.getDate());

        current_scheduleOfDay.getDayList().setBackground(context.getResources().getDrawable(R.drawable.round_shape_calendar_item_selected));

        calendar_root.highlight_week();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월");
        Log.d(TAG, simpleDateFormat.format(this.getDate().getTime())+"");
        viewDate.setText(simpleDateFormat.format(this.getDate().getTime()));
    }

    protected void setCalendar() {
        int cnt = 1;
        int start, end;
        int index;
        Calendar c = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);
        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH)+1, 0);
        end = c.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);

                scheduleOfDay.getTextDay().setEnabled(true);
                scheduleOfDay.getDayList().setClickable(true);
                scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.textGrayColor));
                scheduleOfDay.getTextDay().setBackground(context.getResources().getDrawable(R.drawable.round_shape_calendar_item));
                // j+(7*i)+1 = for문 순서 1~42

                c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), (cnt++)-start+1);


                if (index < start-1 || index > (end+start-2)) {

                    scheduleOfDay.getTextDay().setEnabled(false);
//                    scheduleOfDay.getDayList().setClickable(false);
                    scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.baseBackgroundColorLightGray));
                } else {
                    if (index % 7 == 0) {
                        scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.lightRed));
                    } else if (index % 7 == 6) {
                        scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.lightBlue));
                    }


                }

                if (compareDate(c, today)) {
                    scheduleOfDay.getTextDay().setBackground(context.getResources().getDrawable(R.drawable.round_today_shape));

                }
//                Log.d("manna_js", "makeCalendar: " + date.toString());

                scheduleOfDay.setDate((Calendar) c.clone());
                scheduleOfDay.getTextDay().setText(c.get(Calendar.DAY_OF_MONTH) + "");
            }
        }
    }

    public Calendar getDate() {
        return this.calendar_root.date;
    }

    public void increaseMonth(int range) {
        setDate(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH) + range);
    }

    public void decreaseMonth(int range) {
        setDate(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH) - range);
    }

    protected ScheduleOfDay findDateLayout(Calendar date) {
        for (int i = 0; i < scheduleOfDays.size(); i++) {


            if (compareDate(scheduleOfDays.get(i).getDate(), date)) {

                return scheduleOfDays.get(i);

            }
        }

        Log.d(TAG,"null");
        return null;
    }

    protected ScheduleOfDay findDateLayout(View view) {
        for (int i = 0; i < scheduleOfDays.size(); i++) {
            if (scheduleOfDays.get(i).getDayList() == view) {

                return scheduleOfDays.get(i);
            }
        }

        return null;
    }

    protected boolean compareDate(Calendar a, Calendar b) {
        if (a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.DATE) == b.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    public ListView getListView() {
        return listView;
    }

    public ArrayList<ScheduleOfDay> getScheduleOfDays() {
        return scheduleOfDays;
    }

    public Schedule_List getSchedule_list() {
        return schedule_list;
    }
}
