package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class main_agreement extends AppCompatActivity {

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

        // 달력 생성
        makeCalendar();

        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("일정");
        tabSpec.setContent(R.id.tab03_friend);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab04_friend");
        tabSpec.setIndicator("설정");
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
    }

    private void makeCalendar() {

    }
}
