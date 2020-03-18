package com.android.example.ythis.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.ythis.MainActivity;
import com.android.example.ythis.MessageActivity;
import com.android.example.ythis.Model.Chat;
import com.android.example.ythis.Model.User;
import com.android.example.ythis.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageURL;


    FirebaseUser fuser;
    public MessageAdapter(Context mContext, List<Chat> mChat, String imageURL) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Chat chat = mChat.get(i);
        viewHolder.show_message.setText(chat.getMessage());
        if (MessageActivity.isActionMode == true){
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        }else {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        if (imageURL.equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imageURL).into(viewHolder.profile_image);
        }
        if (i == mChat.size() - 1){
            if (chat.isIsseen()){
                viewHolder.txt_seen.setText("Seen");
            }else{
                viewHolder.txt_seen.setText("Delivered");
            }
        }else {
            viewHolder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public CheckBox checkBox;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            checkBox = itemView.findViewById(R.id.id_checkbox);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }


    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
