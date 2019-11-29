package com.example.manna_project.MainAgreementActivity_Util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class AlertMsg {
    public static void AlertMsg(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("확인", null);
        builder.setMessage(msg);
        builder.create().show();
    }
}
