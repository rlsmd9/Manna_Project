package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.manna_project.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Custom_Calendar implements View.OnClickListener {
    final static String TAG = "manna_js";
    enum CalendarType {
        FULL_CALENDAR, HALF_CALENDAR, WEEK_CALENDAR;
    }

    enum TouchType {
        DOWN, UP;
    }

    Context context;
    LayoutInflater layout;
    Calendar date;
    Custom_GridLayout calendar_root_grid;
    ListView listView;
    // 일정 데이터 받아서 저장할 자료구조
    ArrayList<ScheduleOfDay> scheculeOfDays;


    public Custom_Calendar(Context context, Custom_GridLayout calendar_root_grid, ListView listView, Calendar date) {
        this.context = context;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listView = listView;
        Log.d(TAG, "Custom_Calendar: w");
        this.calendar_root_grid = calendar_root_grid;
        calendar_root_grid.setListView(this.listView);
        Log.d(TAG, "Custom_Calendar: a");
        this.date = Calendar.getInstance();
        this.date.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE),0,0,0);
        this.scheculeOfDays = new ArrayList<>();

        this.calendar_root_grid.calendarType = CalendarType.FULL_CALENDAR;
        makeCalendar();
        setCalendar();
    }

    public Custom_Calendar(Context context, Custom_GridLayout calendar_root_grid, Calendar date, ArrayList<ScheduleOfDay> scheculeOfDays) {
        this.context = context;
        this.date = Calendar.getInstance();
        this.date.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE),0,0,0);
        this.scheculeOfDays = scheculeOfDays;
        this.calendar_root_grid = calendar_root_grid;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.scheculeOfDays = new ArrayList<>();
        this.calendar_root_grid.calendarType = CalendarType.FULL_CALENDAR;
    makeCalendar();
    setCalendar();
}

    public void setDate(Calendar date) {
        this.date = date;
        setCalendar();
    }

    public void setDate(int day) {
        this.date.set(Calendar.DATE, day);

    }

    // month index range is 0 to 11
    public void setDate(int year, int month) {
        this.date.set(year, month, 1);
        setCleanBackground();
        setCalendar();
    }

    // month index range is 0 to 11
    public void setDate(int year, int month, int day) {
        this.date.set(year, month, day);
        setCleanBackground();
        setCalendar();
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
                calendar_root_grid.addView(ly,param);

                scheculeOfDays.add(new ScheduleOfDay(null, textView, ly, null));
            }
        }
    }

    protected void setCleanBackground() {
        for (int i = 0; i < scheculeOfDays.size(); i++) {
        scheculeOfDays.get(i).getDayList().setBackground(calendar_root_grid.getBackground());
        }
    }



    @Override
    public void onClick(View v) {
        ScheduleOfDay selected_scheduleOfDay = findDateLayout(v);
        ScheduleOfDay current_scheduleOfDay = findDateLayout(this.getDate());

        current_scheduleOfDay.getDayList().setBackground(calendar_root_grid.getBackground());
        selected_scheduleOfDay.getDayList().setBackground(context.getResources().getDrawable(R.color.selectedItem));
        this.setDate(selected_scheduleOfDay.getDate());
    }

    protected void setCalendar() {
        int cnt = 1;
        int start, end;
        int index;
        Calendar c = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);
        c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, 0);
        end = c.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheculeOfDays.get(index);

                scheduleOfDay.getTextDay().setEnabled(true);
                scheduleOfDay.getDayList().setClickable(true);
                scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.textGrayColor));
                scheduleOfDay.getTextDay().setBackground(calendar_root_grid.getBackground());
                // j+(7*i)+1 = for문 순서 1~42
//                Log.d("manna_js", "makeCalendar: 1");


                c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), (cnt++)-start+1);


                if (index < start-1 || index > (end+start-2)) {

                    scheduleOfDay.getTextDay().setEnabled(false);
                    scheduleOfDay.getDayList().setClickable(false);
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
        return date;
    }

    public void increaseMonth(int range) {
        setDate(this.date.get(Calendar.YEAR), this.date.get(Calendar.MONTH)+range);
    }

    public void decreaseMonth(int range) {
        setDate(this.date.get(Calendar.YEAR), this.date.get(Calendar.MONTH) - range);
    }

    protected ScheduleOfDay findDateLayout(Calendar date) {
        for (int i = 0; i < scheculeOfDays.size(); i++) {


            if (compareDate(scheculeOfDays.get(i).getDate(), date)) {

                return scheculeOfDays.get(i);

            }
        }

        Log.d("manna_js","null");
        return null;
    }

    protected ScheduleOfDay findDateLayout(View view) {
        for (int i = 0; i < scheculeOfDays.size(); i++) {
            if (scheculeOfDays.get(i).getDayList() == view) {

                return scheculeOfDays.get(i);
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


class ScheduleOfDay {
    private Calendar date;
    private TextView textDay;
    private LinearLayout dayList;
    private CustomSchedule customSchedule;

    public ScheduleOfDay(Calendar date, TextView day, LinearLayout dayList, CustomSchedule customSchedule) {
        this.date = date;
        this.textDay = day;
        this.dayList = dayList;
        this.customSchedule = customSchedule;
    }

    public CustomSchedule getCustomSchedule() {
        return customSchedule;
    }

    public void setCustomSchedule(CustomSchedule customSchedule) {
        this.customSchedule = customSchedule;
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
}

class CustomSchedule {

}

