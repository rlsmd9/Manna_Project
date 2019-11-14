package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.content.Context;
import android.widget.ListView;

import com.example.manna_project.R;

import java.util.ArrayList;

public class Friend_List {
    ListView listView;
    Context context;
    ArrayList<FriendListItem> arrayList;
    FriendListAdapter friendListAdapter;

    public Friend_List(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;

        // 친구 데이터 생성
        arrayList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            arrayList.add(new FriendListItem(0, "홍길동" + i));
        }
        setListItem(arrayList);

        friendListAdapter = new FriendListAdapter(this.getArrayList(), this.context, R.layout.activity_friend_list_item);

        setList();
    }

    public void setListItem(ArrayList<FriendListItem> arrayList) {
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

    public ArrayList<FriendListItem> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<FriendListItem> arrayList) {
        this.arrayList = arrayList;
    }

    public FriendListAdapter getFriendListAdapter() {
        return friendListAdapter;
    }

    public void setFriendListAdapter(FriendListAdapter friendListAdapter) {
        this.friendListAdapter = friendListAdapter;
    }
}
