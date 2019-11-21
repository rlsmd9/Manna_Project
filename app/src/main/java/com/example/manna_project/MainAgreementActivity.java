package com.example.manna_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation.AcceptInvitation_List;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_LinearLayout;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;
import com.example.manna_project.MainAgreementActivity_Util.Invited.Invited_List;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Setting.Setting_List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;


public class MainAgreementActivity extends Activity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    TextView currentDate;
    ImageView customDatePicker;
    Custom_Calendar custom_calendar;
    Friend_List friend_list;
    Setting_List setting_list;
    AcceptInvitation_List acceptInvitation_list;
    Invited_List invited_list;
    TextView invited_Btn;
    TextView accept_Btn;
    ProgressDialog progressDialog;
    FirebaseCommunicator firebaseCommunicator;
    TextView userName;
    MannaUser myInfo;

    static final String TAG = "MANNA_JS";
    static final String TAG2 = "MANNAYC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);
        progressDialog = new ProgressDialog(this);

        userName = findViewById(R.id.main_agreement_titleBar_userName);
        customDatePicker = findViewById(R.id.main_agreement_changeDateBtn);
        customDatePicker.setOnClickListener(this);

        initTabHost();
        custom_calendar.initCalendar();
        firebaseCommunicator = new FirebaseCommunicator();
        firebaseInitializing();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void firebaseInitializing() {
        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetData(MannaUser mannaUser) {
                myInfo = mannaUser;
                userName.setText(mannaUser.getName());
                firebaseCommunicator.addCallBackListener(null);
            }
        });
        firebaseCommunicator.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    protected void initTabHost() {
        final TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab_agreement")) {
                    custom_calendar.mID = 3;
                    custom_calendar.getResultsFromApi();
                }
            }
        });

        // 친구
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_friendlist));
        tabSpec.setContent(R.id.main_friend_layout);
        tabHost.addTab(tabSpec);
        friend_list = new Friend_List(this, (ListView) findViewById(R.id.main_friendList));

        // 약속
        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_schedule));
        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        custom_calendar = new Custom_Calendar(this, this, (Custom_LinearLayout) findViewById(R.id.calendarRoot), (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                (ListView) findViewById(R.id.main_agreement_listView), (TextView) findViewById(R.id.calendar_currentDate), Calendar.getInstance());

        // 일정관리
        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_agreement));
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
        acceptInvitation_list = new AcceptInvitation_List(this, (ListView)findViewById(R.id.main_accept_list));
        invited_list = new Invited_List(this, (ListView)findViewById(R.id.main_invited_list));

        invited_Btn = findViewById(R.id.main_invited_btn);
        accept_Btn = findViewById(R.id.main_acceptInvitation_btn);

        invited_Btn.setOnClickListener(this);
        accept_Btn.setOnClickListener(this);

        // 설정
        tabSpec = tabHost.newTabSpec("tab04_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_setting));
        tabSpec.setContent(R.id.main_settingList);
        tabHost.addTab(tabSpec);
        setting_list = new Setting_List(this, (ListView) findViewById(R.id.main_settingList));
        setting_list.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {
                    startActivity(new Intent(getApplicationContext(),SettingPersonalRoutine.class));
                    // 일정관리
                } else if (position == 3) {


                } else if (position == 4) {
                    Toast.makeText(getApplicationContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(getApplicationContext(), Login_activity.class);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Custom_Calendar.REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    Log.d("MANNA_JS", "앱을 실행시키려면 구글 플레이 서비스가 필요합니다.\"\n" +
                            "                            + \"구글 플레이 서비스를 설치 후 다시 실행하세요.");
                } else {

                    custom_calendar.getResultsFromApi();
                }
                break;


            case Custom_Calendar.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(custom_calendar.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        custom_calendar.mCredential.setSelectedAccountName(accountName);
                        custom_calendar.getResultsFromApi();
                    }
                }
                break;


            case Custom_Calendar.REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    custom_calendar.getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onClick(View v) {
        if (v == customDatePicker) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_datepicker, null);
            builder.setView(view);

            final DatePicker datePicker = view.findViewById(R.id.custom_datepicker);
            datePicker.init(custom_calendar.getDate().get(Calendar.YEAR), custom_calendar.getDate().get(Calendar.MONTH),
                    custom_calendar.getDate().get(Calendar.DAY_OF_MONTH), null);
//            datePicker.setMaxDate(System.currentTimeMillis() - 1000);

            builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "onClick: " + datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth());

                    Calendar calendar = (Calendar) custom_calendar.getDate().clone();

                    Log.d(TAG, "onClick: " + calendar.toString());
                    custom_calendar.setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    if (calendar.get(Calendar.YEAR) != datePicker.getYear() || calendar.get(Calendar.MONTH) != datePicker.getMonth()) {
                        Log.d(TAG, "onClick: @@mkmckm");
                        custom_calendar.mID = 3;
                        custom_calendar.initCalendarUI();
                        custom_calendar.getResultsFromApi();
                        custom_calendar.showView();
                    }


                }
            });

            builder.create().show();
        } else if(v == accept_Btn) {
            acceptInvitation_list.getListView().setVisibility(View.VISIBLE);
            accept_Btn.setTextColor(getResources().getColor(R.color.lightRed));
            invited_Btn.setTextColor(getResources().getColor(R.color.white));
            invited_list.getListView().setVisibility(View.GONE);
        } else if(v == invited_Btn) {
            acceptInvitation_list.getListView().setVisibility(View.GONE);
            accept_Btn.setTextColor(getResources().getColor(R.color.white));
            invited_Btn.setTextColor(getResources().getColor(R.color.lightRed));
            invited_list.getListView().setVisibility(View.VISIBLE);
        }
    }



}
