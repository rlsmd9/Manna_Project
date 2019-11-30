package com.example.manna_project.MainAgreementActivity_Util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.R;

public class Custom_user_icon_view extends LinearLayout {
    MannaUser user;
    Context context;
    TextView textView;

    public Custom_user_icon_view(Context context) {
        super(context);
        this.context = context;
    }

    public Custom_user_icon_view(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public Custom_user_icon_view(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setUser(MannaUser user, int isCanceled) {
        this.user = user;
        Log.d("JS", "setUser: " + user.toString());

        if (isCanceled == Promise.CANCELED)
            textView.setBackground(getResources().getDrawable(R.drawable.round_shape_red_color));
        else if (isCanceled == Promise.INVITED)
            textView.setBackground(getResources().getDrawable(R.drawable.round_shape_gray_color));
        textView.setText(user.getName() + "");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (user!=null)
            Toast.makeText(context, user.geteMail(), Toast.LENGTH_SHORT).show();

        return super.onTouchEvent(event);
    }
}
