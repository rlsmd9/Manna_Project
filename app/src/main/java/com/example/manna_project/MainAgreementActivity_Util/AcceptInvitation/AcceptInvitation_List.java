package com.example.manna_project.MainAgreementActivity_Util.AcceptInvitation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;
import com.example.manna_project.ShowDetailSchedule_Activity;

import java.util.ArrayList;
import java.util.Calendar;

public class AcceptInvitation_List {
    ListView listView;
    Context context;
    ArrayList<Promise> arrayList;
    AcceptInvitationListAdapter acceptInvitationListAdapter;
    public final static String TAG = "JS";
    MainAgreementActivity mainAgreementActivity;

    public AcceptInvitation_List(Context ctx, ListView listView) {
        this.context = ctx;
        this.listView = listView;

        // 친구 데이터 생성
        arrayList = new ArrayList<>();
        mainAgreementActivity = ((MainAgreementActivity)context);

        Log.d(TAG, "AcceptInvitation_List: " + (listView==null));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ShowDetailSchedule_Activity.class);

                intent.putExtra("Mode", 2);
                intent.putExtra("Promise_Info", acceptInvitationListAdapter.getItem(position));
                intent.putExtra("MyInfo", mainAgreementActivity.getMyInfo());
                context.startActivity(intent);
            }
        });

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
