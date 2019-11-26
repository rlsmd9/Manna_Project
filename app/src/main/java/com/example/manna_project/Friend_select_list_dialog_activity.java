package com.example.manna_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.manna_project.MainAgreementActivity_Util.Friend.FriendListAdapter;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

import java.util.ArrayList;

public class Friend_select_list_dialog_activity extends AppCompatActivity {
    ListView listView;
    Button backBtn;
    Button chooseBtn;

    FriendListAdapter adapter;
    ArrayList<MannaUser> users;

    public final static int FRIEND_SELECT_LIST_DIALOG_ACTIVITY_REQUEST_CODE = 311;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_list_dialog_activity);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setReference();
        setEvent();

        Intent intent = getIntent();

        users = intent.getParcelableArrayListExtra("FRIENDLIST");
        boolean[] selectedUsers = intent.getBooleanArrayExtra("selectedUsers");

        int cnt = 0;
        for (MannaUser user: users) {
            Log.d("JS", "onCrewwate: 1 /" + user.toString());
            Log.d("JS", "onCrewwate: 2 /" + selectedUsers[cnt++]);
        }

        adapter = new FriendListAdapter(users, this, R.layout.activity_select_friend_list_dialog_item, selectedUsers);

        listView.setAdapter(adapter);
    }

    private void setEvent() {
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                intent.putExtra("SELECTED_USERS", adapter.getIsCheckedUsers());
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setReference() {
        listView = findViewById(R.id.activity_select_friend_list_dialog_listView);
        backBtn = findViewById(R.id.activity_select_friend_list_dialog_back_btn);
        chooseBtn = findViewById(R.id.activity_select_friend_list_dialog_choose_btn);
    }
}
