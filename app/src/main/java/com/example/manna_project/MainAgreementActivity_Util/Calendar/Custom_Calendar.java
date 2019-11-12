package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.manna_project.R;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Custom_Calendar implements View.OnClickListener {
//    final static String TAG = "manna_js";
    final static String TAG = "manna_JS";
    Custome_Calendar_Listener custome_calendar_listener;

    enum CalendarType {
        FULL_CALENDAR, HALF_CALENDAR, WEEK_CALENDAR;
    }

    enum TouchType {
        DOWN, UP;
    }

    public interface Custome_Calendar_Listener {
        void onChangeMonth();
    }


    Context context;
    LayoutInflater layout;
    Custom_LinearLayout calendar_root;
    GridLayout calendar_layout;
    ListView listView;
    TextView viewDate;
    // 일정 데이터 받아서 저장할 자료구조
    ArrayList<ScheduleOfDay> scheduleOfDays;

    public Custom_Calendar(Context context, Custom_LinearLayout calendar_root, GridLayout calendar_layout, ListView listView, TextView viewDate, Calendar date) {
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

        this.calendar_root.calendarType = CalendarType.FULL_CALENDAR;

        // 일자별 Linear 레이아웃 생성
        makeCalendar();
        // 생성한 일자 자료구조 전달
        calendar_root.setScheduleOfDays(this.scheduleOfDays);

        setCalendar();
        selectDay();
    }

    public void setCustome_calendar_listener(Custome_Calendar_Listener custome_calendar_listener) {
        this.custome_calendar_listener = custome_calendar_listener;
    }

    public void showView() {
        int index;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
                scheduleOfDay.setLayout();
            }
        }
    }

    public void setSchedule(Events events) {
        int start;
        int index;

        Log.d(TAG, "setSchedule: " + events.getItems().size());

        Calendar c = Calendar.getInstance();

        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);

        Log.d(TAG, "setSchedule: start = " + start);

        initCalendarUI();

        for (Event event: events.getItems()) {
            DateTime startEventTime = event.getStart().getDateTime();

            if (startEventTime == null)
                startEventTime = event.getStart().getDate();
            Calendar eventDay = Calendar.getInstance();
            eventDay.setTime(new Date(startEventTime.getValue()));
            index = eventDay.get(Calendar.DAY_OF_MONTH) + start - 2;
            ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
            scheduleOfDay.addEvent(event);
            Log.d(TAG, "setSchedule: end");
        }
    }

    protected void initCalendarUI() {
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
        Log.d(TAG, "setDate: " + date.toString());

        if (date.get(Calendar.YEAR) != calendar_root.date.get(Calendar.YEAR) || date.get(Calendar.MONTH) != calendar_root.date.get(Calendar.MONTH)) {
            this.calendar_root.date = date;
            setCalendar();
            if (custome_calendar_listener != null)custome_calendar_listener.onChangeMonth();
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
            if (custome_calendar_listener != null)custome_calendar_listener.onChangeMonth();
        } else {
            this.calendar_root.date.set(year, month, day);
            setCalendar();
        }
        Log.d(TAG, "setDate: " + calendar_root.date.toString());
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
                        GridLayout.UNDEFINED,GridLayout.FILL,1f),
                        GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f));

                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(context);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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

        selectDay();
        Log.d(TAG, this.getDate().get(Calendar.DAY_OF_MONTH)+"");
    }

    // 현재 저장된 Date를 선택
    protected void selectDay() {
        setCleanBackground();
        ScheduleOfDay current_scheduleOfDay = findDateLayout(this.getDate());

        current_scheduleOfDay.getDayList().setBackground(context.getResources().getDrawable(R.color.selectedItem));

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
                scheduleOfDay.getTextDay().setBackground(calendar_layout.getBackground());
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


}
