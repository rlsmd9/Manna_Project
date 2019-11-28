package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Button acceptButton;
    Button refuseButton;
    Button chatAddButton;
    TextView chatText;
    //----------------

    ArrayList<MannaUser> attendees;
    NoticeBoard_RecyclerViewAdapter adapter;
    FirebaseCommunicator firebaseCommunicator;
    RecyclerView recyclerView;
    LinearLayout attendees_group;
    ArrayList<NoticeBoard_Chat> chat_list;
    Promise promise;
    MannaUser myInfo;
    int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_schedule);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        firebaseCommunicator = new FirebaseCommunicator(this);

        mode = getIntent().getIntExtra("Mode", 1);
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
        acceptButton = findViewById(R.id.activity_show_detail_schedule_accept_btn);
        refuseButton = findViewById(R.id.activity_show_detail_schedule_cancel_btn);
        chatAddButton = findViewById(R.id.activity_show_detail_schedule_chat_add_btn);
        chatText = findViewById(R.id.activity_show_detail_schedule_chat_text);

        chatAddButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        refuseButton.setOnClickListener(this);

        if (mode == 2) {
            myInfo = getIntent().getParcelableExtra("MyInfo");
            if (promise.getLeaderId().equals(myInfo.getUid())) {
                acceptButton.setVisibility(View.VISIBLE);
                acceptButton.setText("약속 확정");
                refuseButton.setText("약속 삭제");
            } else {
                acceptButton.setVisibility(View.GONE);
                refuseButton.setText("약속 취소");
            }
        }

        attendees = promise.getAttendees();
        setAttendeeList();

        title.setText(promise.getTitle());
        leader.setText(promise.getLeader().getName());
        place.setText(promise.getLoadAddress());

        if (promise.getStartTime() == null) {
            date.setText("시간 미정");
        } else {
            StringBuilder txt = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

            txt.append(simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));

            txt.append(" ~ ");

            if (promise.getEndTime().get(Calendar.YEAR) != promise.getStartTime().get(Calendar.YEAR)) simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            else simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

            txt.append(simpleDateFormat.format(new Date(promise.getEndTime().getTimeInMillis())));

            date.setText(txt);
        }

        recyclerView = findViewById(R.id.activity_show_detail_schedule_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chat_list = new ArrayList<>();

        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {

            }

            @Override
            public void afterGetPromise(Promise promise) {

            }

            @Override
            public void afterGetPromiseKey(ArrayList<String> promiseKeys) {

            }

            @Override
            public void afterGetFriendUids(ArrayList<String> friendList) {

            }

            @Override
            public void afterGetChat(NoticeBoard_Chat chat) {
                for(MannaUser mannaUser : attendees){
                    if(mannaUser.getUid().equals(chat.getUserUid()))
                        chat.setUser(mannaUser);
                }
                chat_list.add(chat);
                adapter.notifyDataSetChanged();
            }
        });
        firebaseCommunicator.getChatListByPromise(promise.getPromiseid());
         adapter = new NoticeBoard_RecyclerViewAdapter(getLayoutInflater(), chat_list);
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
        } else if(v == chatAddButton) {
            if (!chatText.getText().toString().isEmpty()) {
                String comment = chatText.getText().toString();
                chatText.setText(null);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
                NoticeBoard_Chat noticeBoard_chat = new NoticeBoard_Chat(myInfo.getUid(),comment,date);
                firebaseCommunicator.addComment(promise.getPromiseid(),noticeBoard_chat);
            }
        } else if (mode == 2) {

            // 수락된 약속 이고 방장일때
            if (promise.getLeaderId().equals(myInfo.getUid())) {
                if (v == acceptButton) {

                } else if(v == refuseButton) {

                }
            } else {
                // 방장 아닐때
                if (v == refuseButton) {

                }
            }

        } else {
            // 초대된 약속
            if (v == acceptButton) {

            } else if(v == refuseButton) {

            }

        }
    }
}
