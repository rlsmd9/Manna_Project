package com.example.manna_project;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SettingPersonalRoutine extends AppCompatActivity implements View.OnClickListener {

    TableLayout routineTableLayout;
    Button backBtn, saveBtn;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_personal_routine);

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
            LinearLayout.LayoutParams rowParams = new TableLayout.LayoutParams(MATCH_PARENT,0 );
            rowParams.weight = 1;
            row.setLayoutParams(rowParams);
            table.addView(row);

            TextView time = new TextView(this);
            LinearLayout.LayoutParams textparams = new TableRow.LayoutParams(0, MATCH_PARENT);
            textparams.weight = 1;
            time.setText(Integer.toString((i%13))+"시");
            time.setTypeface(null, Typeface.BOLD);
            time.setGravity(1);
            time.setLayoutParams(textparams);
            row.addView(time);

            for (int j = 0; j < 7; j++) {
                Button btn = new Button(this);
                LinearLayout.LayoutParams btnparams = new TableRow.LayoutParams(0, MATCH_PARENT);
                btnparams.weight = 1;
                btn.setOnClickListener(this);
                btn.setLayoutParams(btnparams);
                row.addView(btn);
            }

        }

    }

    @Override
    public void onClick(View view) {
        if (view == backBtn) {
            finish();
        } else if (view == saveBtn) {
            //파이어베이스로 일과정보 넘기는 부분
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            view.setBackgroundColor(getResources().getColor(R.color.lightBlue));

        }
    }
}
