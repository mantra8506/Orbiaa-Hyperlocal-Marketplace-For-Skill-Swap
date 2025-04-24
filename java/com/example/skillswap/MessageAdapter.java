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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.senderName.setText(message.getSender());
        holder.messageText.setText(message.getMessage());

        // Make links clickable
        Linkify.addLinks(holder.messageText, Linkify.WEB_URLS);
        holder.messageText.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle link clicks explicitly
        holder.messageText.setOnClickListener(v -> {
            String text = message.getMessage();
            if (text.contains("http://") || text.contains("https://")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.txtSenderName);
            messageText = itemView.findViewById(R.id.txtMessage);
        }
    }
}
