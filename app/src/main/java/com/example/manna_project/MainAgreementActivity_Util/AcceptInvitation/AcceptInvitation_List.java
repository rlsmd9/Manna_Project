package com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation;

import android.content.Context;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AcceptInvitation_List {
    ListView listView;
    Context context;
    ArrayList<AcceptInvitationListItem> arrayList;
    AcceptInvitationListAdapter acceptInvitationListAdapter;

    public AcceptInvitation_List(final Context context, ListView listView) {
        this.context = context;
        this.listView = listView;

        // 친구 데이터 생성
        arrayList = new ArrayList<>();

        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?1", new MannaUser("홍길동1", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?2", new MannaUser("홍길동2", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?3", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?4", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?5", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?6", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?7", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));

        setListItem(arrayList);

        acceptInvitationListAdapter = new AcceptInvitationListAdapter(this.getArrayList(), this.context, R.layout.activity_accept_list_item);

        setList();
    }

    public void setListItem(ArrayList<AcceptInvitationListItem> arrayList) {
        this.arrayList = arrayList;
    }

    public void setList() {
        listView.setAdapter(acceptInvitationListAdapter);
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ArrayList<AcceptInvitationListItem> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<AcceptInvitationListItem> arrayList) {
        this.arrayList = arrayList;
    }

    public AcceptInvitationListAdapter getAcceptInvitationListAdapter() {
        return acceptInvitationListAdapter;
    }

    public void setAcceptInvitationListAdapter(AcceptInvitationListAdapter acceptInvitationListAdapter) {
        this.acceptInvitationListAdapter = acceptInvitationListAdapter;
    }
}
