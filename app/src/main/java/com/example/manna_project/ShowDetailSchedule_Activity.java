package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.Custom_user_icon_view;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_RecyclerViewAdapter;
import com.example.manna_project.MainAgreementActivity_Util.Promise;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowDetailSchedule_Activity extends AppCompatActivity implements View.OnClickListener {

    // Activity Object
    TextView title;
    TextView leader;
    TextView place;
    TextView date;
    ImageView closeButton;
    //--------

    ArrayList<MannaUser> attendees;

    RecyclerView recyclerView;
    LinearLayout attendees_group;
    ArrayList<NoticeBoard_Chat> chat_list;
    Promise promise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_schedule);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        promise = getIntent().getParcelableExtra("Promise_Info");

        if (promise == null) {
            Log.d("JS", "onCreate: promise is null");
            finish();
        }

        Log.d("JS", "onCreate: " + promise.toString());

        title = findViewById(R.id.activity_show_detail_schedule_title);
        leader = findViewById(R.id.activity_show_detail_schedule_leader);
        place = findViewById(R.id.activity_show_detail_schedule_place);
        date = findViewById(R.id.activity_show_detail_schedule_date);
        attendees_group = findViewById(R.id.activity_show_detail_schedule_attendees_group);
        closeButton = findViewById(R.id.activity_show_detail_schedule_close_btn);

        closeButton.setOnClickListener(this);

        attendees = promise.getAttendees();
        setAttendeeList();

        title.setText(promise.getTitle());
        leader.setText(promise.getLeader().getName());
        place.setText(promise.getLoadAddress());

        StringBuilder txt = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));

        txt.append(" ~ ");

        if (promise.getEndTime().get(Calendar.YEAR) != promise.getStartTime().get(Calendar.YEAR)) simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        else simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getEndTime().getTimeInMillis())));

        date.setText(txt);

        recyclerView = findViewById(R.id.activity_show_detail_schedule_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chat_list = new ArrayList<>();

        NoticeBoard_Chat chat = new NoticeBoard_Chat(promise.getLeader(), "테스트", "2019-10-12 12:33");

        chat_list.add(chat);

        NoticeBoard_RecyclerViewAdapter adapter = new NoticeBoard_RecyclerViewAdapter(getLayoutInflater(), chat_list);

        recyclerView.setAdapter(adapter);

    }

    private void setAttendeeList() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        attendees_group.removeAllViews();

        if (attendees == null) return;

        for (MannaUser user: attendees) {
            Custom_user_icon_view view = (Custom_user_icon_view) inflater.inflate(R.layout.user_name_icon_layout, null);

            view.setTextView((TextView) view.findViewById(R.id.user_name_icon));
            view.setUser(user);

            Log.d(MainAgreementActivity.TAG, "setAttendeeList: " + user.toString());

            attendees_group.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == closeButton) {
            finish();
        }
    }
}
