package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.manna_project.MainAgreementActivity;
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
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleCalendarAPI {
    public com.google.api.services.calendar.Calendar mService = null;

    public int mID = 0;

    public GoogleAccountCredential mCredential;
    RequestGoogleApiListener requestGoogleApiListener;

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public static final String TAG = "JS";
    static final String CALENDAR_ID = "MANNA";

    ProgressDialog mProgress;
    Calendar searchStart, searchEnd;
    Events result;

    public enum APIMode {
        NONE, CREATE, ADD, GET;
    }

    APIMode apiMode = APIMode.NONE;

    Activity activity;

    public GoogleCalendarAPI(Activity activity) {
        this.activity = activity;
        mProgress = new ProgressDialog(activity);
        mProgress.setCanceledOnTouchOutside(false);
        initApi();
    }

    public void initApi() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                activity,
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

        // 계정 초기화
        getResultsFromApi(APIMode.CREATE);
    }

    public void setSearchDate(Calendar searchStart, Calendar searchEnd) {
        this.searchStart = searchStart;
        this.searchEnd = searchEnd;
    }

    private String getCalendarID(String calendarTitle){

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                 activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
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

        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해
     * 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);

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
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
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
    public String getResultsFromApi(APIMode mode) {

        this.apiMode = mode;

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우
            chooseAccount();

        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우
            Log.d(TAG, "No network connection available.");
        } else {
//             Google Calendar API 호출
//            Log.d(TAG, "getResultsFromApi: ds");

            if (mode == APIMode.GET) {
                result = null;
                mProgress.setMessage("Calendar 데이터를 읽어오는중 입니다.");
                mProgress.show();
            }

            Log.d(TAG, "getResultsFromApi: mod = " + mode.name());
            new MakeRequestTask(activity, mCredential).execute();
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
        if (EasyPermissions.hasPermissions(activity, Manifest.permission.GET_ACCOUNTS)) {


            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(APIMode.NONE);
            } else {


                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                activity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }



            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {


            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    public interface RequestGoogleApiListener {
        void onRequestEventsListener(Events events);
    }

    public void setRequestGoogleApiListener(RequestGoogleApiListener requestGoogleApiListener) {
        this.requestGoogleApiListener = requestGoogleApiListener;
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
            Log.d(TAG, "onPreExecute: run API MODE = " + apiMode.name());
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d(TAG, "doInBackground: " + apiMode.name());

                if ( apiMode == APIMode.CREATE) {

                    return createCalendar();

                }else if (apiMode == APIMode.ADD) {
//                    return addEvent();
                } else if (apiMode == APIMode.GET) {
                    Log.d(TAG, "doInBackground: dwdwdwdwdwdads,n,");
                    Calendar start = Calendar.getInstance();
                    start.set(Calendar.DAY_OF_MONTH, 1);
                    Calendar end = Calendar.getInstance();
                    end.set(Calendar.DAY_OF_MONTH, 28);

                    return getEvent();
                }


            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        private String getEvent() throws IOException {
            Calendar calendar = Calendar.getInstance();
            calendar.set(searchStart.get(Calendar.YEAR), searchStart.get(Calendar.MONTH),
                    1, 0,0,0);
            DateTime startTime = new DateTime(calendar.getTime());
            calendar.set(searchEnd.get(Calendar.YEAR),searchEnd.get(Calendar.MONTH)+1,
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

            result = events;

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
            Log.d(TAG, "onPostExecute: " + output);

            if (apiMode == APIMode.GET) {
                if (requestGoogleApiListener != null)
                    requestGoogleApiListener.onRequestEventsListener(result);
                mProgress.cancel();
                mProgress.hide();
            }
        }


        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    mActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Log.d(TAG, "onCancelled: MakeRequestTask The following error occurred: " + mLastError.getMessage());
                }
            } else {
                Toast.makeText(mActivity, "Google Calendar를 읽지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: 요청취소됨");
            }
        }


        private String addEvent(Calendar date) {
            String calendarID = getCalendarID(CALENDAR_ID);

            if ( calendarID == null ){
                return "캘린더 생성 오류";
            }

            Event event = new Event()
                    .setSummary("구글 캘린더 테스트")
                    .setLocation("서울시")
                    .setDescription("캘린더에 이벤트 추가하는 것을 테스트합니다.");


            java.util.Calendar calander;
            calander = date;
            SimpleDateFormat simpledateformat;

            simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'nestedclass'HH:mm:ss+09:00", Locale.KOREA);
            // 추가
            String datetime = simpledateformat.format(calander.getTime());

            DateTime startDateTime = new DateTime(datetime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            Log.d(TAG, datetime );


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
