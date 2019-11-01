package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manna_project.R;

import java.util.ArrayList;
import java.util.Calendar;

public class Custom_Calendar {
    Context context;
    LayoutInflater layout;
    Calendar date;
    GridLayout calendar_root_grid;
    // 일정 데이터 받아서 저장할 자료구조
    ArrayList<CustomSchedule> datalist;


    public Custom_Calendar(Context context, GridLayout calendar_root_grid, Calendar date) {
        this.context = context;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.calendar_root_grid = calendar_root_grid;
        this.date = date;
    }

    public Custom_Calendar(Context context, GridLayout calendar_root_grid, Calendar date, ArrayList<CustomSchedule> datalist) {
        this.context = context;
        this.date = date;
        this.datalist = datalist;
        this.calendar_root_grid = calendar_root_grid;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void makeCalendar() {
        // activity_main_agreement의 약속탭에 달력 만들기
        int cnt = 1;
        int start, end;
        Calendar c = Calendar.getInstance();
        c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1);
        start = c.get(Calendar.DAY_OF_WEEK);
        c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, 0);
        end = c.get(Calendar.DAY_OF_MONTH);
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

                textView.setEnabled(true);
                // j+(7*i)+1 = for문 순서 1~42
                Log.d("manna_js", "makeCalendar: 1");
                c.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), (cnt++)-start+1);
                if (j+(7*i)+1 < date.get(Calendar.DAY_OF_WEEK) || j+(7*i)+1 > (end+start-1)) {
                    textView.setEnabled(false);
                } else {
                    if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                        textView.setTextColor(context.getResources().getColor(R.color.lightRed));
                    } else if (c.get(Calendar.DAY_OF_WEEK) == 7) {
                        textView.setTextColor(context.getResources().getColor(R.color.lightBlue));
                    }
                }



                textView.setText(c.get(Calendar.DAY_OF_MONTH) + "");
                ly.addView(textView,linearParams);
                calendar_root_grid.addView(ly,param);
            }
        }
    }
}


class ScheculeOfDay {
    LinearLayout dayList;
    CustomSchedule customSchedule;


}

class CustomSchedule {

}

