package com.example.manna_project.MainAgreementActivity_Util.NoticeBoard;

import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

public class NoticeBoard_Chat {
    MannaUser user;
    String content;
    String date;

    public NoticeBoard_Chat() {
    }

    public NoticeBoard_Chat(MannaUser user, String content, String date) {
        this.user = user;
        this.content = content;
        this.date = date;
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
}
