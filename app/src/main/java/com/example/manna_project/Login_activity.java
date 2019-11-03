package com.example.manna_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_activity extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "MANNA";
    private FirebaseAuth mAuth;

    EditText email, pw;

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        email = findViewById(R.id.login_id);
        pw = findViewById(R.id.login_pw);
        login = findViewById(R.id.login_confirm);

        mAuth = FirebaseAuth.getInstance();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = rootRef.getRoot();

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DBData data = dataSnapshot.child("version").getValue(DBData.class);

                        checkUpdateVersion(data);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
            }
        });



    }

    @Override
    public void onClick(View v) {
        if (v == login) {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {

        } else {

        }
    }

    private void checkUpdateVersion(DBData data) {
        Log.d(TAG, data.latest_version_code + ", " + data.latest_version_name + ", " + data.minimum_version_code + ", " + data.minimum_version_name);
    }

}
