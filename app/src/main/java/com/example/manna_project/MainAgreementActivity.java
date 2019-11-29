package com.example.manna_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation.AcceptInvitation_List;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_LinearLayout;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;
import com.example.manna_project.MainAgreementActivity_Util.Invited.Invited_List;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.MainAgreementActivity_Util.Setting.Setting_List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import org.w3c.dom.Text;

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

    FloatingActionButton main_schedule_add_floating_btn;
    FloatingActionButton main_add_friend_btn;

    MannaUser myInfo;

    ArrayList<Promise> promiseArrayList;
    ArrayList<MannaUser> friendList;
    ArrayList<String> friendUids;
    ArrayList<String> promiseKeyList;

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

        promiseArrayList = new ArrayList<>();
        friendList = new ArrayList<>();

        firebaseCommunicator = new FirebaseCommunicator(this);

        initTabHost();
        custom_calendar.initCalendar();

        firebaseInitializing();
    }

    private void firebaseInitializing() {

        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {
                Log.d(TAG, "afterGetUser: " + mannaUser.toString());
                if(myInfo == null) {
                    myInfo = mannaUser;
                    userName.setText(mannaUser.getName());
                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                    firebaseCommunicator.getFriendList(myInfo.getUid());
                }
                else{
                    friendList.add(mannaUser);
                    friend_list.getFriendListAdapter().notifyDataSetChanged();
                }
            }
            @Override
            public void afterGetPromise(Promise promise){
                promise.initialAttendees();
                promiseArrayList.add(promise);

                getAcceptInvitation_list().setListItem();
                getInvited_list().setListItem();
            }
            @Override
            public void afterGetPromiseKey(ArrayList<String> promiseKeys) {
                promiseKeyList = promiseKeys;
                promiseArrayList.clear();
                getAcceptInvitation_list().setListItem();
                getInvited_list().setListItem();
                int size = promiseKeys.size();
                for(int i =0; i<size;i++){
                    String key = promiseKeys.get(i);
                    firebaseCommunicator.getPromiseByKey(key);
                }
            }
            @Override
            public void afterGetFriendUids(ArrayList<String> friends) {
                 friendUids = friends;
                 int size = friendUids.size();
                 for(int i=0;i<size ; i++) {
                     firebaseCommunicator.getUserById(friendUids.get(i));
                 }
            }

            @Override
            public void afterGetChat(NoticeBoard_Chat chat) {

            }
        });

        // 계정 UID기준으로 데이터 로드
        firebaseCommunicator.getUserById(firebaseCommunicator.getMyUid());
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
                } else if(tabId.equals("tab03_friend")) {
                    // invited and accept reset
                    acceptInvitation_list.getArrayList().clear();
                    invited_list.getArrayList().clear();
                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                }
            }
        });

        // 친구
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_friendlist));
        tabSpec.setContent(R.id.main_friend_layout);
        tabHost.addTab(tabSpec);
        friend_list = new Friend_List(this, (ListView) findViewById(R.id.main_friendList), friendList);
        main_add_friend_btn = findViewById(R.id.main_add_friend_btn);
        main_add_friend_btn.setOnClickListener(this);


        // 약속
        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_schedule));
        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        currentDate.setOnClickListener(this);
        custom_calendar = new Custom_Calendar(this, this, (Custom_LinearLayout) findViewById(R.id.calendarRoot), (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                (ListView) findViewById(R.id.main_agreement_listView), (TextView) findViewById(R.id.calendar_currentDate), Calendar.getInstance());

        // 일정관리
        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_agreement));
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
        acceptInvitation_list = new AcceptInvitation_List(this, (ListView)findViewById(R.id.main_accept_list));
        invited_list = new Invited_List(this, (ListView)findViewById(R.id.main_invited_list));

        main_schedule_add_floating_btn = findViewById(R.id.main_schedule_add_floating_btn);
        main_schedule_add_floating_btn.setOnClickListener(this);

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
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
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

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getBackground().setColorFilter(getResources().getColor(R.color.lightBlue), PorterDuff.Mode.MULTIPLY);
        }

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

            case ShowDetailSchedule_Activity.SHOW_DETAIL_CHEDULE_CODE:
                Log.d(TAG2,resultCode+"");
                if(resultCode == RESULT_OK){
                    Promise fixedPromise;

                    try {
                        fixedPromise = data.getParcelableExtra("FIXED_PROMISE");
                    } catch (Exception e) {
                        fixedPromise = null;
                    }


                    if (fixedPromise != null) {
                        Log.d(TAG, "onActivityResult: " + fixedPromise.getStartTime());
                        Log.d(TAG, "onActivityResult: " + fixedPromise.getEndTime());
                    }


                    acceptInvitation_list.getArrayList().clear();
                    invited_list.getArrayList().clear();
                    Log.d(TAG2,"리절트 들어옴 showdetail");
                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                }
                break;
            case AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: OK");
                    Promise promise = data.getParcelableExtra("made_promise");
                    firebaseCommunicator.upLoadPromise(promise);

                    acceptInvitation_list.getArrayList().clear();
                    invited_list.getArrayList().clear();

                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());

                    Log.d(TAG, "onActivityResult: " + promise.toString());
                } else if(resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "onActivityResult: Cancel");
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

    DataSnapshot foundUserInfo = null;

    @Override
    public void onClick(View v) {
        if (v == customDatePicker || v == currentDate) {
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
            acceptInvitation_list.setListItem();
        } else if(v == invited_Btn) {
            acceptInvitation_list.getListView().setVisibility(View.GONE);
            accept_Btn.setTextColor(getResources().getColor(R.color.white));
            invited_Btn.setTextColor(getResources().getColor(R.color.lightRed));
            invited_list.getListView().setVisibility(View.VISIBLE);
            // 리스트 갱신
            invited_list.setListItem();
        } else if(v == main_schedule_add_floating_btn) {
            Intent intent = new Intent(this, AddScheduleActivity.class);

            Log.d(TAG, "onClick: " + myInfo.toString());

            intent.putParcelableArrayListExtra("FRIENDLIST", friendList);
            intent.putExtra("MYINFO", myInfo);

            startActivityForResult(intent, AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE);
        } else if(v == main_add_friend_btn) {
//            Intent intent = new Intent(this, Friend_Add_Activity.class);
            final AlertDialog dialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.friend_add_layout, null);

            Button cancel = view.findViewById(R.id.add_friend_cancel_btn);
            Button addbtn = view.findViewById(R.id.add_friend_add_btn);
            final Button findbtn = view.findViewById(R.id.add_friend_find_email);
            final EditText emailEdit = view.findViewById(R.id.add_friend_email_txt);
            final TextView add_friend_status_txt = view.findViewById(R.id.add_friend_status_txt);

            builder.setView(view);

            dialog = builder.create();

            addbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!emailEdit.isEnabled() && foundUserInfo != null) {
                        Log.d(TAG, "onClick: " + foundUserInfo.toString());

                        MannaUser mannaUser = new MannaUser(foundUserInfo);

                        Log.d(TAG, "onClick: " + mannaUser.toString());

                        String friendUid = mannaUser.getUid();
                        firebaseCommunicator.addFriend(friendUid);
                        firebaseCommunicator.getUserById(friendUid);

                        dialog.cancel();
                    }
                }
            });

            findbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (findbtn.getText().equals("찾기")) {
                        String res;

                        res = firebaseCommunicator.findFriendByEmail(emailEdit.getText().toString(), new FirebaseCommunicator.SearchCallBackListener() {
                            @Override
                            public void afterFindUser(boolean exist, final DataSnapshot snap) {
                                if (exist) {
                                    emailEdit.setEnabled(false);
                                    findbtn.setText("X");
                                    foundUserInfo = snap;
                                    add_friend_status_txt.setText("계정을 찾았습니다!");
                                    add_friend_status_txt.setTextColor(Color.BLACK);
                                } else {
                                    emailEdit.setEnabled(true);
                                    findbtn.setText("찾기");
                                    foundUserInfo = null;
                                    add_friend_status_txt.setText("계정을 찾지 못했습니다!");
                                    add_friend_status_txt.setTextColor(getResources().getColor(R.color.lightRed));
                                }
                            }
                        });

                        if (!res.equals("success")) {
                            add_friend_status_txt.setTextColor(getResources().getColor(R.color.lightRed));
                            add_friend_status_txt.setText(res);
                        }


                        add_friend_status_txt.setVisibility(View.VISIBLE);
                    } else if(findbtn.getText().equals("X")) {
                        emailEdit.setEnabled(true);
                        findbtn.setText("찾기");
                        add_friend_status_txt.setVisibility(View.GONE);
                    }


                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.show();

//            startActivityForResult(intent, Friend_Add_Activity.ADD_FRIEND_REQUEST_CODE);
        }
    }


    public Friend_List getFriend_list() {
        return friend_list;
    }

    public void setFriend_list(Friend_List friend_list) {
        this.friend_list = friend_list;
    }

    public Setting_List getSetting_list() {
        return setting_list;
    }

    public void setSetting_list(Setting_List setting_list) {
        this.setting_list = setting_list;
    }

    public Invited_List getInvited_list() {
        return invited_list;
    }

    public void setInvited_list(Invited_List invited_list) {
        this.invited_list = invited_list;
    }

    public ArrayList<Promise> getPromiseArrayList() {
        return promiseArrayList;
    }

    public void setPromiseArrayList(ArrayList<Promise> promiseArrayList) {
        this.promiseArrayList = promiseArrayList;
    }

    public FirebaseCommunicator getFirebaseCommunicator() {
        return firebaseCommunicator;
    }

    public void setFirebaseCommunicator(FirebaseCommunicator firebaseCommunicator) {
        this.firebaseCommunicator = firebaseCommunicator;
    }

    public AcceptInvitation_List getAcceptInvitation_list() {
        return acceptInvitation_list;
    }

    public void setAcceptInvitation_list(AcceptInvitation_List acceptInvitation_list) {
        this.acceptInvitation_list = acceptInvitation_list;
    }
    public ArrayList<MannaUser> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<MannaUser> friendList) {
        this.friendList = friendList;
    }

    public MannaUser getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MannaUser myInfo) {
        this.myInfo = myInfo;
    }
}
