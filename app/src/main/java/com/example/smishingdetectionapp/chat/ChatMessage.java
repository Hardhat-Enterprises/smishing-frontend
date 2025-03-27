package com.example.smishingdetectionapp.chat;
/**
 * Represents a chat message in the conversation between user and bot
 */
public class ChatMessage {
   // Constants to identify message sender type
    public static final int USER_MESSAGE = 0;
    public static final int BOT_MESSAGE = 1;

   // The content of the message
    private String message;
    // Type of message (USER_MESSAGE or BOT_MESSAGE)
    private int messageType;

    public ChatMessage(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
    }
// return The content of the message
    public String getMessage() {
        return message;
    }
// return The type of message (USER_MESSAGE or BOT_MESSAGE)
    public int getMessageType() {
        return messageType;
    }
}