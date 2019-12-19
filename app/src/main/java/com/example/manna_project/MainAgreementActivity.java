package com.example.manna_project;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation.AcceptInvitation_List;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_LinearLayout;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.EventVO;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.GoogleCalendarAPI;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;
import com.example.manna_project.MainAgreementActivity_Util.Invited.Invited_List;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.MainAgreementActivity_Util.Schedule;
import com.example.manna_project.MainAgreementActivity_Util.Setting.Setting_List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
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
    FirebaseCommunicator firebaseCommunicator;
    TextView userName;

    FloatingActionButton main_schedule_add_floating_btn;
    FloatingActionButton main_add_friend_btn;

    MannaUser myInfo;

    GoogleCalendarAPI googleCalendarAPI;

    ArrayList<Promise> promiseArrayList;
    ArrayList<MannaUser> friendList;
    ArrayList<String> friendUids;
    ArrayList<String> promiseKeyList;

    static final String TAG = "MANNA_JS";
    static final String TAG2 = "MANNAYC";

    private Event selectedScheduleevent;
    private boolean initial;

    ProgressDialog mProgress;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_agreement);
        initial = true;
        mProgress = new ProgressDialog(this);
        mProgress.setCanceledOnTouchOutside(false);

        mProgress.setMessage("초기화 작업중 입니다.");
        mProgress.show();

        userName = findViewById(R.id.main_agreement_titleBar_userName);
        customDatePicker = findViewById(R.id.main_agreement_changeDateBtn);
        customDatePicker.setOnClickListener(this);

        promiseArrayList = new ArrayList<>();
        friendList = new ArrayList<>();

        googleCalendarAPI = new GoogleCalendarAPI(this);
        firebaseCommunicator = new FirebaseCommunicator(this);

        initTabHost();

        // realm
        Realm.init(MainAgreementActivity.this);
        realm = Realm.getDefaultInstance();

        firebaseInitializing();
        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.CREATE);
    }

    int google_switch = 0;

    private void firebaseInitializing() {

        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {
                Log.d(TAG, "afterGetUser: " + mannaUser.toString());
                if (myInfo == null) {
                    myInfo = mannaUser;
                    if(myInfo.getUid() == null){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        myInfo.setUid(user.getUid());
                        myInfo.seteMail(user.getEmail());
                        myInfo.setName(user.getDisplayName());
                        firebaseCommunicator.updateUserInfo(myInfo.toMap(),myInfo.getUid());
                        startNotificationService();
                    }
                    userName.setText(mannaUser.getName());
                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                    firebaseCommunicator.getFriendList(myInfo.getUid());

                } else {
                    friendList.add(mannaUser);
                    friend_list.getFriendListAdapter().notifyDataSetChanged();
                }
            }


            @Override
            public void afterGetPromise(Promise promise) {
                promise.initialAttendees();
                promiseArrayList.add(promise);

                if (google_switch == 0) {


                    if (promise.getAcceptState().get(myInfo.getUid()) == Promise.FIXED) {
                        google_switch = 1;
                        promise.getAcceptState().put(myInfo.getUid(), Promise.DONE);

                        addGoogleCalendarData(promise, new GoogleCalendarAPI.RequestAddEventGoogleApiListener() {
                            @Override
                            public void onRequestAddEventGoogleApiListener(Promise addPromise, boolean isCanceled) {
                                if (!isCanceled) {
                                    HashMap hash = addPromise.getAcceptState();

                                    hash.put(addPromise.getLeaderId(), Promise.DONE);

                                    firebaseCommunicator.updatePromise(addPromise);
                                }

                                google_switch = 0;
                            }
                        });
                    }
                }

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
                for (int i = 0; i < size; i++) {
                    String key = promiseKeys.get(i);
                    firebaseCommunicator.getPromiseByKey(key);
                }

                mProgress.cancel();
                mProgress.hide();
            }

            @Override
            public void afterGetFriendUids(ArrayList<String> friends) {
                friendUids = friends;
                int size = friendUids.size();
                for (int i = 0; i < size; i++) {
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

    public void startNotificationService(){
        if(NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()){
            Intent intent = new Intent(this,NotificationService.class);
            intent.putExtra("MyInfo",myInfo);
            startService(intent);
        }
    }

    protected void initTabHost() {
        final TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab_agreement")) {
                    downloadGoogleCalendarData();
                } else if (tabId.equals("tab03_friend")) {
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

        ListView agreementListView = findViewById(R.id.main_agreement_listView);

        custom_calendar = new Custom_Calendar(this, this, (Custom_LinearLayout) findViewById(R.id.calendarRoot), (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                agreementListView, (TextView) findViewById(R.id.calendar_currentDate), Calendar.getInstance());

        agreementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected agreementListView // " + custom_calendar.getSchedule_list().getArrayList().get(position).toString());

                String promiseId = custom_calendar.getSchedule_list().getArrayList().get(position).getPromiseId();
                selectedScheduleevent = custom_calendar.getSchedule_list().getArrayList().get(position).getEvent();

                for (Promise promise : promiseArrayList) {
                    if (promise.getPromiseid().equals(promiseId)) {
                        Intent intent = new Intent(getActivity(), ShowDetailSchedule_Activity.class);

                        intent.putExtra("Mode", 3);
                        intent.putExtra("Promise_Info", promise);
                        intent.putExtra("MyInfo", getMyInfo());

                        startActivityForResult(intent, ShowDetailSchedule_Activity.ACCEPTED_PROMISE_CODE);
                        return;
                    }
                }
            }
        });

        // 일정관리
        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_agreement));
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);
        acceptInvitation_list = new AcceptInvitation_List(this, (ListView) findViewById(R.id.main_accept_list));
        invited_list = new Invited_List(this, (ListView) findViewById(R.id.main_invited_list));

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
                    Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                    intent.putExtra("myInfo", myInfo);
                    startActivityForResult(intent, EditProfile.EDITPROFILE_REQUEST_CODE);
                } else if (position == 1) {

                } else if (position == 2) {
                    startActivity(new Intent(getApplicationContext(), SettingPersonalRoutine.class));
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
    protected void onResume() {
        super.onResume();
        if(!initial) {
            acceptInvitation_list.getArrayList().clear();
            invited_list.getArrayList().clear();
            firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
            getAcceptInvitation_list().setListItem();
            getInvited_list().setListItem();
        }
        initial = false;
    }

    public void downloadGoogleCalendarData() {
        googleCalendarAPI.setRequestGetAllEventGoogleApiListener(new GoogleCalendarAPI.RequestGetAllEventGoogleApiListener() {
            @Override
            public void onRequestGetAllEventGoogleApiListener(Events events) {
                // Google Calendar 데이터 로컬 저장
//                Calendar calendar = Calendar.getInstance();
//
//                calendar.setTimeInMillis(events.getItems().get(1).getUpdated().getValue());
//
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                Log.d(TAG, "onRequestGetAllEventGoogleApiListener: size " + events.getItems().size());
                if (events != null) {
                    for (Event e : events.getItems()) {
                        EventVO eventVO = realm.where(EventVO.class).equalTo("mId", e.getId()).findFirst();

                        Log.d(TAG, "onRequestGetAllEventGoogleApiListener: " + e.toString());
                        if (eventVO == null) {
                            Log.d(TAG, "onRequestGetAllEventGoogleApiListener: is null!! i make");

                            realm.beginTransaction();
                            EventVO event = realm.createObject(EventVO.class);
                            event.setEventVO(e);
                            realm.commitTransaction();

                        } else {
                            if (!eventVO.getmUpdated().equals(simpleDateFormat.format(new Date(e.getUpdated().getValue())))) {
                                realm.beginTransaction();
                                eventVO.setEventVO(e);
                                realm.commitTransaction();
                            }

                            Log.d(TAG, "onRequestGetAllEventGoogleApiListener: " + eventVO.toString());
                        }
                    }
                }

                custom_calendar.setSchedule(events);
                custom_calendar.showView();
            }
        });

//        realm.beginTransaction();
//        realm.delete(EventVO.class);
//        realm.commitTransaction();

        Calendar start = (Calendar) custom_calendar.getDate().clone();
        Calendar end = (Calendar) custom_calendar.getDate().clone();

        start.set(Calendar.DAY_OF_MONTH, 1);
        end.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, 1);

        Events savedPromiseData = getSavedPromiseData(start, end);

        custom_calendar.setSchedule(savedPromiseData);
        custom_calendar.showView();

        googleCalendarAPI.setSearchDate(start, end);
        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.GET_ALL_EVENTS_FROM_GOOGLE_CALENDAR);
    }

    public Events getSavedPromiseData(Calendar start, Calendar end) {
        RealmResults<EventVO> realmResults = realm.where(EventVO.class).contains("mStart", start.get(Calendar.YEAR) + "-" + (start.get(Calendar.MONTH)+1)).findAll().sort("mMTStart", Sort.ASCENDING);
        Events events = new Events();

        ArrayList<Event> eventList = new ArrayList<>();

        for (EventVO eventVO : realmResults) {
            eventList.add(eventVO.getEvent());
        }

        events.setItems(eventList);

        return events;
    }

    public void downloadGoogleCalendarData(Calendar start, Calendar end, final String promiseId) {
        googleCalendarAPI.setRequestGetAllEventGoogleApiListener(new GoogleCalendarAPI.RequestGetAllEventGoogleApiListener() {
            @Override
            public void onRequestGetAllEventGoogleApiListener(Events events) {
                ArrayList<Schedule> schedules = new ArrayList<>();
                for (Event event : events.getItems()) {
                    DateTime start = event.getStart().getDateTime();
                    DateTime end = event.getEnd().getDateTime();
                    if (start == null||end ==null){
                      return;
                    }
                    schedules.add(new Schedule(event));
                    Log.d(TAG, "getEvent_: " + String.format("%s (start time : %s), (end time : %s)", event.getSummary(), start, end));
                }
            firebaseCommunicator.updateSchdule(promiseId, myInfo.getUid(), schedules);
            }
        });

        Calendar searchStart = (Calendar) start.clone();
        Calendar searchEnd = (Calendar) end.clone();

        googleCalendarAPI.setSearchDate(searchStart, searchEnd);
        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.GET_ALL_EVENTS_FROM_GOOGLE_CALENDAR);
    }

    public void addGoogleCalendarData(Promise promise, GoogleCalendarAPI.RequestAddEventGoogleApiListener listener) {
        googleCalendarAPI.setRequestAddEventGoogleApiListener(listener);

        Event event = new Event()
                .setSummary(promise.getTitle())
                .setDescription(promise.getPromiseid())
                .setLocation(promise.getLoadAddress());

        SimpleDateFormat simpledateformat;

        simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
        String datetime = simpledateformat.format(promise.getStartTime().getTime());

        DateTime startDateTime = new DateTime(datetime);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul");
        event.setStart(start);

        Log.d(TAG, datetime);

        DateTime endDateTime = new DateTime(promise.getEndTime().getTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        googleCalendarAPI.setAddPromise(promise);
        googleCalendarAPI.setAddEvent(event);
        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case GoogleCalendarAPI.REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    Log.d("MANNA_JS", "앱을 실행시키려면 구글 플레이 서비스가 필요합니다.\"\n" +
                            "                            + \"구글 플레이 서비스를 설치 후 다시 실행하세요.");
                } else {
                    googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.NONE);
                }
                break;


            case GoogleCalendarAPI.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(GoogleCalendarAPI.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        googleCalendarAPI.mCredential.setSelectedAccountName(accountName);
                        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.NONE);
                        googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.CREATE);
                        downloadGoogleCalendarData();
                    }
                }
                break;


            case GoogleCalendarAPI.REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.NONE);
                }
                break;
            case ShowDetailSchedule_Activity.INVITED_PROMISE_CODE:
                if (resultCode == RESULT_OK) {
                    Promise promise;
                    boolean isAccept = data.getBooleanExtra("isAccept", false);
                    if(isAccept) {
                        try {
                            promise = data.getParcelableExtra("Accept Promise");
                        } catch (Exception e) {
                            promise = null;
                        }
                        if(promise.isTimeFixed()==Promise.UNFIXEDTIME) {
                            Calendar start = promise.getStartTime();
                            Calendar end = promise.getEndTime();
                            downloadGoogleCalendarData(start, end,promise.getPromiseid());
                        }
                    }
                }
                break;

            case ShowDetailSchedule_Activity.ACCEPTED_PROMISE_CODE:
                //         Log.d(TAG2,resultCode+"");
                if (resultCode == RESULT_OK) {
                    Promise promise;

                    try {
                        promise = data.getParcelableExtra("FIXED_PROMISE");
                    } catch (Exception e) {
                        promise = null;
                    }

                    if (promise != null) {
                        Log.d(TAG, "onActivityResult: " + promise.getStartTime());
                        Log.d(TAG, "onActivityResult: " + promise.getEndTime());

                        ArrayList<MannaUser> attendees = promise.getAttendees();

                        HashMap hash = promise.getAcceptState();

                        Log.d(TAG, "onActivityResult: " + hash.toString());

                        for (MannaUser user : attendees) {
                            if (!(promise.getAcceptState().get(user.getUid()) == Promise.CANCELED) && !(promise.getAcceptState().get(user.getUid()) == Promise.INVITED))
                                hash.put(user.getUid(), Promise.FIXED);
                            else
                                hash.put(user.getUid(), Promise.CANCELED);
                        }

                        addGoogleCalendarData(promise, new GoogleCalendarAPI.RequestAddEventGoogleApiListener() {
                            @Override
                            public void onRequestAddEventGoogleApiListener(Promise addPromise, boolean isCanceled) {
                                Log.d(TAG, "onRequestAddEventGoogleApiListener: " + addPromise.toString() + ", is cancel = " + isCanceled);
                                if (!isCanceled) {
                                    HashMap hash = addPromise.getAcceptState();

                                    hash.put(addPromise.getLeaderId(), Promise.DONE);

                                    firebaseCommunicator.updatePromise(addPromise);
                                    acceptInvitation_list.getArrayList().clear();
                                    invited_list.getArrayList().clear();
                                    firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                                }
                            }
                        });

                        Log.d(TAG, "onActivityResult: " + hash.toString());
                    }
                } else if (resultCode == ShowDetailSchedule_Activity.RESULT_DELETE) {
                    Log.d(TAG, "onActivityResult: zzfzz");
                    Log.d(TAG, "onActivityResult: " + selectedScheduleevent.getSummary());
                    googleCalendarAPI.setRequestDeleteEventGoogleApiListener(new GoogleCalendarAPI.RequestDeleteEventGoogleApiListener() {
                        @Override
                        public void onRequestDeleteEventGoogleApiListener(boolean isCanceled) {
                            Log.d(TAG, "onRequestDeleteEventGoogleApiListener: " + isCanceled);
                            if (!isCanceled) {
                                downloadGoogleCalendarData();
                            }
                        }
                    });


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            EventVO eventVO = realm.where(EventVO.class).equalTo("mId", selectedScheduleevent.getId()).findFirst();
                            Log.d(TAG, "onRequestDeleteEventGoogleApiListener: " + eventVO.toString());
                            eventVO.deleteFromRealm();
                            downloadGoogleCalendarData();
                        }
                    });

                    googleCalendarAPI.setDeleteEvent(selectedScheduleevent);
                    googleCalendarAPI.getResultsFromApi(GoogleCalendarAPI.APIMode.DELETE);
                    selectedScheduleevent = null;
                }

//                acceptInvitation_list.getArrayList().clear();
//                invited_list.getArrayList().clear();
//                firebaseCommunicator.getAllPromiseKeyById(myInfo.getUid());
                break;
            case AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: OK");
                    Promise promise = data.getParcelableExtra("made_promise");

                    Log.d(TAG, "JSJSJSJSonActivityResult: " + promise.toString());

                    String key = firebaseCommunicator.upLoadPromise(promise);



                    if(promise.isTimeFixed()==Promise.UNFIXEDTIME){
                        downloadGoogleCalendarData(promise.getStartTime(),promise.getEndTime(),key);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "onActivityResult: Cancel");
                }

                break;
            case EditProfile.EDITPROFILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra("isReplaced", false)) {
                        MannaUser user = data.getParcelableExtra("replacedMyInfo");

                        firebaseCommunicator.updateMannaUser(user);
                        myInfo = user;
                    }
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
                        downloadGoogleCalendarData();
                    }

                }
            });

            builder.create().show();
        } else if (v == accept_Btn) {
            acceptInvitation_list.getListView().setVisibility(View.VISIBLE);
            accept_Btn.setTextColor(getResources().getColor(R.color.lightRed));
            invited_Btn.setTextColor(getResources().getColor(R.color.white));
            invited_list.getListView().setVisibility(View.GONE);
            acceptInvitation_list.setListItem();
        } else if (v == invited_Btn) {
            acceptInvitation_list.getListView().setVisibility(View.GONE);
            accept_Btn.setTextColor(getResources().getColor(R.color.white));
            invited_Btn.setTextColor(getResources().getColor(R.color.lightRed));
            invited_list.getListView().setVisibility(View.VISIBLE);
            // 리스트 갱신
            invited_list.setListItem();
        } else if (v == main_schedule_add_floating_btn) {
            Intent intent = new Intent(this, AddScheduleActivity.class);

            Log.d(TAG, "onClick: " + myInfo.toString());

            intent.putParcelableArrayListExtra("FRIENDLIST", friendList);
            intent.putExtra("MYINFO", myInfo);

            startActivityForResult(intent, AddScheduleActivity.ADD_SCHEDULE_REQUEST_CODE);
        } else if (v == main_add_friend_btn) {
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
                    } else if (findbtn.getText().equals("X")) {
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

    public GoogleCalendarAPI getGoogleCalendarAPI() {
        return googleCalendarAPI;
    }

    public void setGoogleCalendarAPI(GoogleCalendarAPI googleCalendarAPI) {
        this.googleCalendarAPI = googleCalendarAPI;
    }

    public Custom_Calendar getCustom_calendar() {
        return custom_calendar;
    }

    public void setCustom_calendar(Custom_Calendar custom_calendar) {
        this.custom_calendar = custom_calendar;
    }

    public MainAgreementActivity getActivity() {
        return this;
    }
}
