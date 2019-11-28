package com.example.manna_project.MainAgreementActivity_Util.Invited;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.manna_project.DBData;
import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;
import com.example.manna_project.ShowDetailSchedule_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Invited_List {
    ListView listView;
    Context context;
    ArrayList<Promise> arrayList;
    InvitedListAdapter invitedListAdapter;
    DatabaseReference DBRef;
    MainAgreementActivity mainAgreementActivity;

    public final static String TAG = "JS";

    public Invited_List(final Context context, ListView listView) {
        this.context = context;
        this.listView = listView;

        mainAgreementActivity = ((MainAgreementActivity)context);
        DBRef = FirebaseDatabase.getInstance().getReference();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ShowDetailSchedule_Activity.class);

                intent.putExtra("Mode", 1);
                intent.putExtra("Promise_Info", invitedListAdapter.getItem(position));

                context.startActivity(intent);
            }
        });


        arrayList = new ArrayList<>();

        setList(arrayList);


        invitedListAdapter = new InvitedListAdapter(this.getArrayList(), this.context, R.layout.activity_invited_list_item);

        setListAdpater();
        
//        loadDataFromFirebase();
    }

    public void setListItem() {

        Log.d(TAG, "Invited_List: " + mainAgreementActivity.getFirebaseCommunicator().getMyUid());


        arrayList.clear();

        for (Promise promise: mainAgreementActivity.getPromiseArrayList()) {
            Log.d(TAG, "setListItem: " + promise.getAcceptState().get(mainAgreementActivity.getFirebaseCommunicator().getMyUid()));
            if (promise.getAcceptState().get(mainAgreementActivity.getFirebaseCommunicator().getMyUid()) == Promise.INVITED) {
                arrayList.add(promise);
            }
        }

        getAcceptInvitationListAdapter().notifyDataSetChanged();
    }

    public void setList(ArrayList<Promise> arrayList) {
        this.arrayList = arrayList;
    }

    public void setListAdpater() {
        listView.setAdapter(invitedListAdapter);
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

    public InvitedListAdapter getAcceptInvitationListAdapter() {
        return invitedListAdapter;
    }

    public void setAcceptInvitationListAdapter(InvitedListAdapter invitedListAdapter) {
        this.invitedListAdapter = invitedListAdapter;
    }
}
