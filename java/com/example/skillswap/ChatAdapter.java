package com.example.skillswap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatMessage> chatMessages;
    private String currentUserEmail;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_sender, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_receiver, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.messageText.setText(message.getMessageText());

        // Make links clickable
        holder.messageText.setAutoLinkMask(Linkify.WEB_URLS);
        holder.messageText.setMovementMethod(LinkMovementMethod.getInstance());

        // Open link when clicked
        holder.messageText.setOnClickListener(v -> {
            String clickedText = holder.messageText.getText().toString();
            if (clickedText.contains("http")) {
                String url = clickedText.substring(clickedText.indexOf("http")).split(" ")[0];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getSenderEmail().equals(currentUserEmail) ? 1 : 0;
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
        }
    }
}
