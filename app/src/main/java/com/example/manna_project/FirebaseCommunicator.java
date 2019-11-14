package com.example.manna_project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseCommunicator {
    public static final String TAG = "MANNAYC";

    public FirebaseDatabase database;
    public DatabaseReference root;
    public DatabaseReference users;
    public DatabaseReference promise;
    public DatabaseReference myRef;
    public MannaUser myInfo;
    private FirebaseUser user;
    private String myUid;

    public FirebaseCommunicator() {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.myUid = user.getUid();
        this.database = FirebaseDatabase.getInstance();
        this.root = database.getReference();
        this.users = root.child("users");
        this.promise = root.child("promises");
        this.myRef = users.child(myUid);

    }

    public void updateMannaUser(MannaUser myInfo) {
        users.child(myUid).setValue(myInfo);
    }

    public void updateUserInfo(HashMap<String, Object> src) {
        myRef.updateChildren(src);
    }

    public void addInitializeListener() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myInfo = new MannaUser(dataSnapshot);
                Log.d(TAG,myInfo.getName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void updateRoutine(String myUid, ArrayList<MannaUser.Routine> Arr) {
        users.child(myUid).child("Routines").setValue(Arr);
    }

    public MannaUser getMyInfo() {
        return myInfo;
    }

    public void addFriend(String friendUid) {
        String myUid = getMyUid();
        HashMap<String, Object> add = new HashMap<>();
        add.put("true", friendUid);
        users.child(myUid).child("FriendList").updateChildren(add);
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }
}
