package com.example.manna_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowDetailSchedule_Activity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, View.OnTouchListener {

    public static final int SHOW_DETAIL_CHEDULE_CODE=19970703;
    public static String TAG = "MANNAYC";
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
    TextView activity_show_detail_schedule_choose_date;
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

    // Naver Map
    MapFragment mapFragment;
    MapView activity_search_place_map;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_schedule);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        firebaseCommunicator = new FirebaseCommunicator(this);

        mode = getIntent().getIntExtra("Mode", 1);
        promise = getIntent().getParcelableExtra("Promise_Info");
        myInfo = getIntent().getParcelableExtra("MyInfo");

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
        activity_search_place_map = findViewById(R.id.activity_search_place_map);
        activity_show_detail_schedule_choose_date = findViewById(R.id.activity_show_detail_schedule_choose_date);

        chatAddButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        refuseButton.setOnClickListener(this);
        activity_show_detail_schedule_choose_date.setOnClickListener(this);


        // naver map
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.activity_search_place_map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.activity_search_place_map, mapFragment).commit();
        }

        marker = new Marker();
        mapFragment.getMapAsync(this);

        place.setOnTouchListener(this);


        if (mode == 2) {
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
                recyclerView.smoothScrollToPosition(chat_list.size()-1);
            }
        });
        firebaseCommunicator.getChatListByPromise(promise.getPromiseid());
        adapter = new NoticeBoard_RecyclerViewAdapter(getLayoutInflater(), chat_list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == place) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                place.setPaintFlags(place.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                Paint paint = new Paint();
                paint.reset();
                place.setPaintFlags(paint.getFlags());

                if (activity_search_place_map.getVisibility() == View.GONE) {
                    activity_search_place_map.setVisibility(View.VISIBLE);
                    mapFragment.getMapAsync(this);
                } else {
                    activity_search_place_map.setVisibility(View.GONE);
                }
            }
        }

        return true;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        LatLng latLng = new LatLng(promise.getLatitude(), promise.getLongitude());

        naverMap.setCameraPosition(new CameraPosition(latLng, 16));
        marker.setPosition(latLng);
        marker.setMap(naverMap);
    }

    private void setAttendeeList() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        attendees_group.removeAllViews();

        if (attendees == null) return;

        for (MannaUser user: attendees) {
            String uid = user.getUid();
            int state = promise.getAcceptState().get(uid);
            Custom_user_icon_view view = (Custom_user_icon_view) inflater.inflate(R.layout.user_name_icon_layout, null);
            view.setTextView((TextView) view.findViewById(R.id.user_name_icon));
            view.setUser(user, state == Promise.CANCELED);

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
                chatAddButton.requestFocus();
            }
        } else if(v == activity_show_detail_schedule_choose_date) {
            Log.d(TAG, "onClick: dkdkdwkdwk");
        } else if (mode == 2) {

            // 수락된 약속 이고 방장일때
            if (promise.getLeaderId().equals(myInfo.getUid())) {
                if (v == acceptButton) {
                    // 약속 확정
                    Intent intent = new Intent(this, MainAgreementActivity.class);

                    intent.putExtra("FIXED_PROMISE", promise);

                    setResult(RESULT_OK, intent);
                    finish();
                } else if(v == refuseButton) {
                    firebaseCommunicator.deletePromise(promise);
                    setResult(RESULT_OK);
                    finish();
                }
            } else {
                // 방장 아닐때
                if (v == refuseButton) {
                    firebaseCommunicator.cancelPromise(promise,myInfo.getUid());
                    Log.d(TAG,"버튼이 눌렸는데?");
                    setResult(RESULT_OK);
                    finish();
                }
            }

        } else {
            // 초대된 약속
            if (v == acceptButton) {
                firebaseCommunicator.acceptPromise(promise,firebaseCommunicator.getMyUid());
                setResult(RESULT_OK);
                finish();
            } else if(v == refuseButton) {
                firebaseCommunicator.cancelPromise(promise,firebaseCommunicator.getMyUid());
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
