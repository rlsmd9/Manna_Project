package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity_Util.Friend.FriendListAdapter;

public class Friend_select_list_dialog_activity extends AppCompatActivity {
    ListView listView;
    FriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_list_dialog_activity);

        Intent intent = getIntent();


//        adapter = new FriendListAdapter();

        setReference();
    }

    private void setReference() {
        listView = findViewById(R.id.activity_select_friend_list_dialog_listView);
    }
}
