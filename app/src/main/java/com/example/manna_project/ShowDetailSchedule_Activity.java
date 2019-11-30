package com.example.manna_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.manna_project.MainAgreementActivity_Util.AlertMsg;
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
    LinearLayout activity_show_detail_schedule_date_group;
    TextView activity_show_detail_schedule_date_start;
    TextView activity_show_detail_schedule_date_end;
    TextView activity_show_detail_schedule_date_label;
    Button activity_show_detail_schedule_commit_date_btn;
    LinearLayout activity_show_detail_schedule_chat_group;
    TextView activity_show_detail_schedule_leader_label;
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

    // date picker
    Calendar start;
    Calendar end;
    int cal_switch;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_schedule);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        firebaseCommunicator = new FirebaseCommunicator(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mode = getIntent().getIntExtra("Mode", 1);
        promise = getIntent().getParcelableExtra("Promise_Info");
        myInfo = getIntent().getParcelableExtra("MyInfo");

        if (promise == null) {
            Log.d("JS", "onCreate: promise is null");
            finish();
        }

        Log.d("JS", "onCreate: " + promise.toString());

        setReferences();

        chatAddButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        refuseButton.setOnClickListener(this);
        date.setOnTouchListener(this);
        activity_show_detail_schedule_date_end.setOnClickListener(this);
        activity_show_detail_schedule_date_start.setOnClickListener(this);
        activity_show_detail_schedule_commit_date_btn.setOnClickListener(this);

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
        if (mode == 3) {
            activity_show_detail_schedule_leader_label.setVisibility(View.GONE);
            leader.setVisibility(View.GONE);
        } else {
            leader.setText(promise.getLeader().getName());
        }

        place.setText(promise.getLoadAddress());

        if (promise.isTimeFixed() == Promise.UNFIXEDTIME) {
            activity_show_detail_schedule_date_label.setText("기간");
            date.setTextColor(getApplicationContext().getResources().getColor(R.color.lightRed));
            date.setText(getConnectDate() + "(시간 미정)");
        } else {
            date.setText(getConnectDate());
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


        start = Calendar.getInstance();
        end = Calendar.getInstance();
    }

    private String getConnectDate() {
        StringBuilder txt = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getStartTime().getTimeInMillis())));

        txt.append(" ~ ");

        if (promise.getEndTime().get(Calendar.YEAR) != promise.getStartTime().get(Calendar.YEAR)) simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        else simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm");

        txt.append(simpleDateFormat.format(new Date(promise.getEndTime().getTimeInMillis())));

        return txt.toString();
    }

    private void setReferences() {
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
        activity_show_detail_schedule_date_group = findViewById(R.id.activity_show_detail_schedule_date_group);
        activity_show_detail_schedule_date_start = findViewById(R.id.activity_show_detail_schedule_date_start);
        activity_show_detail_schedule_date_end = findViewById(R.id.activity_show_detail_schedule_date_end);
        activity_show_detail_schedule_date_label = findViewById(R.id.activity_show_detail_schedule_date_label);
        activity_show_detail_schedule_commit_date_btn = findViewById(R.id.activity_show_detail_schedule_commit_date_btn);
        activity_show_detail_schedule_chat_group = findViewById(R.id.activity_show_detail_schedule_chat_group);
        activity_show_detail_schedule_leader_label = findViewById(R.id.activity_show_detail_schedule_leader_label);
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
                    activity_show_detail_schedule_date_group.setVisibility(View.GONE);
                    activity_search_place_map.setVisibility(View.VISIBLE);
                    mapFragment.getMapAsync(this);
                } else {
                    activity_search_place_map.setVisibility(View.GONE);
                }
            }
        } else if (v == date) {
            if (promise.isTimeFixed() != Promise.UNFIXEDTIME || !promise.getLeaderId().equals(myInfo.getUid())) return true;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                date.setPaintFlags(date.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                Paint paint = new Paint();
                paint.reset();
                date.setPaintFlags(paint.getFlags());

                if (activity_show_detail_schedule_date_group.getVisibility() == View.GONE) {
                    activity_show_detail_schedule_date_group.setVisibility(View.VISIBLE);
                    activity_search_place_map.setVisibility(View.GONE);
                } else {
                    activity_show_detail_schedule_date_group.setVisibility(View.GONE);
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
            view.setUser(user, state);

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
        } else if(v == activity_show_detail_schedule_date_start || v == activity_show_detail_schedule_date_end) {
            if (v == activity_show_detail_schedule_date_start) cal_switch = 1;
            if (v == activity_show_detail_schedule_date_end) cal_switch = 2;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            Log.d(MainAgreementActivity.TAG, "onClick: " + inflater);
            View view = inflater.inflate(R.layout.custom_date_and_time_picker, null);

            setInitDateAndTimePicker(view);

            builder.setView(view);

            builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cal_switch == 1) {
                        start.set(Integer.parseInt(year.getText()+""), month.getValue(), day.getValue(),
                                hour.getValue() + (ampm.getValue() == 1?12:0),min.getValue(),0);

                        activity_show_detail_schedule_date_start.setText(simpleDateFormat.format(new Date(start.getTimeInMillis())));

                        if (activity_show_detail_schedule_date_end.getText().toString().isEmpty()) {
                            end.set(start.get(Calendar.YEAR),start.get(Calendar.MONTH),start.get(Calendar.DAY_OF_MONTH),
                                    hour.getValue() + (ampm.getValue() == 1?12:0)+1,start.get(Calendar.MINUTE),0);
                            activity_show_detail_schedule_date_end.setText(simpleDateFormat.format(new Date(end.getTimeInMillis())));
                        }
                    }
                    else if(cal_switch == 2) {
                        end.set(Integer.parseInt(year.getText()+""), month.getValue(), day.getValue(),
                                hour.getValue() + (ampm.getValue() == 1?12:0),min.getValue(),0);

                        activity_show_detail_schedule_date_end.setText(simpleDateFormat.format(new Date(end.getTimeInMillis())));
                    }
                }
            });

            builder.create().show();
        } else if(v == activity_show_detail_schedule_commit_date_btn) {
            try {
                start.setTime(simpleDateFormat.parse(activity_show_detail_schedule_date_start.getText().toString()));
                end.setTime(simpleDateFormat.parse(activity_show_detail_schedule_date_end.getText().toString()));
            } catch (Exception e) {

            }

            promise.getStartTime().set(Calendar.MILLISECOND, 0);
            promise.getEndTime().set(Calendar.MILLISECOND, 0);
            promise.getStartTime().set(Calendar.SECOND, 0);
            promise.getEndTime().set(Calendar.SECOND, 0);

            Log.d(TAG, "onClick: start = " + start.getTimeInMillis());
            Log.d(TAG, "onClick: end = " + end.getTimeInMillis());
            Log.d(TAG, "onClick: pro start = " + promise.getStartTime().getTimeInMillis());
            Log.d(TAG, "onClick: pro end = " + promise.getEndTime().getTimeInMillis());

            if (activity_show_detail_schedule_date_start.getText().toString().isEmpty() || activity_show_detail_schedule_date_end.getText().toString().isEmpty()) {
                AlertMsg.AlertMsg(this, "만날 시간을 정해주세요");
            } else if(end.getTimeInMillis() < start.getTimeInMillis()) {
                AlertMsg.AlertMsg(this, "종료시간은 시작시간보다 빠를수 없습니다.");
            } else if((start.getTimeInMillis() < promise.getStartTime().getTimeInMillis() || start.getTimeInMillis() > promise.getEndTime().getTimeInMillis()) ||
                    end.getTimeInMillis() < promise.getStartTime().getTimeInMillis() || end.getTimeInMillis() > promise.getEndTime().getTimeInMillis()) {
                AlertMsg.AlertMsg(this, "시간은 처음 지정한 약속 기간내에서 정해주세요");
            } else {
                AlertMsg.AlertMsgRes(this, "한번 정하면 수정할 수 없습니다.\n그래도 저장 하시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 날짜 저장
                        promise.setStartTime(start);
                        promise.setEndTime(end);
                        promise.setTimeFixed(Promise.FiXEDTIME);

                        firebaseCommunicator.updatePromise(promise);

                        activity_show_detail_schedule_date_label.setText("시간");
                        date.setTextColor(place.getTextColors());
                        date.setText(getConnectDate());

                        activity_show_detail_schedule_date_group.setVisibility(View.GONE);
                        setResult(RESULT_OK);
                    }
                });
            }
        } else if (mode == 2) {

            // 수락된 약속 이고 방장일때
            if (promise.getLeaderId().equals(myInfo.getUid())) {
                if (v == acceptButton) {
                    // 약속 확정
                    if (promise.isTimeFixed() == Promise.UNFIXEDTIME) {
                        AlertMsg.AlertMsg(this, "만날 시간을 정해주세요");
                        activity_show_detail_schedule_date_group.setVisibility(View.VISIBLE);
                        return;
                    }

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

    TextView year;
    NumberPicker month;
    NumberPicker day;
    NumberPicker hour;
    NumberPicker min;
    NumberPicker ampm;

    private void setInitDateAndTimePicker(View view) {
        year = view.findViewById(R.id.custom_date_and_time_datePicker_year);
        month = view.findViewById(R.id.custom_date_and_time_datePicker_month);
        month.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        day = view.findViewById(R.id.custom_date_and_time_datePicker_day);
        day.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hour = view.findViewById(R.id.custom_date_and_time_datePicker_hour);
        hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        min = view.findViewById(R.id.custom_date_and_time_datePicker_min);
        min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        ampm = view.findViewById(R.id.custom_date_and_time_datePicker_ampm);
        ampm.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        Calendar today;


        if (cal_switch == 1) today = start;
        else today = end;

        Calendar cal = Calendar.getInstance();

        month.setMinValue(0);
        month.setMaxValue(11);
        month.setValue(today.get(Calendar.MONTH));

        month.setDisplayedValues(new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"});

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Calendar cal = Calendar.getInstance();

                Log.d(MainAgreementActivity.TAG, "onValueChange: " + oldVal + ", " + newVal);

                cal.set(Integer.parseInt(year.getText()+""), month.getValue()+1,0,0,0,0);

                if (oldVal == 11 && newVal == 0) {
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
                    year.setText(cal.get(Calendar.YEAR) + "");
                } else if(oldVal == 0 && newVal == 11) {
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
                    year.setText(cal.get(Calendar.YEAR) + "");
                }

                day.setMaxValue(cal.get(Calendar.DAY_OF_MONTH));
            }
        });

        day.setMinValue(1);
        cal.set(Calendar.MONTH, today.get(Calendar.MONTH)+1,0);
        day.setMaxValue(cal.get(Calendar.DAY_OF_MONTH));
        day.setValue(today.get(Calendar.DAY_OF_MONTH));

        hour.setMinValue(1);
        hour.setMaxValue(12);
        hour.setValue(today.get(Calendar.HOUR));
        hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(MainAgreementActivity.TAG, "onValueChange: " + oldVal + ", " + newVal + ", ampd = " + ampm.getValue());

                if ((oldVal == 12 && newVal == 1) || (oldVal == 1 && newVal == 12)) {
                    if (ampm.getValue() == 0) ampm.setValue(1);
                    else ampm.setValue(0);
                }

            }
        });

        min.setMinValue(0);
        min.setMaxValue(59);
        min.setValue(0);

        ampm.setMinValue(0);
        ampm.setMaxValue(1);
        ampm.setValue(today.get(Calendar.AM_PM));
        ampm.setDisplayedValues(new String[]{"오전","오후"});
    }
}
