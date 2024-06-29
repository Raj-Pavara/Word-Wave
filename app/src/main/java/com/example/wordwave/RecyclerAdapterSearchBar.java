package com.example.wordwave;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class RecyclerAdapterSearchBar extends RecyclerView.Adapter<RecyclerAdapterSearchBar.ViewHolder> {
    Context context;
    ArrayList<Row_RecyclerView_SearchBar> row_searchbar;

    RecyclerAdapterSearchBar(Context context, ArrayList<Row_RecyclerView_SearchBar> row_searchbar) {
        this.context = context;
        this.row_searchbar = row_searchbar;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, fullName;
        ImageView profileIcon, callIcon, videocallIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username_searchlayout_mainactivity);
            fullName = itemView.findViewById(R.id.fullname_searchlayout_mainactivity);
            profileIcon = itemView.findViewById(R.id.profileicon_searchlayout_mainactivity);
            callIcon = itemView.findViewById(R.id.callicon_searchlayout_mainactivity);
            videocallIcon = itemView.findViewById(R.id.videocallicon_searchlayout_mainactivity);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_mainactivity_search_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(row_searchbar.get(position).imageUrl).into(holder.profileIcon);
        holder.userName.setText(row_searchbar.get(position).username);
        holder.fullName.setText(row_searchbar.get(position).fullname);

        holder.itemView.findViewById(R.id.linearlayout_searchlayout_mainactivity)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, IndividualChat.class);
                                intent.putExtra("userId", row_searchbar.get(position).userId);
                                context.startActivity(intent);
                            }
                        }
                );
        holder.profileIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = ProfilePicDialog.createDialog(context, row_searchbar.get(position).username, row_searchbar.get(position).imageUrl);
                        ImageView chat_icon = dialog.findViewById(R.id.chat_icon_profile_pic_dialog);
                        ImageView info_icon = dialog.findViewById(R.id.info_icon_profile_pic_dialog);
                        dialog.show();
                        chat_icon.setOnClickListener(view -> {
                            dialog.cancel();
                            Intent intent = new Intent(context, IndividualChat.class);
                            intent.putExtra("userId", row_searchbar.get(position).userId);
                            context.startActivity(intent);
                        });
                        info_icon.setOnClickListener(
                                view -> {
                                    dialog.cancel();
                                    Intent intent = new Intent(context, IndividualUserInfo.class);
                                    intent.putExtra("userId", row_searchbar.get(position).userId);
                                    context.startActivity(intent);
                                }
                        );

                    }
                }
        );

        holder.callIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // code for call
                    }
                }
        );

        holder.videocallIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //code for video call
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return row_searchbar.size();
    }

    public void fun(ArrayList<Row_RecyclerView_SearchBar> temp) {
        row_searchbar = temp;
        notifyDataSetChanged();
    }

}
