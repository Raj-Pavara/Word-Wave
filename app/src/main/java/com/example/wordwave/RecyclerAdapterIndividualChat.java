package com.example.wordwave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerAdapterIndividualChat extends RecyclerView.Adapter<RecyclerAdapterIndividualChat.ViewHolder> {
    Context context;
    ArrayList<MessageModel> messageModels;
    String currentUserId, targetUserId;
    MenuInflater menuInflater;

    RecyclerAdapterIndividualChat(Context context, ArrayList<MessageModel> messageModels, String currentuserid, String targetUserId, MenuInflater menuInflater) {
        this.context = context;
        this.messageModels = messageModels;
        this.currentUserId = currentuserid;
        this.menuInflater = menuInflater;
        this.targetUserId = targetUserId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView message, timestemp;

        public ViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            if (type == 1) {
                message = itemView.findViewById(R.id.senderMessageText);
                timestemp = itemView.findViewById(R.id.senderMessageTime);
                itemView.setOnCreateContextMenuListener(this);
            } else {
                message = itemView.findViewById(R.id.receiverMessageText);
                timestemp = itemView.findViewById(R.id.recieverMessageTime);
            }
        }

        @Override
        public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
            menuInflater.inflate(R.menu.contextmenurecyclerview, menu);
            menu.findItem(R.id.cancel_menu_id).setOnMenuItemClickListener(onCancelMenu);
            menu.findItem(R.id.deleteforeveryone_menu_id).setOnMenuItemClickListener(onDeleteForEveryOneMenu);
            menu.findItem(R.id.deleteforme_menu_id).setOnMenuItemClickListener(onDeleteForMe);

        }

        private final MenuItem.OnMenuItemClickListener onCancelMenu = menuItem -> {
            return true;
        };

        private final MenuItem.OnMenuItemClickListener onDeleteForEveryOneMenu = menuItem -> {
            removeMessageByTimestamp(messageModels.get(getAdapterPosition()).timeStamp, true);
            return true;
        };
        private final MenuItem.OnMenuItemClickListener onDeleteForMe = menuItem -> {
            removeMessageByTimestamp(messageModels.get(getAdapterPosition()).timeStamp, false);
            return true;
        };
    }

    public void removeMessageByTimestamp(long timestamp, boolean iseveryone) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(currentUserId + targetUserId)
                .child("messages");

        messagesRef.orderByChild("timeStamp").equalTo(timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        String messageId = messageSnapshot.getKey();
                        messageSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                for (int i = 0; i < messageModels.size(); i++) {
                                    if (messageModels.get(i).timeStamp == timestamp) {
                                        messageModels.remove(i);
                                        notifyItemRemoved(i);
                                        break;
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        if (iseveryone && (!targetUserId.equals(currentUserId))) {
            messagesRef = FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(targetUserId + currentUserId)
                    .child("messages");

            messagesRef.orderByChild("timeStamp").equalTo(timestamp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            messageSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }


    //return 1 means sender is currentuser
    public int getItemViewType(int position) {
        if (messageModels.get(position).senderId.equals(currentUserId)) {
            return 1;
        }
        return 2;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_sample_sender_layout, parent, false);
            return new ViewHolder(view, 1);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_sample_receiver_layout, parent, false);
            return new ViewHolder(view, 2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(messageModels.get(position).content);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date(messageModels.get(position).timeStamp));
        holder.timestemp.setText(time);
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public void function(ArrayList<MessageModel> temp) {
        messageModels.clear();
        messageModels = temp;
        notifyDataSetChanged();
    }

}

