package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.Calendar.Schedule.Schedule_List;
import com.example.manna_project.R;
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
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Custom_Calendar implements View.OnClickListener {
//    final static String TAG = "manna_js";
    final static String TAG = "manna_JS";

    enum CalendarType {
        FULL_CALENDAR, HALF_CALENDAR, WEEK_CALENDAR;
    }

    enum TouchType {
        DOWN, UP;
    }

    Context context;
    LayoutInflater layout;
    Custom_LinearLayout calendar_root;
    GridLayout calendar_layout;
    ListView listView;
    TextView viewDate;
    // 일정 데이터 받아서 저장할 자료구조
    ArrayList<ScheduleOfDay> scheduleOfDays;
    Activity mainAgreementActivity;
    Schedule_List schedule_list;

    public Custom_Calendar(MainAgreementActivity mainAgreementActivity, Context context, Custom_LinearLayout calendar_root, GridLayout calendar_layout, ListView listView, TextView viewDate, Calendar date) {
        this.context = context;
        this.layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listView = listView;
        this.calendar_layout = calendar_layout;
        calendar_root.setListView(this.listView);
        this.calendar_root = calendar_root;
        this.calendar_root.setCalendar_root(this.calendar_root);
        this.calendar_root.date = Calendar.getInstance();
        this.calendar_root.date.set(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE),0,0,0);
        this.scheduleOfDays = new ArrayList<>();
        this.viewDate = viewDate;
        this.mainAgreementActivity = mainAgreementActivity;
        this.calendar_root.calendarType = CalendarType.FULL_CALENDAR;
        this.schedule_list = new Schedule_List(this.context, listView);
        // 일자별 Linear 레이아웃 생성
        makeCalendar();
        // 생성한 일자 자료구조 전달
        calendar_root.setScheduleOfDays(this.scheduleOfDays);

        setCalendar();
        selectDay();
    }

    public void showView() {
        int index;
        ScheduleOfDay sod = null;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);

                if (calendar_root.calendarType == CalendarType.FULL_CALENDAR) scheduleOfDay.setScheduleLining(false);
                else scheduleOfDay.setScheduleLining(true);
                scheduleOfDay.setLayout();
            }
        }



        sod = findDateLayout(getDate());
        if (sod != null)
            this.schedule_list.setListItem(sod.getEventsOfDay());
    }

    public void setSchedule(Events events) {
        int start;
        int index;

        Calendar c = Calendar.getInstance();

        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);

        initCalendarUI();

        for (Event event: events.getItems()) {
            DateTime startEventTime = event.getStart().getDateTime();

            if (startEventTime == null)
                startEventTime = event.getStart().getDate();
            Calendar eventDay = Calendar.getInstance();
            eventDay.setTime(new Date(startEventTime.getValue()));
            index = eventDay.get(Calendar.DAY_OF_MONTH) + start - 2;
            ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
            scheduleOfDay.addEvent(event);
        }
    }

    public void initCalendarUI() {
        int index;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);
                scheduleOfDay.getEventsOfDay().clear();
            }
        }
    }

    public void setDate(Calendar date) {
        if (date.get(Calendar.YEAR) != calendar_root.date.get(Calendar.YEAR) || date.get(Calendar.MONTH) != calendar_root.date.get(Calendar.MONTH)) {
            this.calendar_root.date = date;
            setCalendar();
            mID = 3;
            getResultsFromApi();
        } else {
            this.calendar_root.date = date;
        }
    }

    public void setDate(int day) {
        this.calendar_root.date.set(Calendar.DATE, day);
    }

    // month index range is 0 to 11
    public void setDate(int year, int month) {
        int day = 1;

        if (month == Calendar.getInstance().get(Calendar.MONTH)) day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (year != calendar_root.date.get(Calendar.YEAR) || month != calendar_root.date.get(Calendar.MONTH)) {
            this.calendar_root.date.set(year, month, day);
            setCalendar();

            mID = 3;
            getResultsFromApi();
        } else {
            this.calendar_root.date.set(year, month, day);
            setCalendar();

        }
        selectDay();
    }

    // month index range is 0 to 11
    public void setDate(int year, int month, int day) {
        this.getDate().set(year, month, day);

        setCalendar();
        selectDay();
    }

    public void makeCalendar() {
        // activity_main_agreement의 약속탭에 달력 만들기
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                LinearLayout ly = (LinearLayout) layout.inflate(R.layout.activity_main_agreement_calendar_item, null);
                GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                        GridLayout.UNDEFINED,GridLayout.FILL, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL,1f));
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(context);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTypeface(null,Typeface.BOLD);

                textView.setText("init");

                ly.setOnClickListener(this);
                ly.addView(textView,linearParams);
                calendar_layout.addView(ly,param);

                scheduleOfDays.add(new ScheduleOfDay(context,null, textView, ly));
            }
        }
    }

    protected void setCleanBackground() {
        for (int i = 0; i < scheduleOfDays.size(); i++) {
            scheduleOfDays.get(i).getDayList().setBackground(calendar_layout.getBackground());
        }
    }



    @Override
    public void onClick(View v) {
        ScheduleOfDay selected_scheduleOfDay = findDateLayout(v);
        this.setDate(selected_scheduleOfDay.getDate());

        // 선택된 일의 일정을 리스트 뷰에 츄가
        if (selected_scheduleOfDay.getEventsOfDay().size() > 0) {
            Log.d(TAG, "onClick: listClick");
            this.schedule_list.setListItem(selected_scheduleOfDay.getEventsOfDay());
        }

        selectDay();
        Log.d(TAG, this.getDate().get(Calendar.DAY_OF_MONTH)+"");
    }

    // 현재 저장된 Date를 선택
    protected void selectDay() {
        setCleanBackground();
        ScheduleOfDay current_scheduleOfDay = findDateLayout(this.getDate());

        current_scheduleOfDay.getDayList().setBackground(context.getResources().getDrawable(R.color.selectedItem));

        calendar_root.highlight_week();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월");
        Log.d(TAG, simpleDateFormat.format(this.getDate().getTime())+"");
        viewDate.setText(simpleDateFormat.format(this.getDate().getTime()));
    }

    protected void setCalendar() {
        int cnt = 1;
        int start, end;
        int index;
        Calendar c = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), 1,0,0,0);
        start = c.get(Calendar.DAY_OF_WEEK);
        c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH)+1, 0);
        end = c.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                index = j+(7*i);
                ScheduleOfDay scheduleOfDay = scheduleOfDays.get(index);

                scheduleOfDay.getTextDay().setEnabled(true);
                scheduleOfDay.getDayList().setClickable(true);
                scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.textGrayColor));
                scheduleOfDay.getTextDay().setBackground(calendar_layout.getBackground());
                // j+(7*i)+1 = for문 순서 1~42

                c.set(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH), (cnt++)-start+1);


                if (index < start-1 || index > (end+start-2)) {

                    scheduleOfDay.getTextDay().setEnabled(false);
//                    scheduleOfDay.getDayList().setClickable(false);
                    scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.baseBackgroundColorLightGray));
                } else {
                    if (index % 7 == 0) {
                        scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.lightRed));
                    } else if (index % 7 == 6) {
                        scheduleOfDay.getTextDay().setTextColor(context.getResources().getColor(R.color.lightBlue));
                    }


                }

                if (compareDate(c, today)) {
                    scheduleOfDay.getTextDay().setBackground(context.getResources().getDrawable(R.drawable.round_today_shape));

                }
//                Log.d("manna_js", "makeCalendar: " + date.toString());

                scheduleOfDay.setDate((Calendar) c.clone());
                scheduleOfDay.getTextDay().setText(c.get(Calendar.DAY_OF_MONTH) + "");
            }
        }
    }

    public Calendar getDate() {
        return this.calendar_root.date;
    }

    public void increaseMonth(int range) {
        setDate(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH) + range);
    }

    public void decreaseMonth(int range) {
        setDate(this.getDate().get(Calendar.YEAR), this.getDate().get(Calendar.MONTH) - range);
    }

    protected ScheduleOfDay findDateLayout(Calendar date) {
        for (int i = 0; i < scheduleOfDays.size(); i++) {


            if (compareDate(scheduleOfDays.get(i).getDate(), date)) {

                return scheduleOfDays.get(i);

            }
        }

        Log.d(TAG,"null");
        return null;
    }

    protected ScheduleOfDay findDateLayout(View view) {
        for (int i = 0; i < scheduleOfDays.size(); i++) {
            if (scheduleOfDays.get(i).getDayList() == view) {

                return scheduleOfDays.get(i);
            }
        }

        return null;
    }

    protected boolean compareDate(Calendar a, Calendar b) {
        if (a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.DATE) == b.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    /**
     * Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체
     */
    public com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    public int mID = 0;


    public GoogleAccountCredential mCredential;
    ProgressDialog mProgress;


    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = {CalendarScopes.CALENDAR};

    static final String CALENDAR_ID = "MANNA";

    private String getCalendarID(String calendarTitle){

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                mainAgreementActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
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
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해
     * 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }



    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                this.mainAgreementActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    public void initCalendar() {
        mProgress = new ProgressDialog(context);

        mCredential = GoogleAccountCredential.usingOAuth2(
                context,
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

        // 계정 초기화
        mID = 1;
        getResultsFromApi();
    }

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
    public String getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우
            chooseAccount();

        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우
            Log.d(TAG, "No network connection available.");
        } else {
            // Google Calendar API 호출
//            Log.d(TAG, "getResultsFromApi: ds");
            Log.d(TAG, "getResultsFromApi: mid = " + mID);
            new MakeRequestTask(mainAgreementActivity, mCredential).execute();
            return "SUCCESS";
        }
        return null;
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
        if (EasyPermissions.hasPermissions(context, Manifest.permission.GET_ACCOUNTS)) {


            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = mainAgreementActivity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {


                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                mainAgreementActivity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }



            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {


            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    this.mainAgreementActivity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private Activity mActivity;
        List<String> eventStrings = new ArrayList<String>();


        public MakeRequestTask(Activity activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("MANNA_PROJECT")
                    .build();
        }


        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            if (mID == 1){
                mProgress.setMessage("Google Calendar 생성 확인중 입니다.");
            } else if(mID == 3) {
                mProgress.setMessage("Google Calendar 일정을 읽어오는중 입니다.");
            }
            mProgress.show();
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

        private String getEvent() throws IOException {
            Calendar calendar = Calendar.getInstance();
            calendar.set(getDate().get(Calendar.YEAR), getDate().get(Calendar.MONTH),
                    1, 0,0,0);
            DateTime startTime = new DateTime(calendar.getTime());
            calendar.set(getDate().get(Calendar.YEAR),getDate().get(Calendar.MONTH)+1,
                    0, 0,0,0);
            DateTime endTime = new DateTime(calendar.getTime());

            String calendarID = getCalendarID(CALENDAR_ID);
            if ( calendarID == null ){
                return "캘린더를 먼저 생성하세요.";
            }

            Log.d(TAG, "getEvent: dwdw");

            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(100)
                    //.setTimeMin(now)
                    .setTimeMin(startTime)
                    .setTimeMax(endTime)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            Log.d(TAG, "getEvent: dwdw2");
            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();
                if (start == null)
                    start = event.getStart().getDate();
                if (end == null)
                    end = event.getEnd().getDate();

//                event.getAttendees();
                Log.d(TAG, "getEvent: " + String.format("%s (start time : %s), (end time : %s)", event.getSummary(), start, end));
                eventStrings.add(String.format("%s (start time : %s), (end time : %s)", event.getSummary(), start, end));
            }

            setSchedule(events);

            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /*
         * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
         */
        private String createCalendar() throws IOException {

            String ids = getCalendarID(CALENDAR_ID);

            if ( ids != null ){
                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary(CALENDAR_ID);


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
            mProgress.hide();
            Log.d(TAG, "onPostExecute: " + output);

            if ( mID == 3 ) showView();
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    mainAgreementActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.d(TAG, "onCancelled: MakeRequestTask The following error occurred: " + mLastError.getMessage());
                }
            } else {
                Toast.makeText(context, "Google Calendar를 읽지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: 요청취소됨");
            }
        }


        private String addEvent() {
            String calendarID = getCalendarID(CALENDAR_ID);

            if ( calendarID == null ){
                return "캘린더 생성 오류";
            }

            Event event = new Event()
                    .setSummary("구글 캘린더 테스트")
                    .setLocation("서울시")
                    .setDescription("캘린더에 이벤트 추가하는 것을 테스트합니다.");


            java.util.Calendar calander;
            calander = getDate();
            SimpleDateFormat simpledateformat;

            simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
            String datetime = simpledateformat.format(calander.getTime());

            DateTime startDateTime = new DateTime(datetime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            Log.d( TAG, datetime );


            DateTime endDateTime = new  DateTime(datetime);
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
