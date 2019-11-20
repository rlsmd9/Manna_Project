package com.example.manna_project.MainAgreementActivity_Util.NoticeBoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeBoard_RecyclerViewAdapter extends RecyclerView.Adapter<NoticeBoard_RecyclerViewAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<NoticeBoard_Chat> list;

    public NoticeBoard_RecyclerViewAdapter(LayoutInflater inflater, ArrayList<NoticeBoard_Chat> list) {
        this.inflater = inflater;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate();

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Item

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // connect item reference

        }

        public void bindData(NoticeBoard_Chat chat) {

        }
    }
}
