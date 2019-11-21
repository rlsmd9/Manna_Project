package com.example.manna_project.MainAgreementActivity_Util.Setting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.manna_project.Login_activity;
import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.R;
import com.example.manna_project.SettingPersonalRoutine;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Setting_List {
    ListView listView;
    Context context;
    ArrayList<SettingListItem> arrayList;
    SettingListAdapter friendListAdapter;

    public Setting_List(final Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        int img[] = {R.drawable.setting_privacy, R.drawable.setting_alarm, R.drawable.setting_schedule, R.drawable.setting_notice, R.drawable.exit};
        final String settingList[] = {"개인정보 설정", "알림 설정", "일정 관리", "공지사항", "로그아웃"};

        // 친구 데이터 생성
        arrayList = new ArrayList<>();
        for (int i = 0; i < img.length; i++) {
            arrayList.add(new SettingListItem(img[i], settingList[i]));
        }

        setListItem(arrayList);

        friendListAdapter = new SettingListAdapter(this.getArrayList(), this.context, R.layout.activity_setting_list_item);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {
                    context.startActivity(new Intent(context, SettingPersonalRoutine.class));
                    // 일정관리
                } else if (position == 3) {
//                    FirebaseComunicator temp  = new FirebaseComunicator();
//                    temp.updateMannaUser();

                } else if (position == 4) {
                    Toast.makeText(context, "Sign Out", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(context, Login_activity.class);

                    context.startActivity(intent);
                    ((MainAgreementActivity)context).finish();
                }
            }
        });

        setList();
    }

    public void setListItem(ArrayList<SettingListItem> arrayList) {
        this.arrayList = arrayList;
    }

    public void setList() {
        listView.setAdapter(friendListAdapter);
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ArrayList<SettingListItem> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<SettingListItem> arrayList) {
        this.arrayList = arrayList;
    }

    public SettingListAdapter getFriendListAdapter() {
        return friendListAdapter;
    }

    public void setFriendListAdapter(SettingListAdapter friendListAdapter) {
        this.friendListAdapter = friendListAdapter;
    }
}
