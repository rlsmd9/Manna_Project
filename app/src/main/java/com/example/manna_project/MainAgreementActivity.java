package com.example.manna_project;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_GridLayout;
import com.example.manna_project.MainAgreementActivity_Util.Friend.FriendListAdapter;
import com.example.manna_project.MainAgreementActivity_Util.Friend.FriendListItem;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainAgreementActivity extends Activity {
    TextView currentDate;
    Custom_Calendar custom_calendar;
    Friend_List friend_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);

        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();


        // 친구
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("친구");
        tabSpec.setContent(R.id.main_friendList);
        tabHost.addTab(tabSpec);
        Log.d("manna_js", "onCreate: a");
        friend_list = new Friend_List(this, (ListView) findViewById(R.id.main_friendList));


        // 약속
        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("약속");

        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        custom_calendar = new Custom_Calendar(this, (Custom_GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                (ListView) findViewById(R.id.main_agreement_listView), Calendar.getInstance());
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


    // 월 증감 약식 이벤트
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
