package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;

import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_RecyclerViewAdapter;

import java.util.ArrayList;

public class ShowDetailSchedule_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<NoticeBoard_Chat> chat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_schedule);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
