package com.example.manna_project.MainAgreementActivity_Util.Invited;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.manna_project.DBData;
import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.R;
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

        // 친구 데이터 생성
//        arrayList = new ArrayList<>();
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?1", new MannaUser("홍길동1", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?2", new MannaUser("홍길동2", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?3", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?4", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?5", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?6", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));
//        arrayList.add(new InvitedListItem("null", "어디서 만나요?7", new MannaUser("홍길동3", "null", "null"), "숭실대학교", Calendar.getInstance()));




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

//    private void loadDataFromFirebase() {
//        getArrayList().clear();
//
//        final String uid = "XJJkpZ6ojhQ0ttiF9OgfDEzC00K2";
//
//        DatabaseReference promiseRef = DBRef.child("invited/" + uid);
//
//        promiseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot == null || !dataSnapshot.exists()) return;
//
//                for (DataSnapshot invitedSnap: dataSnapshot.getChildren()) {
//                    DBRef.child("promises/" + invitedSnap.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot == null || !dataSnapshot.exists()) return;
//
//                            Promise promise = new Promise(dataSnapshot);
//
////                            promise.getAcceptState().get(promise.g)
//
//                            promise.setUserInfoFromUID(invitedListAdapter);
//
//                            getArrayList().add(promise);
//                            Log.d(TAG, "onDataChange: " + promise.toString());
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                    Log.d(TAG, "onDataChange: " + invitedSnap.toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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
