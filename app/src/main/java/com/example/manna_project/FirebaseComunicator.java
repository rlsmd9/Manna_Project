package com.example.manna_project;

import android.content.Context;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseComunicator {
    public String TAG = "MANNAYC";

    public FirebaseDatabase database;
    public DatabaseReference root;
    public DatabaseReference users;
    public DatabaseReference promise;

    public FirebaseComunicator(){
        this.database = FirebaseDatabase.getInstance();
        root = database.getReference();
        users = root.child("users");
        promise = root.child("promise");
    }
    public void updateMannaUser(){
       MannaUser me = new MannaUser();
       String user = me.getUid();
       users.child(user).setValue(me);
    }
    public void updateRoutine(String myUid, ArrayList<MannaUser.Routine> Arr){
        int i=1;
        for(MannaUser.Routine routine : Arr){
            users.child(myUid).child("Routine").child("Routine"+i++).setValue(routine);
        }
    }
    /*public MannaUser getMyInfo(){
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }*/
    public static String getMyUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
