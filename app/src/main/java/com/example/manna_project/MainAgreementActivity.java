package com.example.manna_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_LinearLayout;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;
import com.example.manna_project.MainAgreementActivity_Util.Setting.Setting_List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainAgreementActivity extends Activity implements View.OnClickListener {
    TextView currentDate;
    ImageView customDatePicker;
    Custom_Calendar custom_calendar;
    Friend_List friend_list;
    Setting_List setting_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);
        customDatePicker = findViewById(R.id.main_agreement_changeDateBtn);

        customDatePicker.setOnClickListener(this);

        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        // 친구
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("친구");
        tabSpec.setContent(R.id.main_friendList);
        tabHost.addTab(tabSpec);
        friend_list = new Friend_List(this, (ListView) findViewById(R.id.main_friendList));


        // 약속
        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("약속");

        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        custom_calendar = new Custom_Calendar(this, (Custom_LinearLayout) findViewById(R.id.calendarRoot), (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                (ListView) findViewById(R.id.main_agreement_listView), (TextView) findViewById(R.id.calendar_currentDate), Calendar.getInstance());

        // 일정관리
        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("일정");
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);

        // 설정
        tabSpec = tabHost.newTabSpec("tab04_friend");
        tabSpec.setIndicator("설정");
        tabSpec.setContent(R.id.main_settingList);
        tabHost.addTab(tabSpec);
        setting_list = new Setting_List(this, (ListView) findViewById(R.id.main_settingList));
    }


    @Override
    public void onClick(View v) {
        if (v == customDatePicker) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_datepicker, null);
            builder.setView(view);

            final DatePicker datePicker = view.findViewById(R.id.custom_datepicker);
            datePicker.init(custom_calendar.getDate().get(Calendar.YEAR), custom_calendar.getDate().get(Calendar.MONTH),
                    custom_calendar.getDate().get(Calendar.DAY_OF_MONTH), null);
//            datePicker.setMaxDate(System.currentTimeMillis() - 1000);

            builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("manna_js", "onClick: " + datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth());
                    custom_calendar.setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                }
            });

            builder.create().show();
        }
    }

    // 월 증감 약식 이벤트
    public void onClick_test(View v) {
        custom_calendar.increaseMonth(1);
    }

    public void onClick_test_1(View v) {
        custom_calendar.decreaseMonth(1);
    }

}
