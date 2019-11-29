package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.manna_project.FirebaseCommunicator;
import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.MainAgreementActivity_Util.NoticeBoard.NoticeBoard_Chat;
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

public class Promise implements Parcelable {

    public static final String TAG = "MANNAYC";
    public static int INVITED = 0;
    public static int ACCEPTED = 1;
    public static int FIXED = 2;
    public static int CANCELED = 3;
    public static int DONE = 4;
    public static int FiXEDTIME = 1;
    public static int UNFIXEDTIME = -1;

    private String promiseid; //
    private String title; //
    private String leaderId; //
    private MannaUser leader; //

    private String loadAddress;
    private double latitude; //
    private double longitude; //

    private Calendar startTime;
    private Calendar endTime;
    private int isTimeFixed;

    DatabaseReference DBRef;

    HashMap<String, Integer> acceptState;
    ArrayList<MannaUser> attendees;


    protected Promise(Parcel in) {
        promiseid = in.readString();
        title = in.readString();
        leaderId = in.readString();
        loadAddress = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        leader = in.readParcelable(MannaUser.class.getClassLoader());
        startTime = (Calendar) in.readSerializable();
        endTime = (Calendar) in.readSerializable();
        isTimeFixed = in.readInt();
        acceptState = in.readHashMap(HashMap.class.getClassLoader());
        attendees = in.readArrayList(MannaUser.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(promiseid);
        dest.writeString(title);
        dest.writeString(leaderId);
        dest.writeString(loadAddress);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(leader, flags);
        dest.writeSerializable(startTime);
        dest.writeSerializable(endTime);
        dest.writeInt(isTimeFixed);
        dest.writeMap(acceptState);
        dest.writeList(attendees);
    }

    public Promise() {

    }

    public Promise(String title, String leaderId, MannaUser leader, String loadAddress, double latitude, double longitude, Calendar startTime, Calendar endTime) {
        this.title = title;
        this.leaderId = leaderId;
        this.leader = leader;
        this.loadAddress = loadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        attendees = new ArrayList<>();
        acceptState = new HashMap<>();
    }

    public Promise(DataSnapshot dataSnapshot, Context context) {

        this.title = dataSnapshot.child("Title").getValue(String.class);
        this.promiseid = dataSnapshot.getKey();
        this.leaderId = dataSnapshot.child("LeaderId").getValue(String.class);
        this.loadAddress = dataSnapshot.child("loadAddress").getValue(String.class);
        this.latitude = dataSnapshot.child("Latitude").getValue(Double.class).doubleValue();
        this.longitude = dataSnapshot.child("Longitude").getValue(Double.class).doubleValue();

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        if (dataSnapshot.child("StartTime").getValue() != null) {
            String temp = dataSnapshot.child("StartTime").getValue(String.class);
            String split[] = temp.split("/");
            startTime.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            temp = dataSnapshot.child("EndTime").getValue(String.class);
            split = temp.split("/");
            endTime.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
        } else {
            startTime = null;
            endTime = null;
        }
        this.isTimeFixed = dataSnapshot.child("isTimeFixed").getValue(Integer.class);

        this.acceptState = new HashMap<>();
        DataSnapshot tmp = dataSnapshot.child("AcceptState");
        for (DataSnapshot hashSnapshot : tmp.getChildren()) {
            this.acceptState.put(hashSnapshot.getKey(), hashSnapshot.getValue(Integer.class));
        }
        DBRef = FirebaseDatabase.getInstance().getReference();
        getLeaderInfo(context);
    }


    public static final Creator<Promise> CREATOR = new Creator<Promise>() {
        @Override
        public Promise createFromParcel(Parcel in) {
            return new Promise(in);
        }

        @Override
        public Promise[] newArray(int size) {
            return new Promise[size];
        }
    };

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

    public String getLoadAddress() {
        return loadAddress;
    }

    public void setLoadAddress(String loadAddress) {
        this.loadAddress = loadAddress;
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

    public int isTimeFixed() {
        return isTimeFixed;
    }

    public void setTimeFixed(int timeFixed) {
        this.isTimeFixed = timeFixed;
    }

    public void addAttendee(MannaUser user) {
        this.attendees.add(user);
        this.acceptState.put(user.getUid(), INVITED);
    }

    public void getLeaderInfo(final Context context) {
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();
        Ref = Ref.child("users").child(leaderId);
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leader = new MannaUser(dataSnapshot);
//                Log.d(TAG, "onDataChange: " + leader.toString());
                MainAgreementActivity mainAgreementActivity = (MainAgreementActivity) context;

                if (mainAgreementActivity != null) {
                    mainAgreementActivity.getInvited_list().getAcceptInvitationListAdapter().notifyDataSetChanged();
                    mainAgreementActivity.getAcceptInvitation_list().getAcceptInvitationListAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAcceptState(MannaUser user, int state) {
        if (this.acceptState.containsKey(user.getUid()))
            this.acceptState.put(user.getUid(), state);
        if (promiseid != null)
            DBRef.child("promises").child(promiseid).child("AcceptState").child(user.getUid()).setValue(state);
    }

    public void initialAttendees() {

        attendees = new ArrayList<>();
        ArrayList<String> attendeesUid = new ArrayList<>();
        HashMap<String, Integer> accept = acceptState;
        Set<String> set = accept.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            attendeesUid.add(iterator.next());
        }
        FirebaseCommunicator firebaseCommunicator = new FirebaseCommunicator(null);
        firebaseCommunicator.addCallBackListener(new FirebaseCommunicator.CallBackListener() {
            @Override
            public void afterGetUser(MannaUser mannaUser) {
                attendees.add(mannaUser);
                Log.d(TAG, mannaUser.toString());
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

            @Override
            public void afterGetChat(NoticeBoard_Chat chat) {

            }
        });
        for (String uid : attendeesUid) {
            firebaseCommunicator.getUserById(uid);
        }
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
                ", loadAddress" + loadAddress +
//                ", startTime=" + startTime.get(Calendar.YEAR) + "-" + startTime.get(Calendar.MONTH) + "-" + startTime.get(Calendar.DAY_OF_MONTH) +
//                ", endTime=" + endTime.get(Calendar.YEAR) + "-" + endTime.get(Calendar.MONTH) + "-" + endTime.get(Calendar.DAY_OF_MONTH) +
                ", acceptState=" + acceptState +
                ", attendees=" + attendees +
                '}';
    }

    public HashMap<String, Object> toMap() {      //attendees는 올리지 않음, AcceptState만 올림
        HashMap<String, Object> result = new HashMap<>();
        result.put("PromiseId", this.promiseid);
        result.put("Title", this.title);
        result.put("LeaderId", this.leaderId);
        result.put("loadAddress", this.loadAddress);
        result.put("Latitude", Double.valueOf(this.latitude));
        result.put("Longitude", Double.valueOf(this.longitude));
        if (startTime != null)
            result.put("StartTime", startTime.get(Calendar.YEAR) + "/" + startTime.get(Calendar.MONTH) + "/" + startTime.get(Calendar.DATE) + "/" + startTime.get(Calendar.HOUR_OF_DAY) + "/" + startTime.get(Calendar.MINUTE));
        if (endTime != null)
            result.put("EndTime", endTime.get(Calendar.YEAR) + "/" + endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.DATE) + "/" + endTime.get(Calendar.HOUR_OF_DAY) + "/" + endTime.get(Calendar.MINUTE));
        result.put("AcceptState", acceptState);
        result.put("isTimeFixed", this.isTimeFixed);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }


}

