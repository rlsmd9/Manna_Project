package com.example.manna_project;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.example.manna_project.MainAgreementActivity_Util.Promise;
import com.example.manna_project.MainAgreementActivity_Util.Routine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class FirebaseCommunicator {
    public static final String TAG = "MANNAYC";

    public Context context;

    public FirebaseDatabase database;
    public DatabaseReference root;
    public DatabaseReference users;
    public DatabaseReference promises;
    public DatabaseReference friendList;
    public DatabaseReference invitedPromises;
    public DatabaseReference myRef;
    public DatabaseReference comments;
    private FirebaseUser user;
    private String myUid;
    private CallBackListener callBackListener;


    public FirebaseCommunicator(Context context) {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.myUid = user.getUid();
//        this.myUid = "XJJkpZ6ojhQ0ttiF9OgfDEzC00K2";
        this.database = FirebaseDatabase.getInstance();
        this.root = database.getReference();
        this.users = root.child("users");
        this.promises = root.child("promises");
        this.friendList = root.child("friendlist");
        this.invitedPromises = root.child("invited");
        this.comments = root.child("comments");
        this.myRef = users.child(myUid);
        this.context = context;
    }


    //-------------------------------------MannaUser에 관한 부분------------------------------------

    public void updateMannaUser(MannaUser myInfo) {
        users.child(myUid).setValue(myInfo);
    }

    public void updateUserInfo(Map<String, Object> src) {
        myRef.updateChildren(src);
    }

    public void updateUserInfo(Map<String, Object> src, String Uid) {
        users.child(Uid).updateChildren(src);
    }

    public void getUserById(String Uid) {
        users.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (callBackListener != null) {
                    callBackListener.afterGetUser(new MannaUser(dataSnapshot));
                } else {
                    Log.d(TAG, "콜백 리스너가 안 달렸음 in getUserById");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserById Error");
            }
        });
    }

    public void updateRoutine(String myUid, ArrayList<Routine> Arr) {
        users.child(myUid).child("Routines").setValue(Arr);
    }


    //-----------------------------------Promise에 관한 부분-----------------------------------------

    public void getAllPromiseKeyById(String Uid) {
        Log.d(TAG, "getAllPromiseKeyById: " + Uid);
        invitedPromises.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (callBackListener != null) {
                    ArrayList<String> promises = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        promises.add(postSnapshot.getValue(String.class));
                    }
                    callBackListener.afterGetPromiseKey(promises);
                } else {
                    Log.d(TAG, "콜백 리스너가 안 달렸음 in getAllPromisekeyById");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getPromiseByKey(String key) {
        promises.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                if (callBackListener != null) {
                    Promise promise = new Promise(dataSnapshot, context);
                    callBackListener.afterGetPromise(promise);

                } else {
                    Log.d(TAG, "콜백 리스너가 안 달렸음 in getPromiseByKey");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getUserById Error");
            }
        });
    }

    public void upLoadPromise(Promise promise){
        DatabaseReference Ref = promises.push();
        String key = Ref.getKey();
        promise.setPromiseid(key);
        Ref.setValue(promise.toMap());

        //초대된 사용자에 초대된 약속에 고유 키를 추가하는 코드
        ArrayList<String> invitedUser = new ArrayList<>();
        HashMap<String,Integer> accept = promise.getAcceptState();
        Set<String> set = accept.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            invitedUser.add(iterator.next());
        }
        int size = invitedUser.size();
        for(int i=0;i<size;i++){
            invitedPromises.child(invitedUser.get(i)).child(key).setValue(key);
        }
    }

    //-------------------------------------------친구에 관한 부분-------------------------------------

    public void getFriendList(String myUid){
        friendList.child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(callBackListener!=null) {
                    ArrayList<String> temp = new ArrayList<>();
                    for (DataSnapshot UidSnapshot : dataSnapshot.getChildren()) {
                        temp.add(UidSnapshot.getValue(String.class));
                    }
                    callBackListener.afterGetFriendUids(temp);
                }else {
                    Log.d(TAG, "콜백 리스너가 안 달렸음 in getFriendList");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addFriend(String friendUid) {
        friendList.child(myUid).child(friendUid).setValue(friendUid);
        friendList.child(friendUid).child(myUid).setValue(myUid);
    }

    public void findFriendByEmail(String email, final SearchCallBackListener listener){

        if (listener == null) return;

        MainAgreementActivity mainAgreementActivity = (MainAgreementActivity)context;
        ArrayList<MannaUser> frineds = mainAgreementActivity.getFriendList();
        for(MannaUser friend : frineds){
            if(email.equals(friend.geteMail())){
                Toast.makeText(context,"이미 친구인 유저입니다.",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (email.equals(mainAgreementActivity.myInfo.geteMail())) {
            Toast.makeText(context,"자기 자신은 추가 할 수 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }

        users.orderByChild("E-mail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildren().iterator().next());
                listener.afterFindUser(dataSnapshot.getValue() != null, dataSnapshot.getChildren().iterator().next());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //----------------------------------------게시판에 관한 기능------------------------------------

    public void addComment(String promiseId,MannaUser user, String comment){

    }
    public void deleteComment(String promiseId,MannaUser user, String comment){

    }


    //----------------------------------------getter setter-----------------------------------------

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


    //------------------------------------콜백메소드------------------------------------------------
    public interface CallBackListener {
        void afterGetUser(MannaUser mannaUser);

        void afterGetPromise(Promise promise);

        void afterGetPromiseKey(ArrayList<String> promiseKeys);

        void afterGetFriendUids(ArrayList<String> friendList);
    }

    public interface SearchCallBackListener {
        void afterFindUser(boolean exist, DataSnapshot userSnap);
    }

    public void addCallBackListener(CallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }
}
