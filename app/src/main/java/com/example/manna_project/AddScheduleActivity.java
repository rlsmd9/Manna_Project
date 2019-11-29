package com.example.manna_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity_Util.Custom_user_icon_view;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.naver.maps.geometry.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    EditText activity_add_schedule_title;
    TextView activity_add_schedule_place;
    EditText activity_add_schedule_place_detail;
    ImageView activity_add_schedule_searchPlace_img;

    TextView activity_add_schedule_date_start;
    TextView activity_add_schedule_date_end;
    CheckBox activity_add_schedule_after_chk;

    LinearLayout attendees_group;
    boolean[] selectedUsers;
    Button activity_add_schedule_add_attendee_btn;

    Button activity_add_schedule_back_btn;
    Button activity_add_schedule_btn;

    Calendar start;
    Calendar end;
    int cal_switch;
    LatLng latLng;

    SimpleDateFormat simpleDateFormat;
    ArrayList<MannaUser> friendList;
    ArrayList<MannaUser> attendees;
    MannaUser myInfo;


    public final static int ADD_SCHEDULE_REQUEST_CODE = 9910; // 스케줄 추가 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Intent intent = getIntent();

        friendList = intent.getParcelableArrayListExtra("FRIENDLIST");
        myInfo = intent.getParcelableExtra("MYINFO");
        Log.d(MainAgreementActivity.TAG, "setAttendeeList: " + myInfo.toString());
        attendees = new ArrayList<>();
        attendees.add(myInfo);

        selectedUsers = new boolean[friendList.size()];

        setReference();
        setEvent();
        setAttendeeList();
    }

    private void setEvent() {
        activity_add_schedule_place.setOnClickListener(this);
        activity_add_schedule_searchPlace_img.setOnClickListener(this);

        activity_add_schedule_date_start.setOnClickListener(this);
        activity_add_schedule_date_end.setOnClickListener(this);

        activity_add_schedule_after_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activity_add_schedule_date_start.setText("나중에 정하기");
                    activity_add_schedule_date_end.setText("나중에 정하기");
                    activity_add_schedule_date_start.setEnabled(false);
                    activity_add_schedule_date_end.setEnabled(false);
                } else {
                    activity_add_schedule_date_start.setText("");
                    activity_add_schedule_date_end.setText("");
                    activity_add_schedule_date_start.setEnabled(true);
                    activity_add_schedule_date_end.setEnabled(true);
                }
            }
        });

        activity_add_schedule_add_attendee_btn.setOnClickListener(this);
        activity_add_schedule_back_btn.setOnClickListener(this);
        activity_add_schedule_btn.setOnClickListener(this);
    }

    private void setReference() {
        activity_add_schedule_title = findViewById(R.id.activity_add_schedule_title);

        activity_add_schedule_place = findViewById(R.id.activity_add_schedule_place);
        activity_add_schedule_place_detail = findViewById(R.id.activity_add_schedule_place_detail);
        activity_add_schedule_searchPlace_img = findViewById(R.id.activity_add_schedule_searchPlace_img);

        activity_add_schedule_date_start = findViewById(R.id.activity_add_schedule_date_start);
        activity_add_schedule_date_end = findViewById(R.id.activity_add_schedule_date_end);
        activity_add_schedule_after_chk = findViewById(R.id.activity_add_schedule_after_chk);

        attendees_group = findViewById(R.id.activity_show_add_schedule_attendees_group);
        activity_add_schedule_add_attendee_btn = findViewById(R.id.activity_add_schedule_add_attendee_btn);

        activity_add_schedule_back_btn = findViewById(R.id.activity_add_schedule_back_btn);
        activity_add_schedule_btn = findViewById(R.id.activity_add_schedule_btn);
    }

    private void setAttendeeList() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        attendees_group.removeAllViews();

        for (MannaUser user: attendees) {
            Custom_user_icon_view view = (Custom_user_icon_view) inflater.inflate(R.layout.user_name_icon_layout, null);

            view.setTextView((TextView) view.findViewById(R.id.user_name_icon));
            view.setUser(user, false);

            Log.d(MainAgreementActivity.TAG, "setAttendeeList: " + user.toString());

            attendees_group.addView(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SearchPlaceActivity.SEARCHPLACE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    activity_add_schedule_place.setText(data.getStringExtra("SelectedAddress"));
                    latLng = data.getParcelableExtra("Location");
                }
                break;
            case Friend_select_list_dialog_activity.FRIEND_SELECT_LIST_DIALOG_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    attendees.clear();
                    attendees.add(myInfo);

                    selectedUsers = data.getBooleanArrayExtra("SELECTED_USERS");

                    for (int i = 0; i < selectedUsers.length; i++) {
//                        Log.d("JS", "onActivityResult: " + user.toString());
                        if (selectedUsers[i])
                            attendees.add(friendList.get(i));
                    }

                    setAttendeeList();

                }
                break;
        }


    }

    @Override
    public void onClick(View v) {
        if (v == activity_add_schedule_place || v == activity_add_schedule_searchPlace_img) {
            startActivityForResult(new Intent(this, SearchPlaceActivity.class), SearchPlaceActivity.SEARCHPLACE_ACTIVITY_REQUEST_CODE);
        } else if(v == activity_add_schedule_date_start || v == activity_add_schedule_date_end) {
            if (v == activity_add_schedule_date_start) cal_switch = 1;
            if (v == activity_add_schedule_date_end) cal_switch = 2;

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

                        activity_add_schedule_date_start.setText(simpleDateFormat.format(new Date(start.getTimeInMillis())));

                        if (activity_add_schedule_date_end.getText().toString().isEmpty()) {
                            end.set(start.get(Calendar.YEAR),start.get(Calendar.MONTH),start.get(Calendar.DAY_OF_MONTH),
                                    hour.getValue() + (ampm.getValue() == 1?12:0)+1,start.get(Calendar.MINUTE),0);
                            activity_add_schedule_date_end.setText(simpleDateFormat.format(new Date(end.getTimeInMillis())));
                        }
                    }
                    else if(cal_switch == 2) {
                        end.set(Integer.parseInt(year.getText()+""), month.getValue(), day.getValue(),
                                hour.getValue() + (ampm.getValue() == 1?12:0),min.getValue(),0);

                        activity_add_schedule_date_end.setText(simpleDateFormat.format(new Date(end.getTimeInMillis())));
                    }
                }
            });

            builder.create().show();
        } else if(v == activity_add_schedule_add_attendee_btn) {
            Intent intent = new Intent(this, Friend_select_list_dialog_activity.class);

            intent.putParcelableArrayListExtra("FRIENDLIST", friendList);
            intent.putExtra("selectedUsers", this.selectedUsers);

            startActivityForResult(intent, Friend_select_list_dialog_activity.FRIEND_SELECT_LIST_DIALOG_ACTIVITY_REQUEST_CODE);
        } else if(v == activity_add_schedule_back_btn) {
            setResult(RESULT_CANCELED);
            finish();
        } else if(v == activity_add_schedule_btn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("확인", null);

            if (activity_add_schedule_title.getText().toString().isEmpty()) {
                builder.setMessage("제목을 입력해 주세요.");
                builder.create().show();
                activity_add_schedule_title.setFocusable(true);
                return;
            } else if(activity_add_schedule_place.getText().toString().isEmpty()) {
                builder.setMessage("장소를 선택해 주세요.");
                builder.create().show();
                activity_add_schedule_place.setFocusable(true);
                return;
            } else if(activity_add_schedule_place_detail.getText().toString().isEmpty()) {
                builder.setMessage("상세 장소를 입력해 주세요.");
                builder.create().show();
                activity_add_schedule_place_detail.setFocusable(true);
                return;
            } else if(activity_add_schedule_after_chk.isChecked() == false) {
                if (activity_add_schedule_date_start.getText().toString().isEmpty()) {
                    builder.setMessage("시작 시간을 입력해 주세요.");
                    builder.create().show();
                    activity_add_schedule_date_start.setFocusable(true);
                    return;
                } else if (activity_add_schedule_date_end.getText().toString().isEmpty()) {
                    builder.setMessage("종료 시간을 입력해 주세요.");
                    builder.create().show();
                    activity_add_schedule_date_end.setFocusable(true);
                    return;
                } else if((end.getTimeInMillis() - start.getTimeInMillis()) <= 0) {
                    builder.setMessage("종료 시간이 시작 시간보다 빠릅니다.");
                    builder.create().show();
                    activity_add_schedule_date_end.setFocusable(true);
                    return;
                }
            }

            Log.d("JS", "onClick: " + (end.getTimeInMillis() - start.getTimeInMillis()));

            String title = activity_add_schedule_title.getText().toString();
            String loadAddress = activity_add_schedule_place.getText().toString() + ", " + activity_add_schedule_place_detail.getText().toString();
            MannaUser leader = myInfo;
            HashMap<String, Integer> acceptState = new HashMap<>();

            for (MannaUser user: attendees) {
                if (user.getUid().equals(myInfo.getUid()))
                    acceptState.put(user.getUid(), Promise.ACCEPTED);
                else acceptState.put(user.getUid(), Promise.INVITED);
            }

            Promise promise = new Promise(title, leader.getUid(), leader, loadAddress, latLng.latitude, latLng.longitude, (activity_add_schedule_after_chk.isChecked()?null:start), (activity_add_schedule_after_chk.isChecked()?null:end));
            promise.setAcceptState(acceptState);
            promise.setAttendees(attendees);

            Log.d("JS", "onClick: " + promise.toString());

            Intent intent = getIntent();

            intent.putExtra("made_promise", promise);

            setResult(RESULT_OK, intent);
            finish();
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

                cal.set(Integer.parseInt(year.getText()+""), month.getValue()+1,0);

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
