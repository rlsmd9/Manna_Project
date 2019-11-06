package com.example.manna_project.MainAgreementActivity_Util.Setting;

import android.content.Context;
import android.widget.ListView;

import com.example.manna_project.R;

import java.util.ArrayList;

public class Setting_List {
    ListView listView;
    Context context;
    ArrayList<SettingListItem> arrayList;
    SettingListAdapter friendListAdapter;

    public Setting_List(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;
        int img[] = {0, 0, 0, 0};
        String settingList[] = {"개인정보 설정", "알림 설정", "일정 관리", "공지사항"};

        // 친구 데이터 생성
        arrayList = new ArrayList<>();
        for (int i = 0; i < img.length; i++) {
            arrayList.add(new SettingListItem(img[i], settingList[i]));
        }
        setListItem(arrayList);

        friendListAdapter = new SettingListAdapter(this.getArrayList(), this.context, R.layout.activity_setting_list_item);

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
