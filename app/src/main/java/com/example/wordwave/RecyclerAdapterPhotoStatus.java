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


public class RecyclerAdapterPhotoStatus extends RecyclerView.Adapter<RecyclerAdapterPhotoStatus.ViewHolder> {
    Context context;
    ArrayList<Row_RecyclerView_Photo_Status> row_photostatus;

    RecyclerAdapterPhotoStatus(Context context, ArrayList<Row_RecyclerView_Photo_Status> row_photostatus) {
        this.context = context;
        this.row_photostatus = row_photostatus;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, timeStemp;
        ImageView statusPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username_photostatusstatusgragment);
            timeStemp = itemView.findViewById(R.id.timestemp_photostatusstatusgragment);
            statusPhoto = itemView.findViewById(R.id.photostatus_statusfragment);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_photostatus, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(row_photostatus.get(position).photostatusUri).into(holder.statusPhoto);
        holder.userName.setText(row_photostatus.get(position).userName);
        holder.timeStemp.setText(row_photostatus.get(position).timeStemp);
        holder.itemView.findViewById(R.id.linearlayout_layout_photostatus).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,PhotoStatus.class);
                        i.putExtra("photoStatus",row_photostatus.get(position).photostatusUri);
                        context.startActivity(i);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return row_photostatus.size();
    }

    public void fun(ArrayList<Row_RecyclerView_Photo_Status> temp) {
        row_photostatus = temp;
        notifyDataSetChanged();
    }

}
