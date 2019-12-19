package com.example.manna_project.MainAgreementActivity_Util.RecommendDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manna_project.R;

import java.util.ArrayList;

public class RecommendDateAdapter extends RecyclerView.Adapter<RecommendDateAdapter.DateViewHolder> {

    private  ArrayList<RecommendDate> recommendDates;
    private LayoutInflater layoutInflater;
    private  Context context;
    private OnItemClickListener onItemClickListener;

    public RecommendDateAdapter(Context context, ArrayList<RecommendDate> recommendDates){
        this.context = context;
        this.recommendDates = recommendDates;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dateView =layoutInflater.inflate(R.layout.item_recommend_date,parent,false);
        DateViewHolder dateViewHolder = new DateViewHolder(dateView);
        return dateViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        RecommendDate recommendDate = recommendDates.get(position);
        holder.startTimeText.setText(recommendDate.getRecommendStarttime());
        holder.endTimeText.setText(recommendDate.getRecommendEndTime());
    }

    @Override
    public int getItemCount() {
        return recommendDates.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeText;
        private TextView endTimeText;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.startTimeText = itemView.findViewById(R.id.recommend_start_time);
            this.endTimeText = itemView.findViewById(R.id.recommend_end_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION&&onItemClickListener!=null)
                        onItemClickListener.OnItemClciked(view,pos);
                }
            });
        }
    }
    public interface OnItemClickListener{
        void OnItemClciked(View v , int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
