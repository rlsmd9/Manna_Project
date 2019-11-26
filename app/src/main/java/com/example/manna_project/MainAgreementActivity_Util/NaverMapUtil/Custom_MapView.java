package com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.naver.maps.map.MapView;

public class Custom_MapView extends MapView {
    public Custom_MapView(Context context) {
        super(context);
    }

    public Custom_MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Custom_MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            getParent().requestDisallowInterceptTouchEvent(true);

        return super.onInterceptTouchEvent(ev);
    }
}
