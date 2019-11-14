package com.example.manna_project;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCommunicator {
    public String TAG = "MANNAYC";

    public FirebaseDatabase database;
    public DatabaseReference root;
    public DatabaseReference users;
    public DatabaseReference promise;
    public DatabaseReference myRef;
    public MannaUser myInfo;


    public FirebaseCommunicator(){
        this.database = FirebaseDatabase.getInstance();
        root = database.getReference();
        users = root.child("users");
        promise = root.child("promise");
        myRef = users.child(getMyUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myInfo = new MannaUser(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateMannaUser(MannaUser myInfo){
       String user = myInfo.getUid();
       users.child(user).setValue(myInfo);
    }


    public void updateRoutine(String myUid, ArrayList<MannaUser.Routine> Arr){
        users.child(myUid).child("Routines").setValue(Arr);
    }

    public MannaUser getMyInfo(){
        return myInfo;
    }

    public void addFriend(String friendUid){
        String myUid = getMyUid();
        HashMap<String,Object> add = new HashMap<>();
        add.put(friendUid,"true");
        users.child(myUid).child("FriendList").updateChildren(add);
    }

    public static String getMyUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
