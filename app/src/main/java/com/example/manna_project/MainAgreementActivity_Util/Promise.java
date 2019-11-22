package com.example.manna_project.MainAgreementActivity_Util;

import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.example.manna_project.FirebaseCommunicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Promise {

    public static final String TAG = "MANNAYC";
    public static int INVITED =0;
    public static int ACCEPTED=1;
    public static int FIXED=2;

    private String promiseid;
    private String title;
    private String leaderId;
    private MannaUser leader;

    private double latitude;
    private double longitude;

    private Calendar startTime;
    private Calendar endTime;

    DatabaseReference DBRef;

    HashMap<String, Integer> acceptState;
    ArrayList<MannaUser> attendees;


    public Promise() {

    }

    public Promise(String title, String leaderId, MannaUser leader, double latitude, double longitude, Calendar startTime, Calendar endTime){
        this.title = title;
        this.leaderId = leaderId;
        this.leader = leader;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        attendees = new ArrayList<>();
        acceptState = new HashMap<>();
        DBRef = FirebaseDatabase.getInstance().getReference();

    }

    public Promise(DataSnapshot dataSnapshot){

        this.title = dataSnapshot.child("Title").getValue(String.class);
        this.promiseid = dataSnapshot.getKey();
        this.leaderId = dataSnapshot.child("LeaderId").getValue(String.class);
        this.latitude = dataSnapshot.child("Latitude").getValue(Double.class).doubleValue();
        this.longitude = dataSnapshot.child("Longitude").getValue(Double.class).doubleValue();

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        String temp = dataSnapshot.child("StartTime").getValue(String.class);
        String split[] = temp.split("/");
        startTime.set(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]),Integer.parseInt(split[4]));
        temp = dataSnapshot.child("EndTime").getValue(String.class);
        split = temp.split("/");
        endTime.set(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]),Integer.parseInt(split[4]));

        this.acceptState = new HashMap<>();
        DataSnapshot tmp = dataSnapshot.child("AcceptState");
        for(DataSnapshot hashSnapshot : tmp.getChildren() ) {
            this.acceptState.put(hashSnapshot.getKey(),hashSnapshot.getValue(Integer.class));
        }
        DBRef = FirebaseDatabase.getInstance().getReference();
        getLeaderInfo();
    }

    public String getPromiseid() {
        return promiseid;
    }

    public void setPromiseid(String promiseid) {
        this.promiseid = promiseid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public MannaUser getLeader() {
        return leader;
    }

    public void setLeader(MannaUser leader) {
        this.leader = leader;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }
    public HashMap<String, Integer> getAcceptState() {
        return acceptState;
    }

    public void setAcceptState(HashMap<String, Integer> acceptState) {
        this.acceptState = acceptState;
    }

    public ArrayList<MannaUser> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<MannaUser> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(MannaUser user){
        this.attendees.add(user);
        this.acceptState.put(user.getUid(),INVITED);
    }

    public void getLeaderInfo(){
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();
        Ref = Ref.child("users").child(leaderId);
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leader = new MannaUser(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAcceptState(MannaUser user, int state){
        if(this.acceptState.containsKey(user.getUid()))
             this.acceptState.put(user.getUid(),state);
    }
    public void initialAttendees(){     //미완성
        attendees = new ArrayList<>();

        ArrayList<String> attendeesUid = new ArrayList<>();
        HashMap<String,Integer> accept = acceptState;
        Set<String> set = accept.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            attendeesUid.add(iterator.next());
        }

        FirebaseCommunicator firebaseCommunicator = new FirebaseCommunicator();
        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {
                attendees.add(mannaUser);
            }

            @Override
            public void afterGetPromise(Promise promise) {

            }

            @Override
            public void afterGetPromiseKey(ArrayList<String> promiseKeys) {

            }

            @Override
            public void afterGetFriendUids(ArrayList<String> friendList) {

            }
        });
    }

    @Override
    public String toString() {
        return "Promise{" +
                "promiseid='" + promiseid + '\'' +
                ", title='" + title + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", leader=" + leader +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", acceptState=" + acceptState +
                ", attendees=" + attendees +
                '}';
    }

    public HashMap<String,Object> toMap(){      //attendees는 올리지 않음, AcceptState만 올림
        HashMap<String,Object> result = new HashMap<>();
        result.put("PromiseId",this.promiseid);
        result.put("Title",this.title);
        result.put("LeaderId" ,this.leaderId);
        result.put("Latitude",Double.valueOf(this.latitude));
        result.put("Longitude",Double.valueOf(this.longitude));
        result.put("StartTime",startTime.get(Calendar.YEAR)+"/"+startTime.get(Calendar.MONTH)+"/"+startTime.get(Calendar.DATE)+"/"+startTime.get(Calendar.HOUR_OF_DAY)+"/"+startTime.get(Calendar.MINUTE));
        result.put("EndTime",endTime.get(Calendar.YEAR)+"/"+endTime.get(Calendar.MONTH)+"/"+endTime.get(Calendar.DATE)+"/"+endTime.get(Calendar.HOUR_OF_DAY)+"/"+endTime.get(Calendar.MINUTE));
        result.put("AcceptState",acceptState);
        return  result;
    }

    public void setUserInfoFromUID(final BaseAdapter adapter) {
        DatabaseReference userRef = DBRef.child("users/" + getLeaderId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || !dataSnapshot.exists()) return;

                MannaUser user = new MannaUser(dataSnapshot);
                setLeader(user);
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

