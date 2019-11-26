package com.example.manna_project.MainAgreementActivity_Util.Friend;

import android.content.Context;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.R;

import java.util.ArrayList;

public class Friend_List {
    ListView listView;
    Context context;
    ArrayList<MannaUser> arrayList;
    FriendListAdapter friendListAdapter;

    public Friend_List(Context context, ListView listView, ArrayList<MannaUser> users) {
        this.context = context;
        this.listView = listView;

        setListItem(users);

        friendListAdapter = new FriendListAdapter(this.getArrayList(), this.context, R.layout.activity_friend_list_item);

        setList();
    }

    public void setListItem(ArrayList<MannaUser> arrayList) {
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

    public ArrayList<MannaUser> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<MannaUser> arrayList) {
        this.arrayList = arrayList;
    }

    public FriendListAdapter getFriendListAdapter() {
        return friendListAdapter;
    }

    public void setFriendListAdapter(FriendListAdapter friendListAdapter) {
        this.friendListAdapter = friendListAdapter;
    }
}
