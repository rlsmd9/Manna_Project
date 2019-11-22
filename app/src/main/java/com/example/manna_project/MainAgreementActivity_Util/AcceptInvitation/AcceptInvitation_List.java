package com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AcceptInvitation_List {
    ListView listView;
    Context context;
    ArrayList<Promise> arrayList;
    AcceptInvitationListAdapter acceptInvitationListAdapter;
    public final static String TAG = "JS";
    MainAgreementActivity mainAgreementActivity;

    public AcceptInvitation_List(final Context context, ListView listView) {
        this.context = context;
        this.listView = listView;

        // 친구 데이터 생성
        arrayList = new ArrayList<>();
        mainAgreementActivity = ((MainAgreementActivity)context);
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?1", new MannaUser("홍길동1", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나!?2", new MannaUser("홍길동2", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?3", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?4", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?5", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?6", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new AcceptInvitationListItem("null", "어디서 만나요?7", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));

        setArrayList(arrayList);

        acceptInvitationListAdapter = new AcceptInvitationListAdapter(this.getArrayList(), this.context, R.layout.activity_accept_list_item);

        setListAdapter();
    }

    public void setListItem() {
        Log.d(TAG, "Invited_List: " + mainAgreementActivity.getFirebaseCommunicator().getMyUid());


        arrayList.clear();

        for (Promise promise: mainAgreementActivity.getPromiseArrayList()) {
            Log.d(TAG, "setListItem: " + promise.getAcceptState().get(mainAgreementActivity.getFirebaseCommunicator().getMyUid()));
            if (promise.getAcceptState().get(mainAgreementActivity.getFirebaseCommunicator().getMyUid()) == Promise.ACCEPTED) {
                arrayList.add(promise);
            }
        }

        getAcceptInvitationListAdapter().notifyDataSetChanged();
    }

    public void setListAdapter() {
        listView.setAdapter(acceptInvitationListAdapter);
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public ArrayList<Promise> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Promise> arrayList) {
        this.arrayList = arrayList;
    }

    public AcceptInvitationListAdapter getAcceptInvitationListAdapter() {
        return acceptInvitationListAdapter;
    }

    public void setAcceptInvitationListAdapter(AcceptInvitationListAdapter acceptInvitationListAdapter) {
        this.acceptInvitationListAdapter = acceptInvitationListAdapter;
    }
}
