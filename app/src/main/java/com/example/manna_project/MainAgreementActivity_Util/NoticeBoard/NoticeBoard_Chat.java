package com.example.manna_project.MainAgreementActivity_Util.NoticeBoard;

public class NoticeBoard_Chat {
    String userId;
    String userName;
    String userIcon;
    String content;
    String date;

    public NoticeBoard_Chat() {
    }

    public NoticeBoard_Chat(String userId, String userName, String userIcon, String content, String date) {
        this.userId = userId;
        this.userName = userName;
        this.userIcon = userIcon;
        this.content = content;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
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
}
