package com.example.manna_project.MainAgreementActivity_Util.NoticeBoard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manna_project.R;

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
        View view = inflater.inflate(R.layout.noticeboard_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
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
        ImageView userIcon;
        TextView userName;
        TextView userContent;
        TextView contentWriteDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // connect item reference
            userIcon = itemView.findViewById(R.id.noticeboard_user_icon_item);
            userName = itemView.findViewById(R.id.noticeboard_user_name_item);
            userContent = itemView.findViewById(R.id.noticeboard_content_item);
            contentWriteDate = itemView.findViewById(R.id.noticeboard_write_date_item);
        }

        public void bindData(NoticeBoard_Chat chat) {
            userName.setText(chat.getUserName());
            userContent.setText(chat.getContent());
            contentWriteDate.setText(chat.getDate());
        }
    }
}
