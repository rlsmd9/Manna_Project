package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

public class main_agreement extends AppCompatActivity {

    GridLayout calendar_root_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);

        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("친구");
        tabSpec.setContent(R.id.tab02_friend);
        calendar_root_grid = findViewById(R.id.dateRootGrid);
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
//        calendar_root_grid.removeAllViews();

        // activity_main_agreement의 약속탭에 달력 만들기
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                TextView tv = new TextView(this);
                tv.setWidth(100);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = calendar_root_grid.getMeasuredWidth()/7;
                layoutParams.height = calendar_root_grid.getMeasuredHeight()/6;
                layoutParams.columnSpec = GridLayout.spec(i);
                layoutParams.rowSpec = GridLayout.spec(j);
                tv.setText("DW");
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setLayoutParams(layoutParams);
//
//                calendar_root_grid.addView(tv);
//                LinearLayout ly = new LinearLayout(this);
//                ly.setOrientation(LinearLayout.VERTICAL);
//                ly.setBackgroundColor(Color.rgb(255,255,0));
                calendar_root_grid.addView(tv);
            }
        }
    }
}
