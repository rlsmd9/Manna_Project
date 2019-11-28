package com.example.manna_project.MainAgreementActivity_Util.NoticeBoard;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class NoticeBoard_Chat {
    MannaUser user;
    String content;
    String date;
    String chatId;
    String UserUid;

    public NoticeBoard_Chat() {
    }

    public NoticeBoard_Chat(MannaUser user, String content, String date) {
        this.user = user;
        this.content = content;
        this.date = date;
}
    public NoticeBoard_Chat(String userUid, String content, String date) {
        this.UserUid = userUid;
        this.content = content;
        this.date = date;
    }
    public NoticeBoard_Chat(DataSnapshot dataSnapshot){
        this.content = dataSnapshot.child("comment").getValue(String.class);
        this.date = dataSnapshot.child("date").getValue(String.class);
        this.chatId = dataSnapshot.child("ChatId").getValue(String.class);
        this.UserUid = dataSnapshot.child("UserUid").getValue(String.class);
    }

    public MannaUser getUser() {
        return user;
    }

    public void setUser(MannaUser user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }


    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUid",this.UserUid);
        result.put("comment",this.content);
        result.put("date",this.date );
        result.put("ChatId",this.chatId);
        return result;
    }
}
