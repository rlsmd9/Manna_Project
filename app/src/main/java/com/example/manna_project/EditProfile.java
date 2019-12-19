package com.example.manna_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.manna_project.MainAgreementActivity_Util.MannaUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class EditProfile extends Activity implements View.OnClickListener {

    public static final int EDITPROFILE_REQUEST_CODE = 6547;
    ImageView activity_edit_profile_img;
    TextView email;
    TextView name;
    EditText nickName;

    Button activity_edit_profile_save_btn;
    Button activity_edit_profile_close_btn;
    private File userProfilleImage;
    private File tempFile;
    private File targetDir;
    boolean fileReadPermission;
    boolean fileWritePermission;

    MannaUser myInfo;

    public final static int SELECT_IMAGE_REQUEST_CODE = 1123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileReadPermission = true;
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileReadPermission = true;
        }

        if (!fileReadPermission || !fileWritePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }

        setReferences();
        setEvents();

        Intent intent = getIntent();

        myInfo = intent.getParcelableExtra("myInfo");

        targetDir = new File(getApplicationContext().getFilesDir()+File.separator+"MannaProject"+File.separator+"userIcon");
        userProfilleImage = new File(targetDir, "UserProfileImage.jpg");

        if (myInfo != null) {
            email.setText(myInfo.geteMail());
            name.setText(myInfo.getName());
            nickName.setText(myInfo.getNickName());
        }

        if (userProfilleImage.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(userProfilleImage.getAbsolutePath());

            activity_edit_profile_img.setImageBitmap(bitmap);
        }
    }

    private void setReferences() {
        activity_edit_profile_img = findViewById(R.id.activity_edit_profile_img);
        activity_edit_profile_save_btn = findViewById(R.id.activity_edit_profile_save_btn);
        activity_edit_profile_close_btn = findViewById(R.id.activity_edit_profile_close_btn);
        email = findViewById(R.id.email);
        name = findViewById(R.id.inputName);
        nickName = findViewById(R.id.nickName);
    }

    private void setEvents() {
        activity_edit_profile_img.setOnClickListener(this);
        activity_edit_profile_save_btn.setOnClickListener(this);
        activity_edit_profile_close_btn.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    if (!targetDir.exists())
                        targetDir.mkdirs(); //create the folder if they don't exist

                    Uri photoUri = data.getData();

                    Cursor cursor = null;

                    try {
                        String[] proj = { MediaStore.Images.Media.DATA };

                        cursor = getContentResolver().query(photoUri, proj, null, null, null);

                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        cursor.moveToFirst();

                        tempFile = new File(cursor.getString(column_index));

                        Log.d("JS", "onActivityResult: " + tempFile.getAbsolutePath());
                        Glide.with(this).load(data.getData()).into(activity_edit_profile_img);
                    } catch (Exception e) {}
                    finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == activity_edit_profile_img) {


            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        } else if(v == activity_edit_profile_save_btn) {
            try {
                copy(tempFile, userProfilleImage);
            } catch (Exception e){e.printStackTrace();}

            Intent intent = new Intent(this, MainAgreementActivity.class);

            intent.putExtra("isReplaced", false);

            if (myInfo.getNickName() == null) myInfo.setNickName("");

            if (!myInfo.getNickName().equals(nickName.getText().toString())) {

                myInfo.setNickName(nickName.getText().toString());

                intent.putExtra("isReplaced", true);
                intent.putExtra("replacedMyInfo", myInfo);
            }

            setResult(RESULT_OK, intent);
            finish();
        } else if(v == activity_edit_profile_close_btn) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 112 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                fileReadPermission = true;
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                fileWritePermission = true;
        }
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
