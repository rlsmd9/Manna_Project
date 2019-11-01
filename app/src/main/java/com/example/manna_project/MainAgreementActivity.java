package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.Custom_Calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainAgreementActivity extends AppCompatActivity {
    TextView currentDate;
    Custom_Calendar custom_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);

        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("친구");
        tabSpec.setContent(R.id.tab02_friend);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("약속");
        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        custom_calendar = new Custom_Calendar(this, (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout), Calendar.getInstance());
        setDate(custom_calendar.getDate());

        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("일정");
        tabSpec.setContent(R.id.tab03_friend);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab04_friend");
        tabSpec.setIndicator("설정");
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
    }

    public void onClick_test(View v) {
        custom_calendar.increaseMonth(1);
        setDate(custom_calendar.getDate());
    }

    public void onClick_test_1(View v) {
        custom_calendar.decreaseMonth(1);
        setDate(custom_calendar.getDate());
    }

    public void setDate(Calendar date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월");
        Log.d("manna_js", simpleDateFormat.format(date.getTime())+"");
        this.currentDate.setText(simpleDateFormat.format(date.getTime()));
    }
}
