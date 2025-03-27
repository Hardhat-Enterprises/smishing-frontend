package com.example.smishingdetectionapp.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;
import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

// List to store all chat messages
    private List<ChatMessage> messages = new ArrayList<>();

    private final Markwon markwon;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat message layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Set the layout parameters for the message text
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set the background and text color based on the message type
        if (message.getMessageType() == ChatMessage.USER_MESSAGE) {
            // Set the background and text color for user messages
            holder.messageText.setBackgroundResource(R.drawable.user_message_background);
            holder.messageText.setTextColor(Color.WHITE);
            textParams.gravity = Gravity.END;
            textParams.setMargins(50, 8, 16, 8);
            holder.messageText.setText(message.getMessage());
        } else {
            // Set the background and text color for bot messages
            holder.messageText.setBackgroundResource(R.drawable.bot_message_background);
            holder.messageText.setTextColor(Color.BLACK);
            textParams.gravity = Gravity.START;
            textParams.setMargins(16, 8, 50, 8);
            markwon.setMarkdown(holder.messageText, message.getMessage());
        }

        holder.messageText.setLayoutParams(textParams);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // ViewHolder class for holding the message text view
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            //check if the messageText is repeated
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    // Constructor for the ChatAdapter
    public ChatAdapter(Context context) {
        messages = new ArrayList<>();
        markwon = Markwon.builder(context)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .build();
    }
}