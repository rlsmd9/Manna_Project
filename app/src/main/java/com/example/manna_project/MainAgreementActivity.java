package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.TabHost;

import com.example.manna_project.MainAgreementActivity_Util.Custom_Calendar;

import java.util.Calendar;

public class MainAgreementActivity extends AppCompatActivity {

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
        custom_calendar = new Custom_Calendar(this, (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout), Calendar.getInstance());
        custom_calendar.makeCalendar();

        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("일정");
        tabSpec.setContent(R.id.tab03_friend);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab04_friend");
        tabSpec.setIndicator("설정");
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
    }


}
