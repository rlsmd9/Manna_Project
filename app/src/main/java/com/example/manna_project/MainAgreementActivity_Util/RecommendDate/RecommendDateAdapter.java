package com.example.manna_project.MainAgreementActivity_Util.RecommendDate;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendDateAdapter extends RecyclerView.Adapter<RecommendDateAdapter.DateViewHolder> {

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
