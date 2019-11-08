package com.example.manna_project;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_Calendar;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Custom_LinearLayout;
import com.example.manna_project.MainAgreementActivity_Util.Friend.Friend_List;
import com.example.manna_project.MainAgreementActivity_Util.Setting.Setting_List;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;

import com.google.api.services.calendar.model.*;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainAgreementActivity extends Activity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    TextView currentDate;
    ImageView customDatePicker;
    Custom_Calendar custom_calendar;
    Friend_List friend_list;
    Setting_List setting_list;
    ProgressDialog progressDialog;
    private int mID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Google Calendar 연동 중...");

        setContentView(R.layout.activity_main_agreement);
        customDatePicker = findViewById(R.id.main_agreement_changeDateBtn);

        customDatePicker.setOnClickListener(this);

        TabHost tabHost = findViewById(R.id.host);
        tabHost.setup();

        // 친구
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab01_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_friendlist));
        tabSpec.setContent(R.id.main_friendList);
        tabHost.addTab(tabSpec);
        friend_list = new Friend_List(this, (ListView) findViewById(R.id.main_friendList));


        // 약속
        tabSpec = tabHost.newTabSpec("tab_agreement");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_schedule));
        tabSpec.setContent(R.id.agreementCalendar);
        tabHost.addTab(tabSpec);
        currentDate = findViewById(R.id.calendar_currentDate);
        custom_calendar = new Custom_Calendar(this, (Custom_LinearLayout) findViewById(R.id.calendarRoot), (GridLayout) findViewById(R.id.main_agreement_calendarGridLayout),
                (ListView) findViewById(R.id.main_agreement_listView), (TextView) findViewById(R.id.calendar_currentDate), Calendar.getInstance());

        // 일정관리
        tabSpec = tabHost.newTabSpec("tab03_friend");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tabhost_agreement));
        tabSpec.setContent(R.id.tab04_friend);
        tabHost.addTab(tabSpec);

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

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff());
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
                    Log.d("manna_js", "onClick: " + datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth());
                    custom_calendar.setDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                }
            });

            builder.create().show();
        }
    }

    private com.google.api.services.calendar.Calendar mService = null;
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     *
     * 사전 조건
     *     - Google Play Services 설치
     *     - 유효한 구글 계정 선택
     *     - 안드로이드 디바이스에서 인터넷 사용 가능
     *
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */
    private String getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우
            Log.d("MANNAJS", "getResultsFromApi: 1");
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우
            Log.d("MANNAJS", "getResultsFromApi: 2");
            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우
            Log.d("MANNAJS", "getResultsFromApi: No network connection available.");
        } else {
            // Google Calendar API 호출
            Log.d("MANNAJS", "getResultsFromApi: 3");
            new MakeRequestTask(this, mCredential).execute();
        }
        return null;
    }



    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }



    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해
     * 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }



    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                MainAgreementActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {


            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {


                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }



            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {


            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    (Activity)this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }



    /*
     * 구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아올때 호출된다.
     */

    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    Toast.makeText(this, " 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                            + "구글 플레이 서비스를 설치 후 다시 실행하세요." , Toast.LENGTH_SHORT);
                } else {

                    getResultsFromApi();
                }
                break;


            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;


            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    /*
     * Android 6.0 (API 23) 이상에서 런타임 권한 요청시 결과를 리턴받음
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  //requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 승인한 경우 호출된다.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 거부한 경우 호출된다.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * 캘린더 이름에 대응하는 캘린더 ID를 리턴
     */
    private String getCalendarID(String calendarTitle){

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();


            for (CalendarListEntry calendarListEntry : items) {

                if ( calendarListEntry.getSummary().toString().equals(calendarTitle)) {

                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }


    /*
     * 비동기적으로 Google Calendar API 호출
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private MainAgreementActivity mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(MainAgreementActivity activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }


        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            progressDialog.show();
            Log.d("MANNAJS", "onPreExecute: 데이터 가져오는 중...");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */
        @Override
        protected String doInBackground(Void... params) {
            try {

                if ( mID == 1) {

                    return createCalendar();

                }else if (mID == 2) {

                    return addEvent();
                }
                else if (mID == 3) {

                    return getEvent();
                }


            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }


        /*
         * CalendarTitle 이름의 캘린더에서 10개의 이벤트를 가져와 리턴
         */
        private String getEvent() throws IOException {


            DateTime now = new DateTime(System.currentTimeMillis());

            String calendarID = getCalendarID("CalendarTitle");
            if ( calendarID == null ){

                return "캘린더를 먼저 생성하세요.";
            }


            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(10)
                    //.setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                if (start == null) {

                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                }


                eventStrings.add(String.format("%s \n (%s)", event.getSummary(), start));
            }


            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /*
         * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
         */
        private String createCalendar() throws IOException {

            String ids = getCalendarID("CalendarTitle");

            if ( ids != null ){

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary("CalendarTitle");


            // 캘린더의 시간대 설정
            calendar.setTimeZone("Asia/Seoul");

            // 구글 캘린더에 새로 만든 캘린더를 추가
            com.google.api.services.calendar.model.Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // 추가한 캘린더의 ID를 가져옴.
            String calendarId = createdCalendar.getId();


            // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // 캘린더의 배경색을 파란색으로 표시  RGB
            calendarListEntry.setBackgroundColor("#0000ff");

            // 변경한 내용을 구글 캘린더에 반영
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // 새로 추가한 캘린더의 ID를 리턴
            return "캘린더가 생성되었습니다.";
        }


        @Override
        protected void onPostExecute(String output) {

            progressDialog.hide();
            Log.d("MANNAJS", "onPostExecute: " + output);

            if ( mID == 3 )  Log.d("MANNAJS",TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            progressDialog.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainAgreementActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("MANNAJS", "onCancelled: MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                Log.d("MANNAJS", "onCancelled: 요청 취소됨");
            }
        }


        private String addEvent() {


            String calendarID = getCalendarID("CalendarTitle");

            if ( calendarID == null ){

                return "캘린더를 먼저 생성하세요.";

            }

            Event event = new Event()
                    .setSummary("구글 캘린더 테스트")
                    .setLocation("서울시")
                    .setDescription("캘린더에 이벤트 추가하는 것을 테스트합니다.");


            java.util.Calendar calander;

            calander = java.util.Calendar.getInstance();
            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
            String datetime = simpledateformat.format(calander.getTime());

            DateTime startDateTime = new DateTime(datetime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            Log.d( "@@@", datetime );


            DateTime endDateTime = new DateTime(datetime);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setEnd(end);

            //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
            //event.setRecurrence(Arrays.asList(recurrence));


            try {
                event = mService.events().insert(calendarID, event).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception : " + e.toString());
            }
            System.out.printf("Event created: %s\n", event.getHtmlLink());
            Log.e("Event", "created : " + event.getHtmlLink());
            String eventStrings = "created : " + event.getHtmlLink();
            return eventStrings;
        }
    }

}
