package com.example.manna_project;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SettingPersonalRoutine extends AppCompatActivity implements View.OnClickListener {

    TableLayout routineTableLayout;
    Button backBtn, saveBtn;
    TimeTableButton timeTableBtn[][];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_personal_routine);

        timeTableBtn = new TimeTableButton[14][7];
        routineTableLayout = (TableLayout) findViewById(R.id.personal_routine_timetable_layout);
        setTimetable(routineTableLayout);
        backBtn = (Button) findViewById(R.id.setting_personal_routine_cancel);
        saveBtn = (Button) findViewById((R.id.setting_personal_routine_save));
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }

    private void setTimetable(TableLayout table) {
        //먼저 사용자의 원래 일과정보를 가져와야함
        for (int i = 8; i < 22; i++) {
            TableRow row = new TableRow(this);
            LinearLayout.LayoutParams rowParams = new TableLayout.LayoutParams(MATCH_PARENT, 0);
            rowParams.weight = 1;
            rowParams.setMargins(0, 0, 0, 0);
            row.setLayoutParams(rowParams);
            table.addView(row);

            TextView time = new TextView(this);
            LinearLayout.LayoutParams textparams = new TableRow.LayoutParams(0, MATCH_PARENT);
            textparams.weight = 1;
            int t = i % 12;
            if (t == 0)
                t = 12;
            time.setText(t + "시");
            time.setTypeface(null, Typeface.BOLD);
            time.setGravity(1);
            textparams.setMargins(0, 0, 0, 0);
            time.setLayoutParams(textparams);
            row.addView(time);

            for (int j = 0; j < 7; j++) {
                TimeTableButton btn = new TimeTableButton(this);
                timeTableBtn[i - 8][j] = btn;
                LinearLayout.LayoutParams btnparams = new TableRow.LayoutParams(0, MATCH_PARENT);
                btnparams.weight = 1;
                btn.setBackgroundResource(R.drawable.edge_rectangular);
                btnparams.setMargins(0, 0, 0, 0);
                btn.setLayoutParams(btnparams);
                btn.setOnClickListener(this);
                row.addView(btn);
            }

        }

    }

    private class TimeTableButton extends AppCompatButton {

        public boolean isClicked;

        private TimeTableButton(Context context) {
            super(context);
            this.isClicked = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == backBtn) {
            finish();
        } else if (view == saveBtn) {
            //이벤트객체 만드는 함수 생성
            makeRoutineObject();
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            TimeTableButton temp = (TimeTableButton) view;
            if (temp.isClicked == true) {
                temp.setBackgroundResource(R.drawable.edge_rectangular);
                temp.isClicked = false;
            } else if (temp.isClicked == false) {
                temp.setBackgroundResource(R.drawable.edge_rectangular_blue);
                temp.isClicked = true;
            }
        }
    }

    private void makeRoutineObject() {       //버튼이 눌려있는 시간대에 시작시간과 끝시간을 가져옴
        int time = 8, day = 0;
        //시작 종료 넣을 자료구조
        ArrayList<MannaUser.Routine> routineArr = new ArrayList<>();
        MannaUser factory = new MannaUser();
        while (time < 22 && day <= 6) {
            if (timeTableBtn[time - 8][day].isClicked) {
                int startTime = time, endTime;
                while (time < 22 && timeTableBtn[time - 8][day].isClicked) {
                    time++;
                }
                endTime = time;
                Log.d("MANNAYC", startTime + "시 ~ " + endTime + "시");
                routineArr.add(factory.new Routine(startTime,endTime,day));
            } else
                time++;

            if (time >= 22) {
                time = 8;
                day++;
            }
        }
        sendRoutine(routineArr);
    }
    private void sendRoutine(ArrayList<MannaUser.Routine> routineArr) {
        String myUid = FirebaseCommunicator.getMyUid();
        FirebaseCommunicator comunicator = new FirebaseCommunicator();
        comunicator.updateRoutine(myUid,routineArr);
    }
}

