package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_RecyclerViewAdapter;
import com.example.manna_project.MainAgreementActivity_Util.Promise;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowDetailSchedule_Activity extends AppCompatActivity {

    // Activity Object
    TextView title;
    TextView leader;
    TextView place;
    TextView date;
    //--------

    ArrayList<MannaUser> attendees;

    RecyclerView recyclerView;
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

        attendees = promise.getAttendees();

        title.setText(promise.getTitle());
        leader.setText(promise.getLeader().getName());
//        place.setText

        StringBuilder txt = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));

        txt.append(" ~ ");

        simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getEndTime().getTimeInMillis())));

        date.setText(txt);

        recyclerView = findViewById(R.id.activity_show_detail_schedule_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chat_list = new ArrayList<>();

        NoticeBoard_Chat chat = new NoticeBoard_Chat("a", "b", "c", "d", "20162510");

        chat_list.add(chat);

        chat = new NoticeBoard_Chat("a", "b", "c", "d", "20162510");

        chat_list.add(chat);

        chat = new NoticeBoard_Chat("a", "b", "c", "d", "20162510");

        chat_list.add(chat);

        NoticeBoard_RecyclerViewAdapter adapter = new NoticeBoard_RecyclerViewAdapter(getLayoutInflater(), chat_list);

        recyclerView.setAdapter(adapter);

    }
}
