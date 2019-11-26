package com.example.manna_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil.Place;

import java.util.ArrayList;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Place> placeList;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener listener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchPlaceAdapter(ArrayList<Place> placeList, Context context) {
        this.context = context;
        this.placeList = placeList;
    }

    public ArrayList<Place> getPlaceList() {
        return placeList;
    }

    public void setPlaceList(ArrayList<Place> placeList) {
        this.placeList = placeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.search_place_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener!= null) {
                            listener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.search_place_recyclerview_item, parent, false);
        MyViewHolder viewholder = new MyViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.name.setText(place.getName());
    }

    @Override
    public int getItemCount() {
        return this.placeList.size();
    }
}
