package com.example.manna_project.MainAgreementActivity_Util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Date;

public class Calendar_Adapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater layoutInflater;
    Date date;
//    일자별 일정을 집어 넣을 자료구조
//    ArrayList

    public Calendar_Adapter(Context context, int layout, Date date) {
        this.context = context;
        this.layout = layout;
        this.date = date;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
