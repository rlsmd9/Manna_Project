package com.example.manna_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
import com.example.manna_project.MainAgreementActivity_Util.Promise;

import java.util.ArrayList;

public class NotificationService extends Service {

    public static final int INVITE_NOTI_ID = 1011;
    MannaUser myInfo;
    FirebaseCommunicator firebaseCommunicator;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    Notification notification;
    ArrayList<String> invitedPromiseKeys;
    ArrayList<Promise> promises;
    int keyNum = 0;


    public NotificationService() {
    }

    @Override
    public void onCreate() {
        firebaseCommunicator = new FirebaseCommunicator(getApplicationContext());
        invitedPromiseKeys = new ArrayList<>();
        promises = new ArrayList<>();
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myInfo = intent.getParcelableExtra("MyInfo");
        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {

            }

            @Override
            public void afterGetPromise(Promise promise) {
               if(promise.getAcceptState().get(myInfo.getUid())==Promise.INVITED){
                   notifying(promise);
               }
            }

            @Override
            public void afterGetPromiseKey(ArrayList<String> promiseKeys) {

            }

            @Override
            public void afterGetFriendUids(ArrayList<String> friendList) {

            }

            @Override
            public void afterGetChat(NoticeBoard_Chat chat) {

            }
        });
        firebaseCommunicator.addNotifyingListner(new FirebaseCommunicator.NotifyingListener() {
            @Override
            public void afterNotifyIntvited(ArrayList<String> keyList) {
                keyNum = keyList.size();
                for(String key : keyList){
                    firebaseCommunicator.getPromiseByKey(key);
                }
            }
        });
        firebaseCommunicator.setNotifyingIfInvited(myInfo.getUid());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        try {
//            firebaseCommunicator.removeNotifyingListener(myInfo.getUid());
//        } catch (Exception e){};
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(NotificationService.this, MainAgreementActivity.class);
        intent.putExtra("Started By Notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(getApplicationContext(), "MANNA")
                .setContentTitle("초대된 약속이 있습니다.")
                .setContentText("구매요청이 있습니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.setting_alarm);
            NotificationChannel channel = new NotificationChannel("MANNA", "약속 초대 알림", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("약속 초대 알림");
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
    }

    public void notifying(Promise promise) {

        String title = promise.getTitle();
        if (title.length() > 20) {
            title = title.substring(0, 20);
        }
        builder.setContentText(title + "...의 약속 초대가 있습니다.");
        notification = builder.build();
        notificationManager.notify(INVITE_NOTI_ID, notification);


    }
}
